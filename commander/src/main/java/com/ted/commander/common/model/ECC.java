/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.ECCState;

import java.io.Serializable;

/**
 * Represents a single gateway enrolled in the system
 */
public class ECC implements Serializable {

    public Long id;
    public Long userAccountId;
    public ECCState state;
    public String securityKey;
    public String version;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public ECCState getState() {
        return state;
    }

    public void setState(ECCState state) {
        this.state = state;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getVersion() {
        if (version == null) version = "";
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ECC ecc = (ECC) o;

        if (id != null ? !id.equals(ecc.id) : ecc.id != null) return false;
        if (securityKey != null ? !securityKey.equals(ecc.securityKey) : ecc.securityKey != null) return false;
        if (state != ecc.state) return false;
        if (userAccountId != null ? !userAccountId.equals(ecc.userAccountId) : ecc.userAccountId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userAccountId != null ? userAccountId.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (securityKey != null ? securityKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ECC{" +
                "id=" + id +
                ", userAccountId=" + userAccountId +
                ", state=" + state +
                ", securityKey='" + securityKey + '\'' +
                '}';
    }
}
