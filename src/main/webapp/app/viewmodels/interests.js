/**
 * Created by MAR on 12.2.2015.
 */
define(['knockout', 'utils'], function(ko, utils){
    var self = {};

    //data
    self.cardMovementsDay = ko.observableArray([]);
    self.cardMovementsWeek = ko.observableArray([]);

    //operations
    self.activate = function () {
        if(self.cardMovementsDay().length == 0) {
            self.fetchMovements();
        }
    };
    self.generateMovements = function() {
        utils.json.get({
            url : './rest/v1.0/cards/generate/movement',
            token : self.user().token,
            success : function(result) {
                self.fetchMovements();
            }
        });
    };
    self.fetchMovements = function() {
        utils.json.get({
            url : "./rest/v1.0/cards/movements/START_OF_WEEK",
            success : function(allData) {
                var initCardMovements = $.map(allData, function(item) {
                    return new CardMovement(item);
                });
                self.cardMovementsWeek(initCardMovements);
            }
        });
        utils.json.get({
            url : "./rest/v1.0/cards/movements/DAY",
            success : function(allData) {
                var initCardDayMovements = $.map(allData, function(item) {
                    return new CardMovement(item);
                });
                self.cardMovementsDay(initCardDayMovements);
            }
        });
    };

    return self;
});
