/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model.history;

import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryQuery implements Serializable {
    Long virtualECCId;
    CalendarKey startDate;
    CalendarKey endDate;


    HistoryType historyType = HistoryType.BILLING_CYCLE;

    public HistoryQuery() {
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

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

    public HistoryType getHistoryType() {
        return historyType;
    }

    public void setHistoryType(HistoryType historyType) {
        this.historyType = historyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryQuery that = (HistoryQuery) o;

        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BillingCycleQuery{" +
                "virtualECCId=" + virtualECCId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
