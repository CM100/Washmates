/*globals  require, Parse*/
var user = require('cloud/User/index.js');
var stripe = require('cloud/Stripe/index.js');
var order = require('cloud/Order/order');
var address = require('cloud/Address/address');
var logger = require('cloud/Utils/logger');
var discount = require('cloud/DiscountCode/discount');


initializeModules();

function initializeModules(config) {
	user.init(config);
	stripe.init(config);
	order.init(config);
	address.init(config);
	discount.init(config);
}

/**
 * @method getAll
 * Gets all entries from a table that passed as parameter. Can be passed different query criteria.
 * @param {String} table     The name of the table from which the results will be returned.
 * @param {Array} include   An array of tables to include.
 *                          e.g. 
 *                          ```javascript
 *                          ['user', 'deliveryAddress']
 *                          ```
 *                          
 * @param {Object} criteria The criteria for restricting the query to certain results. Criteria should 
 *                          have as key the column name on which restrictions will be applied and as value
 *                          another object with compare function name as key and compare value as value . 
 *                          Compare function name is a parse query comparisson function while value the 
 *                          value to compare the column against.
 *                          e.g.
 *                          ```javascript
 *                          criteria = {
 *                          	objectId: {equalTo: '142sfa1'}
 *                          }
 *                          ```
 */
Parse.Cloud.define('getAll', function (request, response) {
	var tableName = request.params.table;

	var criteria = request.params.criteria;

	var include = request.params.include || [];


	logger.debug(include);
	var results = [];

	var processCallback = function (res) {
		results = results.concat(res);
		//Query for the next 1k-set entries since Parse has limit on 1k results per query
		if (res.length === 1000) {
			process(res[res.length - 1].id);
			return;
		}

		response.success(results);
	}

	var castToJSON = function (input) {
		var result;
		if (Object.prototype.toString.call(input) === '[object String]') {
			logger.debug('Input is string. Casting to JSON');
			try {
				result = JSON.parse(input);
			}catch(e){
				result = input;
			}
			logger.debug(result);
		} else {
			result = input;
			logger.debug('Input is object. Do nothing');
		}

		return result;
	}

	var setCriteriaToQuery = function (query, criteria) {
		Object.keys(criteria).forEach(function (columnName) {
			var criterion = castToJSON(criteria[columnName]);
			Object.keys(criterion).forEach(function (compareFnName) {
				logger.debug('query.' + compareFnName + '("' + columnName + '", ' + criterion[compareFnName] + ')');
				query[compareFnName](columnName, castToJSON(criterion[compareFnName]));
			});
		});
		return query;
	}

	var includeTablesToQuery = function (query, includes) {
		includes.forEach(function (tableName) {
			logger.debug('Including ' + tableName + ' to query.');
			query.include(tableName);
		});
	}
	var process = function (skip) {

		var query = new Parse.Query(Parse.Object.extend(tableName));

		if (criteria !== undefined) {
			criteria = castToJSON(criteria);
			setCriteriaToQuery(query, criteria);
		}

		if (include.length > 0) {
			includeTablesToQuery(query, include);
		}

		if (skip) {
			query.greaterThan("objectId", skip);
		}

		query.limit(1000);
		query.ascending("objectId");
		query.find()
			.done(function querySuccess(res) {
				processCallback(res);
			})
			.fail(function queryFailed(error) {
				response.error(error);
			});
	}


	process(false);
});