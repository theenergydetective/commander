/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.io.Serializable;

public class DemandChargeTier implements Serializable {

    Integer id;
    Integer seasonId;
    Long energyPlanId;
    Double peak = 0.0;
    Double rate = 0.0;

    public DemandChargeTier() {
    }

    public DemandChargeTier(Integer id, Long energyPlanId, Integer seasonId, Double peak, Double rate) {
        this.energyPlanId = energyPlanId;
        this.id = id;
        this.peak = peak;
        this.rate = rate;
        this.seasonId = seasonId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Double getPeak() {
        return peak;
    }

    public void setPeak(Double peak) {
        this.peak = peak;
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

        DemandChargeTier that = (DemandChargeTier) o;

        if (energyPlanId != null ? !energyPlanId.equals(that.energyPlanId) : that.energyPlanId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (peak != null ? !peak.equals(that.peak) : that.peak != null) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;
        if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (seasonId != null ? seasonId.hashCode() : 0);
        result = 31 * result + (energyPlanId != null ? energyPlanId.hashCode() : 0);
        result = 31 * result + (peak != null ? peak.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DemandChargeTier{" +
                "id=" + id +
                ", seasonId=" + seasonId +
                ", energyPlanId=" + energyPlanId +
                ", peak=" + peak +
                ", rate=" + rate +
                '}';
    }
}
