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
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;

import java.util.logging.Logger;


public class CommanderNoPostTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(CommanderNoPostTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;

    @UiField
    ListBox atMostField;

    @UiField
    PaperInputElement hourField;
    @UiField
    PaperRadioGroupElement allMTUField;


    final AdviceTrigger adviceTrigger;




    public CommanderNoPostTile(final AdviceTrigger adviceTrigger) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));

        this.adviceTrigger = adviceTrigger;
        atMostField.setSelectedIndex(adviceTrigger.getSendAtMost().ordinal());
        allMTUField.setSelected(adviceTrigger.isAllMTUs()?"0":"1");
        if (adviceTrigger.getDelayMinutes() < 60) adviceTrigger.setDelayMinutes(60);
        hourField.setValue(CommanderFormats.ADVICE_HOUR.format((double)adviceTrigger.getDelayMinutes()/60.0));

        hourField.addEventListener("change", new RangeEventListener(1, 120, hourField));



    }

    @Override
    public boolean validate() {

        hourField.setInvalid(false);
        if (hourField.getValue().trim().length() == 0){
            hourField.setInvalid(true);
        }

        if (hourField.getInvalid() || !alarmRateWidget.validate()) {
            return false;
        }
        return true;
    }

    @Override
    public void update() {
        alarmRateWidget.update();
        adviceTrigger.setSendAtMost(SendAtMostType.values()[atMostField.getSelectedIndex()]);
        int delayMinutes =  (int)Math.round(CommanderFormats.ADVICE_HOUR.parse(hourField.getValue()) * 60);
        adviceTrigger.setDelayMinutes(delayMinutes);
        adviceTrigger.setAllMTUs(allMTUField.getSelected().equals("0"));
    }

    interface DefaultBinder extends UiBinder<Widget, CommanderNoPostTile> {
    }


}
