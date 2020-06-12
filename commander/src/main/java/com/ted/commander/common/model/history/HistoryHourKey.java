package com.ted.commander.common.model.history;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryHourKey implements Serializable {
    int billingCycleHour;

    public int getBillingCycleHour() {
        return billingCycleHour;
    }

    public void setBillingCycleHour(int billingCycleHour) {
        this.billingCycleHour = billingCycleHour;
    }
}
