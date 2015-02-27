var APP_VERSION = 'v2.1.1';

requirejs.config({
    urlArgs: "version=" + APP_VERSION, //this is ok for cache (changes with new version)
    paths: {
        'text': '../lib/require/text',
        'store': '../lib/store/store2.min',
        'durandal':'../lib/durandal/js',
        'plugins' : '../lib/durandal/js/plugins',
        'knockout': '../lib/knockout/knockout-3.2.0',
        'bootstrap': '../lib/bootstrap/js/bootstrap.min',
        'jquery': '../lib/jquery/jquery-1.9.1',
        'alertify': '../lib/alertify/alertify.min',
        'd3': '../lib/c3/d3.min',
        'c3': '../lib/c3/c3.min'
    },
    shim: {
        'bootstrap': {
            deps: ['jquery'],
            exports: 'jQuery'
        },
        /**
         * Chart framework C3 based on D3
         */
        'c3': {
            deps: ['d3'],
            exports: 'd3'
        }
    }
});

define(['durandal/system', 'durandal/app', 'durandal/viewLocator'],  function (system, app, viewLocator) {
    system.debug(false); //set false for production

    //inject data to app
    app.title = 'MTG Inventory';
    app.version = APP_VERSION;

    app.configurePlugins({
        router:true,
        dialog: true
    });

    app.start().then(function() {
        //Replace 'viewmodels' in the moduleId with 'views' to locate the view.
        //Look for partial views in a 'views' folder in the root.
        viewLocator.useConvention();

        //Show the app by setting the root view model for our application with a transition.
        app.setRoot('viewmodels/shell');
    });
});