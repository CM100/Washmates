/*globals require, exports, Parse*/
var errors = require('cloud/Utils/errors');
var logger = require('cloud/Utils/logger');
/**
 * It translates the amount on Stripe acceptable amount since Stripe requires it to be
 * in cents. Also validates the amount given on constructor against basic and Stripe's
 * restrictions.
 * 
 * @class StripeAmount
 * @constructor StripeAmount
 * @protected
 * @param   {Number} chargeAmount The amount to be converted in cents
 * @returns {Number} Amount in cents. Rounded up to the closest digit since javascript
 *                   gets sometime quirky with math calculations.
 */
function StripeAmount(chargeAmount) {
	var amount;
	Number.parseFloat = Number.parseFloat || parseFloat;
	Number.isNaN = Number.isNaN || isNaN;

	function _Amount() {
		/**
		 * @property isValid 
		 */
		this.isValid = false;
		/**
		 * @property errorMessage 
		 */
		this.errorMessage = 'Not validated';

		this._init = function () {
			this._validateAmount();
		};

		/**
		 * Validates the amount. Ensures the amount is a number is greater than 50 cents
		 * which is the minimum allowed amount per transaction for Stripe. Sets the
		 * `isValid` & `errorMessage` properties to the results of the validation.
		 * 
		 * @private
		 * @method _validateAmount
		 */
		this._validateAmount = function () {
			logger.log('Validating given amount.');

			if (Number.isNaN(this.valueOf())) {
				logger.error('The amount of ' + this.valueOf() + ' is considered invalid. Amount from request: ' + chargeAmount);

				this.isValid = false;
				this.errorMessage = errors.CHARGE_AMOUNT_NOT_VALID;
				return;
			}
			if (this.valueOf() < 50) {
				logger.error('The amount of ' + this.valueOf() + ' cents is too small to processed.');

				this.isValid = false;
				this.errorMessage = errors.CHARGE_AMOUNT_TOO_SMALL;
				return;
			}
			this.isValid = true;
			this.errorMessage = undefined;
			logger.log('The amount is valid.');
		}

		this._init();
		return this;
	}

	if (this instanceof StripeAmount) {
		var chargeAmountCents = Math.ceil(Number.parseFloat(+chargeAmount) * 100);
		logger.debug('Charge amount : ' + chargeAmount);
		logger.debug('Charge amount in cents: ' + chargeAmountCents);
		amount = new Number(chargeAmountCents);
		var boundAmountCtor = _Amount.bind(amount);
		return boundAmountCtor();
	}
}
exports.Ctor = StripeAmount;