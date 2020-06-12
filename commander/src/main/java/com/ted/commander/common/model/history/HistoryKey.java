package com.ted.commander.common.model.history;

import java.io.Serializable;

/**
 * Created by pete on 5/19/2016.
 */
public class HistoryKey implements Serializable {

    public enum BatchState{
        INSERT,
        UPDATE
    }


    Long virtualECCId;
    Long startEpoch;


    public HistoryKey(Long virtualECCId, Long startEpoch) {
        this.virtualECCId = virtualECCId;
        this.startEpoch = startEpoch;
    }

    public HistoryKey() {
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
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

        HistoryKey that = (HistoryKey) o;

        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        return startEpoch != null ? startEpoch.equals(that.startEpoch) : that.startEpoch == null;

    }

    @Override
    public int hashCode() {
        int result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (startEpoch != null ? startEpoch.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistoryKey{" +
                "virtualECCId=" + virtualECCId +
                ", startEpoch=" + startEpoch +
                '}';
    }
}


