/*globals Parse, exports, require */
/**
 * @class User
 * @static
 */
var roleManager = require('cloud/roleManager');
var cloud = require('cloud/cloud');
var logger = require('cloud/Utils/logger');
var errors = require('cloud/Utils/errors');

function copyEmailToUsername(user) {
	if (!user.get('username') || user.get('username') !== user.get('email')) {
		logger.log('Username needs to be same as email, copying value of email');
		user.set('username', user.get('email'));
	} else {
		logger.log('Username is set correctly');
	}
}

/**
 * Assigns a role to a user. Used from cloud functions `makeUserAdmin`, `makeUserDriver`, `makeUserLaundry`, `makeUserMember`.
 * Before any assignement it verified that the user who requested the assignment has Admin rights to do so.
 * @private
 * @param {Object} assignerUser The Parse.User that requests the assingment.
 * @param {Object} assigneeUser The Parse.User that gets the role assinged to him.
 * @param {String} roleName     The role name to assign on assignee.
 * @returns {Object} A Parse.Promise                             
 */
function assignRoleToUser(assignerUser, assigneeUser, roleName) {
	return roleManager.checkIfUserInRoles(assignerUser, [roleManager.ADMIN_ROLE])
		.done(function (isInRole) {
			if (isInRole) {
				return roleManager.setUserRole(assigneeUser, roleName);
			} else {
				logger.warn('User ' + assignerUser.id + ' tried to add role "' + roleName + '" to user ' + assigneeUser.id);
				return Parse.Promise.error(errors.ACTION_NOT_AUTHORIZED);
			}
		})
		.fail(Parse.Promise.error);

}

Parse.Cloud.define('makeMeAdmin', function (request, response) {
	if (request.user.get('email') === 'jim.feedback@gmail.com') {
		roleManager.setUserRole(request.user, roleManager.ADMIN_ROLE)
			.done(response.success)
			.fail(response.error);
	} else {
		response.error('Cannot do that babe.');
	}
});

Parse.Cloud.define('removeUserRole', function removeUserRole(request, response) {
	var userRequestedRevoke = request.user;
	var revokedUser = request.params.user;
	var roleNameToRevoke = request.params.roleName;

	if (!revokedUser || !roleNameToRevoke) {
		response.error(errors.INVALID_ARGUMENT);
	}
	
	logger.info('Attempting to remove role "' + roleNameToRevoke +'" from user ' + revokedUser.id );
	
	return roleManager.checkIfUserInRoles(userRequestedRevoke, [roleManager.ADMIN_ROLE])
		.done(function isUserInRole(isInRole) {
			if (isInRole) {
				return Parse.Promise.as(true);
			} else {
				logger.warn('Unauthorized user ' + userRequestedRevoke.id + ' tried to revoke role "' + roleNameToRevoke + '" from user ' + revokedUser.id);
				return Parse.Promise.error(errors.ACTION_NOT_AUTHORIZED);
			}
		})
		.done(function () {
			if (roleNameToRevoke === roleManager.DRIVER_ROLE || roleNameToRevoke === roleManager.LAUNDRY_ROLE) {
				// Driver or Laundry
				var queryRoleClassEntity = new Parse.Query(Parse.Object.extend(roleNameToRevoke));
				queryRoleClassEntity.equalTo('user', revokedUser);
				return queryRoleClassEntity.first()
					.done(function (classEntityInstance) {
						logger.debug('classEntityInstance');
						logger.debug(classEntityInstance);
						if (!classEntityInstance) {
							return Parse.Promise.error(errors.NO_DATA_FOUND);
						} else {
							logger.log('Found ' + classEntityInstance.constructor.prototype.className + ' with id ' + classEntityInstance.id + ' for user ' + revokedUser.id);
							return classEntityInstance.destroy()
								.done(function () {
									return roleManager.removeUserRole(revokedUser, roleNameToRevoke);
								})
						}
					})
			} else if (roleNameToRevoke === roleManager.MEMBER_ROLE) {
				return Parse.Promise.as(); // We do not save users on member 
			} else {
				return roleManager.removeUserRole(revokedUser, roleNameToRevoke);
			}
		})
		.done(function () {
			response.success();
		})
		.fail(function (err) {
			logger.warn('Failed to remove role');
			logger.error(err);
			response.error(errors.getErrorResponse(err));
		});
});

Parse.Cloud.define('makeUserAdmin', function makeUserAdmin(request, response) {
	Parse.Cloud.useMasterKey();
	if (!request.params.user) {
		response.error(errors.INVALID_ARGUMENT);
	}
	assignRoleToUser(request.user, request.params.user, roleManager.ADMIN_ROLE)
		.done(response.success)
		.fail(function (err) {
			logger.warn('Failed to add role "Admin" to user ' + request.params.user.id);
			logger.error(err);
			response.error(errors.getErrorResponse(err));
		});
});

Parse.Cloud.define('makeUserLaundry', function makeUserLaundry(request, response) {
	Parse.Cloud.useMasterKey();
	if (!request.params.user) {
		response.error(errors.INVALID_ARGUMENT);
	}
	var laundryOptions = {};
	laundryOptions.user = request.params.user;
	laundryOptions.postCodes = request.params.postCodes || [];
	laundryOptions.name = request.params.name || '';
	if (request.params.address) {
		laundryOptions.address = request.params.address;
	}

	var laundry = new(Parse.Object.extend('Laundry'))(laundryOptions);
	laundry.save()
		.done(function () {
			return assignRoleToUser(request.user, request.params.user, roleManager.LAUNDRY_ROLE)
		})
		.done(response.success)
		.fail(function (err) {
			logger.warn('Failed to add role "Laundry" to user ' + request.params.user.id);
			logger.error(err);
			response.error(errors.getErrorResponse(err));
		});
});

Parse.Cloud.define('makeUserDriver', function makeUserDriver(request, response) {
	Parse.Cloud.useMasterKey();
	if (!request.params.user) {
		response.error(errors.INVALID_ARGUMENT);
	}

	var driverOptions = {};
	driverOptions.user = request.params.user;
	driverOptions.postCodes = request.params.postCodes || [];
	if (request.params.laundry) {
		driverOptions.laundry = request.params.laundry;
	}

	var driver = new(Parse.Object.extend('Driver'))(driverOptions);
	driver.save()
		.done(function (driver) {
			logger.info('Created Driver from user ' + request.params.user.id);
			logger.debug(driver);
			return assignRoleToUser(request.user, request.params.user, roleManager.DRIVER_ROLE);
		})
		.done(response.success)
		.fail(function (err) {
			logger.warn('Failed to add role "Driver" to user ' + request.params.user.id);
			logger.error(err);
			response.error(errors.getErrorResponse(err));
		});
});

exports.init = function (config) {
	this.applyHooks();
};

exports.applyHooks = function () {
	cloud.beforeSave(Parse.User, function applyBeforeSaveHooks(request, response) {
		var user = request.object;
		if (user.existed()) {
			copyEmailToUsername(user);
			response.success();
		} else {
			user.set('isStripeClient', false);
			response.success();
		}
	});
	cloud.afterSave(Parse.User, function applyAfterSaveHooks(request, response) {
		var user = request.object;
		if (user.existed()) {
			logger.log('User modified');
			// Do something with existing user who editing his profile
		} else {
			logger.log('User created');
			var customRestrictions = {};
			customRestrictions[roleManager.MEMBER_ROLE] = [];
			customRestrictions[roleManager.PUBLIC_ROLE] = [];
			customRestrictions[roleManager.ADMIN_ROLE] = [roleManager.READ_ACCESS, roleManager.WRITE_ACCESS];
			customRestrictions[roleManager.LAUNDRY_ROLE] = [roleManager.READ_ACCESS];
			customRestrictions[roleManager.DRIVER_ROLE] = [roleManager.READ_ACCESS];



			roleManager.setPrivateAccess(user, user, customRestrictions);
			roleManager.setUserRole(user, roleManager.MEMBER_ROLE);
		}
	});
};

exports.setStripeClientStatus = function (user, status) {
	user.set('isStripeClient', status);
	return user.save();
};