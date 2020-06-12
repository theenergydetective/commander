/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import java.io.Serializable;

/**
 * Represents a user enrolled in the com.com.ted.commander system.
 */

public class PasswordResetRequest implements Serializable {


    private String password;
    private String activationKey;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordResetRequest that = (PasswordResetRequest) o;

        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return !(activationKey != null ? !activationKey.equals(that.activationKey) : that.activationKey != null);

    }

    @Override
    public int hashCode() {
        int result = password != null ? password.hashCode() : 0;
        result = 31 * result + (activationKey != null ? activationKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PasswordResetRequest{" +
                ", password='" + password + '\'' +
                ", activationKey='" + activationKey + '\'' +
                '}';
    }
}
