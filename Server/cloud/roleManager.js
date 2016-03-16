/*globals Parse, exports, require*/
/**
 * @class RoleManager
 * @static
 */
var logger = require('cloud/Utils/logger.js');
var errors = require('cloud/Utils/errors');

var _ = Parse._;
var READ_ACCESS = 'read';
var WRITE_ACCESS = 'write';


/* Constant access permissions */
exports.READ_ACCESS = READ_ACCESS;

exports.WRITE_ACCESS = WRITE_ACCESS;

/* Constant User roles */
exports.SELF_ROLE = 'self';

exports.PUBLIC_ROLE = 'public';

exports.MEMBER_ROLE = 'Member';

exports.ADMIN_ROLE = 'Admin';

exports.LAUNDRY_ROLE = 'Laundry';

exports.DRIVER_ROLE = 'Driver';

/* Module methods */
exports.setPrivateAccess = setPrivateAccess;

exports.setUserRole = setUserRole;

exports.removeUserRole = removeUserRole;

exports.checkIfUserInRoles = checkIfUserInRoles;

/**
 * Sets a Role to a user
 * 
 * @method setUserRole
 * @private
 * @param   {Object}  user     A Parse.User object
 * @param   {String}  roleName The name of the desired role.
 * @returns {Promise} A Parse.Promise
 */
function setUserRole(user, roleName) {
	// master key has all permissions
	Parse.Cloud.useMasterKey();

	logger.log('Setting user role...');

	// add user to role
	return getRoleAsync(roleName)
		.fail(function(e){
			logger.warn('Failed to retrieve role ' + roleName + ' with error ' + JSON.stringify(e));
			logger.error(e);
			return Parse.Promise.error(e);
		})
		.done(function(role){
			role.relation('users').add(user);
			// save role
			return role.save();
		})
		.fail(function errorFindingRole(e) {
			logger.error('Role ' + roleName + ' failed to be removed from user '+ user.id +'. Exiting with error ' + e);
			return Parse.Promise.error(e);
		});
}

/**
 * Removes the given role from the given user.
 * @param   {Object}   user     A Parse.User instance
 * @param   {String}   roleName The name of the role to be removed
 * @returns {Function} A Parse.Promise.
 */
function removeUserRole(user, roleName){
	Parse.Cloud.useMasterKey();
	
	logger.log('Removing role "'+ roleName +'" from user ' + user.id);
	
	return getRoleAsync(roleName)
		.fail(function(e){
			logger.warn('Failed to retrieve role ' + roleName + ' with error ' + JSON.stringify(e));
			logger.error(e);
			return Parse.Promise.error(e);
		})
		.done(function(role){
			role.relation('users').remove(user);
			return role.save();
		})
		.fail(function(error){
			logger.warn('Failed to remove role ' + roleName + ' from user ' + user.id );
			logger.error(error);
			return Parse.Promise.error(error);
		});
}


/**
 * Private function for retrieving a Parse.Role object based on the role's name
 * @private
 * @param   {String}   roleName The name of the role to retrieve.
 * @returns {Function} A Parse.Promise. In success callback passes the Parse.Role as 1st parameter.
 */
function getRoleAsync(roleName) {
	var promise = new Parse.Promise();
	if( !roleName ) {
		promise.reject(errors.INVALID_ARGUMENT);
	}
	
	var roleQuery = new Parse.Query(Parse.Role);
	roleQuery.equalTo('name', roleName);
	roleQuery
		.first()
		.done(function onRoleFound(role){
			if ( !role ) {
				promise.reject(errors.NO_DATA_FOUND);
			} else {
				promise.resolve(role);
			}
		})
		.fail(promise.reject);
				
	return promise;
}


/**
 * Sets private access for a user. Additional restrictions can be enforced for public read/write access
 * as well for declared roles. An example of restrictions cound be
 * `{}`
 * 
 * @method setPrivateAccess
 * @private
 * @param   {Object}  user                A valid Parse user
 * @param   {Object}  resource            A Parse Object. The resource where restrictions will apply
 * @param   {Object}  restrictions        An object with `read`/`write` restrictions for public and Roles. As an example could be:
 *                                     	`{public: ["read"], Admin: ["read", "write]}`
 * @param   {Boolean} applyToExistingResource Setup the restrictions to existing users as well. By default the restrictions are applied
 *                                     	only on a newly created user
 * @returns {Promise} A Parse.Promise. If successful returns the resource passed as argument. Otherwise an error message
 */
function setPrivateAccess(user, resource, restrictions, applyToExistingResource) {
	Parse.Cloud.useMasterKey();

	restrictions = restrictions || {};
	restrictions.public = restrictions.public || []; //By default no public restrictions

	applyToExistingResource = applyToExistingResource || false;

	if (!(resource instanceof Parse.Object)) {
		logger.warn("Rolemanager::setPrivateAccess Resource parameter not set correctly. Parse Objects expected");
		return Parse.Promise.error(errors.INVALID_ARGUMENT);
	}

	if (!_.isObject(restrictions)) {
		logger.warn("Rolemanager::setPrivateAccess Restrictions parameter not set correctly. Object expected");
		return Parse.Promise.error(errors.INVALID_ARGUMENT);
	}

	if (resource.existed() && !applyToExistingResource) {
		logger.info('Resource exists, no need to set role. Exiting');
		return Parse.Promise.as(resource);
	}

	var privateAccessACL;
	if (!(user instanceof Parse.User)) {
		privateAccessACL = new Parse.ACL();
		delete restrictions[exports.SELF_ROLE];
	} else {
		privateAccessACL = new Parse.ACL(user);
	}

	logger.debug('Restrictions to be applied ' + JSON.stringify(restrictions));


	// set ACL so only the user himself can see his own data



	restrictions.public = restrictions.public || [];

	Object.keys(restrictions).forEach(function (roleRestriction) {
		var accessSet = restrictions[roleRestriction] || [];
		if (!_.isArray(accessSet)) {
			throw 'Rolemanager::setPrivateAccess An array was expected for restriction ' + roleRestriction + '. ' + accessSet + ' was given.';
		}
		logger.debug('Setting ' + roleRestriction + ' restrictions ' + JSON.stringify(accessSet));
		if (roleRestriction === exports.SELF_ROLE) {

			privateAccessACL.setReadAccess(!!~accessSet.indexOf(READ_ACCESS));
			logger.debug('Setting ' + roleRestriction + '  READ restriction to ' + privateAccessACL.getReadAccess());

			privateAccessACL.setWriteAccess(!!~accessSet.indexOf(WRITE_ACCESS));
			logger.debug('Setting ' + roleRestriction + '  WRITE restriction to ' + privateAccessACL.getWriteAccess());

		} else if (roleRestriction === exports.PUBLIC_ROLE) {

			privateAccessACL.setPublicReadAccess(!!~accessSet.indexOf(READ_ACCESS));
			logger.debug('Setting ' + roleRestriction + '  READ restriction to ' + privateAccessACL.getPublicReadAccess());

			privateAccessACL.setPublicWriteAccess(!!~accessSet.indexOf(WRITE_ACCESS));
			logger.debug('Setting ' + roleRestriction + '  WRITE restriction to ' + privateAccessACL.getPublicWriteAccess());

		} else {

			privateAccessACL.setRoleReadAccess(roleRestriction, !!~accessSet.indexOf(READ_ACCESS));
			logger.debug('Setting ' + roleRestriction + '  READ restriction to ' + privateAccessACL.getRoleReadAccess(roleRestriction));

			privateAccessACL.setRoleWriteAccess(roleRestriction, !!~accessSet.indexOf(WRITE_ACCESS));
			logger.debug('Setting ' + roleRestriction + '  WRITE restriction to ' + privateAccessACL.getRoleWriteAccess(roleRestriction));
		}
	});

	resource.setACL(privateAccessACL);
	resource.set('skipWebHooks', true);
	return resource.save();

}

/**
 * Checks if a user belongs to a specific role
 * 
 * @method checkIfUserInRoles
 * @private
 * @param   {Object}           user     The user in question
 * @param   {Array | String}   roleName The names of the Roles as array or a string space separated.
 *                                      Please use roleManager's role constants
 * @returns {Promise} A Parse.Promise. If successfull return either true/false depending on the user
 *                    is in role or not
 */
function checkIfUserInRoles(user, roleNames) {
	var promise = new Parse.Promise();
	var roleQueries = [];
	
	if ( typeof roleNames === "string" ) {
		roleNames = roleNames.split(' ');
	}
	
	roleNames.forEach(function(roleName){
		var roleQuery = new Parse.Query(Parse.Role);
		roleQuery.equalTo("name", roleName);
		roleQueries.push(roleQuery);
	});
	
	var query;
	if( roleQueries.length === 1 ) {
		query = roleQueries[0];
	} else {
		query = Parse.Query.or.apply(Parse.Query, roleQueries);
	}
	
	query.equalTo("users", user);
	
	query.first()
		.done(function (role) {
			promise.resolve(!!role);
		})
		.fail(promise.reject);
	
	return promise;
}