/**
 * Created by MAR on 13.2.2015.
 */
define(['knockout', 'utils'], function (ko, utils) {
    var self = {};

    self.input = ko.observable("SELECT c FROM Card c WHERE  c.created = DATE({d '2014-09-14'}) ");
    self.output = ko.observable();

    self.submitConsole = function(){
        return utils.json.get({
            url : './rest/v1.0/admin/' + (self.input()),
            success : function(data, status, xhr){
                self.output(JSON.stringify(data, null, 2));
                console.log(data);
            }
        });
    };
    self.executeConsole = function(){
        return utils.json.get({
            url : './rest/v1.0/admin/execute/' + (self.input()),
            success : function(data, status, xhr){
                self.output(JSON.stringify(data, null, 2));
                console.log(data);
            }
        });
    };

    return self;
});