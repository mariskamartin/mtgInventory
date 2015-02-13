/**
 * Created by MAR on 12.2.2015.
 */
define(['viewmodels/home','knockout', 'utils', 'd3','c3'], function (home, ko, utils, d3, c3) {
    var self = this;

    // Data
    self.cardMovementsDay = ko.observableArray([]);
    self.cardMovementsWeek = ko.observableArray([]);
    self.cardDetail = ko.observable(Card.EMPTY);

    // Operations
    self.activate = function(cardId) {
        self.getCard(cardId).done(function(result){
            //TODO insert ko throu extending this pojo
            self.setCardDetail(new Card(result, ko));
        });
        //new sycnhronization function for draw chart (load DOM and load DATA)
        self.synchronizeOnDrawChart = utils.after(2, self.drawChart);
    };

    self.attached = function(view, parent) {
        //create chart
        //TODO try if DOM stays hidden
        self.chart = c3.generate({
            bindto: "#prizeChart",
            data : {
                xs : {},
                columns : []
            },
            axis : {
                x : {
                    type : 'timeseries',
                    tick : {
                        format : '%d.%m.%Y'
                    }
                }
            },
            transition: {
                duration: 150
            }
        });
        self.synchronizeOnDrawChart();
    };
    self.getCard = function(id) {
        return utils.json.get({
            url : './rest/v1.0/cards/' + id
        });
    };
    self.fetchallManagedCards = function() {
        console.log("fetch all cards");
        utils.json.get({
            url : './rest/v1.0/cards/fetch/',
            token : self.user().token,
            success : function(result) {
                console.log(result);
            }
        });
    };
    self.removeCard = function(cardId) {
        return utils.json.del({
            url : './rest/v1.0/cards/' + cardId,
            token : self.user().token,
            success : function(result) {
                self.cards.remove(function(item) {
                    return item.id === result.id;
                });
            }
        });
    };
    self.banCardName = function(cardName) {
        return utils.json.post({
            url : './rest/v1.0/cards/ban',
            token : self.user().token,
            dataJs : {
                idBannedName : cardName
            }
        });
    };
    //movements
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
    //card detail
    self.populateCardDetailFromMovement = function(movement) {
        document.location = MY_INVENOTORY_PAGES.DETAIL+"/"+movement.cardPojo.id;
    };
    self.populateCardDetailFromTable = function(cardPojo) {
        document.location = MY_INVENOTORY_PAGES.DETAIL+"/"+cardPojo.id;
    };
    self.setCardDetail = function(card){
        self.cardDetail(card);
        self.populateCardDetail(card);
    };
    self.populateCardDetail = function(card) {
        utils.json.get({
            url : './rest/v1.0/cards/dailyinfo/' + card.id,
            success : function(result) {
                self.cardDetail(card);
                self.chartData = {};
                var data = self.chartData;
                result.forEach(function(item) {
                    data[item.shop] = data[item.shop] || {
                        x : [ 'x'+item.shop ],
                        xs : {},
                        values : [ item.shop ]
                    };
                    data[item.shop].xs[item.shop] = data[item.shop].x[0];
                    data[item.shop].x.push(item.dayTxt);
                    data[item.shop].values.push(item.price);
                    data[item.shop].storeDay = item.dayTxt;
                    data[item.shop].storeAmount = item.storeAmount;
                });
                var txtSkladem = self.cardDetail().storeAmount() ? "<br />" : "";
                for ( var shop in data) {
                    txtSkladem = txtSkladem ? txtSkladem + "<br />" : txtSkladem;
                    txtSkladem = txtSkladem + data[shop].storeDay + " - " + shop + " - " + data[shop].storeAmount + " ks";
                }
                self.cardDetail().storeAmount(txtSkladem);
                self.synchronizeOnDrawChart();
            }
        });
    };
    /**
     * Draws chart when data and DOM is ready and data is loaded
     */
    self.drawChart = function(){
        var data = self.chartData;
        for ( var shop in data ) {
            self.chart.load({
                xs : data[shop].xs,
                columns : [ data[shop].x, data[shop].values ]
            });
        }
    };
    return self;
});

function CardMovement(pojo) {
    var self = this;
    self.cardPojo = pojo.card;
    self.name = pojo.card.name;
    self.rarity = pojo.card.rarity;
    self.edition = pojo.card.edition;
    self.shop = pojo.shop;
    self.gainPrice = pojo.gainPrice;
    self.gainPercentage = pojo.gainPercentage > 0 ? "+" + pojo.gainPercentage.toFixed(2) + " %" : pojo.gainPercentage
        .toFixed(2)
    + " %";
    self.oldPrice = pojo.oldPrice;
    self.newPrice = pojo.newPrice;
    // others helps
    self.hrefDetail = MY_INVENOTORY_PAGES.DETAIL+"/" + pojo.card.id;
    self.gainStatus = pojo.gainPercentage > 0 ? "success" : "danger";
    self.foilImg = (pojo.card.foil ? " " + utils.icons.star : "");
    self.info = self.edition;
}