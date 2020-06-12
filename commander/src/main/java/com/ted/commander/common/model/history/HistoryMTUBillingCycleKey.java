package com.ted.commander.common.model.history;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by pete on 1/15/2016.
 */
@Embeddable
public class HistoryMTUBillingCycleKey implements Serializable {

    @Column(name = "virtualECC_id")
    Long virtualECCId;

    @Column(name = "mtu_id")
    Long mtuId;

    Integer billingCycleMonth;
    Integer billingCycleYear;

    public HistoryMTUBillingCycleKey() {

    }

    public HistoryMTUBillingCycleKey(HistoryBillingCycleKey billingCycleKey, Long mtuId) {
        this.virtualECCId = billingCycleKey.getVirtualECCId();
        this.billingCycleMonth = billingCycleKey.getBillingCycleMonth();
        this.billingCycleYear = billingCycleKey.getBillingCycleYear();
        this.mtuId = mtuId;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Integer getBillingCycleMonth() {
        return billingCycleMonth;
    }

    public void setBillingCycleMonth(Integer billingCycleMonth) {
        this.billingCycleMonth = billingCycleMonth;
    }

    public Integer getBillingCycleYear() {
        return billingCycleYear;
    }

    public void setBillingCycleYear(Integer billingCycleYear) {
        this.billingCycleYear = billingCycleYear;
    }

    public Long getMtuId() {
        return mtuId;
    }

    public void setMtuId(Long mtuId) {
        this.mtuId = mtuId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryMTUBillingCycleKey that = (HistoryMTUBillingCycleKey) o;

        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (billingCycleMonth != null ? !billingCycleMonth.equals(that.billingCycleMonth) : that.billingCycleMonth != null)
            return false;
        return billingCycleYear != null ? billingCycleYear.equals(that.billingCycleYear) : that.billingCycleYear == null;

    }

    @Override
    public int hashCode() {
        int result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (mtuId != null ? mtuId.hashCode() : 0);
        result = 31 * result + (billingCycleMonth != null ? billingCycleMonth.hashCode() : 0);
        result = 31 * result + (billingCycleYear != null ? billingCycleYear.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "HistoryMTUBillingCycleKey{" +
                "virtualECCId=" + virtualECCId +
                ", mtuId=" + mtuId +
                ", billingCycleMonth=" + billingCycleMonth +
                ", billingCycleYear=" + billingCycleYear +
                '}';
    }
}
