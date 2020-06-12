/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.ted.commander.common.enums.HistoryType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ComparisonQueryRequest implements Serializable {


    CalendarKey startDate;
    CalendarKey endDate;
    List<VirtualECC> locationList = new ArrayList<VirtualECC>();
    HistoryType historyType = HistoryType.DAILY;


    public CalendarKey getStartDate() {
        return startDate;
    }

    public void setStartDate(CalendarKey startDate) {
        this.startDate = startDate;
    }

    public CalendarKey getEndDate() {
        return endDate;
    }

    public void setEndDate(CalendarKey endDate) {
        this.endDate = endDate;
    }

    public List<VirtualECC> getLocationList() {
        if (locationList == null) locationList = new ArrayList<VirtualECC>();
        return locationList;
    }

    public void setLocationList(List<VirtualECC> locationList) {
        this.locationList = locationList;
    }

    public HistoryType getHistoryType() {
        return historyType;
    }

    public void setHistoryType(HistoryType historyType) {
        this.historyType = historyType;
    }


    @Override
    public String toString() {
        return "ComparisonQueryRequest{" +
                "endDate=" + endDate +
                ", startDate=" + startDate +
                ", locationList=" + locationList +
                ", historyType=" + historyType +
                '}';
    }
}
