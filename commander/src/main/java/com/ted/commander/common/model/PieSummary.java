/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PieSummary implements Serializable {

    CalendarKey dailyDate;
    String rateType = "USD";
    boolean isMetric = false;
    List<PieGraphPoint> pieGraphPoints = new ArrayList<>();

    public CalendarKey getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(CalendarKey dailyDate) {
        this.dailyDate = dailyDate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public boolean isMetric() {
        return isMetric;
    }

    public void setMetric(boolean metric) {
        isMetric = metric;
    }

    public List<PieGraphPoint> getPieGraphPoints() {
        return pieGraphPoints;
    }

    public void setPieGraphPoints(List<PieGraphPoint> pieGraphPoints) {
        this.pieGraphPoints = pieGraphPoints;
    }

    @Override
    public String toString() {
        return "PieSummary{" +
                "dailyDate=" + dailyDate +
                ", rateType='" + rateType + '\'' +
                ", isMetric=" + isMetric +
                ", pieGraphPoints=" + pieGraphPoints +
                '}';
    }
}
