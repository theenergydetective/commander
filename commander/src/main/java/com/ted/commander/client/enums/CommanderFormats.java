/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.enums;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Created by pete on 11/1/2014.
 */
public class CommanderFormats {

    public static final NumberFormat TEMP_FORMAT = NumberFormat.getFormat("0.0 F");

    public static final NumberFormat SHORT_KWH_FORMAT = NumberFormat.getFormat("0.0 kWh");
    public static final NumberFormat KWH_FORMAT = NumberFormat.getFormat("0.000 kWh");
    public static final NumberFormat KW_FORMAT = NumberFormat.getFormat("0.000 kW");
    public static final NumberFormat SHORT_KW_FORMAT = NumberFormat.getFormat("0.0 kW");
    public static final NumberFormat PF_FORMAT = NumberFormat.getFormat("#%");
    public static final DateTimeFormat PEAK_TIME_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT);
    public static final DateTimeFormat FULL_PEAK_TIME_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
    public static final NumberFormat VOLTAGE_FORMAT = NumberFormat.getFormat("0.0v");


    public static final NumberFormat DEMAND_FORMAT = NumberFormat.getFormat("0.0 kW");
    public static final NumberFormat DAILY_ENERGY_POWER_FORMAT = NumberFormat.getFormat("0.0 kW");


    public static final NumberFormat ADVICE_HOUR = NumberFormat.getFormat("0.00");
    public static final NumberFormat ADVICE_POWER = NumberFormat.getFormat("0.000");
}
