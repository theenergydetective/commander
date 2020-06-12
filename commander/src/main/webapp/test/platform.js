/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

module.exports = {
    id: 'test platform',
    bootstrap: function () {
        var propertyreplacer = require('cordova/propertyreplacer');

        require('cordova/builder').replaceHookForTesting = function (obj, key) {
            // This doesn't clean up non-clobbering assignments, nor does it work for
            // getters. It does work to un-clobber clobbered / merged symbols, which
            // is generally good enough for tests.
            if (obj[key]) {
                propertyreplacer.stub(obj, key);
            }
        };
    }
};
