/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

/*global spyOn:false */

var propertyreplacer = require('cordova/propertyreplacer');

exports.replace = function (moduleName, newValue) {
    propertyreplacer.stub(define.moduleMap, moduleName, null);
    define.remove(moduleName);
    define(moduleName, function (require, exports, module) {
        module.exports = newValue;
    });
};

