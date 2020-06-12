/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.MTUType;

import java.io.Serializable;

/**
 * Represents a virtual ECC's MTU in the system.
 */
public class VirtualECCMTU implements Serializable {


    Long virtualECCId;
    Long mtuId;
    boolean spyder = false;
    Long accountId;
    MTUType mtuType;
    String mtuDescription;
    Double powerMultiplier;
    Double voltageMultiplier;
    Long lastPost = 0l;

    public VirtualECCMTU() {
    }

    public VirtualECCMTU(Long virtualECCId, Long mtuId, Long accountId, MTUType mtuType, String mtuDescription, Double powerMultiplier, Double voltageMultiplier) {
        this.virtualECCId = virtualECCId;
        this.mtuId = mtuId;
        this.accountId = accountId;
        this.mtuType = mtuType;
        this.mtuDescription = mtuDescription;
        this.powerMultiplier = powerMultiplier;
        this.voltageMultiplier = voltageMultiplier;
        this.spyder = false;
    }

    public VirtualECCMTU(Long virtualECCid, MTU mtu) {
        this.virtualECCId = virtualECCid;
        this.mtuId = mtu.getId();
        this.accountId = mtu.getAccountId();
        this.mtuType = mtu.getMtuType();
        this.mtuDescription = mtu.getDescription();
        this.spyder = mtu.spyder;
        this.powerMultiplier = 1.0;
        this.voltageMultiplier = 1.0;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Long getMtuId() {
        return mtuId;
    }

    public void setMtuId(Long mtuId) {
        this.mtuId = mtuId;
    }

    public MTUType getMtuType() {
        return mtuType;
    }

    public void setMtuType(MTUType mtuType) {
        this.mtuType = mtuType;
    }

    public String getMtuDescription() {
        if (mtuDescription == null) {
            mtuDescription = getHexId();
        }
        return mtuDescription;
    }

    public void setMtuDescription(String mtuDescription) {
        this.mtuDescription = mtuDescription;
    }

    public Double getPowerMultiplier() {
        return powerMultiplier;
    }

    public void setPowerMultiplier(Double powerMultiplier) {

        this.powerMultiplier = powerMultiplier;
        if (this.powerMultiplier > 100) this.powerMultiplier = 100.00;
        if (this.powerMultiplier < -100) this.powerMultiplier = -100.00;

    }

    public Double getVoltageMultiplier() {
        return voltageMultiplier;
    }

    public void setVoltageMultiplier(Double voltageMultiplier) {
        this.voltageMultiplier = voltageMultiplier;
        if (this.voltageMultiplier > 100) this.voltageMultiplier = 100.00;
        if (this.voltageMultiplier < -100) this.voltageMultiplier = -100.00;

    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualECCMTU that = (VirtualECCMTU) o;

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (mtuId != null ? mtuId.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VirtualECCMTU{" +
                "virtualECCId=" + virtualECCId +
                ", mtuId=" + mtuId +
                ", spyder=" + spyder +
                ", accountId=" + accountId +
                ", mtuType=" + mtuType +
                ", mtuDescription='" + mtuDescription + '\'' +
                ", powerMultiplier=" + powerMultiplier +
                ", voltageMultiplier=" + voltageMultiplier +
                ", lastPost=" + lastPost +
                '}';
    }

    public String getHexId() {
        String unpaddedHexId = Long.toHexString(mtuId);
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

}

