package com.ted.commander.common.model.export;

import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.history.WeatherHistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphResponse implements Serializable {
    String currencyCode = "USD";
    VirtualECC virtualECC;
    List<VirtualECCMTU> virtualECCMTUList = new ArrayList<>();

    CalendarKey startCalendarKey; //TED-302: These are the adjusted start/stop epochs for the query (adjusted end date).
    CalendarKey endCalendarKey;

    private List<WeatherHistory> weatherHistory = new ArrayList<>();
    private Map<String, List<HistoryDataPoint>> dataMap = new HashMap<>();

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<WeatherHistory> getWeatherHistory() {
        return weatherHistory;
    }

    public void setWeatherHistory(List<WeatherHistory> weatherHistory) {
        this.weatherHistory = weatherHistory;
    }

    public Map<String, List<HistoryDataPoint>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, List<HistoryDataPoint>> dataMap) {
        this.dataMap = dataMap;
    }

    public VirtualECC getVirtualECC() {
        return virtualECC;
    }

    public void setVirtualECC(VirtualECC virtualECC) {
        this.virtualECC = virtualECC;
    }

    public List<VirtualECCMTU> getVirtualECCMTUList() {
        return virtualECCMTUList;
    }

    public void setVirtualECCMTUList(List<VirtualECCMTU> virtualECCMTUList) {
        this.virtualECCMTUList = virtualECCMTUList;
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

    @Override
    public String toString() {
        return "GraphResponse{" +
                "currencyCode='" + currencyCode + '\'' +
                ", virtualECC=" + virtualECC +
                ", virtualECCMTUList=" + virtualECCMTUList +
                ", startCalendarKey=" + startCalendarKey +
                ", endCalendarKey=" + endCalendarKey +
                ", weatherHistory=" + weatherHistory +
                ", dataMap=" + dataMap +
                '}';
    }
}
