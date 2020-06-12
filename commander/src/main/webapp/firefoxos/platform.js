/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: 'firefoxos',
    cordovaVersion: '3.0.0',

    bootstrap: function () {
        require('cordova/modulemapper').clobbers('cordova/exec/proxy', 'cordova.commandProxy');
        require('cordova/channel').onNativeReady.fire();
    }
};
