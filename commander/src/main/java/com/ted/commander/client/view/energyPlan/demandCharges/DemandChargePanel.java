/*
 * Copyright (c) 2014. The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan.demandCharges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.petecode.common.client.widget.PaperCheckboxElementManager;
import com.ted.commander.client.radioManager.BooleanTypeRadioManager;
import com.ted.commander.client.radioManager.DemandPlanTypeRadioManager;
import com.ted.commander.client.widgets.DemandAverageTimePicker;
import com.ted.commander.client.widgets.NumberPicker;
import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.vaadin.polymer.paper.element.PaperCheckboxElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;

import java.util.logging.Logger;


public class DemandChargePanel extends Composite {

    static final Logger LOGGER = Logger.getLogger(DemandChargePanel.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    private final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    PaperCheckboxElement useDemandChargeCB;

    @UiField
    PaperCheckboxElement saturdayCB;
    @UiField
    PaperCheckboxElement sundayCB;
    @UiField
    PaperCheckboxElement holidayCB;
    @UiField
    PaperCheckboxElement offPeakCB;


    PaperCheckboxElementManager useDemandChargeCBManager;
    PaperCheckboxElementManager saturdayCBManager;
    PaperCheckboxElementManager sundayCBManager;
    PaperCheckboxElementManager holidayCBManager;
    PaperCheckboxElementManager offPeakCBManager;

    @UiField
    DivElement stepQuestionField;


    @UiField
    PaperRadioGroupElement demandTypeGroup;
    @UiField
    PaperRadioGroupElement demandPowerTypeGroup;
    @UiField
    FlowPanel contentPanel;
    @UiField
    DemandAverageTimePicker avgListBox;
    @UiField
    NumberPicker stepListBox;
    @UiField
    DivElement demandChargePanel;

    EnergyPlan energyPlan;
    final DemandPlanTypeRadioManager demandPlanTypeRadioManager;
    final BooleanTypeRadioManager booleanTypeRadioManager;


    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            updateEnergyPlan();
            handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));
        }
    };

    ValueChangeHandler refreshValueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            updateEnergyPlan();
            redrawSeasons();
            handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));
        }
    };


    public DemandChargePanel(EnergyPlan energyPlan) {
        initWidget(defaultBinder.createAndBindUi(this));

        useDemandChargeCBManager = new PaperCheckboxElementManager(useDemandChargeCB);
        saturdayCBManager = new PaperCheckboxElementManager(saturdayCB);
        sundayCBManager = new PaperCheckboxElementManager(sundayCB);
        holidayCBManager = new PaperCheckboxElementManager(holidayCB);
        offPeakCBManager = new PaperCheckboxElementManager(offPeakCB);

        demandPlanTypeRadioManager = new DemandPlanTypeRadioManager(demandTypeGroup);
        booleanTypeRadioManager = new BooleanTypeRadioManager(demandPowerTypeGroup);

        useDemandChargeCBManager.addValueChangeHandler(refreshValueChangeHandler);

        setEnabled(false);
        setActiveEnergyPlan(energyPlan);

        demandPlanTypeRadioManager.addValueChangeHandler(refreshValueChangeHandler);
        booleanTypeRadioManager.addValueChangeHandler(refreshValueChangeHandler);
        saturdayCBManager.addValueChangeHandler(valueChangeHandler);
        sundayCBManager.addValueChangeHandler(valueChangeHandler);
        holidayCBManager.addValueChangeHandler(valueChangeHandler);
        offPeakCBManager.addValueChangeHandler(valueChangeHandler);
        avgListBox.addValueChangeHandler(valueChangeHandler);
        stepListBox.addValueChangeHandler(valueChangeHandler);


    }

    public void updateEnergyPlan() {
        if (!useDemandChargeCB.getChecked()) {
            energyPlan.setDemandPlanType(DemandPlanType.NONE);
            demandChargePanel.getStyle().setDisplay(Style.Display.NONE);
        } else {
            demandChargePanel.getStyle().clearDisplay();
            if (energyPlan.getDemandPlanType().equals(DemandPlanType.NONE)){
                energyPlan.setDemandPlanType(DemandPlanType.TIERED);
                demandPlanTypeRadioManager.setValue(DemandPlanType.TIERED);
                energyPlan.setDemandAverageTime(15);
            }

            energyPlan.setDemandPlanType(demandPlanTypeRadioManager.getValue());
            energyPlan.setDemandUseActivePower(booleanTypeRadioManager.getValue());
            energyPlan.setDemandApplicableSaturday(saturdayCB.getChecked());
            energyPlan.setDemandApplicableSunday(sundayCB.getChecked());
            energyPlan.setDemandApplicableHoliday(holidayCB.getChecked());
            energyPlan.setDemandApplicableOffPeak(offPeakCB.getChecked());
            energyPlan.setDemandAverageTime(avgListBox.getValue());

            energyPlan.setNumberDemandSteps(stepListBox.getValue()==null?2:stepListBox.getValue());

            if (energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED) || energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED_PEAK)) {
                stepQuestionField.getStyle().clearDisplay();
            } else {
                stepQuestionField.getStyle().setDisplay(Style.Display.NONE);
            }


        }


    }

    private void redrawSeasons() {
        contentPanel.clear();
        for (EnergyPlanSeason energyPlanSeason : energyPlan.getSeasonList()) {
            DemandChargeSeason season = new DemandChargeSeason(energyPlan, energyPlanSeason, valueChangeHandler);
            contentPanel.add(season);
        }
    }

    public void setActiveEnergyPlan(final EnergyPlan energyPlan) {
        setEnabled(false);
        this.energyPlan = energyPlan;

        if (energyPlan == null) {
            useDemandChargeCB.setChecked(false);
            demandChargePanel.getStyle().setDisplay(Style.Display.NONE);
            return;
        } else {
            if (energyPlan.getDemandPlanType().equals(DemandPlanType.NONE)) {
                demandChargePanel.getStyle().setDisplay(Style.Display.NONE);
            } else {
                demandChargePanel.getStyle().clearDisplay();
            }

            useDemandChargeCB.setChecked(!energyPlan.getDemandPlanType().equals(DemandPlanType.NONE));
            demandPlanTypeRadioManager.setValue(energyPlan.getDemandPlanType(), false);
            booleanTypeRadioManager.setValue(energyPlan.getDemandUseActivePower());

            saturdayCB.setChecked(energyPlan.getDemandApplicableSaturday());


            sundayCB.setChecked(energyPlan.getDemandApplicableSunday());
            holidayCB.setChecked(energyPlan.getDemandApplicableHoliday());
            offPeakCB.setChecked(energyPlan.getDemandApplicableOffPeak());
            avgListBox.setValue(energyPlan.getDemandAverageTime());
            stepListBox.setValue(energyPlan.getNumberDemandSteps());

            if (energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED) || energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED_PEAK)) {
                stepQuestionField.getStyle().clearDisplay();
            } else {
                stepQuestionField.getStyle().setDisplay(Style.Display.NONE);
            }
            redrawSeasons();
        }
        setEnabled(true);
    }

    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    public void setEnabled(boolean enabled) {
        for (int i = 0; i < contentPanel.getWidgetCount(); i++)
            ((DemandChargeSeason) contentPanel.getWidget(i)).setEnabled(enabled);
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> energyPlanItemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, energyPlanItemChangedHandler);
    }


    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, DemandChargePanel> {
    }


}

