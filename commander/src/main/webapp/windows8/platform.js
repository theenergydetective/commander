/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: 'windows8',
    bootstrap: function () {
        var cordova = require('cordova'),
            exec = require('cordova/exec'),
            channel = cordova.require('cordova/channel'),
            modulemapper = require('cordova/modulemapper');

        modulemapper.clobbers('cordova/exec/proxy', 'cordova.commandProxy');
        channel.onNativeReady.fire();

        var onWinJSReady = function () {
            var app = WinJS.Application;
            var checkpointHandler = function checkpointHandler() {
                cordova.fireDocumentEvent('pause', null, true);
            };

            var resumingHandler = function resumingHandler() {
                cordova.fireDocumentEvent('resume', null, true);
            };

            app.addEventListener("checkpoint", checkpointHandler);
            Windows.UI.WebUI.WebUIApplication.addEventListener("resuming", resumingHandler, false);
            app.start();

        };

        if (!window.WinJS) {
            // <script src="//Microsoft.WinJS.1.0/js/base.js"></script>
            var scriptElem = document.createElement("script");
            if (navigator.appVersion.indexOf("MSAppHost/2.0;") > -1) {
                // windows 8.1 + IE 11
                scriptElem.src = "//Microsoft.WinJS.2.0/js/base.js";
            }
            else {
                // windows 8.0 + IE 10
                scriptElem.src = "//Microsoft.WinJS.1.0/js/base.js";
            }
            scriptElem.addEventListener("load", onWinJSReady);
            document.head.appendChild(scriptElem);
        }
        else {
            onWinJSReady();
        }
    }
};
