//zavisle na jQuery s AJAX

define(['jquery', 'alertify'],  function (jQuery, alertify) {
    /**
     * function is extending props from source to destination
     */
    function extend(destination, source) {
        for (var k in source) {
            if (source.hasOwnProperty(k)) {
                destination[k] = source[k];
            }
        }
        return destination;
    }

    /**
     * Creates a version of the function that will only be run after first being called count times. Useful for grouping asynchronous responses, where you want to be sure that all the async calls have finished, before proceeding.
     * @param {Number} times
     * @param {Function} func
     * @returns {Function}
     */
    function after(times, func) {
        return function() {
            if (--times < 1) {
                return func.apply(this, arguments);
            }
        };
    }

    // -----------------------------------------
    function info(msg, title) {
        notify(msg, title || "Information", "info");

    }

    function success(msg, title) {
        notify(msg, title || "Success", "success");

    }

    function warn(msg, title) {
        notify(msg, title || "Warning", "warn");

    }

    function error(msg, title) {
        notify(msg, title || "Error", "error");

    }

    function notify(msg, title, type) {
        if (alertify) {
            alertify.log(msg,type);
        } else {
            console.log(arguments);
        }
    }

    // -----------------------------------------
    function jsonGet(options) {
        return _ajax('GET', options);
    }

    function jsonPut(options) {
        return _ajax('PUT', options);
    }

    function jsonPost(options) {
        return _ajax('POST', options);
    }

    function jsonDelete(options) {
        return _ajax('DELETE', options);
    }

    function _ajax(type, options) {
        var ajaxOpt = extend({
            type: type,
            contentType: 'application/json',
            headers: {
                "Accept": "application/json; charset=utf-8",
                "X-Auth-Token": options.token || "no-token"
            },
            error: function (response, type, msg) {
                error(response.responseText, msg);
            }
        }, options);
        //set data convert
        ajaxOpt.data = JSON.stringify(options.dataJs);
        //send
        return jQuery.ajax(ajaxOpt);

    }

    //-------------------------------------------
    function formSubmit(method, action, params, fieldType) {
        var form = document.createElement("form");
        form.setAttribute("method", method);
        form.setAttribute("target", "_blank");
        form.setAttribute("action", action);
        form.submitOrig = form.submit;
        for (var key in params) {
            if (params.hasOwnProperty(key)) {
                var hiddenField = document.createElement("input");
                hiddenField.setAttribute("type", fieldType);
                hiddenField.setAttribute("name", key);
                hiddenField.setAttribute("value", params[key]);

                form.appendChild(hiddenField);
            }
        }
        form.submitOrig();
    }

    function openCernyRytir(cardName) {
        var params = {
            jmenokarty: cardName,
            triditpodle: "ceny",
            submit: "Vyhledej"
        };
        formSubmit("POST", "http://www.cernyrytir.cz/index.php3?akce=3", params, "hidden");
    }

    function openTolarie(cardName) {
        var params = {
            name: cardName,
            o: "name",
            od: "a"
        };
        formSubmit("GET", "http://www.tolarie.cz/koupit_karty/", params, "text");

    }

    function openMagicCards(cardName) {
        var params = {
            q: cardName,
            v: "card",
            s: "cname"
        };
        formSubmit("GET", "http://magiccards.info/query", params, "text");
    }

    function generateUUID() {
        var d = new Date().getTime();
        return 'xxxxxxxx-mxxx-ixxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x7 | 0x8)).toString(16);
        });
    }

    //-------------------------------------------
    //export functions...
    return {
        extend: extend,
        after: after,
        links: {
            openTolarie: openTolarie,
            openCernyRytir: openCernyRytir,
            openMagicCards: openMagicCards
        },
        json: {
            put: jsonPut,
            del: jsonDelete,
            get: jsonGet,
            post: jsonPost
        },
        msg: {
            info: info,
            success: success,
            warn: warn,
            error: error
        },
        uuid: generateUUID
    };
});


