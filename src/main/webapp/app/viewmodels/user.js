/**
 * Created by MAR on 13.2.2015.
 * Module for User logic
 */
define(['store', 'knockout', 'utils'], function (store, ko, utils) {
    var USER_STORE = "props";
    var USER_LOGIN = "login";
    var self = {};
    self.userLocalStore = store.namespace("user");
    self.userStore = store.session.namespace("user");
    var savedLoginData = self.userLocalStore.get(USER_LOGIN) || {};

    //registration form
    self.signEmail = ko.observable();
    self.signName = ko.observable();
    self.signPwd = ko.observable();
    self.signPwdCheck = ko.observable();
    self.signPwdInputClass = ko.observable("form-group");
    // login form data
    self.loginEmail = ko.observable(savedLoginData.login);
    self.loginPwd = ko.observable(savedLoginData.pwd);
    self.loginRemember = ko.observable(savedLoginData.login != null || false);
    // user data
    self.user = ko.observable(store.get(USER_STORE) || self.userStore.get(USER_STORE) || User.EMPTY);
    self.isLogged = ko.computed(function(){
        return self.user().idEmail != null;
    });
    self.isNotLogged = ko.computed(function(){
        return !self.isLogged;
    });

    // subscribtions
    self.loginRemember.subscribe(function saveUserLoginDataToLocalStore(newValue) {
        if (newValue) {
            self.userLocalStore.set(USER_LOGIN, {
                login: self.loginEmail(),
                pwd: self.loginPwd()
            });
        } else {
            self.userLocalStore.clear(USER_LOGIN);
        }
    });

    // Operations
    self.loginUser = function() {
        self.authUser(self.loginEmail(), self.loginPwd()).done(function(result) {
            self.user(new User(result));
            self.userStore.set(USER_STORE, ko.toJS(self.user));
        });
    };
    self.logoutUser = function() {
        self.userStore.clear(USER_STORE);
        self.user(User.EMPTY);
    };
    self.savePropsUser = function() {
        console.log([ "save user props", ko.toJS(self.user()) ]);
        // self.updateUser(ko.toJS(self.user()));
    };
    self.registerUser = function() {
        if(self.signPwd() != self.signPwdCheck()){
            self.signPwdInputClass("form-group has-error");
            return;
        }
        self.signPwdInputClass("form-group");

//        alert("Zatím, není možné se registrovat. Registrace bude spuštěna v brzké době. Zatím mají přístup pouze uživatelé zařazeni mezi Beta testery.");
        self.addUser(self.signEmail(), self.signPwd(), self.signName(), utils.uuid(), ["USER"])
            .done(function(response){
                console.log(response);
                self.loginEmail(response.idEmail);
                self.loginPwd(response.password);
                self.loginUser();
            });
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
        return utils.json.post({
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
        utils.json.del({
            url : './rest/v1.0/users/' + emailId,
            token : self.user().token,
            success : function(result) {
                console.log([ "deletedUser", result ]);
            }
        });
    };


    return self;
});

function User(pojo) {
    var self = this;
    self.idEmail = pojo.idEmail;
    self.version = pojo.version;
    self.token = pojo.token;
    self.roles = pojo.roles;
    self.name = pojo.name;
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