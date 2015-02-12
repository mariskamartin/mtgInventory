define(['plugins/router', 'bootstrap'], function (router, bootstrap) {
    return {
        router: router,
        bootstrap: bootstrap, //need to be included because of colapsing navbar
        activate: function () {
            router.map([
                { route: '', title:'Home', moduleId: 'viewmodels/home', nav: true },
                { route: 'detail', title:'Detail', moduleId: 'viewmodels/detail', nav: true },
                { route: 'interests', title:'Interests', moduleId: 'viewmodels/interests', nav: true }
            ]).buildNavigationModel(0);
            
            return router.activate();
        },
        logoutUser : function(){
            console.log("logout user");
        }
    };
});