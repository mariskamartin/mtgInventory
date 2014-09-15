function ConsoleViewModel() {
    var self = this;
    
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
}

ko.applyBindings(new ConsoleViewModel());