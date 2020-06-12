/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.model.DashboardDateRange;
import com.ted.commander.common.model.CalendarKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


public class MonthlyCalendar extends Composite {
    static final Logger LOGGER = Logger.getLogger(MonthlyCalendar.class.getName());
    static final DateTimeFormat yearMonthFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH);
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    VerticalPanel contentPanel;

    DashboardDateRange dashboardDateRange;
    int cellWidth = 0;
    int cellHeight = 0;

    int calendarHeight = 0;
    int calendarWidth = 0;

    HashMap<CalendarKey, CalendarCell> cellHashMap = new HashMap<CalendarKey, CalendarCell>();


    ItemSelectedHandler itemSelectedHandler = new ItemSelectedHandler<CalendarKey>() {
        @Override
        public void onSelected(ItemSelectedEvent<CalendarKey> event) {
            handlerManager.fireEvent(event);
        }
    };


    public MonthlyCalendar() {
        initWidget(defaultBinder.createAndBindUi(this));
    }

    public DashboardDateRange getDate() {
        return dashboardDateRange;
    }


    /**
     * This clears the calendar and draws it for the specified dates
     *
     * @param dashboardDateRange
     */
    public void setDashboardDateRange(DashboardDateRange dashboardDateRange) {


        clear();

        ArrayList<CalendarCellHeader> headerArrayList = new ArrayList<CalendarCellHeader>();


        HorizontalPanel headerPanel = new HorizontalPanel();
        contentPanel.add(headerPanel);


        //Draw the header row.
        for (int d = 0; d < 7; d++) {
            //Find the proper start of the work week based on locale.
            int dow = CalendarUtil.getStartingDayOfWeek() + d;
            if (dow >= 7) dow = 0;
            CalendarCellHeader header = new CalendarCellHeader(dow);
            headerPanel.add(header);
            headerArrayList.add(header);
        }

        //Draw the days of the month.
        Date calDate = new Date(dashboardDateRange.getStartDate().getTime());


        boolean firstCell = true;
        int weekCount = 1;
        HorizontalPanel weekPanel = new HorizontalPanel();
        contentPanel.add(weekPanel);
        while (calDate.getTime() <= dashboardDateRange.getEndDate().getTime()) {
            if (weekPanel.getWidgetCount() == 7) {
                //Draw a new week.
                weekPanel = new HorizontalPanel();
                contentPanel.add(weekPanel);
                weekCount++;
            }
            CalendarKey calendarKey = new CalendarKey(calDate.getYear() + 1900, calDate.getMonth(), calDate.getDate());
            CalendarCell calendarCell = new CalendarCell(calendarKey, (calendarKey.getMonth() % 2) == 0, firstCell);
            firstCell = false;
            calendarCell.addItemSelectedHandler(itemSelectedHandler);
            cellHashMap.put(calendarKey, calendarCell);
            CalendarUtil.addDaysToDate(calDate, 1);
            weekPanel.add(calendarCell);
        }

        if (calendarWidth == 0) calendarHeight = Window.getClientHeight();
        if (calendarWidth == 0) calendarWidth = Window.getClientWidth();


        double ratio = (double) Window.getClientWidth() / (double) Window.getClientHeight();
        LOGGER.fine("PIXEL RATIO IS: " + ratio + " calendarWidth:" + calendarWidth + " calendarHeight:" + calendarHeight);
        int cellHeight = calendarHeight / weekCount;
        int cellWidth = calendarWidth / 7;

        setCellSize(cellWidth, cellHeight);
        for (CalendarCellHeader header : headerArrayList) header.setWidth(cellWidth + "px");
    }

    /**
     * This sets the value of a specific cell field
     *
     * @param key
     * @param value
     * @param gradient
     */
    public void setValue(CalendarKey key, List<String> value, Double gradient, String color) {
        CalendarCell calendarCell = cellHashMap.get(key);
        if (calendarCell != null) {
            calendarCell.setValue(value, gradient, color);
        }

    }

    public void clear(){
        contentPanel.clear();
        cellHashMap.clear();
    }


    public void addItemSelectedHandler(ItemSelectedHandler<CalendarKey> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    public String getWidth() {
        return cellWidth + "px";
    }

    public void setCellSize(int cellWidth, int cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

        Logger.getLogger("").fine("CELL SIZE: " + cellWidth + " x " + cellHeight);

        for (CalendarCell calendarCell : cellHashMap.values()) {
            calendarCell.setCellSize(cellWidth, cellHeight);
        }

    }

    //Calculates the optimal calendar size based on the available dimensions
    public void setCalendarSize(int maxWidth, int maxHeight) {
        Logger.getLogger("").fine("CALENDAR SIZE: " + maxWidth + " x " + maxHeight);
        this.calendarWidth = maxWidth;
        this.calendarHeight = maxHeight;
    }

    public void clearValues() {
        for (CalendarCell calendarCell : cellHashMap.values()) {
            List<String> strings = new ArrayList<>();
            calendarCell.setValue(strings, 0, "transparent");
        }
    }

    //Skin mapping
    @UiTemplate("MonthlyCalendar.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, MonthlyCalendar> {
    }
}
