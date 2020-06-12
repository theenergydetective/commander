package com.ted.commander.common.model.history;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryDayKey implements Serializable {
    int billingCycleDay;
    int billingCycleMonth;
    int billingCycleYear;

    public int getBillingCycleDay() {
        return billingCycleDay;
    }

    public void setBillingCycleDay(int billingCycleDay) {
        this.billingCycleDay = billingCycleDay;
    }

    public int getBillingCycleMonth() {
        return billingCycleMonth;
    }

    public void setBillingCycleMonth(int billingCycleMonth) {
        this.billingCycleMonth = billingCycleMonth;
    }

    public int getBillingCycleYear() {
        return billingCycleYear;
    }

    public void setBillingCycleYear(int billingCycleYear) {
        this.billingCycleYear = billingCycleYear;
    }
}
