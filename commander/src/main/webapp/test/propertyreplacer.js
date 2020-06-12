/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */


// Use this helper module to stub out properties within Jasmine tests.
// Original values will be restored after each test.

var curStubs = null;

function removeAllStubs() {
    for (var i = curStubs.length - 1, stub; stub = curStubs[i]; --i) {
        stub.obj[stub.key] = stub.value;
    }
    curStubs = null;
}

exports.stub = function (obj, key, value) {
    if (!curStubs) {
        curStubs = [];
        jasmine.getEnv().currentSpec.after(removeAllStubs);
    }

    curStubs.push({
        obj: obj,
        key: key,
        value: obj[key]
    });
    obj[key] = value;
};

