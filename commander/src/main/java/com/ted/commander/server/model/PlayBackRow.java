package com.ted.commander.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pete on 1/29/2016.
 */
public class PlayBackRow implements Serializable{

    long virtualECCId;
    long startDate;
    long endDate;

    public PlayBackRow() {
    }

    public long getVirtualECCId() {
        return virtualECCId;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setVirtualECCId(long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "PlayBackRow{" +
                "virtualECCId=" + virtualECCId +
                ", startDate=" + new Date(startDate * 1000) +
                ", endDate=" + new Date(1000 * endDate) +
                '}';
    }
}
