/*globals require, Parse*/
/**
 * @class Schedule
 * @static
 */
var _ = require('underscore');
var logger = require('cloud/Utils/logger');
var errors = require('cloud/Utils/errors');
var orderRestrictions = require('cloud/Order/restrictions');
var Heap = require('cloud/Utils/heap');
var cloud = require('cloud/cloud');

exports.init = function (config) {};

cloud.beforeSave('DriverSchedule', function(request, response, config){
	var driverSchedule = request.object;
	var newFromDate = driverSchedule.get('fromDate'), newToDate = driverSchedule.get('toDate');
	
	if( driverSchedule.existed() &&  
	   !driverSchedule.dirty('fromDate') &&  
	   !driverSchedule.dirty('driver') && 
	   !driverSchedule.dirty('toDate') ) {
		response.success();
	}
	
	if( !driverSchedule.get('driver' ) || !newFromDate || !newToDate ) {
		return response.error(errors.INVALID_ARGUMENT);
	} 
	if ( newFromDate.getTime() >= newToDate.getTime() ) {
		return response.error(errors.SCHEDULE_BAD_DATES);
	}
	if( !driverSchedule.get('isTimeOff') && !driverSchedule.get('toAddress') ) {
		return response.error(errors.INVALID_ARGUMENT);
	}
	
	var driverExistsScheduleQuery = createDriverWithRecurringSchedulesQuery(newFromDate, newToDate);
	driverExistsScheduleQuery.equalTo('driver', driverSchedule.get('driver'));
	if( driverSchedule.get('toAddress') ) {
		driverExistsScheduleQuery.include('toAddress');
	}
	driverExistsScheduleQuery.find()
		.done(function(driverSchedules){
			var allSchedulesAsObject = getSchedulesAsObjects(driverSchedules, newFromDate, newToDate);
			var overLappingSchedules = _.filter(allSchedulesAsObject, function(existingScheduleObject){
				var existingFromTime = existingScheduleObject.fromDate.getTime();
				var existingToTime = existingScheduleObject.toDate.getTime();
				
				var existingStartsInBetween = existingFromTime > newFromDate.getTime() && existingFromTime <= newToDate.getTime()
				var existingEndsInBetween =  existingToTime > newFromDate.getTime() && existingToTime <= newToDate.getTime()
				var existingCompletlyOverlaps = existingFromTime >= newFromDate.getTime() && existingToTime <= newToDate.getTime()
				
				return existingCompletlyOverlaps || existingEndsInBetween || existingStartsInBetween
			});
		
			if( overLappingSchedules.length ) {
				response.error(errors.SCHEDULE_DRIVER_EXISTS);
			} else {
				response.success();
			}
		})
		.fail(function(error){
			response.error(errors.getErrorResponse(error));
		})
});

/**
 * Creates a query that queries for reccuring and none reccuring schedules for laundries that serve within
 * the given postcodes. Also looks only for schedules within certain dates.
 * 
 * @method createLaundryScheduleQueryForPostCodes
 * @private
 * @param   {Array}  postCodes   An array with the postcodes. A valid laundry should serve at least one of them.
 * @param   {Date}   fromDateMin A minimum date to be used as criterion. Finding schedules later than that date.
 * @param   {Date}   toDateMax   A maximum date to be used as criterion. Finding schedules earlier than that date.
 * @returns {Object} A `Parse.Query` object.
 */
var createLaundryScheduleQueryForPostCodes = function createQueryForLaundrySchedulesInPostCodes(postCodes, fromDateMin, toDateMax) {
	logger.debug('Start constructing query for layndrySchedules where serve the given postcodes. Looking for dates between ' + fromDateMin + ' - ' + toDateMax);

	if (fromDateMin > toDateMax) {
		logger.warn('The minimum date(' + fromDateMin + ') to look for laundry schedules is higher that the maximum date(' + toDateMax + ').')
	}

	var laundriesFromPostCodeQuery = new Parse.Query(Parse.Object.extend('Laundry'));
	laundriesFromPostCodeQuery.containedIn('postCodes', postCodes);

	laundriesFromPostCodeQuery.find().then(function laundriesFromPostCodeQueryResponse(r) {
		logger.warn(JSON.stringify(r));
	});


	//One time scheduling
	var laundriesOneTimeOffQueryRepeatWeekdaysNo = new Parse.Query(Parse.Object.extend('LaundrySchedule'));
	laundriesOneTimeOffQueryRepeatWeekdaysNo.equalTo('repeatWeekdays', false);

	var laundriesOneTimeOffQueryRepeatWeekendsNo = new Parse.Query(Parse.Object.extend('LaundrySchedule'));
	laundriesOneTimeOffQueryRepeatWeekendsNo.equalTo('repeatWeekends', false);

	var laundriesOneTimeOffQueryNoRepeat = new Parse.Query(Parse.Object.extend('LaundrySchedule'));
	laundriesOneTimeOffQueryNoRepeat.doesNotExist('repeatWeekdays');
	laundriesOneTimeOffQueryNoRepeat.doesNotExist('repeatWeekends');

	var laundriesOneTimeOffQuery = new Parse.Query.or(laundriesOneTimeOffQueryRepeatWeekdaysNo, laundriesOneTimeOffQueryRepeatWeekendsNo, laundriesOneTimeOffQueryNoRepeat);
	laundriesOneTimeOffQuery.greaterThan('fromDate', fromDateMin);

	//Recurring scheduling
	var laundriesReccuringTimeOffQueryWeekends = new Parse.Query(Parse.Object.extend('LaundrySchedule'));
	laundriesReccuringTimeOffQueryWeekends.equalTo('repeatWeekends', true);

	var laundriesReccuringTimeOffQueryWeekdays = new Parse.Query(Parse.Object.extend('LaundrySchedule'));
	laundriesReccuringTimeOffQueryWeekdays.equalTo('repeatWeekends', true);


	// Joined queries for scheduling
	var laundriesTimeOffQuery = new Parse.Query.or(laundriesOneTimeOffQuery, laundriesReccuringTimeOffQueryWeekdays, laundriesReccuringTimeOffQueryWeekends);
	laundriesTimeOffQuery.include('laundry');
	//	laundriesTimeOffQuery.equalTo('isTimeOff', true);
	laundriesTimeOffQuery.matchesKeyInQuery('laundry', 'objectId', laundriesFromPostCodeQuery);
	laundriesTimeOffQuery.ascending('fromDate');
	laundriesTimeOffQuery.lessThanOrEqualTo('toDate', toDateMax);
	//
	//	laundriesTimeOffQuery.find().then(function laundriesTimeOffQueryResponse(r) {
	//		logger.warn(JSON.stringify(r));
	//	});

	logger.debug('Query generated: ' + JSON.stringify(laundriesTimeOffQuery));
	return laundriesTimeOffQuery;
};

/**
 * Creates a query that queries for  reccuring and none reccuring schedules for drivers that work for specific
 * laundries. Also looks only for schedules within certain dates.
 *
 * @method createDriverScheduleQueryForLaundries
 * @private
 * @param   {Array}  postCodes   An array with the laundries. A valid driver should work for one of them.
 * @param   {Date}   fromDateMin A minimum date to be used as criterion. Finding schedules later than that date.
 * @param   {Date}   toDateMax   A maximum date to be used as criterion. Finding schedules earlier than that date.
 * @returns {Object} A `Parse.Query` object.
 */
var createDriverScheduleQueryForLaundries = function createQueryForDriverScheduleOfLaundries(laundries, fromDateMin, toDateMax) {
	logger.debug('These laundries given as criteria: ' + JSON.stringify(laundries));
	logger.debug('Start constructing query for driverSchedules for drivers who belong to the given laundries. Looking for dates between ' + fromDateMin + ' - ' + toDateMax);

	if (fromDateMin > toDateMax) {
		logger.warn('The minimum date(' + fromDateMin + ') to look for driver schedules is higher that the maximum date(' + toDateMax + ').')
	}

	var driversForLaundryQuery = new Parse.Query('Driver');
	driversForLaundryQuery.containedIn('laundry', laundries);
	
	var driverScheduleQuery = createDriverWithRecurringSchedulesQuery(fromDateMin, toDateMax);
	driverScheduleQuery.matchesKeyInQuery('driver', 'objectId', driversForLaundryQuery);

	logger.debug('Query generated: ' + JSON.stringify(driverScheduleQuery));
	return driverScheduleQuery;
};


var createDriverWithRecurringSchedulesQuery = function createDriverScheduleQuery(fromDateMin, toDateMax){
	logger.debug('Start constructing query for driverSchedules for drivers. Looking for dates between ' + fromDateMin + ' - ' + toDateMax);

	if (fromDateMin > toDateMax) {
		logger.warn('The minimum date(' + fromDateMin + ') to look for driver schedules is higher that the maximum date(' + toDateMax + ').')
	}

	//One time scheduling
	var driverScheduleOneTimeOffQueryRepeatWeekdaysNo = new Parse.Query(Parse.Object.extend('DriverSchedule'));
	driverScheduleOneTimeOffQueryRepeatWeekdaysNo.equalTo('repeatWeekdays', false);

	var driverScheduleOneTimeOffQueryRepeatWeekendsNo = new Parse.Query(Parse.Object.extend('DriverSchedule'));
	driverScheduleOneTimeOffQueryRepeatWeekendsNo.equalTo('repeatWeekends', false);

	var driverScheduleOneTimeOffQueryNoRepeat = new Parse.Query(Parse.Object.extend('DriverSchedule'));
	driverScheduleOneTimeOffQueryNoRepeat.doesNotExist('repeatWeekends');
	driverScheduleOneTimeOffQueryNoRepeat.doesNotExist('repeatWeekdays');

	var driverScheduleOneTimeOffQuery = new Parse.Query.or(driverScheduleOneTimeOffQueryRepeatWeekdaysNo, driverScheduleOneTimeOffQueryRepeatWeekendsNo, driverScheduleOneTimeOffQueryNoRepeat);
	driverScheduleOneTimeOffQuery.greaterThanOrEqualTo('fromDate', fromDateMin);

	//Recurring scheduling
	var driverScheduleReccuringTimeOffQueryWeekends = new Parse.Query(Parse.Object.extend('DriverSchedule'));
	driverScheduleReccuringTimeOffQueryWeekends.equalTo('repeatWeekends', true);

	var driverScheduleReccuringTimeOffQueryWeekdays = new Parse.Query(Parse.Object.extend('DriverSchedule'));
	driverScheduleReccuringTimeOffQueryWeekdays.equalTo('repeatWeekdays', true);

	// Joined query for scheduling
	var driverScheduleQuery = new Parse.Query.or(driverScheduleOneTimeOffQuery, driverScheduleReccuringTimeOffQueryWeekends, driverScheduleReccuringTimeOffQueryWeekdays);
	driverScheduleQuery.include('driver');
	driverScheduleQuery.include('toAddress');
	//	driverScheduleQuery.equalTo('isTimeOff', true);
	driverScheduleQuery.lessThanOrEqualTo('toDate', toDateMax);
	driverScheduleQuery.ascending('fromDate');

	logger.debug('Query generated: ' + JSON.stringify(driverScheduleQuery));
	return driverScheduleQuery;	
}

/**
 * Given the schedules both of driver and laundries, it calculates the reserved hours for each driver on
 * each post code he may serve.
 * 
 * @method getAllSchedules
 * @private
 * @param {Array} [laundrySchedules=[]] The schedule with reserved hours of the laundry service providers
 * @param {Array} [driverSchedules=[]]  The drivers' schedule with all the reserved hours.
 * @param {Object}[dateRestrictions]  	A `DateRestrictions` instance with restrictions for minimum and maximum
 * 										allowed date where the schedule will be rendered.
 * @returns {Array} Returns an array of objects where each object describes the diver availability per post
 *                  code. 
 */
var getAllSchedules = function getAllSchedules(laundrySchedules, driverSchedules, minimumAllowedScheduleDate, maximumAllowedScheduleDate) {
	laundrySchedules = laundrySchedules || [];
	driverSchedules = driverSchedules || [];


	var scheduleResponse = {};

	scheduleResponse.minPickUpDate = minimumAllowedScheduleDate;
	scheduleResponse.maxDeliveryDate = maximumAllowedScheduleDate;
	scheduleResponse.reserved = [];

	//Object {kTAN9H4G5O: Array[1]}
	var lSchedulesPerLaundry = _.groupBy(laundrySchedules, function (schedule) {
		return schedule.get('laundry').id;
	});

	var driverSchedulesPerLaundry = _.groupBy(driverSchedules, function (schedule) {
		return schedule.get('driver').get('laundry').id;
	});

	Object.keys(lSchedulesPerLaundry).forEach(function (laundryId) {
		//Getting the reserved time for drivers of each laundry
		var numberOfDriversForLaundry = _.uniq(_.map(driverSchedulesPerLaundry[laundryId], function (driverSched) {
			return driverSched.get('driver').id;
		})).length;
		var driverSchedulesForLaundry = getDriverReservedHours(driverSchedulesPerLaundry[laundryId], numberOfDriversForLaundry, minimumAllowedScheduleDate, maximumAllowedScheduleDate);
		driverSchedulesForLaundry = driverSchedulesForLaundry.concat(_.map(lSchedulesPerLaundry[laundryId]), function (laundrySchedules) {
			return {
				fromDate: laundrySchedules.get('fromDate'),
				toDate: laundrySchedules.get('toDate')
			}
		});
		scheduleResponse.reserved = scheduleResponse.reserved.concat(driverSchedulesForLaundry);
	});

	return scheduleResponse;
};


function getDriverReservedHours(schedules, numOfDrivers, minimumAllowedScheduleDate, maximumAllowedScheduleDate) {
	var schedulesHeap = getHeapForSchedules(schedules, minimumAllowedScheduleDate, maximumAllowedScheduleDate);

	var freeDrivers = numOfDrivers;
	var reservedSchedule = {
		fromDate: undefined,
		toDate: undefined,
		postCodes: []
	};

	var allReservedSchedules = [];
	while (schedulesHeap.size > 1) {
		var scheduleFragmet = schedulesHeap.pop();
		if (scheduleFragmet.start) {
			freeDrivers--;
			reservedSchedule.postCodes = reservedSchedule.postCodes.concat(scheduleFragmet.postCodes);
			reservedSchedule.fromDate = scheduleFragmet.time;

		} else {
			freeDrivers++;
			if (freeDrivers === 1) {
				reservedSchedule.toDate = scheduleFragmet.time;
				reservedSchedule.postCodes = _.uniq(reservedSchedule.postCodes);
				allReservedSchedules.push(_.clone(reservedSchedule));
				reservedSchedule.postCodes = [];
				reservedSchedule.toDate = undefined;
				reservedSchedule.fromDate = undefined;
			} else {
				scheduleFragmet.postCodes.forEach(function (postCode) {
					var indexOfPostCode = reservedSchedule.postCodes.indexOf(postCode);
					while (~indexOfPostCode) {
						reservedSchedule.postCodes.splice(indexOfPostCode, 1);
						indexOfPostCode = reservedSchedule.postCodes.indexOf(postCode);
					}
				});
			}
		}
	}

	return allReservedSchedules;
}


function getHeapForSchedules(schedules, minimumAllowedScheduleDate, maximumAllowedScheduleDate) {
	var heapComparator = function (aObject, anotherObject) {
		if (aObject.time < anotherObject.time) {
			return true;
		}
		if (aObject.time === anotherObject.time) {
			return aObject.start === true;
		}
		return false;
	};
	var schedulesAsObjects = getSchedulesAsObjects(schedules, minimumAllowedScheduleDate, maximumAllowedScheduleDate);

	var heap = Heap.create([], heapComparator);

	_.forEach(schedulesAsObjects, function (scheduleObject) {
		var shouldRepeat = !!scheduleObject.repeatWeekdays || !!scheduleObject.repeatWeekends;

		heap.push({
			time: scheduleObject.fromDate,
			repeat: shouldRepeat,
			postCodes: scheduleObject.postCodes,
			start: true
		});

		heap.push({
			time: scheduleObject.toDate,
			repeat: shouldRepeat,
			postCodes: scheduleObject.postCodes,
			start: false
		});
	});

	return heap;
}


function getSchedulesAsObjects(schedules, minimumAllowedScheduleDate, maximumAllowedScheduleDate) {
	var expandedSchedules = [];
	schedules = schedules || [];

	schedules.forEach(function (schedule) {
		var scheduleObject = {
			fromDate: schedule.get('fromDate'),
			toDate: schedule.get('toDate'),
			repeatWeekdays: !!schedule.get('repeatWeekdays'),
			repeatWeekends: !!schedule.get('repeatWeekends')
		}
		
		try {
			scheduleObject.postCodes = [schedule.get('toAddress').get('postCode')];
		} catch (e) {
			logger.warn(JSON.stringify(schedule));
			logger.error(e);
		}

		if ((scheduleObject.repeatWeekdays || scheduleObject.repeatWeekends)) {
			expandedSchedules = expandedSchedules.concat(getExpandedSchedulesForRecurring(scheduleObject, minimumAllowedScheduleDate, maximumAllowedScheduleDate));
		} else {
			expandedSchedules.push(scheduleObject);
		}
	});

	return expandedSchedules;
}


function getExpandedSchedulesForRecurring(schedule, minimumAllowedScheduleDate, maximumAllowedScheduleDate) {
	var oneHourInMilliSeconds = 60 * 60 * 1000;
	var oneDayInMilliSeconds = 24 * oneHourInMilliSeconds;
	var expandedSchedules = [];


	var scheduleFromDate = new Date(schedule.fromDate);

	var scheduleDurationInMs = schedule.toDate.getTime() - schedule.fromDate.getTime();

	if (schedule.fromDate < minimumAllowedScheduleDate) {
		scheduleFromDate.setDate(minimumAllowedScheduleDate.getDate());
		scheduleFromDate.setMonth(minimumAllowedScheduleDate.getMonth());
		scheduleFromDate.setYear(minimumAllowedScheduleDate.getFullYear());
		//schedule.toDate = new Date(schedule.fromDate.getTime() + scheduleDurationInMs);
	}

	while (scheduleFromDate <= maximumAllowedScheduleDate) {
		var dayOfWeek = scheduleFromDate.getDay();
		var isWeekEnd = dayOfWeek === 6 || dayOfWeek === 0; //Sat=6, Sun=0

		if ((schedule.repeatWeekdays && !isWeekEnd) || (schedule.repeatWeekends && isWeekEnd)) {
			var clonedSchedule = _.clone(schedule);
			clonedSchedule.fromDate = scheduleFromDate;
			clonedSchedule.toDate = new Date(clonedSchedule.fromDate.getTime() + scheduleDurationInMs);
			expandedSchedules.push(clonedSchedule);
		}

		scheduleFromDate = new Date(scheduleFromDate.getTime() + oneDayInMilliSeconds);
	}

	return expandedSchedules;
}


/**
 * Gets all the reserved hours a business will have in conjuction with the schedule of its drivers
 * 
 * @method getSchedulesForPostCodes
 * @param {Array}  postCodes The post codes on where to look for available laundries. Is an array
 *                           since the user may have more than one adresses registered to him. 
 * @param {Object} response  An object with reserved hours per post code.
 *                           An example of result would be:
 *                           ```javascript
 *                           {
 *                  	     	minPickUpDate: 29 Jul 2015T12:00,
 *                   	       	maxDeliveryDate: 12 Aug 2015T12:00,
 *                    	      	reserved: [{
 * 					      			fromDate: 29 Jul 2015T15:00,
 *	 				        		toDate: 29 Jul 2015T15:30,
 *  	             				postCode: 34111
 * 						      	},{
 * 						      		fromDate: 29 Jul 2015T15:00,
 * 					        		toDate: 29 Jul 2015T15:30,
 *               					postCode: 58100
 * 					    	  	}]
 *			                  }
 *                     ```
 *
 */
Parse.Cloud.define('getSchedulesForPostCodes', function getSchedulesForAddresses(request, response) {

	Parse.Cloud.useMasterKey();
	var laundrySchedules, driverSchedules, laundriesTimeOffQuery;
	var orderDateRestrictions = new orderRestrictions.OrderDate();
	var postCodes = request.params.postCodes;
	if (!_.isArray(postCodes)) {
		logger.error('An array was expected as postCodes parameter by the client. Instead received as parameter: ' + JSON.stringify(postCodes));
		response.error(errors.INVALID_ARGUMENT);
	}

	logger.debug('Trying to calculate schedule based on post codes: ' + JSON.stringify(postCodes));

	/*
	 *
	 *
	 *
	 * Mock response
	 *
	 *
	 * */
		var driverAvailabilityInterval = 1800000;
	
		var scheduleResponseMock = {
			minPickUpDate: orderDateRestrictions.minimumAllowed,
			maxDeliveryDate: orderDateRestrictions.maximumAllowed,
			reserved: []
		};
	
		var numberOfreservedSlots = 10 + Math.random() * (100 - 10)
		for (var i = 0; i <= numberOfreservedSlots; i++) {
			var reserved = {
				fromDate: getRandomDateForMock(orderDateRestrictions.minimumAllowed, orderDateRestrictions.maximumAllowed),
				postCode: [getRandomPostCode()]
			};
			reserved.toDate = new Date(reserved.fromDate.getTime() + driverAvailabilityInterval);
			scheduleResponseMock.reserved.push(reserved);
		}
	
		function getRandomDateForMock(from, to) {
			if (!from) {
				from = new Date(1900, 0, 1).getTime();
			} else {
				from = from.getTime();
			}
			if (!to) {
				to = new Date(2100, 0, 1).getTime();
			} else {
				to = to.getTime();
			}
			var randomDate = new Date(from + Math.random() * (to - from));
	
			randomDate.setMinutes(0);
			randomDate.setSeconds(0);
			randomDate.setMilliseconds(0);
			return new Date(randomDate.getTime());
		}
	
		function getRandomPostCode() {
			return postCodes[Math.floor(Math.random() * postCodes.length)];
		}
	
		/*End of mock response*/
		response.success(scheduleResponseMock);

//	laundriesTimeOffQuery = createLaundryScheduleQueryForPostCodes(postCodes, orderDateRestrictions.minimumAllowed, orderDateRestrictions.maximumAllowed);
//	laundriesTimeOffQuery.find()
//		.done(function getDriversForLaundries(laundrySchedulesResponse) {
//			logger.debug('Found ' + laundrySchedulesResponse.length + ' reserved timeslots from laudries. ' + JSON.stringify(laundrySchedulesResponse));
//			if (!laundrySchedulesResponse || !laundrySchedulesResponse.length) {
//				response.success([]);
//			}
//
//			laundrySchedules = laundrySchedulesResponse;
//
//
//			var laundries = _.map(laundrySchedules, function (schedule) {
//				var laundry = schedule.get('laundry');
//				return laundry;
//			});
//
//			var driverScheduleQuery = createDriverScheduleQueryForLaundries(laundries, orderDateRestrictions.minimumAllowed, orderDateRestrictions.maximumAllowed);
//
//			logger.debug('About to look for driver schedules');
//			return driverScheduleQuery.find();
//		})
//		.done(function calculateSchedules(driverSchedulesResponse) {
//			logger.debug('Based on laundries results found the following driver schedule timeslots: ' + JSON.stringify(driverSchedules));
//			if (!driverSchedulesResponse || !driverSchedulesResponse.length) {
//				response.success([]);
//				return;
//			}
//			driverSchedules = driverSchedulesResponse;
//
//			var scheduleResponse = getAllSchedules(laundrySchedules, driverSchedules, orderDateRestrictions.minimumAllowed, orderDateRestrictions.maximumAllowed);
//
//			logger.debug('The final result for schedules is ' + JSON.stringify(scheduleResponse));
//
//			response.success(scheduleResponse);
//
//		})
//		.fail(function (error) {
//			logger.warn('An error occured while lookign for schedules. Error response: ' + JSON.stringify(error));
//			logger.error(error);
//			response.error(errors.getErrorResponse(error));
//		})
});