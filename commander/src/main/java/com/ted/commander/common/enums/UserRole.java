/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.enums;

/**
 * Created by pete on 6/17/2014.
 */
public enum UserRole {
    ADMIN,
    USER;

    public static UserRole fromString(String role) {
        if (role.toUpperCase().equals("ADMIN")) return ADMIN;
        return USER;
    }
}
