/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit.triggerTiles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.common.enums.SendAtMostType;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.vaadin.polymer.paper.element.PaperInputElement;

import java.util.logging.Logger;


public class RealTimePowerTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(RealTimePowerTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;


    @UiField
    ListBox atMostField;
    @UiField
    PaperInputElement exceedsField;

    final EnergyPlan energyPlan;
    final AdviceTrigger adviceTrigger;

    public RealTimePowerTile(final AdviceTrigger adviceTrigger, final EnergyPlan energyPlan) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));
        this.energyPlan = energyPlan;
        this.adviceTrigger = adviceTrigger;


        atMostField.setSelectedIndex(adviceTrigger.getSendAtMost().ordinal());

        exceedsField.addEventListener("change", new RangeEventListener(0, 1000000, exceedsField));
        exceedsField.setValue(CommanderFormats.ADVICE_POWER.format(adviceTrigger.getAmount()));

    }

    @Override
    public boolean validate() {
        exceedsField.setInvalid(false);
        if (exceedsField.getValue().trim().length() == 0) {
            exceedsField.setInvalid(true);
        }
        return alarmRateWidget.validate() && !exceedsField.getInvalid();
    }

    @Override
    public void update() {
        alarmRateWidget.update();
        adviceTrigger.setSendAtMost(SendAtMostType.values()[atMostField.getSelectedIndex()]);
        adviceTrigger.setAmount(CommanderFormats.ADVICE_POWER.parse(exceedsField.getValue()));
    }

    interface DefaultBinder extends UiBinder<Widget, RealTimePowerTile> {
    }


}
