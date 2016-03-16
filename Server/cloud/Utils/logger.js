/**
 * @class Logger
 * @static
 */
var LOG_LEVEL = {
	WARN: 'warning',
	INFO: 'info',
	ERROR: 'error',
	DEBUG: 'debug'
};
LOG_LEVEL['__default'] = LOG_LEVEL.INFO;
LOG_LEVEL['undefined'] = LOG_LEVEL.__default;
LOG_LEVEL['null'] = LOG_LEVEL.__default;


function MessageLog(message, logLevel) {
	var jsonMessage = {
		origin: '',
		caller: '',
		filePath: '',
		level: logLevel || LOG_LEVEL.__default,
		message: message
	};

	this.asJSON = function toJSON() {
		return JSON.stringify(jsonMessage);
	}
	this.asString = function toString() {
		var fileNameWithoutExtension = jsonMessage.origin.split('.');
		fileNameWithoutExtension.pop();
		return '[' + jsonMessage.level.toUpperCase() + ']' +
			fileNameWithoutExtension.join('.') + '=>' + jsonMessage.caller + ':: ' +
			JSON.stringify(jsonMessage.message);
	}


	var setCallerOriginFromStackArray = function (stackArray, fileName) {
		var origin;
		for (var i = 0; i < stackArray.length; i++) {
			origin = stackArray[i + 1];
			if (!origin) {
				return;
			}
			if (~stackArray[i].indexOf('exportsLogMessage') || ~origin.indexOf(fileName)) {
				
				
				var leftParenthesisIndex = origin.indexOf('(');
				var filePath = origin.slice(leftParenthesisIndex + 1).split('/');
				var filename = filePath.pop().split(':')[0];

				if (~leftParenthesisIndex || (~leftParenthesisIndex && ~origin.indexOf(fileName))) {
					jsonMessage.origin = filename
					jsonMessage.caller = origin.slice(0, leftParenthesisIndex);
					jsonMessage.caller = jsonMessage.caller.replace('Object.', '');
					jsonMessage.filePath = filePath;
					
				} else {
					setCallerOriginFromStackArray(stackArray.slice(i + 2), filename);
				}
				i = stackArray.length; //exit for loop
			}
		}
	}

	try {
		throw new Error('error')
	} catch (e) {
		var stackArray = e.stack.split('at ');
		setCallerOriginFromStackArray(stackArray);

	}
}

function Logger() {
	var DEV_PARSE_APP_ID = 'cGiRSDM8unMmInG0iDO4DG3oesrWm00QGNcU6eUN';
	var isDevelopmentEnv = DEV_PARSE_APP_ID === Parse.applicationId;
	this.log = function logMessage(message, level) {
		var messageLog = new MessageLog(message, level);
		if (level == LOG_LEVEL.DEBUG && !isDevelopmentEnv) {
			return;
		} else if (isDevelopmentEnv) {
			console.log(messageLog.asString());
		} else {
			console.log(messageLog.asJSON());
		}
	};
}


var logger = new Logger();

exports.log = function exportsLogMessage(message) {
	logger.log(message);
};
exports.info = function exportsLogMessage(message) {
	logger.log(message, LOG_LEVEL.INFO);
};
exports.warn = function exportsLogMessage(message) {
	logger.log(message, LOG_LEVEL.WARN);
};
exports.error = function exportsLogMessage(message) {
	logger.log(message, LOG_LEVEL.ERROR);
};
exports.debug = function exportsLogMessage(message) {
	logger.log(message, LOG_LEVEL.DEBUG);
};