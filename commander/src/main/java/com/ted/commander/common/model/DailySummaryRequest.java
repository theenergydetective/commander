/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailySummaryRequest implements Serializable {
    long virtualECCId;
    CalendarKey calendarKey;

    public DailySummaryRequest() {
    }

    public DailySummaryRequest(long virtualECCId, CalendarKey calendarKey) {
        this.virtualECCId = virtualECCId;
        this.calendarKey = calendarKey;
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
    }

    public long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }


    @Override
    public String toString() {
        return "DailySummaryRequest{" +
                "calendarKey=" + calendarKey +
                ", virtualECCId=" + virtualECCId +
                '}';
    }
}
