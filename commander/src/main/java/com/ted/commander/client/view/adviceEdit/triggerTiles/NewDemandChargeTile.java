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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.util.CurrencyUtil;
import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.enums.SendAtMostType;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.vaadin.polymer.paper.element.PaperInputElement;

import java.util.logging.Logger;


public class NewDemandChargeTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(NewDemandChargeTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;
    @UiField
    PaperInputElement delayPicker;
    @UiField
    PaperInputElement minAmountPicker;

    @UiField
    ListBox atMostField;
    @UiField
    DivElement notDemandDiv;
    @UiField
    DivElement demandDiv;

    final EnergyPlan energyPlan;
    final AdviceTrigger adviceTrigger;

    public NewDemandChargeTile(final AdviceTrigger adviceTrigger, final EnergyPlan energyPlan) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));
        this.energyPlan = energyPlan;
        this.adviceTrigger = adviceTrigger;


        if (energyPlan.getDemandPlanType().equals(DemandPlanType.NONE)) {
            demandDiv.getStyle().setDisplay(Style.Display.NONE);
        } else {
            notDemandDiv.getStyle().setDisplay(Style.Display.NONE);
        }

        minAmountPicker.setLabel("Minimum Amount (" + CurrencyUtil.getCurrencySymbol(energyPlan.getRateType()) + ")");

        delayPicker.addEventListener("change", new RangeEventListener(0, 120, delayPicker));
        minAmountPicker.addEventListener("change", new RangeEventListener(0, 10000, minAmountPicker));

        atMostField.setSelectedIndex(adviceTrigger.getSendAtMost().ordinal());
        delayPicker.setValue(CommanderFormats.ADVICE_HOUR.format((double)adviceTrigger.getDelayMinutes()/60.0));
        minAmountPicker.setValue(CommanderFormats.ADVICE_POWER.format(adviceTrigger.getAmount()));
    }

    @Override
    public boolean validate() {

        delayPicker.setInvalid(false);
        if (delayPicker.getValue().trim().length() == 0){
            delayPicker.setInvalid(true);
        }

        minAmountPicker.setInvalid(false);
        if (minAmountPicker.getValue().trim().length() == 0){
            minAmountPicker.setInvalid(true);
        }

        if (delayPicker.getInvalid() || minAmountPicker.getInvalid() || !alarmRateWidget.validate()) {
            return false;
        }

        alarmRateWidget.validate();
        return !energyPlan.getDemandPlanType().equals(DemandPlanType.NONE);
    }

    @Override
    public void update() {
        alarmRateWidget.update();
        adviceTrigger.setSendAtMost(SendAtMostType.values()[atMostField.getSelectedIndex()]);
        int delayMinutes = (int)CommanderFormats.ADVICE_HOUR.parse(delayPicker.getValue())/60;
        adviceTrigger.setDelayMinutes(delayMinutes);
        adviceTrigger.setAmount(CommanderFormats.ADVICE_POWER.parse(minAmountPicker.getValue()));
    }

    interface DefaultBinder extends UiBinder<Widget, NewDemandChargeTile> {
    }


}
