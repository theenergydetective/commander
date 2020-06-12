package com.ted.commander.common.model.export;


import com.ted.commander.common.model.CalendarKey;

import java.io.Serializable;

public class HistoryDataPoint implements Serializable {

    CalendarKey calendarKey;
    Double energyValue;
    Double costValue;
    Double demandValue;

    public HistoryDataPoint() {
    }

    public Double getDemandValue() {
        return demandValue;
    }

    public void setDemandValue(Double demandValue) {
        this.demandValue = demandValue;
    }

    public Double getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(Double energyValue) {
        this.energyValue = energyValue;
    }

    public Double getCostValue() {
        return costValue;
    }

    public void setCostValue(Double costValue) {
        this.costValue = costValue;
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
    }

    @Override
    public String toString() {
        return "HistoryDataPoint{" +
                "calendarKey=" + calendarKey +
                ", energyValue=" + energyValue +
                ", costValue=" + costValue +
                ", demandValue=" + demandValue +
                '}';
    }
}
