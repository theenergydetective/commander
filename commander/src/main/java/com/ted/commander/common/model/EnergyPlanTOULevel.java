/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.TOUPeakType;

import java.io.Serializable;

public class EnergyPlanTOULevel implements Serializable {


    Long energyPlanId;
    String touLevelName;
    TOUPeakType peakType;

    public EnergyPlanTOULevel() {
    }

    public EnergyPlanTOULevel(Long energyPlanId, TOUPeakType peakType, String touLevelName) {
        this.energyPlanId = energyPlanId;
        this.peakType = peakType;
        this.touLevelName = touLevelName;
    }

    public Long getEnergyPlanId() {
        return energyPlanId;
    }

    public void setEnergyPlanId(Long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    public String getTouLevelName() {
        return touLevelName;
    }

    public void setTouLevelName(String touLevelName) {
        this.touLevelName = touLevelName;
    }

    public TOUPeakType getPeakType() {
        return peakType;
    }

    public void setPeakType(TOUPeakType peakType) {
        this.peakType = peakType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPlanTOULevel that = (EnergyPlanTOULevel) o;

        if (energyPlanId != null ? !energyPlanId.equals(that.energyPlanId) : that.energyPlanId != null) return false;
        if (peakType != that.peakType) return false;
        if (touLevelName != null ? !touLevelName.equals(that.touLevelName) : that.touLevelName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = energyPlanId != null ? energyPlanId.hashCode() : 0;
        result = 31 * result + (touLevelName != null ? touLevelName.hashCode() : 0);
        result = 31 * result + (peakType != null ? peakType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyPlanTOULevel{" +
                "energyPlanId=" + energyPlanId +
                ", touLevelName='" + touLevelName + '\'' +
                ", tou=" + peakType +
                '}';
    }
}
