/*globals require, exports, Parse*/

var errors = require('cloud/Utils/errors');
var logger = require('cloud/Utils/logger');
var orderStatus = require('cloud/Order/status');

exports.init = function () {};

/**
 * Calculates the discounted amount based on the given discount code and
 * total amount for an order before discount
 * 
 * @method calculateDiscountAmountAsync
 * @private
 * @param   {Object}  code        Parse Object or Pointer of a `DiscountCode` class
 * @param   {Number}  totalAmount The total amount of an order before discount
 * @returns {Promise} A Promise with the discount amount if the code is given.
 */
exports.calculateDiscountAmountAsync = function (code, totalAmount, services) {
	logger.info('Calculating discount amount for code' + code.id);
	return exports.verifyDiscountCodeAsync(code)
		.done(function (discountCode) {
			var discountAmount = discountCode.get('amount');

			if (discountCode.get('isPercentage')) {
				if (discountAmount > 1) {
					discountAmount = discountAmount / 100;
				}
				discountAmount = discountAmount * totalAmount;
				logger.info('Discount is percentage. The effective amount to charge is set to ' + totalAmount + ' AUD. A discount of ' + discountAmount + ' AUD was calculated.');

			} else {
				logger.info('The effective amount to charge is set to ' + totalAmount + ' AUD. A discount of ' + discountAmount + ' AUD was provided.');

			}
			return Parse.Promise.as(discountAmount);
		})
		.fail(function (error) {
			logger.warn('Error calculating discount for code ' + JSON.stringify(code));
			logger.error(error);
			return Parse.Promise.error(error);
		});
};

/**
 * Verifies that the discount code is valid and the user has never used it on
 * a past order.
 * 
 * @method verifyDiscountCodeAsync
 * @private
 * @param   {Object} discountCode  The code string provided to the user
 * @param   {Object} appliedByUser A `Parse.User` object of the user who
 *                                 asked verification of the code.
 * @returns {Promise} A `Parse.Promise` with the discount code object if valid or
 *                    an error message if invalid.
 */
exports.verifyDiscountCodeAsync = function (discountCode, appliedByUser) {
	Parse.Cloud.useMasterKey();
	var discountCodeString = Parse._.isString(discountCode) ? discountCode : (discountCode.id || discountCode.get('code'));
	logger.info('Verification of discount code ' + discountCodeString + ' was requested by user ' + appliedByUser.id);
	var discountCodeQuery = new Parse.Query(Parse.Object.extend('DiscountCode'));
	if (discountCode.id) {
		discountCodeQuery.equalTo('objectId', discountCode.id);
	} else {
		discountCodeQuery.equalTo('code', discountCodeString);
	}
	return discountCodeQuery.first()
		.fail(function(error){
			logger.warn('Unable to find discount code for query ' + JSON.stringify(discountCodeQuery));
			logger.error(error);
			return Parse.Promise.error(error);
		})
		.done(function verifyDiscountCodeOnly(discount) {
			var now = new Date();
			var error;

			if (!discount) {
				logger.warn('The code ' + discountCodeString + ' was invalid [non existing].');
				logger.error(errors.DISCOUNT_CODE_INVALID);
				error = new Error(errors.DISCOUNT_CODE_INVALID);
				return Parse.Promise.error(error);

			}

			if (discount.get('expiresAt') !== undefined && discount.get('expiresAt') < now) {
				logger.info('Discount code with ID ' + discount.id + ' is already expired at ' + discount.get('expiresAt'));
				error = new Error(errors.DISCOUNT_CODE_EXPIRED);
				return Parse.Promise.error(error);
			}

			if (!discount.get('canBeUsedOnce')) {
				return Parse.Promise.as(discount);
				
			} else {
				if (!appliedByUser) {
					logger.error('Discount code verification could not proceed because no user provided.');
					return Parse.Promise.error(errors.INVALID_ARGUMENT);
				}
				var ordersWithDiscountQuery = new Parse.Query(Parse.Object.extend('Order'));
				ordersWithDiscountQuery.equalTo('discountCode', discount);
				ordersWithDiscountQuery.equalTo('user', appliedByUser);
				return ordersWithDiscountQuery.find().done(function (ordersDiscounted) {
					var failedOrders = Parse._.filter(ordersDiscounted, function(order){
						return order.get('status') === orderStatus.ORDER_CANCELLED || order.get('status') === orderStatus.ON_HOLD;
					})
					// Allow to be used again only if all the orders done so far have failed to complete.
					if( ordersDiscounted.length > 0 && failedOrders.length !== ordersDiscounted.length ) {
						var error = new Error(errors.DISCOUNT_CODE_USED);
						return Parse.Promise.error(error);
					} else {
						logger.info('Discount code with ID ' + discount.id + ' is valid');
						return Parse.Promise.as(discount);
					}
				});
			}
		})
		.fail(function(error){
			logger.warn('Failed to verify the discount code');
			logger.error(error);
			return Parse.Promise.error(error);
		});
};

/**
 * Parse cloud function for verification of discount code.
 * 
 * @method verifyDiscountCode
 * @param   {String} params.discountCode  The code string provided to the user
 * @returns {Promise} A `Parse.Promise` with the discount code object if valid or
 *                    an error message if invalid.
 */
Parse.Cloud.define('verifyDiscountCode', function (request, response) {
	var user = request.user;
	var discountCode = request.params.discountCode;

	var discount = new(Parse.Object.extend('DiscountCode'))({
		code: discountCode
	});

	if (!discountCode) {
		response.error(errors.DISCOUNT_CODE_INVALID);
		return;
	}
	exports.verifyDiscountCodeAsync(discount, user)
		.done(function verifyDiscountCodeOnSuccess(discount) {
			response.success(discount);
		})
		.fail(function verifyDiscountCodeOnError(error) {
			logger.warn('Error during verification of discount code.')
			logger.error(error);
			response.error(errors.getErrorResponse(error));
		});
});