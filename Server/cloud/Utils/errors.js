/*globals exports, require*/
/**
 * @class Errors
 * @static
 */
var _ = require('underscore');

/*Generic Errors*/
exports.GENERIC_ERROR_RESPONSE = 'Something went bad. We will shortly look into it.';
exports.INVALID_ARGUMENT = 'The request could not be completed due to some missing parameters. If the issue persists please contact Cleanium team';
exports.NO_DATA_FOUND = 'No data found for your request';

exports.ACTION_NOT_AUTHORIZED = "The requested action is not authorized for you. This incident will be reported.";

/*Payments*/
exports.CHARGE_NOT_AUTHORIZED = 'This account is not authorized to charge money. This incindent will be reported';
exports.CHARGE_AMOUNT_NOT_VALID = 'The amount for the charge could not be processed';
exports.CHARGE_AMOUNT_TOO_SMALL = 'The amount for the charge could not be processed';
exports.CHARGE_ALREADY_OCCURED = 'The order has been already charged on the past. Charging an order more than once is not allowed';
exports.REFUND_NOT_AUTHORIZED = 'This account is not authorized to refund an order. This incident will be reported';

exports.USER_NOT_STRIPE_CUSTOMER = 'No stripe details found linked with that user';
exports.USER_ALREADY_STRIPE_CUSTOMER = 'The user has been already linked to a stripe customer';


/*Ordering*/
exports.ORDER_DATE_DELIVERY_PICKUP_NOT_VALID = 'The dropoff date appears to be earlier than pick up date.';
exports.ORDER_DATE_DURATION_NOT_VALID = 'The duration provided is not valid.';
exports.ORDER_DATE_DURATION_NOT_EQUALS_AVAILABILITY_INTERVAL = 'It seems that the duration of a pick up or delivery is different than drivers allowed duration';
exports.ORDER_DATES_INVALID_FOR_DRY_CLEAN = 'The minimum period between a pick up and dropoff when dry clean is selected is 24h.';
exports.ORDER_DATES_INVALID_BETWEEN_DELIVERY_DROPOFF = 'The minimum period between a pick up and dropoff is 6h.';
exports.ORDER_STATUS_INVALID = "The status given for this order is invalid";
exports.USER_HAS_NO_CC_DATA = "Unable to proceed with order. There is no credit card entered.";
exports.ORDER_DATES_BIG_DIFFERENCE_BETWEEN_PICKUP_DELIVERY = "The maximum allowed date for scheduling a drop off is 7 days after pick up date.";
exports.ORDER_DATE_BIG_DIFFERENCE_BETWEEN_NOW_PICKUP = "You can not schedule a pick up date further than one week from now.";
exports.USER_NOT_AUTHORIZED_FOR_ORDER = "Placing an order on behalf of other user is not allowed. The incident will be reported";


exports.DRIVER_NOT_AVAILABLE_PICKUP = 'Unfortunately there is no driver available on your area able to collect your clothings on the specified date.';
exports.DRIVER_NOT_AVAILABLE_DELIVERY = 'Unfortunately there is no driver available on your area able to delivery back to you your clothings on the specified date.';

/* DiscountCodes */
exports.DISCOUNT_CODE_INVALID = 'The code provided is invalid';
exports.DISCOUNT_CODE_USED = 'You have already used this code in a previous order';
exports.DISCOUNT_CODE_EXPIRED = 'This code has expired';

exports.SCHEDULE_DRIVER_EXISTS = 'The specific driver has already a schedule in that time frame.';
exports.SCHEDULE_LAUNDRY_EXISTS = 'The specific laundry has already a schedule in that time frame.';
exports.SCHEDULE_BAD_DATES = 'The dates specified for pickup/dopoff seem wrong. Please review your input.';
/**
 * Helper function which iterates over all error messages and returns the proper one 
 * as response to client. If the error is not found, the generic error is returned instead
 * 
 * @method getErrorResponse
 * @private
 * @param   {Object|String} error The `Error` object to be resolved
 * @returns {String}        The error message for the specified error object or generic 
 *                          error response
 */
exports.getErrorResponse = function(error) {
	
	var errorMessage = '';
	if( _.isString(error) ) {
		errorMessage = error;
	} else {
		errorMessage = error.message;
	}
	
	var message = _.find(exports, function(existingMessage){
		return existingMessage === errorMessage
	});
	
	return message || exports.GENERIC_ERROR_RESPONSE;
};


/**
 * Maps possible errors from stripe charge response to custom application errors
 * 
 * @method getStripeErrorsFromCharge
 * @private
 * @param   {Object} charge Stripe `Charge` object
 * @returns {String} A string with all errors contained in a `Charge` response.
 */
exports.getStripeErrorsFromCharge = function (charge) {
	var errorResponse = 'Payment failed. ';
	var chargeErrors = {
		cvc_check: {
			'undefined': '',
			pass: '',
			unchecked: '',
			fail: 'The CVC is incorrect. ',
			unavailable: 'The bank did not check the CVC provided. '
		},
		address_zip_check: {
			'undefined': '',
			pass: '',
			unchecked: '',
			fail: 'The ZIP code provided is incorrect. ',
			unavailable: 'The bank did not check the ZIP code provided. '
		}
	}
	errorResponse += chargeErrors.cvc_check[charge.cvc_check];
	errorResponse += chargeErrors.address_zip_check[charge.address_zip_check];
	return errorResponse;
};