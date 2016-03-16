/* globals require, exports */
var roleManager = require('cloud/roleManager');
var cloud = require('cloud/cloud');
var logger = require('cloud/Utils/logger');


function setProtectedAccessToAddress(user, address) {
	logger.log('Setting access only to user, admins and bussiness partners');
	var customRestrictions = {};
	customRestrictions[roleManager.MEMBER_ROLE] = [];
	customRestrictions[roleManager.PUBLIC_ROLE] = [];
	customRestrictions[roleManager.ADMIN_ROLE] = [roleManager.READ_ACCESS, roleManager.WRITE_ACCESS];
	customRestrictions[roleManager.LAUNDRY_ROLE] = [roleManager.READ_ACCESS];
	customRestrictions[roleManager.DRIVER_ROLE] = [roleManager.READ_ACCESS];


	roleManager.setPrivateAccess(user, address, customRestrictions);

}

exports.init = function (config) {

	
	cloud.afterSave('Address', function applyAfterSaveHooks(request, response) {
		var user = request.user;
		var address = request.object;
		if (address.existed()) {
			logger.log('Address modified');

		} else {
			logger.log('Address created');
			setProtectedAccessToAddress(user, address)
		}
	});
};