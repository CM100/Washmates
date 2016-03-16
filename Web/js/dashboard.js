var parseService = require('./parseService');
var app = require('./app');

var queries = [];
queries.push(parseService.getUsers());
queries.push(parseService.getIssues());
queries.push(parseService.getOrders());
queries.push(parseService.getLaundries());
queries.push(parseService.getDrivers());

app.ready(function () {
	Parse.Promise.when(queries).then(function (users, issues, orders, laundries, drivers) {
		var dashboard = new Dashboard(users, issues, orders, laundries, drivers);
		dashboard.setHeroCounters();
		dashboard.drawCharts();
	})
});

function Dashboard(users, issues, orders, laundries, drivers) {

	this.setHeroCounters = function () {
		$('#users-counter').text(users.length);
		$('#issues-counter').text(issues.length);
		$('#laundries-counter').text(laundries.length);
		$('#orders-counter').text(orders.length);
		$('#drivers-counter').text(drivers.length);
	};

	this.drawCharts = function () {

		this.drawOrdersPerDate();
		this.drawOrdersStatusChart();
		this.drawOrdersRatingChart()
	};

	this.drawOrdersPerDate = function () {
		var ordersSorted = Parse._.sortBy(orders, 'updatedAt');
		var completedOrdersPerDate = Parse._.filter(ordersSorted, function (order) {
			return order.get('payment') !== undefined;
		});

		var allOrdersPerDateData = Parse._.countBy(ordersSorted, function (order) {
			var dateOnly = order.updatedAt.toLocaleDateString();
			var date = new Date(dateOnly);
			return date.getTime();
		});

		allOrdersPerDateData = Parse._.map(allOrdersPerDateData, function (vals, key) {
			return [key, vals]
		})

		var completedOrdersPerDateData = Parse._.countBy(completedOrdersPerDate, function (order) {
			var dateOnly = order.updatedAt.toLocaleDateString();
			var date = new Date(dateOnly);
			return date.getTime();
		});
		completedOrdersPerDateData = Parse._.map(completedOrdersPerDateData, function (vals, key) {
			return [key, vals]
		})

		$.plot($("#orders-chart-multi"), [{
			data: allOrdersPerDateData,
			label: "All orders"
        }, {
			data: completedOrdersPerDateData,
			label: "Completed orders",
			yaxis: 2
        }], {
			series: {
				lines: {
					show: true
				},
				points: {
					show: true
				}
			},
			xaxis: {
				mode: 'time'
            },
			yaxis: {
				min: 0
            },
			legend: {
				position: 'sw'
			},
			grid: {
				hoverable: true //IMPORTANT! this is needed for tooltip to work
			},
			tooltip: true,
			tooltipOpts: {
				content: function (label, time, quantity, flotItem) {
					var chartDate = new Date();
					chartDate.setTime(time);
					var content = quantity + ' orders created at ' + chartDate.toLocaleDateString();
					return content;
				},
				xDateFormat: "%y-%0m-%0d",
				onHover: function (flotItem, $tooltipEl) {
					// console.log(flotItem, $tooltipEl);
				}
			}

		});
	}

	this.drawOrdersStatusChart = function () {
		var numOfOrdersPerStatus = Parse._.countBy(orders, function (order) {
			return order.get('status');
		});

		var numOrdersPersStatusData = Parse._.map(numOfOrdersPerStatus, function (val, key) {
			return {
				label: key,
				data: val
			};
		});
		//Flot Pie Chart

		var plotObj = $.plot($("#orders-status-pie"), numOrdersPersStatusData, {
			series: {
				pie: {
					show: true
				}
			},
			grid: {
				hoverable: true
			},
			tooltip: true,
			tooltipOpts: {
				content: "%p.0%, %s", // show percentages, rounding to 2 decimal places
				shifts: {
					x: 20,
					y: 0
				},
				defaultTheme: false
			}
		});

	}

	this.drawOrdersRatingChart = function () {
		var numOfOrdersPerRating = Parse._.countBy(orders, function (order) {
			var rating = order.get('rating');
			return rating ? rating * 5 + '/5' : 'Not Rated';
		});

		var numOrdersPerRatingData = Parse._.map(numOfOrdersPerRating, function (val, key) {
			return {
				label: key,
				data: val
			};
		});
		//Flot Pie Chart

		var plotObj = $.plot($("#orders-rating-pie"), numOrdersPerRatingData, {
			series: {
				pie: {
					show: true
				}
			},
			grid: {
				hoverable: true
			},
			tooltip: true,
			tooltipOpts: {
				content: "%p.0%, %s", // show percentages, rounding to 2 decimal places
				shifts: {
					x: 20,
					y: 0
				},
				defaultTheme: false
			}
		});
	}

}