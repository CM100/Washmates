/*globals Parse, exports, require  */
/**
 * @class Cloud
 * @static
 */
Parse._skippedHooks = [];

var logger = require('cloud/Utils/logger');
var _ = require('underscore');
var SKIP_HOOK_FLAG = 'skipWebHooks';

function SkippedHook(resource, hook) {
	var hookComponents = hook.split(/(?=[A-Z])/);
	this.resourceName = _.isString(resource) ? resource : resource.prototype.className;

	this.precondition = hookComponents[0]; //before|after
	this.action = hookComponents[1]; //Save|Delete...

	this._skippedHooksIndex = _.indexOf(_.pluck(Parse._skippedHooks, 'resource'), this.resourceName);

	this.register = function (shouldSkip) {
		if (~this._skippedHooksIndex) {
			logger.log('Skipped hook already registered. Aborting hook registration').
			return;
		} else {
			var hookMeta = {
				resource: resource
			};
			hookMeta['before' + this.action] = !!shouldSkip;
			hookMeta['after' + this.action] = !!shouldSkip;
			this._skippedHooksIndex = Parse._skippedHooks.length;
			Parse._skippedHooks.push(hookMeta);
		}
	}

	this.unregister = function () {
		if (this.precondition === 'after') {
			Parse._skippedHooks[this._skippedHooksIndex][hook] = false;
		}
		Parse._skippedHooks[this._skippedHooksIndex]['before' + this.action] = false;
	}

	this.shouldSkip = function () {
		return Parse._skippedHooks[this._skippedHooksIndex][hook];
	}

}
/**
 * Wrapper around beforeSave hook in order to skip triggering any logic when flag `skipWebHooks` is set
 * Also stores the name of the resource in an array in order to propagate similar logic on afterSave
 * @param {String|Parse.Object} resource The Parse Class as string or Parse Object (e.g. Parse.User) 
 *                                       on which the hook will apply
 * @param {Function}            action   A function to be called on beforeSave hook
 */
var beforeSave = function beforeSave(resource, action) {
	var hook = new SkippedHook(resource, 'beforeSave');

	Parse.Cloud.beforeSave(resource, function (request, response) {
		var wrapperAction = function wrapperBeforeSave(request, response) {
			hook.register(request.object.get(SKIP_HOOK_FLAG));
			if (hook.shouldSkip()) {
				logger.log('Before save called with skip set. Aborting hook');
				request.object.unset(SKIP_HOOK_FLAG);
				response.success();
			} else {
				logger.log('Before save called with skip unset. Continuing...');
				action.apply(null, arguments)
			}
		}
		if (_.isEmpty(Parse.Config.current())) {
			logger.debug('Config present on before save, sending local config.')
			wrapperAction(request, response, Parse.Config.current());
		} else {
			Parse.Config.get()
				.done(function (config) {
					logger.debug('Config not present on before save, getting config.')
					wrapperAction(request, response, config);
				});
		}
	});

};

var afterSave = function (resource, action) {
	var hook = new SkippedHook(resource, 'afterSave');

	Parse.Cloud.afterSave(resource, function (request, response) {
		var wrapperAction = function wrappedAfterSave(request, response) {
			hook.register();
			logger.log('After save checks for skipped webhooks in ' + JSON.stringify(Parse._skippedHooks));
			if (hook.shouldSkip()) {
				logger.log('After save called with skip set. Unsetting skippedHooks and aborting hook .');
				hook.unregister();
				response.success();
			} else {
				logger.log('After save called with skip unset. Continuing...');
				action.apply(null, arguments)
			}
		};
		if (_.isEmpty(Parse.Config.current())) {
			logger.debug('Config present on after save, sending local config.')
			wrapperAction(request, response, Parse.Config.current());
		} else {
			Parse.Config.get()
				.done(function (config) {
					logger.debug('Config not present on after save, getting config.')
					wrapperAction(request, response, config);
				});
		}
	});
};

exports.beforeSave = beforeSave;
exports.afterSave = afterSave;