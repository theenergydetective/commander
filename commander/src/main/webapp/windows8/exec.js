/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

/*jslint sloppy:true, plusplus:true*/
/*global require, module, console */

var cordova = require('cordova');
var execProxy = require('cordova/exec/proxy');

/**
 * Execute a cordova command.  It is up to the native side whether this action
 * is synchronous or asynchronous.  The native side can return:
 *      Synchronous: PluginResult object as a JSON string
 *      Asynchronous: Empty string ""
 * If async, the native side will cordova.callbackSuccess or cordova.callbackError,
 * depending upon the result of the action.
 *
 * @param {Function} success    The success callback
 * @param {Function} fail       The fail callback
 * @param {String} service      The name of the service to use
 * @param {String} action       Action to be run in cordova
 * @param {String[]} [args]     Zero or more arguments to pass to the method
 */
module.exports = function (success, fail, service, action, args) {

    var proxy = execProxy.get(service, action),
        callbackId,
        onSuccess,
        onError;

    if (proxy) {
        callbackId = service + cordova.callbackId++;
        // console.log("EXEC:" + service + " : " + action);
        if (typeof success === "function" || typeof fail === "function") {
            cordova.callbacks[callbackId] = {success: success, fail: fail};
        }
        try {
            // callbackOptions param represents additional optional parameters command could pass cancel, like keepCallback or
            // custom callbackId, for example {callbackId: id, keepCallback: true, status: cordova.callbackStatus.JSON_EXCEPTION }
            // CB-5806 [Windows8] Add keepCallback support to proxy
            onSuccess = function (result, callbackOptions) {
                callbackOptions = callbackOptions || {};
                cordova.callbackSuccess(callbackOptions.callbackId || callbackId,
                    {
                        status: callbackOptions.status || cordova.callbackStatus.OK,
                        message: result,
                        keepCallback: callbackOptions.keepCallback || false
                    });
            };
            onError = function (err, callbackOptions) {
                callbackOptions = callbackOptions || {};
                cordova.callbackError(callbackOptions.callbackId || callbackId,
                    {
                        status: callbackOptions.status || cordova.callbackStatus.ERROR,
                        message: err,
                        keepCallback: callbackOptions.keepCallback || false
                    });
            };
            proxy(onSuccess, onError, args);

        } catch (e) {
            console.log("Exception calling native with command :: " + service + " :: " + action + " ::exception=" + e);
        }
    } else {
        if (typeof fail === "function") {
            fail("Missing Command Error");
        }
    }
};
