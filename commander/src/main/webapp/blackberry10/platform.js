/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {

    id: "blackberry10",

    bootstrap: function () {

        var channel = require('cordova/channel'),
            addEventListener = document.addEventListener;

        //ready as soon as the plugins are
        channel.onPluginsReady.subscribe(function () {
            channel.onNativeReady.fire();
        });

        //pass document online/offline event listeners to window
        document.addEventListener = function (type) {
            if (type === "online" || type === "offline") {
                window.addEventListener.apply(window, arguments);
            } else {
                addEventListener.apply(document, arguments);
            }
        };

        //map blackberry.event to document
        if (!window.blackberry) {
            window.blackberry = {};
        }
        window.blackberry.event =
        {
            addEventListener: document.addEventListener,
            removeEventListener: document.removeEventListener
        };

    }

};
