package com.ted.commander.common.model.export;

import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.CalendarKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pete on 1/31/2015.
 */
public class BillingRequest implements Serializable {
    protected String guid;
    protected Long userId;
    protected List<Long> virtualECCIdList = new ArrayList<Long>();
    protected CalendarKey startDate;
    protected CalendarKey endDate;
    protected DataExportFileType dataExportFileType;
    protected HistoryType historyType;

    public BillingRequest() {

    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getVirtualECCIdList() {
        return virtualECCIdList;
    }

    public void setVirtualECCIdList(List<Long> virtualECCIdList) {
        this.virtualECCIdList = virtualECCIdList;
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

    public DataExportFileType getDataExportFileType() {
        return dataExportFileType;
    }

    public void setDataExportFileType(DataExportFileType dataExportFileType) {
        this.dataExportFileType = dataExportFileType;
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

        BillingRequest that = (BillingRequest) o;

        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (virtualECCIdList != null ? !virtualECCIdList.equals(that.virtualECCIdList) : that.virtualECCIdList != null)
            return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        return dataExportFileType == that.dataExportFileType;
    }

    @Override
    public int hashCode() {
        int result = guid != null ? guid.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (virtualECCIdList != null ? virtualECCIdList.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (dataExportFileType != null ? dataExportFileType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BillingRequest{" +
                "guid='" + guid + '\'' +
                ", userId=" + userId +
                ", virtualECCIdList=" + virtualECCIdList +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", dataExportFileType=" + dataExportFileType +
                ", historyType=" + historyType +
                '}';
    }
}
