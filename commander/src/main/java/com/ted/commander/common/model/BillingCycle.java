/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.google.gwt.core.client.GWT;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingCycle implements Serializable {


    long startEpoch; //Time zone adjusted epoch
    long endEpoch;  //Time zone adjusted epoch
    long energyPlanId;
    private CalendarKey startKey;
    private CalendarKey endKey;

    public BillingCycle() {

    }

    public BillingCycle(long startEpoch, long endEpoch, CalendarKey startKey, CalendarKey endKey, Long energyPlanId) {
        this.startEpoch = startEpoch;
        this.endEpoch = endEpoch;
        this.startKey = startKey;
        this.endKey = endKey;
        this.energyPlanId = energyPlanId;

    }

    public CalendarKey getStartKey() {
        return startKey;
    }

    public void setStartKey(CalendarKey startKey) {
        this.startKey = startKey;
    }

    public CalendarKey getEndKey() {
        return endKey;
    }

    public void setEndKey(CalendarKey endKey) {
        this.endKey = endKey;
    }

    public long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public long getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(long endEpoch) {
        this.endEpoch = endEpoch;
    }

    public long getEnergyPlanId() {
        return energyPlanId;
    }

    public void setEnergyPlanId(long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillingCycle that = (BillingCycle) o;

        if (startEpoch != that.startEpoch) return false;
        if (endEpoch != that.endEpoch) return false;
        if (energyPlanId != that.energyPlanId) return false;
        if (startKey != null ? !startKey.equals(that.startKey) : that.startKey != null) return false;
        return !(endKey != null ? !endKey.equals(that.endKey) : that.endKey != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (startEpoch ^ (startEpoch >>> 32));
        result = 31 * result + (int) (endEpoch ^ (endEpoch >>> 32));
        result = 31 * result + (startKey != null ? startKey.hashCode() : 0);
        result = 31 * result + (endKey != null ? endKey.hashCode() : 0);
        result = 31 * result + (int) (energyPlanId ^ (energyPlanId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BillingCycle{" +
                "startEpoch=" + startEpoch +
                ", endEpoch=" + endEpoch +
                ", startKey=" + startKey +
                ", endKey=" + endKey +
                ", energyPlanId=" + energyPlanId +
                '}';
    }


    @JsonIgnore
    public boolean in(CalendarKey calendarKey) {
        Date calendarDate = new Date(calendarKey.getYear() - 1900, calendarKey.getMonth(), calendarKey.getDate());
        Date startDate = new Date(startKey.getYear() - 1900, startKey.getMonth(), startKey.getDate());
        Date endDate = new Date(endKey.getYear() - 1900, endKey.getMonth(), endKey.getDate());
        GWT.log(">>>startDate:" + startDate + " calendarDate: " + calendarDate + " endDate:" + endDate);
        return (calendarDate.getTime() >= startDate.getTime() && calendarDate.getTime() < endDate.getTime());
    }
}

