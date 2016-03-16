
module.exports = {
    parseToJSON: function (parseObj) {
        var result = parseObj.toJSON();

        if (parseObj.createdAt) {
            result.createdAt = parseObj.createdAt;
            result.updatedAt = parseObj.updatedAt;
        }

        Object.keys(parseObj.attributes).forEach(function (attrKey) {
            var attrVal = parseObj.get(attrKey);
            //File types return the file url
            if (attrVal instanceof Parse.File) {
                result[attrKey] = attrVal.url();
                return;
            }
            //Date types return normal date
            if (result[attrKey].__type === "Date") {
                result[attrKey] = new Date(result[attrKey].iso);
            }

            if (attrVal instanceof Parse.Object) {
                result[attrKey] = module.exports.parseToJSON(attrVal);
            }

        });
        return result;
    },

    parseDeepClone: function(parseObj){
        var result = parseObj.clone();

        Object.keys(parseObj.attributes).forEach(function (attrKey) {
            var attrVal = parseObj.get(attrKey);
            if (attrVal instanceof Parse.File) {
                result[attrKey] = attrVal.url();
                return;
            }
            if (attrVal instanceof Parse.Object) {
                result[attrKey] = module.exports.parseDeepClone(attrVal);
            }

        });
        return result;
    },


    parseToPointer: function(parseObj) {
        return { className: parseObj.className, objectId: parseObj.id, "__type": "Pointer"};
    },


    redirectTo: function (page) {
        if (page.startsWith('http')) {
            window.location.href = page;
        } else if (page.substr(page.lastIndexOf('.') + 1) === 'html') {
            window.location.href = window.location.href.replace(/(.*)\/.*(\.html).*/i, '$1/' + page);
        } else {
            window.location.href = window.location.href.replace(/(.*)\/.*(\.html).*/i, '$1/' + page + '$2');
        }
    },

    deserializeUrl: function(url){
        var urlComponents = url.split('?');
        var urlSearchString = urlComponents[1];
        var searchParams = {};
        urlSearchString.split('&').forEach(function(searchParam){
            var searchParamDecomposed = searchParam.split('=');
            searchParams[searchParamDecomposed[0]] = searchParamDecomposed[1];
        });

        return searchParams;
    }
}
