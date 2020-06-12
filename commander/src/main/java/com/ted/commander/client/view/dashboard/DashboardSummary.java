/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.model.DashboardResponse;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.history.HistoryBillingCycle;

import java.util.Date;
import java.util.logging.Logger;


public class DashboardSummary extends Composite {

    static final Logger LOGGER = Logger.getLogger(DashboardSummary.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField
    DivElement dashboardTitleLabel;
    @UiField
    DivElement rateInEffectField;
    @UiField
    DivElement rateField;
    @UiField
    DivElement projKWHField;
    @UiField
    DivElement projBillField;
    @UiField
    HTMLPanel mainPanel;
    @UiField
    DivElement demandCostField;
    @UiField
    DivElement demandPeakField;
    @UiField
    DivElement daysLeftValue;
    @UiField
    DivElement daysLeftField;
    @UiField
    DivElement rateInEffectValue;
    @UiField
    DivElement rateValue;
    @UiField
    DivElement projKWHValue;
    @UiField
    DivElement projKWHFieldLabel;
    @UiField
    DivElement projBillFieldLabel;
    @UiField
    DivElement projBillValue;
    @UiField
    DivElement demandPeakValue;
    @UiField
    DivElement demandCostValue;
    @UiField
    DivElement demandPeakFieldLabel;


    public DashboardSummary() {
        initWidget(defaultBinder.createAndBindUi(this));

        dashboardTitleLabel.setInnerText(WebStringResource.INSTANCE.billingCycleStarting());


        rateInEffectValue.setInnerText("$0.000");
        rateValue.setInnerText(WebStringResource.INSTANCE.flat());
        daysLeftValue.setInnerText("0");
        projKWHValue.setInnerText("0.0 kWh");
        projBillValue.setInnerText("$0.00");

        demandCostField.getStyle().setDisplay(Style.Display.NONE);
        demandPeakField.getStyle().setDisplay(Style.Display.NONE);

    }


    public void setWidth(String width) {
        mainPanel.setWidth(width);
    }

    public void setSummary(DashboardResponse dashboardResponse) {
        HistoryBillingCycle historyBillingCycle = dashboardResponse.getCurrentBillingCycle();

        if (historyBillingCycle == null || historyBillingCycle.getCalendarKey() == null ) {
            dashboardTitleLabel.setInnerText(WebStringResource.INSTANCE.noBillingCycleFound());
            daysLeftField.getStyle().setDisplay(Style.Display.NONE);
            rateInEffectField.getStyle().setDisplay(Style.Display.NONE);
            rateField.getStyle().setDisplay(Style.Display.NONE);
            projBillField.getStyle().setDisplay(Style.Display.NONE);
            projKWHField.getStyle().setDisplay(Style.Display.NONE);
            return;
        } else {
            projBillField.getStyle().clearDisplay();
            projKWHField.getStyle().clearDisplay();
        }

        EnergyPlan energyPlan = dashboardResponse.getEnergyPlan();

        Date date = new Date();
        boolean isComplete = (historyBillingCycle.getEndEpoch() <= date.getTime()/1000);

        if (isComplete) {
            daysLeftField.getStyle().setDisplay(Style.Display.NONE);
            rateInEffectField.getStyle().setDisplay(Style.Display.NONE);
            rateField.getStyle().setDisplay(Style.Display.NONE);

            projBillFieldLabel.setInnerText(WebStringResource.INSTANCE.mtdCost());
            projKWHFieldLabel.setInnerText(WebStringResource.INSTANCE.mtdKWH());
        } else {

            daysLeftField.getStyle().clearDisplay();
            rateInEffectField.getStyle().clearDisplay();
            rateField.getStyle().clearDisplay();

            projBillFieldLabel.setInnerText(WebStringResource.INSTANCE.projCost());
            projKWHFieldLabel.setInnerText(WebStringResource.INSTANCE.projKWH());
        }

        NumberFormat currencyFormat = NumberFormat.getSimpleCurrencyFormat(energyPlan.getRateType());
        currencyFormat.overrideFractionDigits(4); //TED-305


        if (energyPlan != null && !energyPlan.getDemandPlanType().equals(DemandPlanType.NONE)){
            demandCostField.getStyle().clearDisplay();
            demandPeakField.getStyle().clearDisplay();
            demandCostValue.setInnerText(currencyFormat.format(historyBillingCycle.getDemandCost()));

            StringBuilder demandPeakLabel = new StringBuilder(WebStringResource.INSTANCE.demand());
            demandPeakLabel.append(" on ");
            demandPeakLabel.append(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.MONTH_NUM_DAY).format(new Date(1000 * historyBillingCycle.getDemandCostPeakCalendarKey().toLocalEpoch())));
            demandPeakFieldLabel.setInnerText(demandPeakLabel.toString());
            StringBuilder peakString = new StringBuilder(CommanderFormats.SHORT_KW_FORMAT.format(historyBillingCycle.getDemandPeak()/1000.0));
            if (energyPlan.getDemandPlanType().equals(DemandPlanType.TOU)){
                peakString.append(" (").append(historyBillingCycle.getDemandCostPeakTOUName()).append(") ");
            }
            demandPeakValue.setInnerText(peakString.toString());
        } else {
            demandCostField.getStyle().setDisplay(Style.Display.NONE);
            demandPeakField.getStyle().setDisplay(Style.Display.NONE);
        }



        Date theDate = new Date();
        CalendarUtil.resetTime(theDate);
        theDate.setYear(historyBillingCycle.getCalendarKey().getYear() - 1900);
        theDate.setMonth(historyBillingCycle.getCalendarKey().getMonth());
        theDate.setDate(historyBillingCycle.getMeterReadStartDate());

        dashboardTitleLabel.setInnerText(WebStringResource.INSTANCE.billingCycleStarting() + " " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(theDate));


        long daysLeft = historyBillingCycle.getEndEpoch() - ((new Date()).getTime() / 1000);
        daysLeftValue.setInnerText(Math.floor(daysLeft / 86400) + "");

        rateInEffectValue.setInnerText(currencyFormat.format(historyBillingCycle.getRateInEffect()));

        if (isComplete) {
            projBillValue.setInnerText(currencyFormat.format(historyBillingCycle.getNetCost()));
            projKWHValue.setInnerText(CommanderFormats.SHORT_KWH_FORMAT.format(historyBillingCycle.getNet() / 1000.0));
        } else {
            //TODO: Come up w/ the projected cost. (Server side?)
            projBillValue.setInnerText(currencyFormat.format(dashboardResponse.getProjectedCost()));
            projKWHValue.setInnerText(CommanderFormats.SHORT_KWH_FORMAT.format(dashboardResponse.getProjectedKWH() / 1000.0));
        }

        StringBuilder rateFieldString = new StringBuilder();
        try {
            switch (energyPlan.getPlanType()) {
                case FLAT: {
                    rateFieldString.append(WebStringResource.INSTANCE.flat());
                    break;
                }

                case TIER: {
                    rateFieldString.append(WebStringResource.INSTANCE.tier()).append(" ").append(dashboardResponse.getTier() + 1);
                    break;
                }

                case TOU: {
                    if (dashboardResponse.getTou() != null) {
                        rateFieldString.append(energyPlan.getTouLevels().get(dashboardResponse.getTou().ordinal()).getTouLevelName());
                    }
                    break;
                }
                case TIERTOU: {
                    rateFieldString.append(WebStringResource.INSTANCE.tier()).append(" ").append(dashboardResponse.getTier() + 1);
                    rateFieldString.append(", ");
                    rateFieldString.append(energyPlan.getTouLevels().get(dashboardResponse.getTou().ordinal()).getTouLevelName());
                    break;
                }
            }
            rateValue.setInnerText(rateFieldString.toString());
        } catch(Exception ex){
            LOGGER.warning("Bad Energy Plan: " + ex.toString());;
            rateValue.setInnerText("Warning: Bad Energy Plan");
        }


    }


    @UiTemplate("DashboardSummary.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, DashboardSummary> {
    }


}
