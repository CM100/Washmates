var parseService = require('./parseService');
var app = require('./app');

var getDriversPromise = parseService.getDrivers({
	'user.lname': {
		ascending: ''
	}
});
app.ready(function () {

	parseService.getDriverSchedules().done(function (schedules) {

			var calendarScheduleEvents = new ScheduleEvents(schedules);

			var calendar = new Calendar(calendarScheduleEvents);
			calendar.render();

			if (app.isUserAdmin()) {
				var driversSelector = new DriversSelector({
					el: '#driver-calendar-select',
					source: getDriversPromise,
					change: function (driverSelected) {
						calendar.renderForDriver(driverSelected);
						scheduleEventModal.setDriver(driverSelected);
					}
				});
			}

			var scheduleEventModal = new ScheduleEventModal({
				isTimeOff: true,
				created: calendar.addScheduleEvent.bind(calendar)
			});

		})
		.fail(function (error) {
			console.log(error);
		});

})

/**
 * Maps a Parse.DriverSchedule or Parse.LaundrySchedyle  to calendar schedule event
 * @param   {Object} schedule a Parse.DriverSchedule or Parse.LaundrySchedyle
 * @returns {Object} An object that can be handled by bootstrap calendar
 */
function ScheduleEvent(schedule) {

	var calendarSchedule = {};
	var schedulOwnerName, destinationStreet, scheduleTitle = 'No title for this event';
	var scheduleOwner = schedule.constructor.prototype.className.toLowerCase().replace('schedule', ''); // driver or laundry

	schedule.get = schedule.get || function (prop) {
		return schedule[prop];
	}
	schedule.set = schedule.set || function (prop, val) {
		return schedule[prop] = val;
	}

	var ownerTypesOptions = {
		driver: {
			class: "event-info",
			timeOffClass: "event-inverse",
			recurringClass: "event-warning"
		},
		laundry: {
			class: "event-success",
			timeOffClass: "event-inverse",
			recurringClass: "event-warning"
		}
	}

	destinationStreet = schedule.get('toAddress') && schedule.get('toAddress').get('street');

	if (app.isUserDriver()) {
		if (destinationStreet) {
			scheduleTitle = 'Driving to ' + destinationStreet + '.';
		}
		if (schedule.get('isTimeOff')) {
			scheduleTitle = 'Time Off.';
		}
	} else {
		// Laundries have name but drivers do not. So we check the user data.
		schedulOwnerName = schedule.get(scheduleOwner).get('name') || schedule.get(scheduleOwner).get('user').get('fname') + ' ' + schedule.get(scheduleOwner).get('user').get('lname');

		if (destinationStreet) {
			scheduleTitle = schedulOwnerName + ' drives to ' + destinationStreet + '.';
		}
		if (schedule.get('isTimeOff')) {
			scheduleTitle = schedulOwnerName + ' has time off.';
		}
	}

	this.class = schedule.get('isTimeOff') ? ownerTypesOptions[scheduleOwner].timeOffClass : ownerTypesOptions[scheduleOwner].class;
	this.title = scheduleTitle;
	this.start = schedule.get('fromDate').getTime();
	this.end = schedule.get('toDate').getTime();
	this.id = schedule.id;
	this.parseObject = app.utils.parseToJSON(schedule);
	this.parseOriginal = schedule;

	return this;
}


function ScheduleEvents(schedules) {
	var hourInMilliSeconds = 60 * 60 * 1000;
	var dayInMilliSeconds = 24 * hourInMilliSeconds;
	var today = new Date();
	today.setHours(0);
	today.setMinutes(0);
	today.setSeconds(0);
	today.setMilliseconds(0);


	this.expandedSchedules = [];

	this.expandSchedules = function (fromDate, toDate) {
		var allSchedules = [];

		schedules.forEach(function (schedule) {
			if (schedule.get('repeatWeekends') || schedule.get('repeatWeekdays')) {
				allSchedules = allSchedules.concat(this.expandRecurringSchedule(schedule, fromDate, toDate));
			} else {
				allSchedules.push(schedule);
			}
		}.bind(this));

		this.expandedSchedules = allSchedules;
	};

	this.expandRecurringSchedule = function (schedule, minimumAllowedScheduleDate, maximumAllowedScheduleDate) {
		var expandedSchedules = [];

		var scheduleFromDate = new Date(schedule.get('fromDate'));

		var scheduleDurationInMs = schedule.get('toDate').getTime() - schedule.get('fromDate').getTime();

		if (schedule.get('fromDate') < minimumAllowedScheduleDate) {
			scheduleFromDate.setDate(minimumAllowedScheduleDate.getDate());
			scheduleFromDate.setMonth(minimumAllowedScheduleDate.getMonth());
			scheduleFromDate.setYear(minimumAllowedScheduleDate.getFullYear());
		}

		while (scheduleFromDate <= maximumAllowedScheduleDate) {
			var dayOfWeek = scheduleFromDate.getDay();
			var isWeekEnd = dayOfWeek === 6 || dayOfWeek === 0; //Sat=6, Sun=0

			if ((schedule.get('repeatWeekdays') && !isWeekEnd) || (schedule.get('repeatWeekends') && isWeekEnd)) {
				var clonedSchedule = app.utils.parseDeepClone(schedule);
				clonedSchedule.id = schedule.id;
				clonedSchedule.set('fromDate', scheduleFromDate);
				clonedSchedule.set('toDate', new Date(clonedSchedule.get('fromDate').getTime() + scheduleDurationInMs));
				expandedSchedules.push(clonedSchedule);
			}

			scheduleFromDate = new Date(scheduleFromDate.getTime() + dayInMilliSeconds);
		}

		return expandedSchedules;
	}

	this.expandSchedules(today, new Date(today.getTime() + (15 * dayInMilliSeconds)));

	return Parse._.map(this.expandedSchedules, function (schedule) {
		return new ScheduleEvent(schedule);
	});
}


/**
 * Populates the given <select> element with driver names. 
 * 
 * @class DriversSelector
 * @param   {Object} options available options are `source`, `el` & `change`. 
 *                         * `source`: A Parse.Promise, or function or array of data. Either way, everything 
 *                         is expected eventually to return an array of Driver Parse.Objects
 *                         * `el`: The jQuery selector for the <select> element
 *                         * `change`: The onchange callback which will be triggered when a driver is selected
 * @returns {Object} instance of DriversSelector
 */
function DriversSelector(options) {
	options.source = options.source || [];
	options.el = options.el;
	options.change = options.change || function () {};
	var $select = $(options.el);
	
	this._driverSelected = undefined;

	this.render = function () {
		options.source.forEach(function (driver) {
			var driverUser = driver.get('user')
			$select.append($("<option></option>")
				.attr("value", driver.id)
				.text(driverUser.get('lname') + ' ' + driverUser.get('fname')));
		});
		//Async selecting
		if( this._driverSelected ){
			this.selectDriver(this._driverSelected);
		}
		this._bindEvents();
	};

	this.selectDriver = function (driver) {
		$select.find('[value*="' + driver.id + '"]').attr('selected', true);
		this._driverSelected = driver;
	};
	
	this.getSelectedDriver = function(){
		return this._driverSelected;
	}

	this._bindEvents = function () {
		$select.on('change', function (e) {
			this._driverSelected = Parse._.findWhere(options.source, {id: $select.val()});
			options.change.call(this, Parse._.findWhere(options.source, {
				id: $(e.target).val()
			}), e);
		}.bind(this));
	}

	this._processSource = function () {
		if (options.source instanceof Parse.Promise) {
			options.source.done(function (result) {
				options.source = result;
				this._processSource();
			}.bind(this))
		} else if (options.source instanceof Function) {
			options.source = options.source();
			this._processSource();
		} else if (options.source instanceof Array) {
			this.render();
		}
	};

	this._processSource();

	return this;
}


/**
 * Handles the rendering of calendar. Gets scheduleEvents already formatted and able to be parsed for calendar
 * 
 * @param   {Array}  schedules Already formated calendar events
 * @returns {Object} The calendar element
 */
function Calendar(schedules) {
	var _ = Parse._;
	var calendar;
	
	
	this._driverSelected = app.isUserDriver() ? app.currentDriver : undefined;

	this.render = function (driverSchedules) {
		if (calendar) {
			this._unBindEvents();
			calendar.setOptions({
				events_source: driverSchedules || schedules
			});
			calendar.view();
			this._bindEvents()
			return;
		}

		var options = {
			events_source: driverSchedules || schedules,
			view: 'month',
			modal: '#events-modal',
			modal_type: 'template',
			modal_title: function (event) {
				return event.title;
			},
			tmpl_path: '../js/lib/bootstrap.calendar/tmpls/',
			tmpl_cache: false,
			display_week_numbers: true,
			weekbox: true,
			onAfterEventsLoad: function (events) {
				if (!events) {
					return;
				}
				var list = $('#eventlist');
				list.html('');

				$.each(events, function (key, val) {
					$(document.createElement('li'))
						.html('<a href="' + val.url + '">' + val.title + '</a>')
						.appendTo(list);
				});
			},
			onAfterViewLoad: function (view) {
				$('.page-header h3').text(this.getTitle());
				$('.btn-group button').removeClass('active');
				$('button[data-calendar-view="' + view + '"]').addClass('active');
			},
			onAfterModalShown: function(){
				var changeEventModal = new ChangeEventModal({
					events: schedules,
					change: function(scheduleEvent){
						this.render();
					}.bind(this),
					delete: function(scheduleEvent){
						schedules = Parse._.without(schedules, Parse._.findWhere(schedules, {id: scheduleEvent.id}));
						this.render();
					}.bind(this),
				});
			}.bind(this),
			onAfterModalHidden: function(){
				$(this).off();
			},
			classes: {
				months: {
					general: 'label'
				}
			}
		};
		
		


		calendar = $('#calendar').calendar(options);
		this._bindEvents();
		return calendar;
	}

	this.renderForDriver = function (driver) {
		var schedulesForDriver = Parse._.filter(schedules, function (schedule) {
			return schedule.parseObject.driver.objectId === driver.id;
		});
		this._driverSelected = driver;
		this._unBindEvents();
		this.render(schedulesForDriver);
	}

	this.addScheduleEvent = function (schedule) {
		schedules.push(schedule);
		if (this._driverSelected) {
			this.renderForDriver(this._driverSelected);
		} else {
			this.render()
		}
	}

	this._bindEvents = function () {
		var dateFromSelectors = '#schedule-timepicker-from input';
		var dateToSelectors = '#schedule-timepicker-to input';
		var that = this;

		$('.btn-group button[data-calendar-nav]').each(function () {
			var $this = $(this);
			$this.click(function () {
				calendar.navigate($this.data('calendar-nav'));
			});
		});

		$('.btn-group button[data-calendar-view]').each(function () {
			var $this = $(this);
			$this.click(function () {
				calendar.view($this.data('calendar-view'));
			});
		});

	};

	this._unBindEvents = function () {
		$('.btn-group button[data-calendar-nav]').off();
		$('.btn-group button[data-calendar-view]').off();
	}
}

/**
 * Modal handlings for calendar modal. Changes in a calendar event (schedule) are handled here.
 * @param   {Object}   options * `schedules` list of available schedules.
 *                             * `change` handler when a schedule have been changed and saved in parse.
 * @returns {[[Type]]} [[Description]]
 */
function ChangeEventModal(options) {
	var noop = function(){};
	options.change = options.change || noop;
	options.delete = options.delete || noop;
	options.events = options.events || [];
	var modal = $('#events-modal');
	
	
	
	var dateFromSelectors = '#change-schedule-timepicker-from';
	var dateToSelectors = '#change-schedule-timepicker-to';
	
	var selectedScheduleId = modal.find('#event-meta').attr('data-schedule-id')
	var scheduleEvent = Parse._.findWhere(options.events, {id: selectedScheduleId});
	var scheduleObject = scheduleEvent.parseObject ? scheduleEvent.parseObject : '';
	
	if( !scheduleObject ) {
		console.error('No schedule found for this id');
		return;
	}
	
	
	var driversSelector = new DriversSelector({
		source: getDriversPromise,
		el: '#driver-schedule-assign'
	});
	driversSelector.selectDriver({id: scheduleObject.driver.objectId});
	
	this._bindEvents = function () {
		var steppingMinutes = app.config.get('driverAvailabilityInterval') / (60 /*seconds in a minute*/ * 1000 /*ms in a second*/ );
		
		
		modal.on('click', '.change-schedule-save-btn', function (e) {
			e.preventDefault();
			e.stopPropagation();			
			var driverSelected = driversSelector.getSelectedDriver();
			
			var fromDateSelected = new Date(modal.find('#change-schedule-timepicker-from').val());
			var toDateSelected = new Date(modal.find('#change-schedule-timepicker-to').val());
			var shouldSaveSchedule = false;
			var schedule = new (Parse.Object.extend('DriverSchedule'))(scheduleObject);
			
			if( driverSelected.id !== scheduleObject.driver.objectId ) {
				schedule.set('driver', app.utils.parseToPointer(driverSelected));
				scheduleObject.driver = app.utils.parseToJSON(driverSelected);
				shouldSaveSchedule = true;
			}
			if( fromDateSelected.getTime() !== scheduleObject.fromDate.getTime() ) {
				scheduleObject.fromDate=  fromDateSelected;
				shouldSaveSchedule = true;
			}
			if( toDateSelected.getTime() !== scheduleObject.toDate.getTime() ) {
				scheduleObject.toDate = toDateSelected;
				shouldSaveSchedule = true;
			}
			
			if( shouldSaveSchedule ) {
				if( scheduleObject.repeatWeekends || scheduleObject.repeatWeekdays ) {
					$(document.body).showAlertDialog('You can not alter a recurring event. Consider deleting it and creating a new one.');
					return;
				}
				var toAddress = new (Parse.Object.extend('Address'))(scheduleObject.toAddress);
				var fromAddress = new (Parse.Object.extend('Address'))(scheduleObject.fromAddress);
				schedule.set('toAddress', app.utils.parseToPointer(toAddress));
				schedule.set('fromAddress', app.utils.parseToPointer(fromAddress));
				
				schedule.save()
					.done(function(sched){
						sched.set('toAddress', toAddress);
						sched.set('fromAddress', fromAddress);
						sched.set('driver', driverSelected);
						var newScheduleEvent = new ScheduleEvent(sched);
						Parse._.defaults(newScheduleEvent, scheduleEvent);
						options.change(sched);
						modal.modal('hide');
					})
					.fail($(document.body).showAlertDialog);
			}
			console.log('saving');
		});
		
		
		modal.on('click', '.change-schedule-delete-btn', function(e){
			e.preventDefault();
			e.stopPropagation();
			var alrt = $(document.body).showConfirmDialog('You are about to delete this event. Proceed?');
			alrt.on('ok', function(){
				scheduleEvent.parseOriginal.destroy().done(function(){
					options.delete(scheduleEvent);
				})
				modal.modal('hide');
			});
			
			alrt.on('cancel', function(){
				modal.modal('hide');
			})
		});
		
		
		modal.find(dateFromSelectors).datetimepicker({
			toolbarPlacement: 'top',
			defaultDate: scheduleObject.fromDate,
			showClear: true,
			stepping: steppingMinutes,
			showTodayButton: true
		});

		
		modal.find(dateToSelectors).datetimepicker({
			toolbarPlacement: 'top',
			defaultDate: scheduleObject.toDate,
			showClear: true,
			stepping: steppingMinutes,
			useCurrent: false
		});

		
		modal.find(dateFromSelectors).on("dp.change", function (e) {
			$(dateToSelectors).data("DateTimePicker").minDate(e.date);
		});
		

		modal.find(dateToSelectors).on("dp.change", function (e) {
			$(dateFromSelectors).data("DateTimePicker").maxDate(e.date);
		});		
	}
	
	this._bindEvents();
	
	return this;
}
/**
 * [[Description]]
 * @param {[[Type]]} driver    [[Description]]
 * @param {[[Type]]} isTimeOff [[Description]]
 */
function ScheduleEventModal(options) {
	options.driver = options.driver || app.currentDriver;
	options.isTimeOff = options.isTimeOff === undefined ? true : options.isTimeOff;
	options.laundry = options.laundry || app.currentLaundry;
	options.created = options.created || function () {};

	var instance = this;

	var dateFromSelectors = '#schedule-timepicker-from input';
	var dateToSelectors = '#schedule-timepicker-to input';

	this.setDriver = function (driver) {
		if (driver instanceof Parse.Object && driver.constructor.prototype.className === "Driver") {
			options.driver = driver;
			this._unBindEvents();
			this._bindEvents();
		}
	}

	this._bindEvents = function () {
		$('#create-schedule-modal').on('show.bs.modal', function (event) {
			var button = $(event.relatedTarget); // Button that triggered the modal
			var modal = $(this);
			var steppingMinutes = app.config.get('driverAvailabilityInterval') / (60 /*seconds in a minute*/ * 1000 /*ms in a second*/ );

			modal.find(dateFromSelectors).datetimepicker({
				toolbarPlacement: 'top',
				showClear: true,
				stepping: steppingMinutes,
				showTodayButton: true
			});

			modal.find(dateToSelectors).datetimepicker({
				toolbarPlacement: 'top',
				showClear: true,
				stepping: steppingMinutes,
				useCurrent: false
			});

			modal.find(dateFromSelectors).on("dp.change", function (e) {
				$(dateToSelectors).data("DateTimePicker").minDate(e.date);
				console.log('show')
			});

			modal.find(dateToSelectors).on("dp.change", function (e) {
				$(dateFromSelectors).data("DateTimePicker").maxDate(e.date);
			});



			var driverSelectorOptions = {};
			driverSelectorOptions.el = 'select#driver-timeoff-select';
			driverSelectorOptions.source = options.driver ? [options.driver] : getDriversPromise;
			var driverSelector = new DriversSelector(driverSelectorOptions);
			var modalTitle = '';

			if (options.driver) {
				var selectedUserDriver = options.driver.get('user')
				driverSelector.selectDriver(options.driver);
				modal.find(driverSelectorOptions.el).hide();
				modal.find('label[for="' + $(driverSelectorOptions.el)[0].id + '"]').hide();
				modalTitle = "(" + selectedUserDriver.get('lname') + ' ' + selectedUserDriver.get('fname') + ")";
			}

			if (options.isTimeOff) {
				modalTitle = "- Time Off - " + modalTitle;
			}

			modal.find('.modal-title span').text(modalTitle);

		});

		$('#create-schedule-modal').on('hide.bs.modal', function (event) {
			var modal = $(this);
			modal.find(dateFromSelectors).data("DateTimePicker").clear();
			modal.find(dateToSelectors).data("DateTimePicker").clear();
			modal.off();
		});

		$('#create-schedule-modal').on('click', '.modal-footer #create-new-schedule-btn', function (e) {
			var modal = $(e.delegateTarget);
			var fromDate = new Date(modal.find('#schedule-timepicker-from input').val());
			var toDate = new Date(modal.find('#schedule-timepicker-to input').val());
			var chosenRecurringOptions = modal.find('.recurring-options input').parent('.active').children();
			var repeatWeekdays = !!chosenRecurringOptions.parent().find('[name=repeat-weekdays]').length;
			var repeatWeekends = !!chosenRecurringOptions.parent().find('[name=repeat-weekends]').length;

			var DriverSchedule = Parse.Object.extend('DriverSchedule');
			var scheduleOptions = {
				driver: options.driver,
				fromDate: fromDate,
				toDate: toDate,
				isTimeOff: options.isTimeOff,
				repeatWeekdays: repeatWeekdays,
				repeatWeekends: repeatWeekends
			}
			if (options.driver) {
				scheduleOptions.driver = options.driver;
			}
			if (options.laundry) {
				scheduleOptions.laundry = options.laundry;
			}
			var schedule = new DriverSchedule(scheduleOptions);

			if (!isNaN(fromDate.getTime()) && !isNaN(toDate)) {
				schedule.save()
					.done(function (newSchedule) {
						options.created.call(undefined, new ScheduleEvent(newSchedule));
						modal.modal('hide');
					}.bind(this))
					.fail($(document.body).showAlertDialog);
			}
		});
	}

	this._unBindEvents = function () {
		$('#create-schedule-modal').off();
	}

	this._bindEvents();
}