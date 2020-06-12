/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.WeatherHistory;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardResponse implements Serializable {
    VirtualECC virtualECC;
    HistoryBillingCycle currentBillingCycle;
    List<HistoryDay> historyDayList = new ArrayList<>();
    List<WeatherHistory> weatherHistoryList = new ArrayList<>();
    EnergyPlan energyPlan;
    double projectedKWH;
    double projectedCost;
    TOUPeakType tou;
    int tier;
    boolean hasNetPoints = true;

    public HistoryBillingCycle getCurrentBillingCycle() {
        return currentBillingCycle;
    }

    public void setCurrentBillingCycle(HistoryBillingCycle currentBillingCycle) {
        this.currentBillingCycle = currentBillingCycle;
    }

    public List<HistoryDay> getHistoryDayList() {
        return historyDayList;
    }

    public void setHistoryDayList(List<HistoryDay> historyDayList) {
        this.historyDayList = historyDayList;
    }

    public List<WeatherHistory> getWeatherHistoryList() {
        return weatherHistoryList;
    }

    public void setWeatherHistoryList(List<WeatherHistory> weatherHistoryList) {
        this.weatherHistoryList = weatherHistoryList;
    }

    public VirtualECC getVirtualECC() {
        return virtualECC;
    }

    public void setVirtualECC(VirtualECC virtualECC) {
        this.virtualECC = virtualECC;
    }

    public EnergyPlan getEnergyPlan() {
        return energyPlan;
    }

    public void setEnergyPlan(EnergyPlan energyPlan) {
        this.energyPlan = energyPlan;
    }

    public double getProjectedKWH() {
        return projectedKWH;
    }

    public void setProjectedKWH(double projectedKWH) {
        this.projectedKWH = projectedKWH;
    }

    public double getProjectedCost() {
        return projectedCost;
    }

    public void setProjectedCost(double projectedCost) {
        this.projectedCost = projectedCost;
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

    public boolean isHasNetPoints() {
        return hasNetPoints;
    }

    public void setHasNetPoints(boolean hasNetPoints) {
        this.hasNetPoints = hasNetPoints;
    }

    @Override
    public String toString() {
        return "DashboardResponse{" +
                "currentBillingCycle=" + currentBillingCycle +
                ", historyDayList=" + historyDayList +
                '}';
    }


}
