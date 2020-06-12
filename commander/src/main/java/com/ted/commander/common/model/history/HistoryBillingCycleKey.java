package com.ted.commander.common.model.history;

import com.ted.commander.common.model.CalendarKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by pete on 1/15/2016.
 */
@Embeddable
public class HistoryBillingCycleKey implements Serializable {

    @Column(name = "virtualECC_id")
    Long virtualECCId;

    Integer billingCycleMonth;
    Integer billingCycleYear;

    public HistoryBillingCycleKey() {

    }

    public HistoryBillingCycleKey(Long virtualECCId, CalendarKey calendarKey) {
        this.virtualECCId = virtualECCId;
        this.billingCycleMonth = calendarKey.getMonth();
        this.billingCycleYear = calendarKey.getYear();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryBillingCycleKey that = (HistoryBillingCycleKey) o;

        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (billingCycleMonth != null ? !billingCycleMonth.equals(that.billingCycleMonth) : that.billingCycleMonth != null)
            return false;
        return billingCycleYear != null ? billingCycleYear.equals(that.billingCycleYear) : that.billingCycleYear == null;

    }

    @Override
    public int hashCode() {
        int result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (billingCycleMonth != null ? billingCycleMonth.hashCode() : 0);
        result = 31 * result + (billingCycleYear != null ? billingCycleYear.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistoryBillingCycleKey{" +
                "virtualECCId=" + virtualECCId +
                ", billingCycleMonth=" + billingCycleMonth +
                ", billingCycleYear=" + billingCycleYear +
                '}';
    }
}
