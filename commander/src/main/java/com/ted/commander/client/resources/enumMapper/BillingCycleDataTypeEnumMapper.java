/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources.enumMapper;

import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.enums.BillingCycleDataType;

/**
 * Created by pete on 10/28/2014.
 */
public class BillingCycleDataTypeEnumMapper {

    public static String getDescription(BillingCycleDataType billingCycleDataType) {
        switch (billingCycleDataType) {
            case COST_PER_DAY:
                return WebStringResource.INSTANCE.costPerDay();
            case KWH_NET:
                return WebStringResource.INSTANCE.kwhNet();
            case AVG_PF:
                return WebStringResource.INSTANCE.averagePF();
            case PEAK_DEMAND:
                return WebStringResource.INSTANCE.peakDemand();
            case PEAK_VOLTAGE:
                return WebStringResource.INSTANCE.peakVoltage();
            case LOW_VOLTAGE:
                return WebStringResource.INSTANCE.lowVoltage();
            case PEAK_TEMP:
                return WebStringResource.INSTANCE.peakTemp();
            case LOW_TEMP:
                return WebStringResource.INSTANCE.lowTemp();
            case CLOUD_COVERAGE:
                return WebStringResource.INSTANCE.cloudCoverage();
            case KWH_CONSUMED:
                return WebStringResource.INSTANCE.kwhConsumed();
            case KWH_GENERTED:
                return WebStringResource.INSTANCE.kwHGenerated();
            case TOU_RATES:
                return WebStringResource.INSTANCE.touRate();
            default:
                return null;
        }
    }
}
