/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.datePicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.model.DateRange;
import com.petecode.common.client.widget.ValueChangeManager;
import com.petecode.common.client.widget.paper.PaperDateRangePicker;
import com.petecode.common.client.widget.paper.PaperMonthDateRangePicker;
import com.ted.commander.common.enums.HistoryType;


public class DatePicker extends Composite implements HasValue<DateRange>{
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    ValueChangeManager<DateRange> valueChangeManager = new ValueChangeManager<>();


    @UiField
    SimplePanel pickerPanel;

    HasValue<DateRange> currentWidget = null;


    public DatePicker() {
        initWidget(defaultBinder.createAndBindUi(this));
        setHistoryType(HistoryType.HOURLY);
    }

    public void setHistoryType(HistoryType historyType){

        int maxPicker = 3;
        if (Window.getClientWidth() < 1200) maxPicker = 2;
        if (Window.getClientWidth() < 800) maxPicker = 1;

        pickerPanel.clear();
        switch (historyType){
            case MINUTE:
                currentWidget = new PaperDateRangePicker(1);
                break;
            case HOURLY:
                currentWidget = new PaperDateRangePicker(maxPicker);
                break;
            case DAILY:
                currentWidget = new PaperDateRangePicker(maxPicker);
                break;
            case BILLING_CYCLE:
                currentWidget = new PaperMonthDateRangePicker();
                break;
        }

        pickerPanel.add((Widget)currentWidget);

        currentWidget.addValueChangeHandler(new ValueChangeHandler<DateRange>() {
            @Override
            public void onValueChange(ValueChangeEvent<DateRange> valueChangeEvent) {
                valueChangeManager.setValue(valueChangeEvent.getValue(), true);
            }
        });
        currentWidget.setValue(valueChangeManager.getValue(), false);
    }

    @Override
    public DateRange getValue() {
        return valueChangeManager.getValue();
    }

    @Override
    public void setValue(DateRange dateRange) {
        setValue(dateRange, false);
    }

    @Override
    public void setValue(DateRange dateRange, boolean b) {
        valueChangeManager.setValue(dateRange, b);
        if (currentWidget != null) currentWidget.setValue(dateRange, false);
    }


    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateRange> valueChangeHandler) {
        return valueChangeManager.addValueChangeHandler(valueChangeHandler);
    }

    interface DefaultBinder extends UiBinder<Widget, DatePicker> {
    }
}
