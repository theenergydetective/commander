/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import java.io.Serializable;

/**
 * Represents a user enrolled in the com.com.ted.commander system.
 */

public class UserEmail implements Serializable {

    private String oldEmail;
    private String newEmail;

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
