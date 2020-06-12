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
public class ExportRequest implements Serializable {
    protected String guid;
    protected Long accountId;
    protected Long userId;
    protected Long virtualECCId;
    protected boolean exportNet = false;
    protected boolean exportLoad = false;
    protected boolean exportGeneration = false;
    protected boolean exportDemandCost = false;
    protected List<Long> mtuIdList = new ArrayList<Long>();
    protected HistoryType historyType;
    protected CalendarKey startDate;
    protected CalendarKey endDate;
    protected DataExportFileType dataExportFileType;
    private boolean exportWeather;

    public ExportRequest() {

    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public boolean isExportNet() {
        return exportNet;
    }

    public void setExportNet(boolean exportNet) {
        this.exportNet = exportNet;
    }

    public boolean isExportLoad() {
        return exportLoad;
    }

    public void setExportLoad(boolean exportLoad) {
        this.exportLoad = exportLoad;
    }

    public boolean isExportGeneration() {
        return exportGeneration;
    }

    public void setExportGeneration(boolean exportGeneration) {
        this.exportGeneration = exportGeneration;
    }

    public List<Long> getMtuIdList() {
        return mtuIdList;
    }

    public void setMtuIdList(List<Long> mtuIdList) {
        this.mtuIdList = mtuIdList;
    }

    public HistoryType getHistoryType() {
        return historyType;
    }

    public void setHistoryType(HistoryType historyType) {
        this.historyType = historyType;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DataExportFileType getDataExportFileType() {
        return dataExportFileType;
    }

    public void setDataExportFileType(DataExportFileType dataExportFileType) {
        this.dataExportFileType = dataExportFileType;
    }

    public boolean isExportDemandCost() {
        return exportDemandCost;
    }

    public void setExportDemandCost(boolean exportDemandCost) {
        this.exportDemandCost = exportDemandCost;
    }

    public boolean isExportWeather() {
        return exportWeather;
    }

    public void setExportWeather(boolean exportWeather) {
        this.exportWeather = exportWeather;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportRequest that = (ExportRequest) o;

        if (exportNet != that.exportNet) return false;
        if (exportLoad != that.exportLoad) return false;
        if (exportGeneration != that.exportGeneration) return false;
        if (exportDemandCost != that.exportDemandCost) return false;
        if (exportWeather != that.exportWeather) return false;
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (mtuIdList != null ? !mtuIdList.equals(that.mtuIdList) : that.mtuIdList != null) return false;
        if (historyType != that.historyType) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        return dataExportFileType == that.dataExportFileType;

    }

    @Override
    public int hashCode() {
        int result = guid != null ? guid.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (virtualECCId != null ? virtualECCId.hashCode() : 0);
        result = 31 * result + (exportNet ? 1 : 0);
        result = 31 * result + (exportLoad ? 1 : 0);
        result = 31 * result + (exportGeneration ? 1 : 0);
        result = 31 * result + (exportDemandCost ? 1 : 0);
        result = 31 * result + (mtuIdList != null ? mtuIdList.hashCode() : 0);
        result = 31 * result + (historyType != null ? historyType.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (dataExportFileType != null ? dataExportFileType.hashCode() : 0);
        result = 31 * result + (exportWeather ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExportRequest{" +
                "guid='" + guid + '\'' +
                ", accountId=" + accountId +
                ", userId=" + userId +
                ", virtualECCId=" + virtualECCId +
                ", exportNet=" + exportNet +
                ", exportLoad=" + exportLoad +
                ", exportGeneration=" + exportGeneration +
                ", exportDemandCost=" + exportDemandCost +
                ", mtuIdList=" + mtuIdList +
                ", historyType=" + historyType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", dataExportFileType=" + dataExportFileType +
                ", exportWeather=" + exportWeather +
                '}';
    }
}
