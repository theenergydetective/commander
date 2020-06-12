package com.ted.commander.client.model;

import com.ted.commander.common.enums.MTUType;

/**
 * Created by pete on 1/31/2015.
 */
public class DataExportDataPoint {

    private Long accountId;
    private Long virtualECCId;
    private Long mtuId;
    private MTUType mtuType;
    private boolean isSelected;
    private String description;
    private boolean lineGraph = false;

    public DataExportDataPoint(Long virtualECCId, Long mtuId, String description) {
        this.virtualECCId = virtualECCId;
        this.mtuId = mtuId;
        this.description = description;
        mtuType = MTUType.STAND_ALONE;
        isSelected = false;
        lineGraph = false;
    }

    public DataExportDataPoint(Long virtualECCId, MTUType mtuType, String description) {
        this.virtualECCId = virtualECCId;
        this.mtuType = mtuType;
        mtuId = null;
        isSelected = false;
        this.description = description;
        lineGraph = false;
    }

    public DataExportDataPoint() {
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Long getMtuId() {
        return mtuId;
    }

    public void setMtuId(Long mtuId) {
        this.mtuId = mtuId;
    }

    public MTUType getMtuType() {
        return mtuType;
    }

    public void setMtuType(MTUType mtuType) {
        this.mtuType = mtuType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLineGraph() {
        return lineGraph;
    }

    public void setLineGraph(boolean lineGraph) {
        this.lineGraph = lineGraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataExportDataPoint that = (DataExportDataPoint) o;

        if (isSelected != that.isSelected) return false;
        if (lineGraph != that.lineGraph) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (mtuType != that.mtuType) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (virtualECCId != null ? virtualECCId.hashCode() : 0);
        result = 31 * result + (mtuId != null ? mtuId.hashCode() : 0);
        result = 31 * result + (mtuType != null ? mtuType.hashCode() : 0);
        result = 31 * result + (isSelected ? 1 : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (lineGraph ? 1 : 0);
        return result;
    }
}
