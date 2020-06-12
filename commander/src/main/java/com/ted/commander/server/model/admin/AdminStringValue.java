/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.admin;


import java.io.Serializable;


public class AdminStringValue implements Serializable {


    private String stringValue;


    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return "AdminStringValue{" +
                "stringValue='" + stringValue + '\'' +
                '}';
    }
}
