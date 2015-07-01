/**
 * Created by MAR on 13.2.2015.
 */
define(['knockout', 'utils', 'plugins/router'], function (ko, utils, router) {
    var self = {};

    self.input = ko.observable("SELECT c FROM Card c WHERE  c.created = DATE({d '2014-09-14'}) ");
    self.output = ko.observable();

    // Operations
    self.activate = function(sql) {
        //deserialize from url
        if(sql) self.input(decodeURI(sql));
    };

    self.submitConsole = function(){
        self._serializeToUrl();
        return utils.json.get({
            url : './rest/v1.0/admin/' + (self.input()),
            success : function(data, status, xhr){
                self.output(JSON.stringify(data, null, 2));
                console.log(data);
            }
        });
    };
    self.executeConsole = function(){
        self._serializeToUrl();
        return utils.json.get({
            url : './rest/v1.0/admin/execute/' + (self.input()),
            success : function(data, status, xhr){
                self.output(JSON.stringify(data, null, 2));
                console.log(data);
            }
        });
    };
    self._serializeToUrl = function(){
        router.navigate('console/'+encodeURI(self.input()), { replace: true, trigger: false });
    };

    return self;
});