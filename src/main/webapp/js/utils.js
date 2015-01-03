//zavisle na jQuery s AJAX

(function(global){
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

    // -----------------------------------------
    function info(msg, title) {
        notify(msg, title || "Information", "info");        
    };
    function success(msg, title) {
        notify(msg, title || "Success", "success");        
    };
    function warn(msg, title) {
        notify(msg, title || "Warning", "warn");        
    };
    function error(msg, title) {
        notify(msg, title || "Error", "error");        
    };
    function notify(msg, title, type) {
        if(PNotify){
            new PNotify({
                title : title,
                text: msg,
                type: type
            });
        } else {
            console.log(arguments);
        }
    };
    
    // -----------------------------------------
    function jsonGet(options){
        return _ajax('GET', options);
    };

    function jsonPut(options){
        return _ajax('PUT', options);
    };
    
    function jsonPost(options){
        return _ajax('POST', options);
    };
    
    function jsonDelete(options){
        return _ajax('DELETE', options);
    };

    function _ajax(type, options){
        var ajaxOpt = extend({
            type : type,
            contentType : 'application/json',
            headers: {
                "Accept" : "application/json; charset=utf-8", 
                "X-Auth-Token": options.token || "no-token"
            },
            error : function(response, type, msg){
                error(response.responseText, msg);
            }
        }, options);
        //set data convert
        ajaxOpt.data = JSON.stringify(options.dataJs);
        //send
        return $.ajax(ajaxOpt);        
    };

    //-------------------------------------------
    
    function openCernyRytir(cardName){
        var params = {
            jmenokarty : cardName,
            triditpodle : "ceny",
            submit : "Vyhledej"
        };
        utils.links.submitForm("POST", "http://www.cernyrytir.cz/index.php3?akce=3", params, "hidden");
    };
    
    function openTolarie(cardName){
        var params = {
            name : cardName,
            o : "name",
            od : "a"
        };
        utils.links.submitForm("GET", "http://www.tolarie.cz/koupit_karty/", params, "text");        
    };
    
    function openMagicCards(cardName){
        var params = {
            q : cardName,
            v : "card",
            s : "cname"
        };
        utils.links.submitForm("GET", "http://magiccards.info/query", params, "text");        
    };
    
    function formSubmit(method, action, params, fieldType) {
        var form = document.createElement("form");
        form.setAttribute("method", method);
        form.setAttribute("target", "_blank");
        form.setAttribute("action", action);
        form.submitOrig = form.submit;
        for(var key in params) {
            if(params.hasOwnProperty(key)) {
                var hiddenField = document.createElement("input");
                hiddenField.setAttribute("type", fieldType);
                hiddenField.setAttribute("name", key);
                hiddenField.setAttribute("value", params[key]);

                form.appendChild(hiddenField);
             }
        }
        form.submitOrig();
    };    
    
    function generateUUID() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-mxxx-ixxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x7|0x8)).toString(16);
        });
        return uuid;
    };
    //-------------------------------------------
    //export functions...
    global.utils = {
        extend : extend,
        links : {
           submitForm : formSubmit, 
           openTolarie : openTolarie, 
           openCernyRytir : openCernyRytir, 
           openMagicCards : openMagicCards 
        },
        json : {
            put: jsonPut,
            del: jsonDelete,
            get: jsonGet,
            post : jsonPost
        },
        msg : {
            info : info,
            success : success,
            warn : warn,
            error : error
        },
        uuid : generateUUID,
        
        //others
        icons : {
            star : "<span class=\"glyphicon glyphicon-star\" title=\"Foil version of card\"></span>",
            plus : "<span class=\"glyphicon glyphicon-plus\"></span>",
            watch : "<span class=\"glyphicon glyphicon-eye-open\"></span>",
            unwatch : "<span class=\"glyphicon glyphicon-eye-close\" title=\"You are watching card state, on store, change price\"></span>"
        }
    };
})(this);


