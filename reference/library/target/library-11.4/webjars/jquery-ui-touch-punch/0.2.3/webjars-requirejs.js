/*global requirejs */

// Ensure any request for this webjar brings in dependencies. For example if this webjar contains
// bootstrap.js which depends on jQuery then you would have the following configuration.
//
requirejs.config({
    paths: {
        'jquery-ui-touch-punch' : webjars.path('jquery-ui-touch-punch', 'jquery.ui.touch-punch') 
    },
    shim: {
        'jquery-ui-touch-punch': [ 'webjars!jquery-ui.js' ]
    }
});
