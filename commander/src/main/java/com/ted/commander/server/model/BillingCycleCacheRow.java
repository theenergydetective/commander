/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;

import com.ted.commander.common.enums.TOUPeakType;

import java.io.Serializable;
import java.util.Date;


public class BillingCycleCacheRow implements Serializable {
    long virtualEccId = 0;
    long energyPlanId = 0;
    long epochTime = 0;
    Date convertedDate = new Date();
    double energyDifference = 0;
    double voltage = 0;
    double demandPeak = 0;
    double pf = 0;
    int season = 0;
    TOUPeakType tou = TOUPeakType.OFF_PEAK;
    int tier = 0;
    double cost = 0;
    int billingCycleMonth = 0;
    int billingCycleYear = 0;
    double loadDifference = 0;
    double genDifference = 0;
    double loadCost = 0;
    double genCost = 0;

    public BillingCycleCacheRow() {

    }


    public long getVirtualEccId() {
        return virtualEccId;
    }

    public void setVirtualEccId(long virtualEccId) {
        this.virtualEccId = virtualEccId;
    }

    public long getEnergyPlanId() {
        return energyPlanId;
    }

    public void setEnergyPlanId(long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    public Date getConvertedDate() {
        return convertedDate;
    }

    public void setConvertedDate(Date convertedDate) {
        this.convertedDate = convertedDate;
    }

    public double getEnergyDifference() {
        return energyDifference;
    }

    public void setEnergyDifference(double energyDifference) {
        this.energyDifference = energyDifference;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getDemandPeak() {
        return demandPeak;
    }

    public void setDemandPeak(double demandPeak) {
        this.demandPeak = demandPeak;
    }

    public double getPf() {
        return pf;
    }

    public void setPf(double pf) {
        this.pf = pf;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public TOUPeakType getTou() {
        return tou;
    }

    public void setTou(TOUPeakType tou) {
        this.tou = tou;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getBillingCycleMonth() {
        return billingCycleMonth;
    }

    public void setBillingCycleMonth(int billingCycleMonth) {
        this.billingCycleMonth = billingCycleMonth;
    }

    public int getBillingCycleYear() {
        return billingCycleYear;
    }

    public void setBillingCycleYear(int billingCycleYear) {
        this.billingCycleYear = billingCycleYear;
    }

    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }

    public double getLoadDifference() {
        return loadDifference;
    }

    public void setLoadDifference(double loadDifference) {
        this.loadDifference = loadDifference;
    }

    public double getGenDifference() {
        return genDifference;
    }

    public void setGenDifference(double genDifference) {
        this.genDifference = genDifference;
    }

    public double getLoadCost() {
        if (loadCost == Double.NaN) return 0;
        return loadCost;
    }

    public void setLoadCost(double loadCost) {
        this.loadCost = loadCost;
    }

    public double getGenCost() {
        return genCost;
    }

    public void setGenCost(double genCost) {
        this.genCost = genCost;
    }

    @Override
    public String toString() {
        return "BillingCycleCacheRow{" +
                "virtualEccId=" + virtualEccId +
                ", energyPlanId=" + energyPlanId +
                ", epochTime=" + epochTime +
                ", convertedDate=" + convertedDate +
                ", energyDifference=" + energyDifference +
                ", voltage=" + voltage +
                ", demandPeak=" + demandPeak +
                ", pf=" + pf +
                ", season=" + season +
                ", tou=" + tou +
                ", tier=" + tier +
                ", cost=" + cost +
                ", billingCycleMonth=" + billingCycleMonth +
                ", billingCycleYear=" + billingCycleYear +
                ", loadDifference=" + loadDifference +
                ", genDifference=" + genDifference +
                ", loadCost=" + loadCost +
                ", genCost=" + genCost +
                '}';
    }
}
