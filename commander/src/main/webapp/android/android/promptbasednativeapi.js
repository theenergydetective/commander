/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

/**
 * Implements the API of ExposedJsApi.java, but uses prompt() to communicate.
 * This is used only on the 2.3 simulator, where addJavascriptInterface() is broken.
 */

module.exports = {
    exec: function (service, action, callbackId, argsJson) {
        return prompt(argsJson, 'gap:' + JSON.stringify([service, action, callbackId]));
    },
    setNativeToJsBridgeMode: function (value) {
        prompt(value, 'gap_bridge_mode:');
    },
    retrieveJsMessages: function (fromOnlineEvent) {
        return prompt(+fromOnlineEvent, 'gap_poll:');
    }
};
