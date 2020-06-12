/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

//var firefoxos = require('cordova/platform');
var cordova = require('cordova');
var execProxy = require('cordova/exec/proxy');

module.exports = function (success, fail, service, action, args) {
    var proxy = execProxy.get(service, action);
    if (proxy) {
        var callbackId = service + cordova.callbackId++;
        //console.log("EXEC:" + service + " : " + action);
        if (typeof success == "function" || typeof fail == "function") {
            cordova.callbacks[callbackId] = {success: success, fail: fail};
        }
        try {
            proxy(success, fail, args);
        }
        catch (e) {
            console.log("Exception calling native with command :: " + service + " :: " + action + " ::exception=" + e);
        }
    }
    else {
        fail && fail("Missing Command Error");
    }
};
