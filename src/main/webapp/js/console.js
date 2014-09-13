function ConsoleViewModel() {
    var self = this;
    self.submitConsole = function(){
        console.log("submit console");
        console.log(arguments);
    };
}

ko.applyBindings(new ConsoleViewModel());