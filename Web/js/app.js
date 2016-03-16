/*globals app, Parse, Stripe, $, document, require*/

module.exports = (function () {

    var appKey = 'cGiRSDM8unMmInG0iDO4DG3oesrWm00QGNcU6eUN';
    var jsKey = 'wu1BKdGTuLdSe0XLgp0s0b8wVpIIXTGxH5B6G8Oi';

    var utils = require('./utils');
    var Stripe = require('./stripe');
    var parseService = require('./parseService');

    function App() {

        var app = this;
        this.config = undefined;

        this.utils = utils;

		/*emails of all known priviledged users*/
        this.adminUsers = [];
        this.driverUsers = [];
        this.laundryUsers = [];

		/* These take values only if user is laundry or driver */
		this.currentDriver = undefined;
		this.currentLaundry = undefined;
		
        this._onReadyCallbacks = [];

        this.ready = function (callback, context) {
            if( callback ) {
                this._onReadyCallbacks.push(callback.bind(context));
            } else {
                this._onReadyCallbacks.forEach(function(callback) {
                    callback();
                });
            }
        };


        this.isUserAdmin = function (user) {
            user = user || Parse.User.current();
            return user && !!~this.adminUsers.indexOf(user.get('username'));
        };


        this.isUserLaundry = function (user) {
            user = user || Parse.User.current();
            return user && !!~this.laundryUsers.indexOf(user.get('username'));
        };


        this.isUserDriver = function (user) {
            user = user || Parse.User.current();
            return user && !!~this.driverUsers.indexOf(user.get('username'));
        };
		
		this.getUserRoleName = function (user) {
			user = user || Parse.User.current();
			return this.isUserAdmin(user) ? 'Admin' : this.isUserDriver(user) ? 'Driver' : this.isUserLaundry(user) ? 'Laundry' : 'Member'
		}
		
		this.setRoleToUser = function(roleName, user) {
			var rolesArrayName = roleName.toLowerCase() + 'Users';
			this.adminUsers = Parse._.without(this.adminUsers, user.get('email'));
			this.driverUsers = Parse._.without(this.driverUsers, user.get('email'));
			this.laundryUsers = Parse._.without(this.laundryUsers, user.get('email'));
			if( this[rolesArrayName] ) {
				this[rolesArrayName].push(user.get('email'));
			}
		}

        this.init = function (appKey, jsKey) {

            Parse.initialize(appKey, jsKey);

            this._redirectToLoginIfNeeded();

            this.config = Parse.Config.current();

            var gotConfigPromise = new Parse.Promise();
            //Initialize actions
            var initActions = [];
            initActions.push(gotConfigPromise);

			if (!this.config || !Parse._.isEmpty(this.config)) {
                Parse.Config.get()
                    .then(function (config) {
                        gotConfigPromise.resolve(config);
                    });
            } else {
                gotConfigPromise.resolve(this.config);
            }

            if (Parse.User.current()) {
                initActions.push(this._getUsersForElevatedRolesAsync());
            }


            Parse.Promise.when(initActions)
                .then(function (config, userRoles) {
                    $(document).ready(function () {
                        Stripe.setPublishableKey(config.get('stripeApiKey'));
                        app._bindAccessEvents();
                        app._setRoleClassToDocument();
                        app.ready();
                    });
                });
        };

        this._redirectToLoginIfNeeded = function(){
            if( !Parse.User.current() && !~window.location.pathname.indexOf('login.html') ) {
                app.utils.redirectTo('login');
            }
        };

        this._getUsersForElevatedRolesAsync = function (user) {
            return parseService.getAllUsersPerRole().done(function(usersPerRole){
                var mapFunction = function(user){
                    return user.get('username');
                };
                app.adminUsers = Parse._.map(usersPerRole.adminUsers, mapFunction);
                app.driverUsers = Parse._.map(usersPerRole.driverUsers, mapFunction);
                app.laundryUsers = Parse._.map(usersPerRole.laundryUsers, mapFunction);


                if( app.isUserDriver() ) {
                    return parseService.getDrivers({user: { equalTo: app.utils.parseToPointer(Parse.User.current()) }}).done(function(drivers){
                        if( drivers.length ) {
                            app.currentDriver = drivers[0];
                        }
                    })
                } else if( app.isUserLaundry() ) {
                    return parseService.getLaundries({user: { equalTo: app.utils.parseToPointer(Parse.User.current()) }}).done(function(laundries){
                        if( laundries.length ) {
                            app.currentLaundry = laundries[0];
                        }
                    })
                } else {
                    return Parse.Promise.as();
                }
            });
        };


        this._bindAccessEvents = function () {

            $('.login-form').on('submit', function (e) {
                e.preventDefault();
                e.stopPropagation();
                var user = new Parse.User();
                $(this).serializeArray().forEach(function (input) {
                    user.set(input.name, input.value);
                });
                user.logIn()
                    .done(function () {
                        console.log('User is logged in');
                        app._getUsersForElevatedRolesAsync().done(function () {
                            app._setRoleClassToDocument();
                            app.utils.redirectTo('dashboard');
                        });
                    })
                    .fail(function (e) {
                        console.log('User login failed ' + e);
                    });
            });

            $('.register-form').on('submit', function (e) {
                e.preventDefault();
                e.stopPropagation();
                var user = new Parse.User();
                $(this).serializeArray().forEach(function (input) {
                    user.set(input.name, input.value);
                    if (input.name === 'email') {
                        user.set('username', input.value);
                    }
                });
                user.signUp()
                    .done(function () {
                        console.log(arguments);
                    })
                    .fail(function (e) {
                        console.log(e);
                    });
            });

            $('.log-out-btn').on('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
                Parse.User.logOut().then(function () {
                    app.utils.redirectTo('login');
                });
            });
        };


        this._setRoleClassToDocument = function () {
            $(document.body).removeClass('admin-user laundry-user driver-user member-user');

            if (this.isUserAdmin()) {
                $(document.body).addClass('admin-user');
            } else if (this.isUserLaundry()) {
                $(document.body).addClass('laundry-user');
            } else if (this.isUserDriver()) {
                $(document.body).addClass('driver-user');
            } else {
                $(document.body).addClass('member-user');
            }
        }
    }

    var appInstance = new App();
    appInstance.init(appKey, jsKey);

    return appInstance;



})();
