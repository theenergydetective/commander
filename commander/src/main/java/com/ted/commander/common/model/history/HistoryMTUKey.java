package com.ted.commander.common.model.history;

import java.io.Serializable;

/**
 * Created by pete on 5/19/2016.
 */
public class HistoryMTUKey implements Serializable {
    Long virtualECCId;
    Long mtuId;
    Long startEpoch;

    public HistoryMTUKey(Long virtualECCId, Long mtuId, Long startEpoch) {
        this.virtualECCId = virtualECCId;
        this.mtuId = mtuId;
        this.startEpoch = startEpoch;
    }

    public HistoryMTUKey() {
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

    public Long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryMTUKey that = (HistoryMTUKey) o;

        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        return startEpoch != null ? startEpoch.equals(that.startEpoch) : that.startEpoch == null;

    }

    @Override
    public int hashCode() {
        int result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (mtuId != null ? mtuId.hashCode() : 0);
        result = 31 * result + (startEpoch != null ? startEpoch.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistoryMTUKey{" +
                "virtualECCId=" + virtualECCId +
                ", mtuId=" + mtuId +
                ", startEpoch=" + startEpoch +
                '}';
    }
}
