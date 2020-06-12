/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: 'browser',
    cordovaVersion: '3.4.0',

    bootstrap: function () {

        var moduleMapper = require('cordova/modulemapper');
        var channel = require('cordova/channel');

        moduleMapper.clobbers('cordova/exec/proxy', 'cordova.commandProxy');

        channel.onPluginsReady.subscribe(function () {
            channel.onNativeReady.fire();
        });

        // FIXME is this the right place to clobber pause/resume? I am guessing not
        // FIXME pause/resume should be deprecated IN CORDOVA for pagevisiblity api
        document.addEventListener('webkitvisibilitychange', function () {
            if (document.webkitHidden) {
                channel.onPause.fire();
            }
            else {
                channel.onResume.fire();
            }
        }, false);

        // End of bootstrap
    }
};
