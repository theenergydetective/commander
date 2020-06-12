/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: 'ios',
    bootstrap: function () {
        require('cordova/channel').onNativeReady.fire();
    }
};

