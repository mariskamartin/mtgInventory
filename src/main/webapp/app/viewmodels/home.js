/**
 * Created by MAR on 12.2.2015.
 */
define(['knockout', 'utils', 'viewmodels/user'], function (ko, utils, userVM) {
    var self = {};

    self.activate = function () {

    };

    // Data
    self.user = userVM.user;
    self.newText = ko.observable();
    self.cards = ko.observableArray([]);

    // Operations
    self.addUserCardFromTable = function(card, event) {
        console.log(["usercard", card]);
        //nekontroluje to duplicitu >/
        utils.json.post({
            url : './rest/v1.0/cards/user/' + self.user().idEmail + '/add/'+card.id,
            token : self.user().token
        }).done(function(data){
            console.log(data);
            utils.msg.success("Card saved.");
        });
    };
    self.getUserCards = function() {
        utils.json.get({
            url : './rest/v1.0/cards/user/' + self.user().idEmail,
            token : self.user().token,
            success : function(result) {
                console.log(["user cards", result]);
            }
        });
    };
    self.findCardInForm = function() {
        self.findCard(self.newText());
    };
    self.findCard = function(text) {
        utils.json.get({
            url : './rest/v1.0/cards/find/' + text,
            success : function(result) {
                var results = [];
                result.forEach(function(item) {
                    results.push(new Card(item, ko));
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
                    self.cards.push(new Card(item, ko));
                });
            }
        });
    };

    return self;
});

// TODO: prestehovat do defaults
var ICONS = {
    star: "<span class=\"glyphicon glyphicon-star\" title=\"Foil version of card\"></span>",
        plus: "<span class=\"glyphicon glyphicon-plus\"></span>",
        watch: "<span class=\"glyphicon glyphicon-eye-open\"></span>",
        unwatch: "<span class=\"glyphicon glyphicon-eye-close\" title=\"You are watching card state, on store, change price\"></span>"
};


//TODO: prdelat na spravne adresy z routeru
MY_INVENOTORY_PAGES = {
    HOME: "#home",
    INTERESTS : "#interests",
    DETAIL : "#detail"
};

function Card(pojo, ko) {
    var self = this;
    self.id = pojo.id;
    self.name = pojo.name;
    self.rarity = pojo.rarity;
    self.edition = pojo.edition;
    self.foil = pojo.foil;
    self.storeAmount = ko ? ko.observable(pojo.storeAmount) : pojo.storeAmount;
    // others helps
    self.hrefDetail = MY_INVENOTORY_PAGES.DETAIL+"/" + pojo.id;
    self.editionKey = pojo.editionKey;
    self.foilTxt = pojo.foil ? "FOIL" : "";
    self.foilImg = (pojo.foil ? " " + ICONS.star : "");
    self.crLink = "<a href=\"javascript:utils.links.openCernyRytir('" + this.name + "');\">Černý Rytíř</a>";
    self.tolarieLink = "<a href=\"javascript:utils.links.openTolarie('" + this.name + "');\">Tolarie</a>";
    self.magicCardsLink = "<a href=\"javascript:utils.links.openMagicCards('" + this.name + "');\">MagicCards</a>";
    if (self.name && self.name !== "UNKNOWN") {
        self.img = "<img src='http://cdn.manaclash.com/images/cards/210x297/" + this.editionKey + "/"
        + this.name.replace(/ /g, "-").replace(/[,'´]/g, "").toLowerCase() + ".jpg' class='img-thumbnail'></img>";
    }
}

Card.EMPTY = new Card({
    id : 0,
    name : "UNKNOWN",
    price : 0,
    storeAmount : "",
    rarity : "UNKNOWN",
    edition : "UNKNOWN",
    editionKey : "UNKNOWN",
    foil : false
});
