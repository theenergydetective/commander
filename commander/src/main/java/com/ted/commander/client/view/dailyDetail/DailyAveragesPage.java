/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dailyDetail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.DailySummary;
import com.ted.commander.common.model.EnergyPlan;

import java.util.Date;


public class DailyAveragesPage extends Composite{

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    DailyStatRow kwRow;
    @UiField
    DailyStatRow kwhRow;
    @UiField
    DailyStatRow costRow;
    @UiField
    DailyStatRow demandRow;
    @UiField
    DailyStatRow peakVoltageRow;
    @UiField
    DailyStatRow lowVoltageRow;
    @UiField
    DailyStatRow powerFactorRow;
    @UiField
    DivElement averageText;
    @UiField
    DailyStatRow cloudRow;
    @UiField
    DailyStatRow windRow;
    @UiField
    DailyStatRow tempRow;

    public DailyAveragesPage(EnergyPlan energyPlan, DailySummary summary, NumberFormat currencyFormat) {
        initWidget(defaultBinder.createAndBindUi(this));

        Date d = new Date();

        if ((d.getYear() + 1900) == summary.getDailyDate().getYear() &&
                d.getMonth() == summary.getDailyDate().getMonth() &&
                d.getDate() == summary.getDailyDate().getDate()) {
            kwRow.setValue(CommanderFormats.KW_FORMAT.format(summary.getRecentPower() / 1000.0));
            kwRow.setVisible(true);

            kwRow.setCurrentText(WebStringResource.INSTANCE.recent());
            kwhRow.setCurrentText(WebStringResource.INSTANCE.recent());
            costRow.setCurrentText(WebStringResource.INSTANCE.recent());
            demandRow.setCurrentText(WebStringResource.INSTANCE.recent());
            peakVoltageRow.setCurrentText(WebStringResource.INSTANCE.recent());
            lowVoltageRow.setCurrentText(WebStringResource.INSTANCE.recent());
            powerFactorRow.setCurrentText(WebStringResource.INSTANCE.recent());



        } else {
            kwRow.setValue("");
            kwRow.setVisible(false);
        }


        tempRow.setCurrentText(WebStringResource.INSTANCE.minMax());
        cloudRow.setCurrentText(WebStringResource.INSTANCE.minMax());
        windRow.setCurrentText(WebStringResource.INSTANCE.minMax());
        kwhRow.setCurrentText(WebStringResource.INSTANCE.totalForDay());
        costRow.setCurrentText(WebStringResource.INSTANCE.totalForDay());
        demandRow.setCurrentText(WebStringResource.INSTANCE.peakForDay());
        peakVoltageRow.setCurrentText(WebStringResource.INSTANCE.peakForDay());
        lowVoltageRow.setCurrentText(WebStringResource.INSTANCE.lowForDay());
        powerFactorRow.setCurrentText(WebStringResource.INSTANCE.averageForDay());

        d.setYear(summary.getDailyDate().getYear() - 1900);
        d.setMonth(summary.getDailyDate().getMonth());
        d.setDate(summary.getDailyDate().getDate());
        CalendarUtil.resetTime(d);
        averageText.setInnerText(WebStringResource.INSTANCE.averageSentence().replace("XXXXX", DateTimeFormat.getFormat("EEEE").format(d)));

        kwRow.setAvg(CommanderFormats.SHORT_KWH_FORMAT.format(summary.getAveragePower() / 1000.0));

        costRow.setValue(currencyFormat.format(summary.getCost()));
        costRow.setAvg(currencyFormat.format(summary.getAvgCost()));

        kwhRow.setValue(CommanderFormats.SHORT_KWH_FORMAT.format(summary.getEnergy() / 1000.0));
        kwhRow.setAvg(CommanderFormats.SHORT_KWH_FORMAT.format(summary.getAvgEnergy() / 1000.0));

        demandRow.setValue(CommanderFormats.SHORT_KW_FORMAT.format((summary.getDemand() / 1000.0)) + formatPeakTime(summary.getDemandTime()));
        demandRow.setAvg(CommanderFormats.SHORT_KW_FORMAT.format((summary.getAvgDemand() / 1000.0)));

        peakVoltageRow.setValue(CommanderFormats.VOLTAGE_FORMAT.format(summary.getPeakVoltage()) + formatPeakTime(summary.getPeakVoltageTime()));
        peakVoltageRow.setAvg(CommanderFormats.VOLTAGE_FORMAT.format(summary.getAvgPeakVoltage()));

        lowVoltageRow.setValue(CommanderFormats.VOLTAGE_FORMAT.format(summary.getLowVoltage()) + formatPeakTime(summary.getLowVoltageTime()));
        lowVoltageRow.setAvg(CommanderFormats.VOLTAGE_FORMAT.format(summary.getAvgLowVoltage()));

        powerFactorRow.setValue(CommanderFormats.PF_FORMAT.format(summary.getPowerFactor() / 100));
        powerFactorRow.setAvg(CommanderFormats.PF_FORMAT.format(summary.getAvgPowerFactor() / 100));


        if (summary.getWeatherGraphPoints().size() > 0) {


            NumberFormat weatherFormat = NumberFormat.getFormat("#");

            cloudRow.setValue(weatherFormat.format(summary.getMinClouds()) + " / " + weatherFormat.format(summary.getPeakClouds()) + " %");
            cloudRow.setAvg(weatherFormat.format(summary.getAvgCloud()) + " %");

            if (summary.isMetric()) {
                windRow.setValue(weatherFormat.format(summary.getMinWind()) + " / " + weatherFormat.format(summary.getPeakWind()) + " kph");
                tempRow.setValue(weatherFormat.format(summary.getMinTemp()) + " / " + weatherFormat.format(summary.getPeakTemp()) + " C");
                tempRow.setAvg(weatherFormat.format(summary.getAvgTemp()) + " C");
                windRow.setAvg(weatherFormat.format(summary.getAvgWind()) + " kmh");
            } else {
                windRow.setValue(weatherFormat.format(summary.getMinWind()) + " / " + weatherFormat.format(summary.getPeakWind()) + " mph");
                tempRow.setValue(weatherFormat.format(summary.getMinTemp()) + " / " + weatherFormat.format(summary.getPeakTemp()) + " F");
                tempRow.setAvg(weatherFormat.format(summary.getAvgTemp()) + " F");
                windRow.setAvg(weatherFormat.format(summary.getAvgWind()) + " mph");
            }

            tempRow.setVisible(true);
            windRow.setVisible(true);
            cloudRow.setVisible(true);
        } else {
            tempRow.setVisible(false);
            windRow.setVisible(false);
            cloudRow.setVisible(false);

        }


    }

    private String formatPeakTime(CalendarKey peakTime) {
        if (peakTime == null) return "";
        StringBuilder stringBuilder = new StringBuilder(" @ ");
        Date date = new Date();
        CalendarUtil.resetTime(date);
        date.setYear(peakTime.getYear() - 1900);
        date.setMonth(peakTime.getMonth());
        date.setDate(peakTime.getDate());
        date.setHours(peakTime.getHour());
        date.setMinutes(peakTime.getMin());
        stringBuilder.append(CommanderFormats.PEAK_TIME_FORMAT.format(date));
        return stringBuilder.toString();
    }


    interface DefaultBinder extends UiBinder<Widget, DailyAveragesPage> {
    }
}
