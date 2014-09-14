function User(pojo) {
    var self = this;
    self.idEmail = pojo.idEmail;
    self.version = pojo.version;
    self.token = pojo.token;
    self.roles = pojo.roles;
    self.name = ko.observable(pojo.name);
    // helpers
    self.isAdmin = (pojo.roles && (pojo.roles.indexOf(User.ROLES.ADMIN) >= 0)) || false;
}

User.ROLES = {
    ADMIN : "ADMIN",
    VIP : "VIP"
};

User.EMPTY = new User({
    name : "unknown name"
});

MY_INVENOTORY_PAGES = {
    HOME: "#/home",
    INTERESTS : "#/interests",
    DETAIL : "#/detail"
};


function Card(pojo) {
    var self = this;
    self.id = pojo.id;
    self.name = pojo.name;
    self.rarity = pojo.rarity;
    self.edition = pojo.edition;
    self.foil = pojo.foil;
    self.storeAmount = ko.observable(pojo.storeAmount);
    // others helps
    self.hrefDetail = MY_INVENOTORY_PAGES.DETAIL+"/" + pojo.id;
    self.editionKey = pojo.editionKey;
    self.foilTxt = pojo.foil ? "FOIL" : "";
    self.crLink = "<a href=\"javascript:utils.links.openCernyRytir('" + this.name + "');\">Černý Rytíř</a>";
    self.tolarieLink = "<a href=\"javascript:utils.links.openTolarie('" + this.name + "');\">Tolarie</a>";
    self.magicCardsLink = "<a href=\"javascript:utils.links.openMagicCards('" + this.name + "');\">MagicCards</a>";
    if (self.name && self.name !== "UNKNOWN") {
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
    self.hrefDetail = MY_INVENOTORY_PAGES.DETAIL+"/" + pojo.card.id;
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

/**
 * ----------------------------------------------------------------------------------- User ViewModel
 */
function UserViewModel() {
    var USER_STORE = "props";
    var self = this;
    self.userStore = store.session.namespace("user");

    // Data
    self.loginEmail = ko.observable();
    self.loginPwd = ko.observable();
    self.user = ko.observable(store.get(USER_STORE) || self.userStore.get(USER_STORE) || User.EMPTY);

    // Operations
    self.loginUser = function() {
        self.authUser(self.loginEmail(), self.loginPwd()).done(function(result) {
            self.user(new User(result));
            self.userStore.set(USER_STORE, ko.toJS(self.user));
        });
    };
    self.logoutUser = function() {
        self.userStore.clear(USER_STORE);
        store.clear(USER_STORE);
        self.user(User.EMPTY);
    };
    self.savePropsUser = function() {
        console.log([ "save user props", ko.toJS(self.user()) ]);
        // self.updateUser(ko.toJS(self.user()));
    };
    self.registerUser = function() {
        alert("Zatím, není možné se registrovat. Registrace bude spuštěna v brzké době. Zatím mají přístup pouze uživatelé zařazeni mezi Beta testery.");
    };
    self.authUser = function(login, pwd) {
        return utils.json.post({
            url : './rest/v1.0/users/authenticate/',
            dataJs : {
                loginEmail : login,
                password : pwd
            }
        });
    };
    self.addUser = function(email, pwd, name, token, roles) {
        utils.json.post({
            url : './rest/v1.0/users/',
            dataJs : {
                idEmail : email,
                password : pwd,
                name : name || "",
                token : token || (email + "-token"),
                roles : roles || []
            },
            success : function(result) {
                console.log([ "addUser", result ]);
            }
        });

    };
    self.updateUser = function(user) {
        utils.json.put({
            url : './rest/v1.0/users/',
            dataJs : user,
            token : self.user().token,
            success : function(result) {
                console.log([ "updatedUser", result ]);
            }
        });
    };
    self.deleteUser = function(emailId) {
        utils.json.post({
            url : './rest/v1.0/users/' + emailId,
            token : self.user().token,
            success : function(result) {
                console.log([ "deletedUser", result ]);
            }
        });
    };
};

/**
 * Inventory ViewModel
 */
function InventoryViewModel() {
    var self = this;

    // Data
    self.newText = ko.observable();
    self.activePage = ko.observable();
    self.cards = ko.observableArray([]);
    self.cardMovementsDay = ko.observableArray([]);
    self.cardMovementsWeek = ko.observableArray([]);
    self.cardDetail = ko.observable(Card.EMPTY);

    self.pages = MY_INVENOTORY_PAGES;
    
    // Operations
    self.findCardInForm = function() {
        self.findCard(self.newText());
    };
    self.findCard = function(text) {
        utils.json.get({
            url : './rest/v1.0/cards/find/' + text,
            success : function(result) {
                var results = [];
                result.forEach(function(item) {
                    results.push(new Card(item));
                });
                self.cards(results);
            }
        });
    };
    self.getCard = function(id) {
        return utils.json.get({
            url : './rest/v1.0/cards/' + id
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
//    utils.json.get({
//        url : "./rest/v1.0/cards",
//        success : function(allData) {
//            var initCards = $.map(allData, function(item) {
//                return new Card(item);
//            });
//            self.cards(initCards);
//        }
//    });

}

// -------------------------------------------------------------------------------------------
/**
 * Main application object
 */
var myInventory = {
    /**
     * All viewModels
     */
    viewModels : {
        inventory : new InventoryViewModel(),
        user : new UserViewModel()
    },
    /**
     * Routovani aplikace na in-pages
     */
    routes : [ {
        url : MY_INVENOTORY_PAGES.HOME,
        root : true,
        action : function() {
            //load or start for home
            myInventory.viewModels.inventory.activePage(MY_INVENOTORY_PAGES.HOME);
        }
    }, {
        url : MY_INVENOTORY_PAGES.INTERESTS,
        action : function() {
            //load or start for interests
            myInventory.viewModels.inventory.activePage(MY_INVENOTORY_PAGES.INTERESTS);
            myInventory.viewModels.inventory.fetchMovements();
        }
    }, {
        url : MY_INVENOTORY_PAGES.DETAIL + "(/:card_id)(/:action)",
        action : function() {
            //load or start for interests
            myInventory.viewModels.inventory.activePage(MY_INVENOTORY_PAGES.DETAIL);
            if(this.params["card_id"] && !this.params["action"]){
                myInventory.viewModels.inventory.getCard(this.params["card_id"]).done(function(result){
                    myInventory.viewModels.inventory.setCardDetail(new Card(result));
                });
            } else if(this.params["card_id"] && this.params["action"] && this.params["action"] === "delete"){
                myInventory.viewModels.inventory.removeCard(this.params["card_id"]);
            }
        }
    } ],
    
    start : function(){
        // Knockout bindings
        var homePageViewModel = utils.extend(this.viewModels.inventory, this.viewModels.user);
        ko.applyBindings(homePageViewModel);
        // Prepare Routing
        this.routes.forEach(function(item) {
            Path.map(item.url).to(item.action);
            if (item.root)
                Path.root(item.url);
        });
        Path.listen();
    }
};

//start with document ready
$(document).ready(function() {
    myInventory.start();
});

// -------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------
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
