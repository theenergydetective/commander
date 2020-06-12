/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.enums;

/**
 * Created by pete on 12/19/2014.
 */
public enum SendAtMostType {

    MINUTE,
    HOURLY,
    DAILY,
    BILLING_CYCLE;

    @Override
    public String toString() {
        return name();
    }
}