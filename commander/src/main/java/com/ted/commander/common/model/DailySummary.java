/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.history.HistoryHour;
import com.ted.commander.common.model.history.HistoryMTUHour;
import com.ted.commander.common.model.history.WeatherHistory;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailySummary implements Serializable {

    CalendarKey dailyDate;
    List<WeatherHistory> weatherGraphPoints = new ArrayList<WeatherHistory>();
    double cost = 0;
    double avgCost = 0;
    double energy = 0;
    double avgEnergy = 0;
    double recentPower = 0;
    double averagePower = 0;
    double demand = 0;
    CalendarKey demandTime;
    double avgDemand = 0;
    double peakVoltage = 0;
    CalendarKey peakVoltageTime;
    double avgPeakVoltage = 0;
    double lowVoltage = 0;
    CalendarKey lowVoltageTime;
    double avgLowVoltage = 0;
    double powerFactor = 0;
    double avgPowerFactor = 0;
    double avgTemp = 0;
    double avgWind = 0;
    double avgCloud = 0;
    boolean isMetric = false;

    double peakTemp = 0.0;
    double minTemp = 0.0;
    int peakWind = 0;
    int minWind = 0;
    int minClouds = 0;
    int peakClouds = 0;


    EnergyPlan energyPlan = new EnergyPlan();
    VirtualECCType virtualECCType;

    private Map<String, String> mtuDescription = new HashMap<>();

    private List<HistoryHour> hourlyData;
    private Map<String, List<HistoryMTUHour>> standAloneData = new HashMap<>();


    public DailySummary() {

    }

    public EnergyPlan getEnergyPlan() {
        return energyPlan;
    }

    public void setEnergyPlan(EnergyPlan energyPlan) {
        this.energyPlan = energyPlan;
    }

    public CalendarKey getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(CalendarKey dailyDate) {
        this.dailyDate = dailyDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(double avgCost) {
        this.avgCost = avgCost;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getAvgEnergy() {
        return avgEnergy;
    }

    public void setAvgEnergy(double avgEnergy) {
        this.avgEnergy = avgEnergy;
    }

    public double getRecentPower() {
        return recentPower;
    }

    public void setRecentPower(double recentPower) {
        this.recentPower = recentPower;
    }

    public double getAveragePower() {
        return averagePower;
    }

    public void setAveragePower(double averagePower) {
        this.averagePower = averagePower;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    public CalendarKey getDemandTime() {
        return demandTime;
    }

    public void setDemandTime(CalendarKey demandTime) {
        this.demandTime = demandTime;
    }

    public double getAvgDemand() {
        return avgDemand;
    }

    public void setAvgDemand(double avgDemand) {
        this.avgDemand = avgDemand;
    }

    public double getPeakVoltage() {
        return peakVoltage;
    }

    public void setPeakVoltage(double peakVoltage) {
        this.peakVoltage = peakVoltage;
    }

    public CalendarKey getPeakVoltageTime() {
        return peakVoltageTime;
    }

    public void setPeakVoltageTime(CalendarKey peakVoltageTime) {
        this.peakVoltageTime = peakVoltageTime;
    }

    public double getAvgPeakVoltage() {
        return avgPeakVoltage;
    }

    public void setAvgPeakVoltage(double avgPeakVoltage) {
        this.avgPeakVoltage = avgPeakVoltage;
    }

    public double getLowVoltage() {
        return lowVoltage;
    }

    public void setLowVoltage(double lowVoltage) {
        this.lowVoltage = lowVoltage;
    }

    public CalendarKey getLowVoltageTime() {
        return lowVoltageTime;
    }

    public void setLowVoltageTime(CalendarKey lowVoltageTime) {
        this.lowVoltageTime = lowVoltageTime;
    }

    public double getAvgLowVoltage() {
        return avgLowVoltage;
    }

    public void setAvgLowVoltage(double avgLowVoltage) {
        this.avgLowVoltage = avgLowVoltage;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
    }

    public double getAvgPowerFactor() {
        return avgPowerFactor;
    }

    public void setAvgPowerFactor(double avgPowerFactor) {
        this.avgPowerFactor = avgPowerFactor;
    }

    public List<WeatherHistory> getWeatherGraphPoints() {
        return weatherGraphPoints;
    }

    public void setWeatherGraphPoints(List<WeatherHistory> weatherGraphPoints) {
        this.weatherGraphPoints = weatherGraphPoints;
    }

    public Map<String, String> getMtuDescription() {
        return mtuDescription;
    }

    public void setMtuDescription(Map<String, String> mtuDescription) {
        this.mtuDescription = mtuDescription;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public double getAvgWind() {
        return avgWind;
    }

    public void setAvgWind(double avgWind) {
        this.avgWind = avgWind;
    }

    public double getAvgCloud() {
        return avgCloud;
    }

    public void setAvgCloud(double avgCloud) {
        this.avgCloud = avgCloud;
    }

    public boolean isMetric() {
        return isMetric;
    }

    public void setMetric(boolean metric) {
        isMetric = metric;
    }

    public void setIsMetric(boolean isMetric) {
        this.isMetric = isMetric;
    }

    public VirtualECCType getVirtualECCType() {
        return virtualECCType;
    }

    public void setVirtualECCType(VirtualECCType virtualECCType) {
        this.virtualECCType = virtualECCType;
    }

    public double getPeakTemp() {
        return peakTemp;
    }

    public void setPeakTemp(double peakTemp) {
        this.peakTemp = peakTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public int getPeakWind() {
        return peakWind;
    }

    public void setPeakWind(int peakWind) {
        this.peakWind = peakWind;
    }

    public int getMinWind() {
        return minWind;
    }

    public void setMinWind(int minWind) {
        this.minWind = minWind;
    }

    public int getMinClouds() {
        return minClouds;
    }

    public void setMinClouds(int minClouds) {
        this.minClouds = minClouds;
    }

    public int getPeakClouds() {
        return peakClouds;
    }

    public void setPeakClouds(int peakClouds) {
        this.peakClouds = peakClouds;
    }

    public List<HistoryHour> getHourlyData() {
        return hourlyData;
    }

    public void setHourlyData(List<HistoryHour> hourlyData) {
        this.hourlyData = hourlyData;
    }

    public Map<String, List<HistoryMTUHour>> getStandAloneData() {
        return standAloneData;
    }

    public void setStandAloneData(Map<String, List<HistoryMTUHour>> standAloneData) {
        this.standAloneData = standAloneData;
    }

    @Override
    public String toString() {
        return "DailySummary{" +
                "dailyDate=" + dailyDate +
                ", mtuDescription=" + mtuDescription +
                ", weatherGraphPoints=" + weatherGraphPoints +
                ", cost=" + cost +
                ", avgCost=" + avgCost +
                ", energy=" + energy +
                ", avgEnergy=" + avgEnergy +
                ", recentPower=" + recentPower +
                ", averagePower=" + averagePower +
                ", demand=" + demand +
                ", demandTime=" + demandTime +
                ", avgDemand=" + avgDemand +
                ", peakVoltage=" + peakVoltage +
                ", peakVoltageTime=" + peakVoltageTime +
                ", avgPeakVoltage=" + avgPeakVoltage +
                ", lowVoltage=" + lowVoltage +
                ", lowVoltageTime=" + lowVoltageTime +
                ", avgLowVoltage=" + avgLowVoltage +
                ", powerFactor=" + powerFactor +
                ", avgPowerFactor=" + avgPowerFactor +
                ", avgTemp=" + avgTemp +
                ", avgWind=" + avgWind +
                ", avgCloud=" + avgCloud +
                ", isMetric=" + isMetric +
                ", energyPlan=" + energyPlan +
                '}';
    }
}
