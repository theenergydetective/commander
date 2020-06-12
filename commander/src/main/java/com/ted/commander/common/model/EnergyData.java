/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.io.Serializable;

public class EnergyData implements Serializable {

    Long mtuId;
    Long timeStamp;
    Long accountId;
    Double energy;
    Double energyDifference;
    Double powerFactor;
    Double voltage;

    double avg5;
    double avg10;
    double avg15;
    double avg20;
    double avg30;

    boolean processed = false;
    boolean smoothing = false;


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

    public Double getEnergyDifference() {
        return energyDifference;
    }

    public void setEnergyDifference(Double energyDifference) {
        this.energyDifference = energyDifference;
    }

    public Double getPowerFactor() {
        if (powerFactor < 0 || powerFactor > 1000) return 98.0;
        return powerFactor;
    }

    public void setPowerFactor(Double pf) {
        this.powerFactor = pf;
    }

    public Double getVoltage() {
        if (voltage < 0 || voltage > 1000) return 120.0;
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyData that = (EnergyData) o;

        if (processed != that.processed) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (timeStamp != null ? !timeStamp.equals(that.timeStamp) : that.timeStamp != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (energy != null ? !energy.equals(that.energy) : that.energy != null) return false;
        if (energyDifference != null ? !energyDifference.equals(that.energyDifference) : that.energyDifference != null)
            return false;
        if (powerFactor != null ? !powerFactor.equals(that.powerFactor) : that.powerFactor != null) return false;
        return voltage != null ? voltage.equals(that.voltage) : that.voltage == null;

    }

    @Override
    public int hashCode() {
        int result = mtuId != null ? mtuId.hashCode() : 0;
        result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (energy != null ? energy.hashCode() : 0);
        result = 31 * result + (energyDifference != null ? energyDifference.hashCode() : 0);
        result = 31 * result + (powerFactor != null ? powerFactor.hashCode() : 0);
        result = 31 * result + (voltage != null ? voltage.hashCode() : 0);
        result = 31 * result + (processed ? 1 : 0);
        return result;
    }

    public double getAvg5() {
        return avg5;
    }

    public void setAvg5(double avg5) {
        this.avg5 = avg5;
    }

    public double getAvg10() {
        return avg10;
    }

    public void setAvg10(double avg10) {
        this.avg10 = avg10;
    }

    public double getAvg15() {
        return avg15;
    }

    public void setAvg15(double avg15) {
        this.avg15 = avg15;
    }

    public double getAvg20() {
        return avg20;
    }

    public void setAvg20(double avg20) {
        this.avg20 = avg20;
    }

    public double getAvg30() {
        return avg30;
    }

    public void setAvg30(double avg30) {
        this.avg30 = avg30;
    }

    public boolean isSmoothing() {
        return smoothing;
    }

    public void setSmoothing(boolean smoothing) {
        this.smoothing = smoothing;
    }

    @Override
    public String toString() {
        return "EnergyData{" +
                "mtuId=" + mtuId +
                ", timeStamp=" + timeStamp +
                ", accountId=" + accountId +
                ", energy=" + energy +
                ", energyDifference=" + energyDifference +
                ", powerFactor=" + powerFactor +
                ", voltage=" + voltage +
                ", avg5=" + avg5 +
                ", avg10=" + avg10 +
                ", avg15=" + avg15 +
                ", avg20=" + avg20 +
                ", avg30=" + avg30 +
                ", processed=" + processed +
                '}';
    }
}
