/*globals Parse, require, exports*/
/**
 * @class Order
 * @static
 */
var orderStatus = require('cloud/Order/status');
var errors = require('cloud/Utils/errors');
var logger = require('cloud/Utils/logger');
var cloud = require('cloud/cloud');
var notifications = require('cloud/Utils/notification');
var _ = require('underscore');
var restrictions = require('cloud/Order/restrictions');
var roleManager = require('cloud/roleManager');
var stripe = require('cloud/Stripe/index');
var schedule = require('cloud/Order/schedule');


/**
 * Allows the user to rate an order. Params are send as a json object with the following properties:
 * 
 * @method rateOrder
 * @param {String} orderId The id of the order about to be rated.
 * @param {Number} rating[rating=0] The rating in percentage. Minimum value is 0.0 and maximum is 1.0.
 */
Parse.Cloud.define("rateOrder", function (request, response) {
	Parse.Cloud.useMasterKey();
	var rating = +request.params.rating, //cast to number just in case
		order = Parse.Object("Order");
	if (rating < 0 || rating > 1) {
		response.error(errors.INVALID_ARGUMENT);
	}

	order.id = request.params.orderId;
	order.set('rating', request.params.rating);
	order.save()
		.done(response.success)
		.fail(function (error) {
			logger.warn('An error occured while rating the order.');
			logger.error(error);
			response.error(errors.getErrorResponse(error));
		});
});



Parse.Cloud.define("cancelOrder", function(request, response){
	Parse.Cloud.useMasterKey();
	var orderId = request.params.id;
	var order = new (Parse.Object.extend('Order'))({id: orderId});
	
	order.set('status', orderStatus.ORDER_CANCELLED);
	order.save()
		.done(function(result){
			response.success(result);
		})
		.fail(function(error){
			response.error(error);
		})
});


/**
 * Cloud function `createOrder` creates an order for the user who invokes this function. As parameter is passed an object representing
 * an order
 * 
 * @method createOrder
 * @param {Object} order    An order object which includes certain properties as described by the example:
 *                          ```javascript
 *                          {
 *                          pickUpSchedule: {
 *	                            fromDate: [Date],
 *  	                        toDate: [Date]
 *                           },
 *                           (user: {
 *	                            id|objectId: [String]
 *                           },)
 *                           deliverySchedule: {
 *	                            fromDate: [Date],
 *  	                        toDate: [Date]
 *                           },
 *                           pickUpAddress: {
 *                              id|objectId: [String],
 *                              postCode: [Number]
 *                           },
 *                           deliveryAddress: {
 *                              id|objectId: [String],
 *                              postCode: [Number]
 *                           },
 *                           (discountCode: {
 *                              code: [String]
 *                           },)
 *                           (washAndDry: [Boolean])
 *                           }
 *                           ```
 */
Parse.Cloud.define('createOrder', function (request, response) {
	Parse.Cloud.useMasterKey();

	var user, promise, discountCodeQuery,
		necessaryPreFetchAsyncs = [],
		orderObject = request.params.order;

	if (_.isString(orderObject)) {
		orderObject = JSON.parse(orderObject);
		orderObject = orderObject.order !== undefined ? orderObject.order : orderObject;
	}

	orderObject.washAndDry = orderObject.washAndDry === 'true' || !!orderObject.washAndDry;
	orderObject.pickUpSchedule.fromDate = new Date(orderObject.pickUpSchedule.fromDate);
	orderObject.pickUpSchedule.toDate = new Date(orderObject.pickUpSchedule.toDate);
	orderObject.deliverySchedule.fromDate = new Date(orderObject.deliverySchedule.fromDate);
	orderObject.deliverySchedule.toDate = new Date(orderObject.deliverySchedule.toDate);
	orderObject.pickUpAddress.postCode = +orderObject.pickUpAddress.postCode;
	orderObject.deliveryAddress.postCode = +orderObject.deliveryAddress.postCode;

	logger.info('Requested creation of order with the following properties: ' + JSON.stringify(orderObject));

	necessaryPreFetchAsyncs.push(Parse.Config.get());
	if (orderObject.user && (orderObject.user.id || orderObject.user.objectId)) {
		user = new(Parse.Object.extend('User'))({
			id: orderObject.user.id || orderObject.user.objectId
		});
		necessaryPreFetchAsyncs.push(roleManager.checkIfUserInRoles(request.user, roleManager.ADMIN_ROLE));
		necessaryPreFetchAsyncs.push(user.fetch());
	}
	if (orderObject.discountCode && orderObject.discountCode.code) {
		discountCodeQuery = new Parse.Query(Parse.Object.extend('DiscountCode'));
		discountCodeQuery.equalTo('code', orderObject.discountCode.code);
		necessaryPreFetchAsyncs.push(discountCodeQuery.first());
	}
	if (necessaryPreFetchAsyncs.length === 0) {
		promise = new Parse.Promise();
		necessaryPreFetchAsyncs.push(promise);
		promise.resolve();
	}


	Parse.Promise.when(necessaryPreFetchAsyncs)
		.done(function createOrder(config) {
			var args = Array.prototype.slice.call(arguments),
				user = _.find(args, function (arg) {
					return arg instanceof Parse.User;
				}),
				discountCode = _.find(args, function (arg) {
					return arg && arg.constructor && arg.constructor.prototype.className === 'DiscountCode';
				}),
				isAdmin = _.find(args, function (arg) {
					return typeof arg === "boolean";
				}),
				order = new(Parse.Object.extend('Order'))(),
				pickUpAddress = new(Parse.Object.extend('Address'))(orderObject.pickUpAddress),
				deliveryAddress = new(Parse.Object.extend('Address'))(orderObject.deliveryAddress),
				pickUpSchedule = new(Parse.Object.extend('DriverSchedule'))(orderObject.pickUpSchedule),
				deliverySchedule = new(Parse.Object.extend('DriverSchedule'))(orderObject.deliverySchedule);

			order.set('pickUpAddress', pickUpAddress);
			order.set('deliveryAddress', deliveryAddress);
			order.set('pickUpSchedule', pickUpSchedule);
			order.set('deliverySchedule', deliverySchedule);
			order.set('washAndDry', orderObject.washAndDry);
			order.set('status', orderStatus.ORDER_PLACED);
			if (user) {
				if (isAdmin || user.id === request.user.id) {
					order.set('user', user);
				} else {
					logger.warn('Unauthorized user ' + request.user.id + ' attempted to create an order for user ' + orderObject.user);
					response.error(errors.USER_NOT_AUTHORIZED_FOR_ORDER);
				}
			} else {
				order.set('user', request.user);
			}
			if (discountCode) {
				discountCode.get('usersUsedIt').push(order.get('user').id);
				order.set('discountCode', discountCode);
			}

			var orderValidator = new restrictions.OrderValidator(order, config);

			if (!orderValidator.isValid) {
				logger.warn('Order ' + order.id + ' failed validation. Aborting');
				response.error(orderValidator.errorMessages.concat('. '));
				return;
			}

			return assignDriverAndLaundryToOrder(order);
		})
		.done(function saveOrder(order) {
			return order.save();
		})
		.done(function (order) {
			logger.info('Order ' + order.id + ' has been placed');
			response.success(order);
		})
		.fail(function (error) {
			logger.warn('Error creating order');
			logger.error(error);
			response.error(errors.getErrorResponse(error));
		});


});

/**
 * Notifies the user for a status change event on the order by sending a push notification
 * 
 * @method notifyUserStatusChange
 * @private
 * @param {Object} order  The order in question as a Parse.Object
 * @param {Object} config The Parse.Config file
 * @returns {Object} A Parse.Promise
 */
function notifyUserStatusChange(order, config) {
	var orderStatus, configStatuses, statusDataFromConfig;
	configStatuses = config.get('orderStatuses');
	orderStatus = order.get('status');

	statusDataFromConfig = _.find(configStatuses, function (configStatus) {
		return configStatus.name === orderStatus.toUpperCase();
	});

	return notifications.sendUserNotifications(statusDataFromConfig.text, [order.get('user').id], {orderId: order.id});
}


/**
 * Generates a Parse.Query which is used to find all the available drivers who operate within a certain
 * post code which is retrieved from either a pickUp or delivery driver's schedule.
 * 
 * @method createAvailableDriverQueryForDateAndCode
 * @private
 * @param   {Object} schedule A Parse.Object for DriverSchedule table
 * @param   {Number} postCode A postal code where the query should be based on finding available driver
 * @returns {Object} A Parse.Query object.
 */
function createAvailableDriverQueryForDateAndCode(schedule, postCode, laundry) {

	// Reserved Laundries query
	var LaundrySchedule = Parse.Object.extend('LaundrySchedule');
	var laundriesWithScheduleQuerySuperSet = new Parse.Query(LaundrySchedule);
	laundriesWithScheduleQuerySuperSet.lessThanOrEqualTo('fromDate', schedule.get('fromDate'));
	laundriesWithScheduleQuerySuperSet.greaterThanOrEqualTo('toDate', schedule.get('toDate'));

	var laundriesWithScheduleForQuerySubSet = new Parse.Query(LaundrySchedule);
	laundriesWithScheduleForQuerySubSet.lessThan('toDate', schedule.get('fromDate'));

	var laundriesWithScheduleForQuery = Parse.Query.or(laundriesWithScheduleQuerySuperSet, laundriesWithScheduleForQuerySubSet);

	var notAvailableLaundryQuery = new Parse.Query(Parse.Object.extend('Laundry'));
	notAvailableLaundryQuery.matchesKeyInQuery('objectId', 'laundry', laundriesWithScheduleForQuery);

	// Reserved Drivers query
	var DriverSchedule = Parse.Object.extend('DriverSchedule');
	var notAvailableDriverQuerySuperSet = new Parse.Query(DriverSchedule);
	notAvailableDriverQuerySuperSet.lessThanOrEqualTo('fromDate', schedule.get('fromDate'));
	notAvailableDriverQuerySuperSet.greaterThanOrEqualTo('toDate', schedule.get('toDate'));

	var notAvailableDriverQuerySubSet = new Parse.Query(DriverSchedule);
	notAvailableDriverQuerySubSet.lessThan('toDate', schedule.get('fromDate'));

	var notAvailableDriverQuery = Parse.Query.or(notAvailableDriverQuerySuperSet, notAvailableDriverQuerySubSet);

	// Checking availability of driver by excluding reserved schedules
	var availableDriverQuery = new Parse.Query(Parse.Object.extend('Driver'));
	availableDriverQuery.containedIn('postCodes', [postCode]);
	availableDriverQuery.doesNotMatchKeyInQuery('objectId', 'drivers', notAvailableLaundryQuery);
	availableDriverQuery.doesNotMatchKeyInQuery('objectId', 'driver', notAvailableDriverQuery);
	if (laundry) {
		availableDriverQuery.equalTo('laundry', laundry);
	}

	return availableDriverQuery;
}


/**
 * Given an order assigns pickup/delivery drivers to order and a laundry whihch will
 * handle the processing of that order
 * 
 * @method assignDriverAndLaundryToOrder
 * @private
 * @param   {Object} order The order in question
 * @returns {Object} A Parse.Promise. If successfull the order will be returned on success. 
 *                   Otherwise the error message that occured during processing.
 */
function assignDriverAndLaundryToOrder(order) {
	var driverQueriedPromise, availablePickUpDriverQuery, availableDeliveryDriverQuery,
		pickUpSchedule = order.get('pickUpSchedule'),
		pickUpPostCode = order.get('pickUpAddress').get('postCode'),
		pickUpDriver = order.get('pickUpDriver'),

		deliveryPostCode = order.get('deliveryAddress').get('postCode'),
		deliverySchedule = order.get('deliverySchedule'),
		deliveryDriver = order.get('deliveryDriver'),

		promises = [];
	//	if (!!pickUpSchedule && pickUpSchedule.get('isTimeOff') === undefined) {
	//		pickUpSchedule.set('isTimeOff', true);
	//	}
	//	if (!!deliverySchedule && deliverySchedule.get('isTimeOff') === undefined) {
	//		deliverySchedule.set('isTimeOff', true);
	//	}



	if (pickUpDriver === undefined && deliveryDriver !== undefined) {
		availablePickUpDriverQuery = createAvailableDriverQueryForDateAndCode(pickUpSchedule, pickUpPostCode, deliveryDriver.get('laundry'));
		driverQueriedPromise = availablePickUpDriverQuery.find();
		promises = [availablePickUpDriverQuery.find(), Parse.Promise.as([deliveryDriver])];
	}

	if (deliveryDriver === undefined && pickUpDriver !== undefined) {
		availableDeliveryDriverQuery = createAvailableDriverQueryForDateAndCode(deliverySchedule, deliveryPostCode, pickUpDriver.get('laundry'));
		driverQueriedPromise = availableDeliveryDriverQuery.find();
		promises = [Parse.Promise.as([pickUpDriver]), availableDeliveryDriverQuery.find()];
	}

	if (pickUpDriver === undefined && deliveryDriver === undefined) {
		availablePickUpDriverQuery = createAvailableDriverQueryForDateAndCode(pickUpSchedule, pickUpPostCode);
		driverQueriedPromise = availablePickUpDriverQuery.find();
		var findPickUpDrivers = availablePickUpDriverQuery.find();
		var findDeliveryDriverBasedOnPickup = findPickUpDrivers.done(function (pickUpDrivers) {
			logger.debug('Based on the drivers got from pickup "' + JSON.stringify(pickUpDrivers) + '" attempting to get delivery drivers.');
			if (pickUpDrivers.length) {
				availableDeliveryDriverQuery = createAvailableDriverQueryForDateAndCode(deliverySchedule, deliveryPostCode, pickUpDrivers[0].get('laundry'));
				return availableDeliveryDriverQuery.find();
			} else {
				return Parse.Promise.error(errors.DRIVER_NOT_AVAILABLE_PICKUP);
			}

		});
		promises = [findPickUpDrivers, findDeliveryDriverBasedOnPickup];
	}


	return Parse.Promise.when(promises)
		.done(function (pickUpDrivers, deliveryDrivers) {
			logger.debug('Looked for pickUpDriver and deliveryDrivers. Got ' + JSON.stringify(pickUpDrivers) + ' and ' + JSON.stringify(deliveryDrivers));
			if (pickUpDrivers && pickUpDrivers.length) {
				logger.debug('Pickup Drivers found for order.');
			} else {
				logger.debug('Pickup drivers not found for order.');
				logger.debug(arguments);
				return Parse.Promise.error(errors.DRIVER_NOT_AVAILABLE_PICKUP);
			}

			if (deliveryDrivers && deliveryDrivers.length) {
				logger.debug('Delivery drivers found for order.');
			} else {
				logger.debug('Delivery drivers not found for order.');
				logger.debug(arguments);
				return Parse.Promise.error(errors.DRIVER_NOT_AVAILABLE_DELIVERY);
			}


			pickUpSchedule.set('driver', pickUpDrivers[0]);
			pickUpSchedule.set('toAddress', order.get('pickUpAddress'));
			order.set('pickUpDriver', pickUpDrivers[0]);
			order.set('pickUpSchedule', pickUpSchedule);

			deliverySchedule.set('driver', deliveryDrivers[0]);
			deliverySchedule.set('toAddress', order.get('deliveryAddress'));
			order.set('deliveryDriver', deliveryDrivers[0]);
			order.set('deliverySchedule', deliverySchedule);

			order.set('laundry', pickUpDrivers[0].get('laundry'));

			return Parse.Promise.as(order);
		});
}

function getFullOrderAsync(order) {
	Parse.Cloud.useMasterKey();
	var includeToQuery = function (query, column) {
		if (order.get(column)) {
			query.include(column);
		}
	};
	var fullOrderFetching, orderQuery,
		fetchingPointers = [],
		fetchingDirtyPointers = [];


	if (order.id) {
		logger.debug('Order exists, trying to make a query');
		orderQuery = new Parse.Query(Parse.Object.extend('Order'));
		orderQuery.equalTo('objectId', order.id);
		includeToQuery(orderQuery, 'user');
		includeToQuery(orderQuery, 'pickUpDriver');
		includeToQuery(orderQuery, 'deliveryDriver');
		includeToQuery(orderQuery, 'pickUpSchedule');
		includeToQuery(orderQuery, 'deliverySchedule');
		includeToQuery(orderQuery, 'pickUpAddress');
		includeToQuery(orderQuery, 'deliveryAddress');
		includeToQuery(orderQuery, 'payment');
		includeToQuery(orderQuery, 'discountCode');
		includeToQuery(orderQuery, 'laundry');
		fullOrderFetching = orderQuery.first();

		Object.keys(order.attributes).forEach(function (attrKey) {
			//Here we iterate and fetch order's dirty pointers only since these wont be replaced later on
			//but we still need to have those objects.
			var attrVal = order.get(attrKey);
			if (order.dirty(attrKey) && attrVal instanceof Parse.Object && _.isEmpty(attrVal.attributes)) {
				fetchingDirtyPointers.push(attrVal.fetch());
			}
		});
	} else {
		// Everything expected to be `dirty` from pointers so fetch will take place
		// on next step
		logger.debug('Order is new, returning it as result');
		Object.keys(order.attributes).forEach(function (attributeKey) {
			var attributeVal = order.get(attributeKey);
			if (attributeVal instanceof Parse.Object && _.isEmpty(attributeVal.attributes)) {
				fetchingPointers.push(order.get(attributeKey).fetch());
			}
		});

		fullOrderFetching = Parse.Promise.when(fetchingPointers);
	}



	return Parse.Promise.when([fullOrderFetching, Parse.Promise.when(fetchingDirtyPointers)])
		.done(function (queryOrder) {
			Object.keys(queryOrder.attributes).forEach(function (attrKey) {
				var attrVal = queryOrder.get(attrKey);
				if (!order.dirty(attrKey)) {
					order.set(attrKey, attrVal, {
						silent: true
					});
				}
			});
			logger.debug(queryOrder.toJSON());
			return Parse.Promise.as(order);

		})
		.fail(function (e) {
			return Parse.Promise.error(e);
		});
}



exports.init = function initOrder() {

	cloud.beforeSave("Order", function (request, response, config) {
		var orderRequest = request.object;
		Parse.Cloud.useMasterKey();


		getFullOrderAsync(orderRequest)
			.done(function (order) {
				logger.log('Trying to handle before save logic for order. ' + JSON.stringify(order.toJSON()));

				if (order.existed()) {
					var orderValidator = new restrictions.OrderValidator(order, config);

					if (!orderValidator.isValid) {
						response.error(orderValidator.errorMessages.concat('. '));
					}
					if (!order.get('user')) {
						logger.warn('No user set for an order. Setting the user that made the request as owner of the order but this should be handled programmatically.');
						order.set('user', request.user);
					}
					if (order.dirty('status')) {
						logger.info('Status of the order ' + order.id + ' changed to ' + order.get('status'));
						if (order.get('status') === orderStatus.CLEANING) {
							stripe.chargeOrder(order)
								.done(function orderChargedSuccess(payment) {
									order.set('payment', payment);
									notifyUserStatusChange(order, config);
									response.success(order);
								})
								.fail(function orderChargedError(error) {
									logger.warn('Charging order failed');
									logger.error(JSON.stringify(error));

									//									order.set('status', orderStatus.ON_HOLD);
									//									order.save();

									response.error(errors.getErrorResponse(error));
									//return Parse.Promise.as(order);
								});

						} else {
							notifyUserStatusChange(order, config);
							response.success(order);
						}

					}
				} else {
					response.success(order);
				}

			})
			.fail(function (e) {
				response.error(errors.getErrorResponse(e));
			});
	});



};