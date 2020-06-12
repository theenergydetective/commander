/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

var cordova = require('cordova'),
    utils = require('cordova/utils');

var callbackId = 1;
cordova.callbacks = [];

cordova.callback = function () {
    var scId = arguments[0];
    var callbackRef = null;

    var parameters = [];
    for (var i = 1; i < arguments.length; i++) {
        parameters[i - 1] = arguments[i];
    }
    callbackRef = cordova.callbacks[scId];

    // Even IDs are success-, odd are error-callbacks - make sure we remove both
    if ((scId % 2) !== 0) {
        scId = scId - 1;
    }
    // Remove both the success as well as the error callback from the stack
    delete cordova.callbacks[scId];
    delete cordova.callbacks[scId + 1];

    if (typeof callbackRef == "function") callbackRef.apply(this, parameters);
};

cordova.callbackWithoutRemove = function () {
    var scId = arguments[0];
    var callbackRef = null;

    var parameters = [];
    for (var i = 1; i < arguments.length; i++) {
        parameters[i - 1] = arguments[i];
    }
    callbackRef = cordova.callbacks[scId];

    if (typeof(callbackRef) == "function") callbackRef.apply(this, parameters);
};

function ubuntuExec(success, fail, service, action, args) {
    if (callbackId % 2) {
        callbackId++;
    }

    var scId = callbackId++;
    var ecId = callbackId++;
    cordova.callbacks[scId] = success;
    cordova.callbacks[ecId] = fail;

    args.unshift(ecId);
    args.unshift(scId);

    navigator.qt.postMessage(JSON.stringify({messageType: "callPluginFunction", plugin: service, func: action, params: args}));
}
module.exports = ubuntuExec;
