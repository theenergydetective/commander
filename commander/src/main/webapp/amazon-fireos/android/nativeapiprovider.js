/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

/**
 * Exports the ExposedJsApi.java object if available, otherwise exports the PromptBasedNativeApi.
 */

var nativeApi = this._cordovaNative || require('cordova/android/promptbasednativeapi');
var currentApi = nativeApi;

module.exports = {
    get: function () {
        return currentApi;
    },
    setPreferPrompt: function (value) {
        currentApi = value ? require('cordova/android/promptbasednativeapi') : nativeApi;
    },
    // Used only by tests.
    set: function (value) {
        currentApi = value;
    }
};
