/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit.triggerTiles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.common.model.AdviceTrigger;

import java.util.logging.Logger;


public class RateChangeTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(RateChangeTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;

    final AdviceTrigger adviceTrigger;

    public RateChangeTile(final AdviceTrigger adviceTrigger) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));
        this.adviceTrigger = adviceTrigger;
    }

    @Override
    public boolean validate() {
        return alarmRateWidget.validate();
    }

    @Override
    public void update() {
        alarmRateWidget.update();
    }

    interface DefaultBinder extends UiBinder<Widget, RateChangeTile> {
    }


}
