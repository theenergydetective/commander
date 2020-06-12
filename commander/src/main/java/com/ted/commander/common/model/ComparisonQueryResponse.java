/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryHour;
import com.ted.commander.common.model.history.HistoryMinute;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComparisonQueryResponse implements Serializable {
    String currencyCode = "USD";
    List<VirtualECC> virtualECCList;
    Map<Long, List<HistoryBillingCycle>> billingCycleHistoryList = new HashMap<>();
    Map<Long, List<HistoryDay>> dayHistoryList = new HashMap<>();
    Map<Long, List<HistoryHour>> hourHistoryList = new HashMap<>();
    Map<Long, List<HistoryMinute>> minuteHistoryList = new HashMap<>();

    CalendarKey startCalendarKey; //TED-302: These are the adjusted start/stop epochs for the query (adjusted end date).
    CalendarKey endCalendarKey;


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<VirtualECC> getVirtualECCList() {
        return virtualECCList;
    }

    public void setVirtualECCList(List<VirtualECC> virtualECCList) {
        this.virtualECCList = virtualECCList;
    }

    public Map<Long, List<HistoryBillingCycle>> getBillingCycleHistoryList() {
        return billingCycleHistoryList;
    }

    public void setBillingCycleHistoryList(Map<Long, List<HistoryBillingCycle>> billingCycleHistoryList) {
        this.billingCycleHistoryList = billingCycleHistoryList;
    }

    public Map<Long, List<HistoryDay>> getDayHistoryList() {
        return dayHistoryList;
    }

    public void setDayHistoryList(Map<Long, List<HistoryDay>> dayHistoryList) {
        this.dayHistoryList = dayHistoryList;
    }

    public Map<Long, List<HistoryHour>> getHourHistoryList() {
        return hourHistoryList;
    }

    public void setHourHistoryList(Map<Long, List<HistoryHour>> hourHistoryList) {
        this.hourHistoryList = hourHistoryList;
    }

    public Map<Long, List<HistoryMinute>> getMinuteHistoryList() {
        return minuteHistoryList;
    }

    public void setMinuteHistoryList(Map<Long, List<HistoryMinute>> minuteHistoryList) {
        this.minuteHistoryList = minuteHistoryList;
    }

    public CalendarKey getStartCalendarKey() {
        return startCalendarKey;
    }

    public void setStartCalendarKey(CalendarKey startCalendarKey) {
        this.startCalendarKey = startCalendarKey;
    }

    public CalendarKey getEndCalendarKey() {
        return endCalendarKey;
    }

    public void setEndCalendarKey(CalendarKey endCalendarKey) {
        this.endCalendarKey = endCalendarKey;
    }
}
