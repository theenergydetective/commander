package com.ted.commander.client.widgets.timePicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.widgets.AMPMPicker;
import com.ted.commander.client.widgets.NumberPicker;

import java.util.logging.Logger;

public class TimePicker extends Composite {

    final static Logger LOGGER = Logger.getLogger(TimePicker.class.toString());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    AMPMPicker amPmField;
    @UiField
    NumberPicker minField;
    @UiField
    NumberPicker hourField;

    @UiConstructor
    public TimePicker(int interval) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setTime(int hour, int min) {
        if (hour > 12) {
            amPmField.setValue("PM", false);
            hourField.setValue(hour - 12, false);
        } else if (hour == 0) {
            amPmField.setValue("AM", false);
            hourField.setValue(12, false);
        } else if (hour == 12) {
            amPmField.setValue("PM", false);
            hourField.setValue(12, false);
        } else {
            amPmField.setValue("AM", false);
            hourField.setValue(hour, false);
        }
        minField.setValue(min, false);
    }

    public int getHour() {
        int hour = hourField.getValue();

        if (amPmField.getValue().equals("AM")) {
            if (hour == 12) hour = 0;
        } else {
            if (hour != 12) hour += 12;
        }

       return hour;
    }

    public int getMinute() {
        return minField.getValue();
    }

    public void addChangeHandler(ValueChangeHandler handler) {
        hourField.addValueChangeHandler(handler);
        minField.addValueChangeHandler(handler);
        amPmField.addValueChangeHandler(handler);
    }

    public void setEnabled(boolean b) {
        hourField.setEnabled(b);
        minField.setEnabled(b);
        amPmField.setEnabled(b);
    }

    @Override
    public void addStyleName(String style) {
        hourField.addStyleName(style);
        minField.addStyleName(style);
        amPmField.addStyleName(style);
    }

    @Override
    public void setStylePrimaryName(String style) {
        hourField.setStylePrimaryName(style);
        minField.setStylePrimaryName(style);
        amPmField.setStylePrimaryName(style);
    }

    @Override
    public void setWidth(String width) {
        hourField.setWidth(width);
        minField.setWidth(width);
        amPmField.setWidth(width);
    }

    interface MyUiBinder extends UiBinder<Widget, TimePicker> {
    }


}
