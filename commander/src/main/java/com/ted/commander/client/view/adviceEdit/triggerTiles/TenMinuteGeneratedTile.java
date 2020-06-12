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
import com.vaadin.polymer.paper.element.PaperInputElement;

import java.util.logging.Logger;


public class TenMinuteGeneratedTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(TenMinuteGeneratedTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;

    @UiField
    ListBox atMostField;
    @UiField
    PaperInputElement exceedsField;
    @UiField
    PaperInputElement hourField;


    final AdviceTrigger adviceTrigger;

    public TenMinuteGeneratedTile(final AdviceTrigger adviceTrigger) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));

        this.adviceTrigger = adviceTrigger;


        atMostField.setSelectedIndex(adviceTrigger.getSendAtMost().ordinal());

        exceedsField.addEventListener("change", new RangeEventListener(0, 100000, exceedsField));
        hourField.addEventListener("change", new RangeEventListener(0, 72, hourField));

        exceedsField.setValue(CommanderFormats.ADVICE_POWER.format(adviceTrigger.getAmount()));
        hourField.setValue(CommanderFormats.ADVICE_HOUR.format((double)adviceTrigger.getDelayMinutes()/60.0));

    }

    @Override
    public boolean validate() {
        exceedsField.setInvalid(false);
        if (exceedsField.getValue().trim().length() == 0) {
            exceedsField.setInvalid(true);
        }

        hourField.setInvalid(false);
        if (hourField.getValue().trim().length() == 0) {
            hourField.setInvalid(true);
        }
        return alarmRateWidget.validate() && !exceedsField.getInvalid() && !hourField.getInvalid();
    }

    @Override
    public void update() {
        alarmRateWidget.update();
        adviceTrigger.setSendAtMost(SendAtMostType.values()[atMostField.getSelectedIndex()]);
        adviceTrigger.setAmount(CommanderFormats.ADVICE_POWER.parse(exceedsField.getValue()));

        int delayMinutes = (int)(CommanderFormats.ADVICE_HOUR.parse(hourField.getValue())*60.0);
        adviceTrigger.setDelayMinutes(delayMinutes);
    }

    interface DefaultBinder extends UiBinder<Widget, TenMinuteGeneratedTile> {
    }


}
