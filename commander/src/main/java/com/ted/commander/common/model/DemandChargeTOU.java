/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.TOUPeakType;

import java.io.Serializable;

public class DemandChargeTOU implements Serializable {


    TOUPeakType peakType;
    Integer seasonId;
    Long energyPlanId;
    Double rate = 0.0;

    public DemandChargeTOU() {
    }

    public DemandChargeTOU(TOUPeakType peakType, Long energyPlanId, Integer seasonId, Double rate) {
        this.peakType = peakType;
        this.energyPlanId = energyPlanId;
        this.seasonId = seasonId;
        this.rate = rate;
    }

    public TOUPeakType getPeakType() {
        return peakType;
    }

    public void setPeakType(TOUPeakType peakType) {
        this.peakType = peakType;
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

        DemandChargeTOU that = (DemandChargeTOU) o;

        if (energyPlanId != null ? !energyPlanId.equals(that.energyPlanId) : that.energyPlanId != null) return false;
        if (peakType != that.peakType) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;
        if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = peakType != null ? peakType.hashCode() : 0;
        result = 31 * result + (seasonId != null ? seasonId.hashCode() : 0);
        result = 31 * result + (energyPlanId != null ? energyPlanId.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DemandChargeTOU{" +
                "tou=" + peakType +
                ", seasonId=" + seasonId +
                ", energyPlanId=" + energyPlanId +
                ", rate=" + rate +
                '}';
    }
}
