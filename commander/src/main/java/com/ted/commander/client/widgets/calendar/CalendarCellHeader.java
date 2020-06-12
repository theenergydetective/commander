/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.resources.WebStringResource;

/**
 * Created by pete on 10/4/2014.
 */
public class CalendarCellHeader extends Composite {
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    @UiField
    SimplePanel contentField;
    @UiField
    Label dayOfWeekField;
    @UiField
    WebStringResource stringRes;


    public CalendarCellHeader(int dayOfWeek) {
        initWidget(defaultBinder.createAndBindUi(this));


        switch (dayOfWeek) {
            case 0:
                dayOfWeekField.setText(stringRes.sunday());
                break;
            case 1:
                dayOfWeekField.setText(stringRes.monday());
                break;
            case 2:
                dayOfWeekField.setText(stringRes.tuesday());
                break;
            case 3:
                dayOfWeekField.setText(stringRes.wednesday());
                break;
            case 4:
                dayOfWeekField.setText(stringRes.thursday());
                break;
            case 5:
                dayOfWeekField.setText(stringRes.friday());
                break;
            case 6:
                dayOfWeekField.setText(stringRes.saturday());
                break;
        }

    }

    @Override
    public void setWidth(String width) {
        contentField.setWidth(width);
        dayOfWeekField.setWidth(width);
    }

    //Skin mapping
    @UiTemplate("CalendarCellHeader.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, CalendarCellHeader> {
    }
}
