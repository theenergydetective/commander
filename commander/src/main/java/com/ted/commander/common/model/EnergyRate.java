/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.TOUPeakType;

import java.io.Serializable;

public class EnergyRate implements Serializable {


    Integer seasonId;
    Long energyPlanId;
    TOUPeakType peakType;
    int tierId;
    Double rate = 0.10;

    public EnergyRate() {
    }

    public EnergyRate(Integer seasonId, int tierId, TOUPeakType peakType) {
        this.seasonId = seasonId;
        this.peakType = peakType;
        this.tierId = tierId;
        rate = 0.10;
    }

    public EnergyRate(Integer seasonId, int tierId, TOUPeakType peakType, Double rate) {
        this.seasonId = seasonId;
        this.peakType = peakType;
        this.tierId = tierId;
        this.rate = rate;
    }

    public EnergyRate(Long energyPlanId, Integer seasonId, int tierId, TOUPeakType peakType, Double rate) {
        this.energyPlanId = energyPlanId;
        this.seasonId = seasonId;
        this.tierId = tierId;
        this.peakType = peakType;
        this.rate = rate;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public Long getEnergyPlanId() {
        return energyPlanId;
    }

    public void setEnergyPlanId(Long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    public TOUPeakType getPeakType() {
        return peakType;
    }

    public void setPeakType(TOUPeakType peakType) {
        this.peakType = peakType;
    }

    public int getTierId() {
        return tierId;
    }

    public void setTierId(int tierId) {
        this.tierId = tierId;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyRate that = (EnergyRate) o;

        if (tierId != that.tierId) return false;
        if (energyPlanId != null ? !energyPlanId.equals(that.energyPlanId) : that.energyPlanId != null) return false;
        if (peakType != that.peakType) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;
        if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seasonId != null ? seasonId.hashCode() : 0;
        result = 31 * result + (energyPlanId != null ? energyPlanId.hashCode() : 0);
        result = 31 * result + (peakType != null ? peakType.hashCode() : 0);
        result = 31 * result + tierId;
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyRate{" +
                "seasonId=" + seasonId +
                ", energyPlanId=" + energyPlanId +
                ", tou=" + peakType +
                ", tierId=" + tierId +
                ", rate=" + rate +
                '}';
    }

    public String getKey() {
        return tierId + peakType.name();
    }

}
