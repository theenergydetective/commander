/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyPlanSeason implements Serializable {
    Integer id;
    String seasonName = "Season 1";
    Integer seasonMonth = 0;
    Integer seasonDayOfMonth = 0;

    List<AdditionalCharge> additionalChargeList = new ArrayList<AdditionalCharge>();
    List<EnergyPlanTier> tierList = new ArrayList<EnergyPlanTier>();
    List<EnergyPlanTOU> touList = new ArrayList<EnergyPlanTOU>();
    List<DemandChargeTier> demandChargeTierList = new ArrayList<DemandChargeTier>();
    List<DemandChargeTOU> demandChargeTOUList = new ArrayList<DemandChargeTOU>();
    List<EnergyRate> energyRateList = new ArrayList<EnergyRate>();

    public EnergyPlanSeason(Integer id, String seasonName, Integer seasonMonth, Integer seasonDayOfMonth) {
        this.id = id;
        this.seasonName = seasonName;
        this.seasonMonth = seasonMonth;
        this.seasonDayOfMonth = seasonDayOfMonth;
    }

    public EnergyPlanSeason() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Integer getSeasonMonth() {
        return seasonMonth;
    }

    public void setSeasonMonth(Integer seasonMonth) {
        this.seasonMonth = seasonMonth;
    }

    public Integer getSeasonDayOfMonth() {
        return seasonDayOfMonth;
    }

    public void setSeasonDayOfMonth(Integer seasonDayOfMonth) {
        this.seasonDayOfMonth = seasonDayOfMonth;
    }

    public List<EnergyPlanTier> getTierList() {
        return tierList;
    }

    public void setTierList(List<EnergyPlanTier> tierList) {
        this.tierList = tierList;
    }

    public List<EnergyPlanTOU> getTouList() {
        return touList;
    }

    public void setTouList(List<EnergyPlanTOU> touList) {
        this.touList = touList;
    }

    public List<AdditionalCharge> getAdditionalChargeList() {
        return additionalChargeList;
    }

    public void setAdditionalChargeList(List<AdditionalCharge> additionalChargeList) {
        this.additionalChargeList = additionalChargeList;
    }

    public List<DemandChargeTier> getDemandChargeTierList() {
        return demandChargeTierList;
    }

    public void setDemandChargeTierList(List<DemandChargeTier> demandChargeTierList) {
        this.demandChargeTierList = demandChargeTierList;
    }

    public List<DemandChargeTOU> getDemandChargeTOUList() {
        return demandChargeTOUList;
    }

    public void setDemandChargeTOUList(List<DemandChargeTOU> demandChargeTOUList) {
        this.demandChargeTOUList = demandChargeTOUList;
    }

    public List<EnergyRate> getEnergyRateList() {
        return energyRateList;
    }

    public void setEnergyRateList(List<EnergyRate> energyRateList) {
        this.energyRateList = energyRateList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPlanSeason that = (EnergyPlanSeason) o;

        if (additionalChargeList != null ? !additionalChargeList.equals(that.additionalChargeList) : that.additionalChargeList != null)
            return false;
        if (demandChargeTOUList != null ? !demandChargeTOUList.equals(that.demandChargeTOUList) : that.demandChargeTOUList != null)
            return false;
        if (demandChargeTierList != null ? !demandChargeTierList.equals(that.demandChargeTierList) : that.demandChargeTierList != null)
            return false;
        if (energyRateList != null ? !energyRateList.equals(that.energyRateList) : that.energyRateList != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (seasonDayOfMonth != null ? !seasonDayOfMonth.equals(that.seasonDayOfMonth) : that.seasonDayOfMonth != null)
            return false;
        if (seasonMonth != null ? !seasonMonth.equals(that.seasonMonth) : that.seasonMonth != null) return false;
        if (seasonName != null ? !seasonName.equals(that.seasonName) : that.seasonName != null) return false;
        if (tierList != null ? !tierList.equals(that.tierList) : that.tierList != null) return false;
        if (touList != null ? !touList.equals(that.touList) : that.touList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (seasonName != null ? seasonName.hashCode() : 0);
        result = 31 * result + (seasonMonth != null ? seasonMonth.hashCode() : 0);
        result = 31 * result + (seasonDayOfMonth != null ? seasonDayOfMonth.hashCode() : 0);
        result = 31 * result + (additionalChargeList != null ? additionalChargeList.hashCode() : 0);
        result = 31 * result + (tierList != null ? tierList.hashCode() : 0);
        result = 31 * result + (touList != null ? touList.hashCode() : 0);
        result = 31 * result + (demandChargeTierList != null ? demandChargeTierList.hashCode() : 0);
        result = 31 * result + (demandChargeTOUList != null ? demandChargeTOUList.hashCode() : 0);
        result = 31 * result + (energyRateList != null ? energyRateList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyPlanSeason{" +
                "id=" + id +
                ", seasonName='" + seasonName + '\'' +
                ", seasonMonth=" + seasonMonth +
                ", seasonDayOfMonth=" + seasonDayOfMonth +
                ", additionalChargeList=" + additionalChargeList +
                ", tierList=" + tierList +
                ", touList=" + touList +
                ", demandChargeTierList=" + demandChargeTierList +
                ", demandChargeTOUList=" + demandChargeTOUList +
                ", energyRateList=" + energyRateList +
                '}';
    }

    public boolean before(EnergyPlanSeason that) {
        //Check month
        if (this.getSeasonMonth() == null) return true;
        if (this.getSeasonDayOfMonth() == null) return true;
        if (that.getSeasonMonth() == null) return true;
        if (that.getSeasonDayOfMonth() == null) return true;

        if (this.getSeasonMonth() < that.getSeasonMonth()) return true;
        if (this.getSeasonMonth() > that.getSeasonMonth()) return false;

        //Same month, check date
        if (this.getSeasonDayOfMonth() <= that.getSeasonDayOfMonth()) return true;
        else return false;


    }
}
