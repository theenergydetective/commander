/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: "ubuntu",
    bootstrap: function () {
        var channel = require("cordova/channel"),
            cordova = require('cordova'),
            exec = require('cordova/exec'),
            modulemapper = require('cordova/modulemapper');

        modulemapper.clobbers('cordova/exec/proxy', 'cordova.commandProxy');
        require('cordova/channel').onNativeReady.fire();
    }
};
