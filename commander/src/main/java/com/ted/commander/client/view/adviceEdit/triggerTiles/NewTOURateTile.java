/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit.triggerTiles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanTOULevel;
import com.vaadin.polymer.paper.element.PaperInputElement;
import com.vaadin.polymer.paper.element.PaperToggleButtonElement;

import java.util.logging.Logger;


public class NewTOURateTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(NewTOURateTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;
    @UiField
    PaperInputElement minutePicker;
    @UiField
    DivElement offPeakName;
    @UiField
    DivElement peakName;
    @UiField
    DivElement midPeakName;
    @UiField
    DivElement superPeakName;
    @UiField
    DivElement offPeakDiv;
    @UiField
    DivElement peakDiv;
    @UiField
    DivElement midPeakDiv;
    @UiField
    DivElement superPeakDiv;
    @UiField
    DivElement notTOUDiv;
    @UiField
    DivElement touDiv;
    @UiField
    PaperToggleButtonElement offPeakToggle;
    @UiField
    PaperToggleButtonElement peakToggle;
    @UiField
    PaperToggleButtonElement midPeakToggle;
    @UiField
    PaperToggleButtonElement superPeakToggle;

    final EnergyPlan energyPlan;
    final AdviceTrigger adviceTrigger;

    public NewTOURateTile(final AdviceTrigger adviceTrigger, final EnergyPlan energyPlan) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));
        this.energyPlan = energyPlan;
        this.adviceTrigger = adviceTrigger;

        minutePicker.setValue(adviceTrigger.getMinutesBefore() + "");
        minutePicker.addEventListener("change", new RangeEventListener(0, 120, minutePicker));

        offPeakToggle.setChecked(adviceTrigger.isOffPeakApplicable());
        peakToggle.setChecked(adviceTrigger.isPeakApplicable());
        midPeakToggle.setChecked(adviceTrigger.isMidPeakApplicable());
        superPeakToggle.setChecked(adviceTrigger.isSuperPeakApplicable());



        switch (energyPlan.getPlanType()){
            case FLAT:
            case TIER:
                touDiv.getStyle().setDisplay(Style.Display.NONE);
                break;
            default:
                notTOUDiv.getStyle().setDisplay(Style.Display.NONE);
                if (energyPlan.getTouLevels().size() == 2){
                    midPeakDiv.getStyle().setDisplay(Style.Display.NONE);
                    superPeakDiv.getStyle().setDisplay(Style.Display.NONE);
                    midPeakToggle.setChecked(false);
                    superPeakToggle.setChecked(false);

                }
                if (energyPlan.getTouLevels().size() == 3){
                    superPeakDiv.getStyle().setDisplay(Style.Display.NONE);
                    superPeakToggle.setChecked(false);
                }

                for (EnergyPlanTOULevel energyPlanTOULevel: energyPlan.getTouLevels()){
                    switch(energyPlanTOULevel.getPeakType()){
                        case OFF_PEAK:
                            offPeakName.setInnerText(energyPlanTOULevel.getTouLevelName());
                            break;
                        case PEAK:
                            peakName.setInnerText(energyPlanTOULevel.getTouLevelName());
                            break;
                        case MID_PEAK:
                            midPeakName.setInnerText(energyPlanTOULevel.getTouLevelName());
                            break;
                        case SUPER_PEAK:
                            superPeakName.setInnerText(energyPlanTOULevel.getTouLevelName());
                            break;
                    }

                }
        }






    }

    @Override
    public boolean validate() {
        minutePicker.setInvalid(false);
        if (minutePicker.getValue().trim().length() == 0){
            minutePicker.setInvalid(true);
        }

        if (minutePicker.getInvalid() || !alarmRateWidget.validate()) {
            return false;
        }
        return energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU);
    }

    @Override
    public void update() {
        alarmRateWidget.update();
        adviceTrigger.setMinutesBefore(Integer.parseInt(minutePicker.getValue()));
        if (adviceTrigger.getMinutesBefore() > 120) adviceTrigger.setMinutesBefore(120);
        adviceTrigger.setOffPeakApplicable(offPeakToggle.getChecked());
        adviceTrigger.setPeakApplicable(peakToggle.getChecked());
        adviceTrigger.setMidPeakApplicable(midPeakToggle.getChecked());
        adviceTrigger.setSuperPeakApplicable(superPeakToggle.getChecked());
    }

    interface DefaultBinder extends UiBinder<Widget, NewTOURateTile> {
    }


}
