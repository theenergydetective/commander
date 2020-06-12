/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.io.Serializable;

public class EnergyPlanTier implements Serializable {

    Integer id;
    Integer seasonId;
    Long energyPlanId;
    Long kwh;

    public EnergyPlanTier() {
    }

    public EnergyPlanTier(Integer id, Integer seasonId, Long energyPlanId, Long kwh) {
        this.id = id;
        this.seasonId = seasonId;
        this.energyPlanId = energyPlanId;
        this.kwh = kwh;
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

    public Long getKwh() {
        return kwh;
    }

    public void setKwh(Long kwh) {
        this.kwh = kwh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPlanTier that = (EnergyPlanTier) o;

        if (energyPlanId != null ? !energyPlanId.equals(that.energyPlanId) : that.energyPlanId != null)
            return false;
        if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (kwh != null ? !kwh.equals(that.kwh) : that.kwh != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (seasonId != null ? seasonId.hashCode() : 0);
        result = 31 * result + (energyPlanId != null ? energyPlanId.hashCode() : 0);
        result = 31 * result + (kwh != null ? kwh.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyPlanTier{" +
                "id=" + id +
                ", seasonId=" + seasonId +
                ", energyPlanId=" + energyPlanId +
                ", kwh=" + kwh +
                '}';
    }
}
