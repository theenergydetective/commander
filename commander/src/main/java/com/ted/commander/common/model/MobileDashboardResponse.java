/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.common.model.history.WeatherHistory;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileDashboardResponse implements Serializable {

    HistoryBillingCycle historyBillingCycle;
    HistoryMinute historyMinute;
    HistoryDay historyDay;
    WeatherHistory weatherHistory;
    EnergyPlan energyPlan;
    int tier = 0;
    String touName = "";

    public HistoryMinute getHistoryMinute() {
        return historyMinute;
    }

    public void setHistoryMinute(HistoryMinute historyMinute) {
        this.historyMinute = historyMinute;
    }

    public HistoryDay getHistoryDay() {
        return historyDay;
    }

    public void setHistoryDay(HistoryDay historyDay) {
        this.historyDay = historyDay;
    }

    public WeatherHistory getWeatherHistory() {
        return weatherHistory;
    }

    public void setWeatherHistory(WeatherHistory weatherHistory) {
        this.weatherHistory = weatherHistory;
    }

    public EnergyPlan getEnergyPlan() {
        return energyPlan;
    }

    public void setEnergyPlan(EnergyPlan energyPlan) {
        this.energyPlan = energyPlan;
    }

    public HistoryBillingCycle getHistoryBillingCycle() {
        return historyBillingCycle;
    }

    public void setHistoryBillingCycle(HistoryBillingCycle historyBillingCycle) {
        this.historyBillingCycle = historyBillingCycle;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public String getTouName() {
        return touName;
    }

    public void setTouName(String touName) {
        this.touName = touName;
    }
}
