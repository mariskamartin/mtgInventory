function User(pojo) {
    var self = this;
    self.id = pojo.id;
    self.token = pojo.token;
    self.name = ko.observable(pojo.name);
}

function Card(pojo) {
    var self = this;
    self.id = pojo.id;
    self.name = pojo.name;
    self.rarity = pojo.rarity;
    self.edition = pojo.edition;
    self.foil = pojo.foil;
    self.storeAmount = ko.observable(pojo.storeAmount);
    // others helps
    self.editionKey = pojo.editionKey;
    self.foilTxt = pojo.foil ? "FOIL" : "";
    self.crLink = "<a href=\"javascript:utils.links.openCernyRytir('" + this.name + "');\">Černý Rytíř</a>";
    self.tolarieLink = "<a href=\"javascript:utils.links.openTolarie('" + this.name + "');\">Tolarie</a>";
    self.magicCardsLink = "<a href=\"javascript:utils.links.openMagicCards('" + this.name + "');\">MagicCards</a>";
    if (self.name !== "UNKNOWN") {
        self.img = "<img src='http://cdn.manaclash.com/images/cards/210x297/" + this.editionKey + "/"
            + this.name.replace(/ /g, "-").replace(/[,'´]/g, "").toLowerCase() + ".jpg' class='img-thumbnail'></img>";
    }
}

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
    self.gainStatus = pojo.gainPercentage > 0 ? "success" : "danger";
    self.info = self.edition + (pojo.card.foil ? " " + utils.icons.star : "");
}

Card.EMPTY = new Card({
    id : 0,
    name : "UNKNOWN",
    price : 0,
    storeAmount : "",
    rarity : "UNKNOWN",
    edition : "UNKNOWN",
    editionKey : "UNKNOWN",
    foil : false,
});

// Overall viewmodel for this screen, along with initial state
function InventoryViewModel() {
    var self = this;

    // Login Data
    self.loginEmail = ko.observable();
    self.loginPwd = ko.observable();
    self.user = ko.observable(null);
    // Data
    self.newText = ko.observable();
    self.cards = ko.observableArray([]);
    self.cardMovementsDay = ko.observableArray([]);
    self.cardMovementsWeek = ko.observableArray([]);
    self.cardDetail = ko.observable(Card.EMPTY);

    // Operations
    self.loginUser = function() {
        console.log([ "login", self.loginEmail(), self.loginPwd() ]);
        self.user(new User({
            id : "das",
            token : "d654da6s54da6s5d4a6s5d4",
            name : "Martin M."
        }));
    };
    self.addCard = function() {
        // send to server and response update to VM
        // utils.json.post({
        // url : './rest/v1.0/cards/',
        // dataJs : {
        // id : "",
        // name : self.newText(),
        // edition : "MAGIC_2015",
        // rarity : "COMMON"
        // },
        // success : function(result) {
        // self.cards.push(new Card(result));
        // }
        // });
    };
    self.removeCard = function(card) {
        utils.json.del({
            url : './rest/v1.0/cards/' + card.id,
            success : function(result) {
                self.cards.remove(function(item) {
                    return item.id === result.id;
                });
            }
        });
    };
    self.fetchCard = function() {
        utils.json.get({
            url : './rest/v1.0/cards/fetch/' + self.newText(),
            success : function(result) {
                result.forEach(function(item) {
                    self.cards.push(new Card(item));
                });
            }
        });
    };
    self.fetchallManagedCards = function() {
        utils.json.get({
            url : './rest/v1.0/cards/fetch/',
            success : function(result) {
                console.log(result);
            }
        });
    };
    self.generateMovements = function() {
        utils.json.get({
            url : './rest/v1.0/cards/generate/movement',
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
    self.populateCardDetailFromMovement = function(movement) {
        console.log(movement);
        self.populateCardDetail(new Card(movement.cardPojo));
    };
    self.populateCardDetail = function(card) {
        utils.json.get({
            url : './rest/v1.0/cards/dailyinfo/' + card.id,
            success : function(result) {
                self.cardDetail(card);
                var data = {};
                result.forEach(function(item) {
                    data[item.shop] = data[item.shop] || {
                        x : [ 'x' ],
                        values : [ item.shop ]
                    };
                    data[item.shop].x.push(item.dayTxt);
                    data[item.shop].values.push(item.price);
                    data[item.shop].storeDay = item.dayTxt;
                    data[item.shop].storeAmount = item.storeAmount;
                });
                chart.unload();
                setTimeout(function() {
                    var txtSkladem = self.cardDetail().storeAmount() ? "<br />" : "";
                    for ( var shop in data) {
                        txtSkladem = txtSkladem ? txtSkladem + "<br />" : txtSkladem;
                        txtSkladem = txtSkladem + data[shop].storeDay + " - " + shop + " - " + data[shop].storeAmount
                            + " ks";

                        chart.load({
                            columns : [ data[shop].x, data[shop].values ]
                        });
                    }
                    self.cardDetail().storeAmount(txtSkladem);
                }, 1000);

                document.getElementById("cardDetail").scrollIntoView(true);
            }
        });
    };

    // Load initial state from server
    utils.json.get({
        url : "./rest/v1.0/cards",
        success : function(allData) {
            var initCards = $.map(allData, function(item) {
                return new Card(item);
            });
            self.cards(initCards);
        }
    });
    self.fetchMovements();

}
/**
 * Main application object
 */
var myInventory = {
    /**
     * All viewModels
     */
    viewModels : {
        inventory : new InventoryViewModel()
    },
    authUser : function(login, pwd) {
        utils.json.post({
            url : './rest/v1.0/users/authenticate/',
            dataJs : {
                loginEmail : login,
                password : pwd
            },
            success : function(result) {
                console.log([ "authUser", result ]);
            }
        });
    },
    addUser : function(email, pwd, name, token) {
        utils.json.post({
            url : './rest/v1.0/users/',
            dataJs : {
                idEmail : email,
                password : pwd,
                name : name || "",
                token : token || (email+"-token")
            },
            success : function(result) {
                console.log([ "addUser", result ]);
            }
        });

    }
};

// Knockout bindings
ko.applyBindings(myInventory.viewModels.inventory);

// Others
var chart = c3.generate({
    data : {
        x : 'x',
        columns : []
    },
    axis : {
        x : {
            type : 'timeseries',
            tick : {
                format : '%d.%m.%Y'
            }
        }
    }
});
