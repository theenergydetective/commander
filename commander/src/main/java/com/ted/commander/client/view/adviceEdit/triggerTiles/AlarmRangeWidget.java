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
import com.ted.commander.common.model.AdviceTrigger;

import java.util.logging.Logger;


public class AlarmRangeWidget extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(AlarmRangeWidget.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final AdviceTrigger adviceTrigger;
    @UiField
    ListBox startTime;
    @UiField
    ListBox endTime;

    public AlarmRangeWidget(final AdviceTrigger adviceTrigger) {
        initWidget(defaultBinder.createAndBindUi(this));

        this.adviceTrigger = adviceTrigger;
        addItems(startTime);
        addItems(endTime);

        startTime.setSelectedIndex(adviceTrigger.getStartTime() / 15);
        endTime.setSelectedIndex(adviceTrigger.getEndTime() / 15);

    }

    private void balanceTime() {
        if (endTime.getSelectedIndex() < startTime.getSelectedIndex()) {
            endTime.setSelectedIndex(startTime.getSelectedIndex());
        }
    }

    private void addItems(ListBox listBox) {
        for (int m = 0; m < 1440; m = m + 15) {
            if (m == 0) {
                listBox.addItem("Midnight", "0");
            } else if (m == 720) {
                listBox.addItem("Noon", "720");
            } else {
                int hour = m / 60;
                int min = m % 60;
                boolean am = m < 720;
                if (hour > 12) hour -= 12;
                if (hour == 0) hour = 12;
                StringBuilder time = new StringBuilder().append(hour).append(":");
                if (min < 10) time.append("0");
                time.append(min);
                if (am) time.append(" AM");
                else time.append(" PM");
                listBox.addItem(time.toString(), m + "");
            }
        }
        listBox.addItem("Next AM", "1440");
    }

    @Override
    public boolean validate() {
        balanceTime();
        return true;
    }

    @Override
    public void update() {
        balanceTime();
        adviceTrigger.setStartTime(Integer.parseInt(startTime.getSelectedValue()));
        adviceTrigger.setEndTime(Integer.parseInt(endTime.getSelectedValue()));

    }

    interface DefaultBinder extends UiBinder<Widget, AlarmRangeWidget> {
    }


}
