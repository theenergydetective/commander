/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.model;


import com.ted.commander.common.enums.BillingCycleDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pete on 10/27/2014.
 */
public class DashboardOptions {
    List<BillingCycleDataType> graphedOptions = null;
    BillingCycleDataType gradientOption = null;

    public DashboardOptions() {
        graphedOptions = new ArrayList<BillingCycleDataType>();
        graphedOptions.add(BillingCycleDataType.KWH_NET);
        gradientOption = BillingCycleDataType.KWH_NET;
    }

    public List<BillingCycleDataType> getGraphedOptions() {
        return graphedOptions;
    }

    public void setGraphedOptions(List<BillingCycleDataType> graphedOptions) {
        this.graphedOptions = graphedOptions;
    }

    public BillingCycleDataType getGradientOption() {
        return gradientOption;
    }

    public void setGradientOption(BillingCycleDataType gradientOption) {
        this.gradientOption = gradientOption;
    }
}
