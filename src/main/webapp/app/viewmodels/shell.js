define(['plugins/router', 'bootstrap', 'durandal/app', 'viewmodels/user'], function (router, bootstrap, app, user) {
    //need to be included because of colapsing navbar
    return {
        router: router,
        app: app,
        user: user,

        activate: function () {
            router.map([
                { route: '', title:'Home', moduleId: 'viewmodels/home', nav: true },
                { route: 'detail/:cardId', title:'Detail', moduleId: 'viewmodels/detail', hash:'#detail', nav: true },
                { route: 'interests', title:'Interests', moduleId: 'viewmodels/interests', nav: true },
                { route: 'not-found', title:'Not Found', moduleId: 'viewmodels/not-found', nav: false },
                { route: 'console', title:'Console', moduleId: 'viewmodels/console', nav: false },
                { route: 'log', title:'Log', moduleId: 'viewmodels/log', nav: false }
            ]).buildNavigationModel(0)
                .mapUnknownRoutes('viewmodels/not-found', 'url-not-found'); //this redirect unknown url calling to home

            return router.activate();
        }
    };
});