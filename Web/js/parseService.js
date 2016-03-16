/*globals Parse, require, module*/

var amplify = require('./amplify.min.js');

module.exports = {
    getOrders: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'Order',
            include: ['deliveryAddress', 'deliveryDriver', 'deliveryDriver.user', 'deliverySchedule', 'pickUpDriver', 'pickUpDriver.user', 'pickUpSchedule', 'pickUpAddress', 'laundry', 'laundry.user', 'user', 'discountCode'],
            criteria: criteria
        })
    },
    getIssues: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'Issue',
            include: ['order', 'order.user'],
            criteria: criteria
        });
    },
    getAddresses: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'Address',
            include: ['user'],
            criteria: criteria
        });
    },
    getLaundries: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'Laundry',
            include: ['address', 'user'],
            criteria: criteria
        });
    },
    getDrivers: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'Driver',
            include: ['laundry', 'laundry.user', 'user'],
            criteria: criteria
        });
    },
    getServices: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'Service',
            criteria: criteria
        });
    },
    getUsers: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: '_User',
            criteria: criteria
        });
    },
    getDriverSchedules: function (criteria) {
        return Parse.Cloud.run('getAll', {
            table: 'DriverSchedule',
            include: ['driver', 'driver.user', 'driver.laundry', 'toAddress'],
            criteria: criteria
        });
    },
    _getUsersForRole: function (roleName) {
        var promise = new Parse.Promise();
        var query = (new Parse.Query(Parse.Role));
        query.equalTo("name", roleName);
        query.first().done(function (role) {
            role.relation('users').query().find()
                .done(function (users) {
                    return promise.resolve(users);
                })
                .fail(promise.reject);
        });
        return promise;
    },
    getAllUsersPerRole: function () {
        var promise = new Parse.Promise();
        Parse.Promise.when(this._getUsersForRole('Admin'), this._getUsersForRole('Laundry'), this._getUsersForRole('Driver'))
        .done(function (adminUsers, laundryUsers, driverUsers) {
            var mapFunction = function (user) {
                return user.get('username');
            };
            return promise.resolve({adminUsers: adminUsers, driverUsers: driverUsers, laundryUsers: laundryUsers});
        })
        .fail(promise.reject);
        return promise;
    },
    save: function (parseObject) {
        return parseObject.save().done(function (result) {
            return Parse.Promise.as(result);
        });
    },
    destroy: function (parseObject) {
        return parseObject.destroy().done(function (result) {
            return Parse.Promise.as(result);
        });
    }
};
