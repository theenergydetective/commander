/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;

import com.ted.commander.common.model.EnergyData;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.history.EnergyDifference;
import com.ted.commander.common.model.history.HistoryMTUBillingCycleKey;

import java.io.Serializable;

/**
 * Created by pete on 9/12/2014.
 */

public class StandAlonePost implements Serializable {

    VirtualECC virtualECC;
    VirtualECCMTU virtualECCMTU;
    Long mtuId;
    Long minuteEpoch;
    Long dayEpoch;
    Long hourEpoch;
    HistoryMTUBillingCycleKey mtuBillingCycleKey;
    EnergyPlan energyPlan;
    int season;
    EnergyDifference energyDifference;
    Double lastDifference;
    EnergyData energyData;
    private long timestamp;

    public VirtualECC getVirtualECC() {
        return virtualECC;
    }

    public void setVirtualECC(VirtualECC virtualECC) {
        this.virtualECC = virtualECC;
    }

    public VirtualECCMTU getVirtualECCMTU() {
        return virtualECCMTU;
    }

    public void setVirtualECCMTU(VirtualECCMTU virtualECCMTU) {
        this.virtualECCMTU = virtualECCMTU;
    }

    public Long getMtuId() {
        return mtuId;
    }

    public void setMtuId(Long mtuId) {
        this.mtuId = mtuId;
    }

    public Long getMinuteEpoch() {
        return minuteEpoch;
    }

    public void setMinuteEpoch(Long minuteEpoch) {
        this.minuteEpoch = minuteEpoch;
    }

    public Long getDayEpoch() {
        return dayEpoch;
    }

    public void setDayEpoch(Long dayEpoch) {
        this.dayEpoch = dayEpoch;
    }

    public Long getHourEpoch() {
        return hourEpoch;
    }

    public void setHourEpoch(Long hourEpoch) {
        this.hourEpoch = hourEpoch;
    }

    public HistoryMTUBillingCycleKey getMtuBillingCycleKey() {
        return mtuBillingCycleKey;
    }

    public void setMtuBillingCycleKey(HistoryMTUBillingCycleKey mtuBillingCycleKey) {
        this.mtuBillingCycleKey = mtuBillingCycleKey;
    }

    public EnergyPlan getEnergyPlan() {
        return energyPlan;
    }

    public void setEnergyPlan(EnergyPlan energyPlan) {
        this.energyPlan = energyPlan;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public EnergyDifference getEnergyDifference() {
        return energyDifference;
    }

    public void setEnergyDifference(EnergyDifference energyDifference) {
        this.energyDifference = energyDifference;
    }

    public Double getLastDifference() {
        return lastDifference;
    }

    public void setLastDifference(Double lastDifference) {
        this.lastDifference = lastDifference;
    }

    public EnergyData getEnergyData() {
        return energyData;
    }

    public void setEnergyData(EnergyData energyData) {
        this.energyData = energyData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StandAlonePost that = (StandAlonePost) o;

        if (season != that.season) return false;
        if (timestamp != that.timestamp) return false;
        if (virtualECC != null ? !virtualECC.equals(that.virtualECC) : that.virtualECC != null) return false;
        if (virtualECCMTU != null ? !virtualECCMTU.equals(that.virtualECCMTU) : that.virtualECCMTU != null)
            return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (minuteEpoch != null ? !minuteEpoch.equals(that.minuteEpoch) : that.minuteEpoch != null) return false;
        if (dayEpoch != null ? !dayEpoch.equals(that.dayEpoch) : that.dayEpoch != null) return false;
        if (hourEpoch != null ? !hourEpoch.equals(that.hourEpoch) : that.hourEpoch != null) return false;
        if (mtuBillingCycleKey != null ? !mtuBillingCycleKey.equals(that.mtuBillingCycleKey) : that.mtuBillingCycleKey != null)
            return false;
        if (energyPlan != null ? !energyPlan.equals(that.energyPlan) : that.energyPlan != null) return false;
        if (energyDifference != null ? !energyDifference.equals(that.energyDifference) : that.energyDifference != null)
            return false;
        if (lastDifference != null ? !lastDifference.equals(that.lastDifference) : that.lastDifference != null)
            return false;
        return energyData != null ? energyData.equals(that.energyData) : that.energyData == null;

    }

    @Override
    public int hashCode() {
        int result = virtualECC != null ? virtualECC.hashCode() : 0;
        result = 31 * result + (virtualECCMTU != null ? virtualECCMTU.hashCode() : 0);
        result = 31 * result + (mtuId != null ? mtuId.hashCode() : 0);
        result = 31 * result + (minuteEpoch != null ? minuteEpoch.hashCode() : 0);
        result = 31 * result + (dayEpoch != null ? dayEpoch.hashCode() : 0);
        result = 31 * result + (hourEpoch != null ? hourEpoch.hashCode() : 0);
        result = 31 * result + (mtuBillingCycleKey != null ? mtuBillingCycleKey.hashCode() : 0);
        result = 31 * result + (energyPlan != null ? energyPlan.hashCode() : 0);
        result = 31 * result + season;
        result = 31 * result + (energyDifference != null ? energyDifference.hashCode() : 0);
        result = 31 * result + (lastDifference != null ? lastDifference.hashCode() : 0);
        result = 31 * result + (energyData != null ? energyData.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "StandAlonePost{" +
                "virtualECC=" + virtualECC +
                ", virtualECCMTU=" + virtualECCMTU +
                ", mtuId=" + mtuId +
                ", minuteEpoch=" + minuteEpoch +
                ", dayEpoch=" + dayEpoch +
                ", hourEpoch=" + hourEpoch +
                ", mtuBillingCycleKey=" + mtuBillingCycleKey +
                ", energyPlan=" + energyPlan +
                ", season=" + season +
                ", energyDifference=" + energyDifference +
                ", lastDifference=" + lastDifference +
                ", energyData=" + energyData +
                ", timestamp=" + timestamp +
                '}';
    }
}
