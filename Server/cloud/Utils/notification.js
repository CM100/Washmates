/* globals Parse, exports, require*/
/**
 * @class NotificationUtils
 */
var async = require('cloud/Utils/async');
var logger = require('cloud/Utils/logger');
var errors = require('cloud/Utils/errors');
var _ = Parse._;
var CHANNEL_PREFIX = 'C';


/**
 * Sends user push notifications based on their IDs. Due to parse limitation a channel can not start from number
 * although user IDs can. Therefor a channel prefix is appended to ensure that. 
 * 
 * ATTENTION:: The same prefix has to be appended on the client side as well.
 * 
 * @method sendUserNotifications
 * @private
 * @param   {String}  message        The message that the notification will display.
 * @param   {Array}   [receivers=[]] An array of user IDs that would receive the notificaiton.
 * @returns {Promise} A Parse.Promise
 */
var sendUserNotifications = function (message, receivers, payload) {
	receivers = receivers || [];
	if (!_.isArray(receivers) || !receivers.length) {
		logger.error("Utils::sendUserNotifications Receivers expected to be array");
		return Parse.Promise.error(errors.INVALID_ARGUMENT);

	}
	receivers = receivers.map(function addChannelPrefix(receiver) {
		return CHANNEL_PREFIX + receiver;
	});

	var defaultData = {
		alert: message,
		badge: 1
	};
	
	return async.runAsPromise(Parse.Push, 'send', {
		channels: receivers,
		data: Parse._.defaults(payload, defaultData)
	});
};

exports.sendUserNotifications = sendUserNotifications;