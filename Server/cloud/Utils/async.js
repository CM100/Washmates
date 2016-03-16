/*globals Parse, exports*/
/**
 * @class AsyncUtils
 */
/**
 * Gets a parse asynchronous call and transforms it into a promise.
 * 
 * @method runAsPromise
 * @private
 * @param   {Object}  parseObject The parse object on which the async method will be called for.
 * @param   {String}  method      The name of the method to call as a string. e.g. 'save'
 * @returns {Promise} A Parse.Promise
 */
var runAsPromise = function (parseObject, method) {
	var promise = new Parse.Promise();
	var params = Array.prototype.slice.call(arguments, 2, arguments.length);
	params.push({
		success: function () {
			promise.resolve.apply(promise, arguments);
		},
		error: function () {
			promise.reject.apply(promise, arguments);
		}
	});

	parseObject[method].apply(parseObject, params);

	return promise;
};




exports.runAsPromise =  runAsPromise;