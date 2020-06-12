/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model.history;


import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherHistory implements Serializable {

    CalendarKey calendarKey;
    double peakTemperature = 0;
    double lowTemperature = 0;

    double temp;
    int clouds = 0;
    int windspeed = 0;
    CalendarKey peakTempTime;
    CalendarKey lowTempTime;


    public WeatherHistory() {
    }

    ;

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
    }

    public double getPeakTemperature() {
        return peakTemperature;
    }

    public void setPeakTemperature(double peakTemperature) {
        this.peakTemperature = peakTemperature;
    }

    public double getLowTemperature() {
        return lowTemperature;
    }

    public void setLowTemperature(double lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public int getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(int windspeed) {
        this.windspeed = windspeed;
    }

    public CalendarKey getPeakTempTime() {
        return peakTempTime;
    }

    public void setPeakTempTime(CalendarKey peakTempTime) {
        this.peakTempTime = peakTempTime;
    }

    public CalendarKey getLowTempTime() {
        return lowTempTime;
    }

    public void setLowTempTime(CalendarKey lowTempTime) {
        this.lowTempTime = lowTempTime;
    }


    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "WeatherHistory{" +
                "calendarKey=" + calendarKey +
                ", peakTemperature=" + peakTemperature +
                ", lowTemperature=" + lowTemperature +
                ", temp=" + temp +
                ", clouds=" + clouds +
                ", windspeed=" + windspeed +
                ", peakTempTime=" + peakTempTime +
                ", lowTempTime=" + lowTempTime +
                '}';
    }
}
