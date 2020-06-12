/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

var cordova = require('cordova');
var execProxy = require('cordova/exec/proxy');

module.exports = function (success, fail, service, action, args) {

    var proxy = execProxy.get(service, action);

    if (proxy) {
        var callbackId = service + cordova.callbackId++;

        if (typeof success == "function" || typeof fail == "function") {
            cordova.callbacks[callbackId] = {success: success, fail: fail};
        }

        try {
            proxy(success, fail, args);
        }
        catch (e) {
            // TODO throw maybe?
            var msg = "Exception calling :: " + service + " :: " + action + " ::exception=" + e;
            console.log(msg);
        }
    }
    else {
        fail && fail("Missing Command Error");
    }
};
