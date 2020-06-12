/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.google.gwt.user.datepicker.client.CalendarUtil;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pete on 10/22/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarKey implements Serializable {
    int month;
    int date;
    int year;
    int hour;
    int min;


    transient Long localEpoch = null;


    public CalendarKey(int year, int month, int date, int hour, int min) {
        this.month = month;
        this.date = date;
        this.year = year;
        this.hour = hour;
        this.min = min;
    }

    public CalendarKey(Date theDate) {
        this(theDate.getYear() + 1900, theDate.getMonth(), theDate.getDate(), theDate.getHours(), theDate.getMinutes());
    }


    public CalendarKey() {
        this(2014, 0, 1, 0, 0);
    }

    public CalendarKey(int year, int month) {
        this(year, month, 1, 0, 0);
    }

    public CalendarKey(int year, int month, int date) {
        this(year, month, date, 0, 0);
    }


    private static void addMonthsToDate(Date date, int months) {
        if (months != 0) {
            int month = date.getMonth();
            int year = date.getYear();
            int resultMonthCount = year * 12 + month + months;
            int resultYear = (int) Math.floor((double) resultMonthCount / 12.0D);
            int resultMonth = resultMonthCount - resultYear * 12;
            date.setMonth(resultMonth);
            date.setYear(resultYear);
        }

    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarKey that = (CalendarKey) o;

        if (date != that.date) return false;
        if (hour != that.hour) return false;
        if (min != that.min) return false;
        if (month != that.month) return false;
        if (year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + date;
        result = 31 * result + year;
        result = 31 * result + hour;
        result = 31 * result + min;
        return result;
    }

    @Override
    public String toString() {
        return "CalendarKey{" +
                "year=" + year +
                ", month=" + month +
                ", date=" + date +
                ", hour=" + hour +
                ", min=" + min +
                '}';
    }

    @JsonIgnore
    public CalendarKey clone() {
        Date newDate = new Date(year - 1900, month, date, hour, min, 0);
        newDate = new Date(1000 * (newDate.getTime() / 1000));
        return new CalendarKey(newDate.getYear() + 1900, newDate.getMonth(), newDate.getDate(), newDate.getHours(), newDate.getMinutes());
    }

    @JsonIgnore
    public CalendarKey addDay(int day) {
        Date newDate = new Date(year - 1900, month, date, hour, min, 0);
        newDate = new Date(1000 * (newDate.getTime() / 1000));
        CalendarUtil.addDaysToDate(newDate, day);
        return new CalendarKey(newDate.getYear() + 1900, newDate.getMonth(), newDate.getDate(), newDate.getHours(), newDate.getMinutes());
    }

    @JsonIgnore
    public CalendarKey addMonth(int month) {
        Date newDate = new Date(year - 1900, this.month, date, hour, min, 0);
        newDate = new Date(1000 * (newDate.getTime() / 1000));
        CalendarUtil.addMonthsToDate(newDate, month);
        return new CalendarKey(newDate.getYear() + 1900, newDate.getMonth(), newDate.getDate(), newDate.getHours(), newDate.getMinutes());
    }

    public long toLocalEpoch() {
        if (localEpoch == null) {
            localEpoch = new Date(year - 1900, month, date, hour, min, 0).getTime() / 1000;
        }
        return localEpoch;
    }

    @JsonIgnore
    public int monthDiff(CalendarKey compareMonth) {
        //TODO: Ugly, but these should be small data sets.
        long se = toLocalEpoch() * 1000;
        long ee = compareMonth.toLocalEpoch() * 1000;

        //In case they are backwards
        if (ee < se) {
            ee = toLocalEpoch() * 1000;
            se = compareMonth.toLocalEpoch() * 1000;
        }

        Date eeDate = new Date(ee);
        //addMonthsToDate(eeDate, 1);
        ee = eeDate.getTime();

        int months = 0;
        while (se < ee) {
            Date seDate = new Date(se);
            CalendarUtil.addMonthsToDate(seDate, 1);
            se = seDate.getTime();
            months++;
        }
        return months;
    }
}
