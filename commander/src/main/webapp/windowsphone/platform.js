/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: 'windowsphone',
    bootstrap: function () {
        var cordova = require('cordova'),
            exec = require('cordova/exec');

        // Inject a listener for the backbutton, and tell native to override the flag (true/false) when we have 1 or more, or 0, listeners
        var backButtonChannel = cordova.addDocumentEventHandler('backbutton');
        backButtonChannel.onHasSubscribersChange = function () {
            exec(null, null, "CoreEvents", "overridebackbutton", [this.numHandlers == 1]);
        };
    }
};
