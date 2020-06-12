/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.io.Serializable;

public class EnergyDataLastCur implements Serializable {

    Long mtuId;
    Long timeStamp;
    Long accountId;
    Double energy;
    private double energyDifference;

    public EnergyDataLastCur(EnergyData energyData) {
        this.mtuId = energyData.getMtuId();
        this.timeStamp = energyData.getTimeStamp();
        this.accountId =  energyData.getAccountId();
        this.energy = energyData.getEnergy();
        this.energyDifference = energyData.getEnergyDifference();
    }

    public EnergyDataLastCur() {
    }


    public Long getMtuId() {
        return mtuId;
    }

    public void setMtuId(Long mtuId) {
        this.mtuId = mtuId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public void setEnergyDifference(double energyDifference) {
        this.energyDifference = energyDifference;
    }

    public double getEnergyDifference() {
        return energyDifference;
    }

    @Override
    public String toString() {
        return "EnergyDataLastCur{" +
                "mtuId=" + mtuId +
                ", timeStamp=" + timeStamp +
                ", accountId=" + accountId +
                ", energy=" + energy +
                ", energyDifference=" + energyDifference +
                '}';
    }
}
