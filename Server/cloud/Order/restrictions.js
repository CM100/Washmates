/*globals Parse, require, exports*/
/**
 * @class OrderRestrictions
 * @static
 */
var logger = require('cloud/Utils/logger');
var errors = require('cloud/Utils/errors');
var orderStatuses = require('cloud/Order/status');
var _ = require('underscore');


var oneHourInMilliSeconds = 60 * 60 * 1000;
var oneDayInMilliSeconds = 24 * oneHourInMilliSeconds;
var oneWeekInMilliSeconds = 7 * oneDayInMilliSeconds;

/**
 * A constructor that calculates based on the current date the minimum and maximum order to be placed
 * This sucks a lot cause moment was unable to add hours/days although in client side works. Also setTime
 * breaks for some reason.
 * 
 * @class OrderDate
 * @private
 * @returns {Object} An object with `minimumAllowed` and `maximumAllowed` dates an order can be placed
 */
exports.OrderDate = function OrderDateRestrictions(config) {
	config = config || Parse.Config.current();

	var driverAvailabilityInterval = config.get('driverAvailabilityInterval') || 1800000;

	var absoluteToHour = new Date();
	absoluteToHour.setMilliseconds(0);
	absoluteToHour.setSeconds(0);
	absoluteToHour.setMinutes(0);

	var msDiffereceFromAbsoluteHour = Date.now() - absoluteToHour;

	var anHourFromNow;

	if (msDiffereceFromAbsoluteHour % driverAvailabilityInterval > 0) {
		var nextAvailableSlotFromAbsoluteHour = (~~(msDiffereceFromAbsoluteHour / driverAvailabilityInterval) + 1) * driverAvailabilityInterval;
		anHourFromNow = new Date(absoluteToHour.getTime() + nextAvailableSlotFromAbsoluteHour);
	} else {
		anHourFromNow = new Date();
	}


	logger.debug('About to add one hour from ' + anHourFromNow);
	anHourFromNow = new Date(anHourFromNow.getTime() + oneHourInMilliSeconds);
	logger.debug('The new date an hour ahead is ' + anHourFromNow);

	var twoWeeksFromNow = new Date();
	logger.debug('About to add two weeeks from ' + twoWeeksFromNow);
	twoWeeksFromNow = new Date(anHourFromNow.getTime() + (2 * oneWeekInMilliSeconds));
	logger.debug('The new date two weeks ahead is ' + twoWeeksFromNow);

	this.minimumAllowed = anHourFromNow;
	this.maximumAllowed = twoWeeksFromNow;

	return this;
};

/**
 * Validates an order based on restrictions described on specification and some obvious ones
 * 
 * @class OrderValidator
 * @private
 * @param {Object} order  A Parse.Object for Order.
 * @param {Object} config The Parse.Config object (can be found on Parse application dashboard)
 */
exports.OrderValidator = function OrderValidator(order, config) {


	this.isValid = true;

	this.errorMessages = [];

	/**
	* Validates an order in a synchronous maner. Is used when all of the order's dependencies
	* are present as `Parse.Object`s and not pointers. Its use is on create order function where
	* all the data for an order are given beforehand
	* 
	* @method validate
	*/
	this.validate = function validateOrder() {
		logger.info('Validating order ' + order.id);

		this._validateSchedule();
		this._validateStatus();

		logger.info('Order validated as ' + (this.isValid ? 'valid' : 'invalid'));

	}
	
	/**
	* Validates the scheduling part of an order based on business logic and basic restrictions
	* that need to be met.
	* 
	* @method _validateSchedule
	* @private
	*/
	this._validateSchedule = function validateSchedule() {
		logger.info('Validating schedule for order ' + order.id);
		
		var pickUpSchedule = order.get('pickUpSchedule');
		logger.debug('pickUpSchedule: ' + JSON.stringify(pickUpSchedule) );
		
		var deliverySchedule = order.get('deliverySchedule');
		logger.debug('deliverySchedule: ' + JSON.stringify(deliverySchedule) );
		
		var pickUpFromDate = pickUpSchedule.get('fromDate').getTime();
		logger.debug('pickUpFromDate: ' + JSON.stringify(pickUpFromDate) );
		
		var pickUpToDate = pickUpSchedule.get('toDate').getTime();
		logger.debug('pickUpToDate: ' + JSON.stringify(pickUpToDate) );
		
		var pickUpDuration = pickUpToDate - pickUpFromDate;
		logger.debug('pickUpDuration: ' + JSON.stringify(pickUpDuration) );
		
		var deliveryFromDate = deliverySchedule.get('fromDate').getTime();
		logger.debug('deliveryFromDate: ' + JSON.stringify(deliveryFromDate) );
		
		var deliveryToDate = deliverySchedule.get('toDate').getTime();
		logger.debug('deliveryToDate: ' + JSON.stringify(deliveryToDate) );
		
		var deliveryDuration = deliveryToDate - deliveryFromDate;
		logger.debug('deliveryDuration: ' + JSON.stringify(deliveryDuration) );
		
		var pickUpDeliveryTimeDifferenceMS = deliveryFromDate - pickUpFromDate;
		logger.debug('pickUpDeliveryTimeDifferenceMS: ' + JSON.stringify(pickUpDeliveryTimeDifferenceMS) );
		
		var shouldDryClean = !!order.get('washAndDry');
		logger.debug('shouldDryClean: ' + JSON.stringify(shouldDryClean) );


		var now = new Date();

		
		if (pickUpFromDate > pickUpToDate) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_DATE_DURATION_NOT_VALID);
			logger.warn('Order invalid. fromDate is higher than toDate for pick up.');
		}

		if (deliveryFromDate > deliveryToDate) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_DATE_DURATION_NOT_VALID);
			logger.warn('Order invalid. fromDate is higher than toDate for delivery.');
		}

		if (deliveryFromDate < pickUpFromDate) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_DATE_DELIVERY_PICKUP_NOT_VALID);
			logger.warn('Order invalid. The delivery date given is smaller than pick up date.')
		}

		if (deliveryDuration !== pickUpDuration && pickUpDuration !== config.get('driverAvailabilityInterval')) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_DATE_DURATION_NOT_EQUALS_AVAILABILITY_INTERVAL);
			logger.warn('Order invalid. The specified duration of a pick up or delivery does not match the duration set on the settings variable driverAvailabilityInterval.');
		}

		if (shouldDryClean) {
			if (pickUpDeliveryTimeDifferenceMS < oneDayInMilliSeconds) {
				this.isValid = false;
				this.errorMessages.push(errors.ORDER_DATES_INVALID_FOR_DRY_CLEAN);
				logger.warn('Order invalid. Dryclean is selected but the difference between pickup-delivery is set to less than 24h.')
			}
		} else {
			if (pickUpDeliveryTimeDifferenceMS < 6 * oneHourInMilliSeconds) {
				this.isValid = false;
				this.errorMessages.push(errors.ORDER_DATES_INVALID_BETWEEN_DELIVERY_DROPOFF);
				logger.warn('Order invalid. The difference between pickup-delivery is set to less than 6h.')
			}
		}

		if (deliveryFromDate - pickUpFromDate > oneWeekInMilliSeconds) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_DATES_BIG_DIFFERENCE_BETWEEN_PICKUP_DELIVERY);
			logger.warn('Order invalid. The difference between pickup date and delivery date is higher than 7 days.')
		}

		if (pickUpFromDate - now.getTime() > oneWeekInMilliSeconds) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_DATE_BIG_DIFFERENCE_BETWEEN_NOW_PICKUP);
			logger.warn('Order invalid. The requested pick up date is later than 7 days from now.');
		}
	}

	/**
	* Validates the status text of an order based on available statuses.
	* 
	* @method _validateStatus
	* @private
	*/
	this._validateStatus = function validateStatus() {
		var configStatuses = Object.keys(orderStatuses);
		var orderStatus = order.get('status');
		
		logger.debug('Checking status ' + orderStatus + ' in cofnig statuses ' + JSON.stringify(configStatuses) );
		
		var orderStatusInConfig = _.find(configStatuses, function(configStatus){
			return orderStatuses[configStatus] === orderStatus;
		});
		
		logger.debug('For the given status found order status in config ' + orderStatusInConfig ) ;

		if (orderStatusInConfig === undefined) {
			this.isValid = false;
			this.errorMessages.push(errors.ORDER_STATUS_INVALID);
			logger.warn('Order invalid. The status ' + orderStatus + ' can not be found in config. ');
		}
	}

	this.validate();
}