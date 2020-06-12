/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.MTUType;

import java.io.Serializable;

/**
 * Represents a single unique MTU
 */
public class MTU implements Serializable {

    Long id;
    Long accountId;
    String description;
    MTUType mtuType;
    boolean spyder = false;
    Long lastPost = 0l;
    Long validation = 0l;
    boolean noNegative = false;

    public MTU() {
    }

    public MTU(Long id, Long accountId, String description, MTUType mtuType) {
        this.id = id;
        this.accountId = accountId;
        this.description = description;
        this.mtuType = mtuType;
        this.spyder = false;
        this.validation = 0l;
        this.noNegative = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getDescription() {
        if (description == null) {
            description = getHexId();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MTUType getMtuType() {
        return mtuType;
    }

    public void setMtuType(MTUType mtuType) {
        this.mtuType = mtuType;
    }

    public boolean isSpyder() {
        return spyder;
    }

    public void setSpyder(boolean spyder) {
        this.spyder = spyder;
    }

    public Long getLastPost() {
        return lastPost;
    }

    public void setLastPost(Long lastPost) {
        this.lastPost = lastPost;
    }

    public Long getValidation() {
        return validation;
    }

    public void setValidation(Long validation) {
        this.validation = validation;
    }

    public boolean isNoNegative() {
        return noNegative;
    }

    public void setNoNegative(boolean noNegative) {
        this.noNegative = noNegative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MTU mtu = (MTU) o;

        if (spyder != mtu.spyder) return false;
        if (id != null ? !id.equals(mtu.id) : mtu.id != null) return false;
        if (accountId != null ? !accountId.equals(mtu.accountId) : mtu.accountId != null) return false;
        if (description != null ? !description.equals(mtu.description) : mtu.description != null) return false;
        if (mtuType != mtu.mtuType) return false;
        return lastPost != null ? lastPost.equals(mtu.lastPost) : mtu.lastPost == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        return result;
    }

    public String getHexId() {
        String unpaddedHexId = Long.toHexString(id);
        StringBuilder hexId = new StringBuilder();
        if (spyder) {
            for (int i = 0; i < 6 - unpaddedHexId.length(); i++) hexId.append("0");
            hexId.append(unpaddedHexId);
        } else {
            for (int i = 0; i < 8 - unpaddedHexId.length(); i++) hexId.append("0");
            hexId.append(unpaddedHexId);
        }
        return hexId.toString();
    }

    @Override
    public String toString() {
        return "MTU{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", description='" + description + '\'' +
                ", mtuType=" + mtuType +
                ", spyder=" + spyder +
                ", lastPost=" + lastPost +
                '}';
    }
}
