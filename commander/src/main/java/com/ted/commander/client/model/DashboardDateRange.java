/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.model;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.common.model.CalendarKey;

import java.util.Date;

/**
 * Created by pete on 10/31/2014.
 */
public class DashboardDateRange {

    final Date startDate;
    final Date endDate;
    final Date calendarDate;


    public DashboardDateRange(Date theDate) {
        if (theDate == null){
            theDate = new Date();
            theDate.setDate(1);
        }

        calendarDate = new Date(theDate.getYear(), theDate.getMonth(), theDate.getDate(), 0, 0, 0);
        CalendarUtil.resetTime(calendarDate);

        startDate = calendarDate;
        CalendarUtil.resetTime(startDate);

        while (startDate.getDay() != CalendarUtil.getStartingDayOfWeek()) {
            CalendarUtil.addDaysToDate(startDate, -1);
        }

        endDate = new Date(calendarDate.getTime());
        CalendarUtil.resetTime(endDate);
        CalendarUtil.addMonthsToDate(endDate, 1);

        int endDayOfWeek = CalendarUtil.getStartingDayOfWeek();
        if (endDayOfWeek == 0) endDayOfWeek = 6;
        else endDayOfWeek = endDayOfWeek - 1;

        if (endDate.getDay() != CalendarUtil.getStartingDayOfWeek()) {
            while (endDate.getDay() != endDayOfWeek) {
                CalendarUtil.addDaysToDate(endDate, 1);
            }
        }
    }

    public DashboardDateRange() {
        this(null);
    }


    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getCalendarDate() {
        return calendarDate;
    }

    public CalendarKey getStartDateKey() {
        return new CalendarKey(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate());

    }

    public CalendarKey getEndDateKey() {
        return new CalendarKey(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate());
    }

    public CalendarKey getCalendarKey() {
        return new CalendarKey(calendarDate.getYear() + 1900, calendarDate.getMonth(), 1);
    }

    @Override
    public String toString() {
        return "DashboardDateRange{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", calendarDate=" + calendarDate +
                '}';
    }
}
