/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

/**
 * Creates a gap bridge used to notify the native code about commands.

 * @private
 */
var cordova = require('cordova'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    base64 = require('cordova/base64');


function massageMessageNativeToJs(message) {
    if (message.CDVType == 'ArrayBuffer') {
        var stringToArrayBuffer = function (str) {
            var ret = new Uint8Array(str.length);
            for (var i = 0; i < str.length; i++) {
                ret[i] = str.charCodeAt(i);
            }
            return ret.buffer;
        };
        var base64ToArrayBuffer = function (b64) {
            return stringToArrayBuffer(atob(b64));
        };
        message = base64ToArrayBuffer(message.data);
    }
    return message;
}

function convertMessageToArgsNativeToJs(message) {
    var args = [];
    if (!message || !message.hasOwnProperty('CDVType')) {
        args.push(message);
    } else if (message.CDVType == 'MultiPart') {
        message.messages.forEach(function (e) {
            args.push(massageMessageNativeToJs(e));
        });
    } else {
        args.push(massageMessageNativeToJs(message));
    }
    return args;
}

function massageArgsJsToNative(args) {
    if (!args || utils.typeName(args) != 'Array') {
        return args;
    }
    var ret = [];
    args.forEach(function (arg, i) {
        if (utils.typeName(arg) == 'ArrayBuffer') {
            ret.push({
                'CDVType': 'ArrayBuffer',
                'data': base64.fromArrayBuffer(arg)
            });
        } else {
            ret.push(arg);
        }
    });
    return ret;
}

function OSXExec() {

    var successCallback, failCallback, service, action, actionArgs, splitCommand;
    var callbackId = 'INVALID';

    successCallback = arguments[0];
    failCallback = arguments[1];
    service = arguments[2];
    action = arguments[3];
    actionArgs = arguments[4];

    // Register the callbacks and add the callbackId to the positional
    // arguments if given.
    if (successCallback || failCallback) {
        callbackId = service + cordova.callbackId++;
        cordova.callbacks[callbackId] =
        {success: successCallback, fail: failCallback};
    }

    actionArgs = massageArgsJsToNative(actionArgs);

    if (window.cordovabridge && window.cordovabridge.exec) {
        window.cordovabridge.exec(callbackId, service, action, actionArgs);
    } else {
        alert('window.cordovabridge binding is missing.');
    }
}


OSXExec.nativeCallback = function (callbackId, status, message, keepCallback) {
    var success = status === 0 || status === 1;
    var args = convertMessageToArgsNativeToJs(message);
    cordova.callbackFromNative(callbackId, success, status, args, keepCallback);
};

module.exports = OSXExec;
