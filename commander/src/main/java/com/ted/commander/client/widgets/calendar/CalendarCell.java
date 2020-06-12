/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.CalendarKey;

import java.util.Date;
import java.util.List;

/**
 * Created by pete on 10/4/2014.
 */
public class CalendarCell extends Composite {
    final static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
    final static DateTimeFormat shortDateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final CalendarKey calendarKey;
    final boolean inMonth;
    final boolean firstCell;
    @UiField
    AbsolutePanel containerPanel;
    @UiField
    AbsolutePanel fillPanel;
    @UiField
    Label dayOfMonthField;
    @UiField
    AbsolutePanel textSpacer;
    @UiField
    HTML amountField;
    @UiField
    FocusPanel touchPanel;
    @UiField
    CalendarCellStyle calStyle;
    AbsolutePanel fillColorPanel;
    int cellWidth = 94;
    int cellHeight = 94;

    public CalendarCell(final CalendarKey calendarKey, final boolean inMonth, final boolean firstCell) {
        this.calendarKey = calendarKey;
        this.inMonth = inMonth;
        this.firstCell = firstCell;

        initWidget(defaultBinder.createAndBindUi(this));


        touchPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new ItemSelectedEvent<CalendarKey>(calendarKey));
            }
        });



    }


    public void setCellSize(int width, int height) {

        //Set the outer panel
        this.cellWidth = width - 2;
        this.cellHeight = height;
        containerPanel.setWidth(cellWidth + "px");
        containerPanel.setHeight(cellHeight + "px");
        fillPanel.setWidth("100%");
        fillPanel.setHeight(cellHeight + "px");
        textSpacer.setHeight(cellHeight + "px");
        touchPanel.setHeight(cellHeight + "px");
        touchPanel.setWidth(cellWidth + "px");

        containerPanel.getElement().getStyle().setProperty("maxWidth", cellWidth + "px");
        containerPanel.getElement().getStyle().setProperty("maxHeight", cellHeight + "px");


        if (!firstCell && calendarKey.getDate() != 1) {
            dayOfMonthField.setText(calendarKey.getDate() + "");
        } else {

            Date calDate = new Date();
            calDate.setDate(1);
            calDate.setYear(calendarKey.getYear() - 1900);
            calDate.setMonth(calendarKey.getMonth());
            calDate.setDate(calendarKey.getDate());

            CalendarUtil.resetTime(calDate);

            dayOfMonthField.setText(dateTimeFormat.format(calDate));

            if (dayOfMonthField.getElement().getClientWidth() >= cellWidth) {
                dayOfMonthField.setText(shortDateTimeFormat.format(calDate));
            }
        }


        amountField.getElement().getStyle().setColor("#00236a");


        fillColorPanel = new AbsolutePanel();
        amountField.setWidth(cellWidth + "px");

        if (!inMonth) {
            containerPanel.getElement().getStyle().setBackgroundColor("#f7f7f7");
        } else {
            containerPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
        }

        fillPanel.add(fillColorPanel, 0, 0);
    }

    private void drawGradient(double percent, String color) {
        fillPanel.remove(fillColorPanel);
        int h = ((int) ((double) cellHeight * percent));
        if (h < 0) h = 0;
        fillColorPanel.setSize(cellWidth + "px", h + "px");

        fillColorPanel.removeStyleName(calStyle.greenGradient());
        fillColorPanel.removeStyleName(calStyle.yellowGradient());
        fillColorPanel.removeStyleName(calStyle.redGradient());
        fillColorPanel.removeStyleName(calStyle.generation50Gradient());
        fillColorPanel.removeStyleName(calStyle.generation80Gradient());
        fillColorPanel.removeStyleName(calStyle.generation100Gradient());

        switch(color){
            case "green": {
                fillColorPanel.addStyleName(calStyle.greenGradient());
                break;
            }
            case "red": {
                fillColorPanel.addStyleName(calStyle.redGradient());
                break;
            }
            case "yellow": {
                fillColorPanel.addStyleName(calStyle.yellowGradient());
                break;
            }
            case "gen50": {
                fillColorPanel.addStyleName(calStyle.generation50Gradient());
                break;
            }
            case "gen80": {
                fillColorPanel.addStyleName(calStyle.generation80Gradient());
                break;
            }
            case "gen100": {
                fillColorPanel.addStyleName(calStyle.generation100Gradient());
                break;
            }
        }



        fillPanel.add(fillColorPanel, 0, cellHeight - h);
    }

    public void setValue(List<String> text, double percent, String color) {
        StringBuilder listBuilder = new StringBuilder();
        for (String s : text) {
            listBuilder.append(s).append("<br>");
        }

        amountField.setHTML(listBuilder.toString());
        if (amountField.getElement().getClientWidth() > cellWidth) {
            //TODO: Shrink text
        }


        if (percent > 0) {
            drawGradient(percent, color);
            fillColorPanel.setVisible(true);
        } else {
            fillColorPanel.setVisible(false);
        }
    }

    public void addItemSelectedHandler(ItemSelectedHandler<CalendarKey> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    //Skin mapping
    @UiTemplate("CalendarCell.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, CalendarCell> {
    }
}
