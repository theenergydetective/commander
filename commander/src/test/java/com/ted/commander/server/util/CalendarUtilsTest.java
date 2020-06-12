/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;

import static org.junit.Assert.*;

import com.ted.commander.common.model.CalendarKey;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by pete on 11/3/2014.
 */
public class CalendarUtilsTest {


    @Test
    public void testZeroTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 11);
        calendar.set(Calendar.SECOND, 44);
        calendar.set(Calendar.MILLISECOND, 555);
        CalendarUtils.zeroTime(calendar);
        assertFalse(0==calendar.get(Calendar.YEAR));
        assertFalse(0==calendar.get(Calendar.MONTH));
        assertFalse(0==calendar.get(Calendar.DATE));
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void testFromCalendarKey(){
        CalendarKey calendarKey = new CalendarKey(2014, 1,1,13,11);
        Calendar calendar = CalendarUtils.fromCalendarKey(calendarKey, "US/Pacific");
        assertEquals(2014, calendar.get(Calendar.YEAR));
        assertEquals(1, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DATE));
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(11, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void testDaysLeft(){
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.YEAR, 2014);
        endCalendar.set(Calendar.MONTH, 10);
        endCalendar.set(Calendar.DATE, 31);
        CalendarUtils.zeroTime(endCalendar);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(Calendar.YEAR, 2014);
        nowCalendar.set(Calendar.MONTH, 10);
        nowCalendar.set(Calendar.DATE, 3);
        CalendarUtils.zeroTime(nowCalendar);
        assertEquals(27, CalendarUtils.daysLeft(nowCalendar, endCalendar));

        nowCalendar.set(Calendar.YEAR, 2014);
        nowCalendar.set(Calendar.MONTH, 11);
        nowCalendar.set(Calendar.DATE, 1);
        assertEquals(0, CalendarUtils.daysLeft(nowCalendar, endCalendar));


        nowCalendar.set(Calendar.YEAR, 2014);
        nowCalendar.set(Calendar.MONTH, 11);
        nowCalendar.set(Calendar.DATE, 2);
        assertEquals(0, CalendarUtils.daysLeft(nowCalendar, endCalendar));

    }

    @Test
    public void testTimezoneMatch(){
        assertEquals("US/Eastern", CalendarUtils.calculateTimezone(-50, true).getID());
        assertEquals("US/Central", CalendarUtils.calculateTimezone(-60, true).getID());
        assertEquals("US/Mountain", CalendarUtils.calculateTimezone(-70, true).getID());
        assertEquals("US/Pacific", CalendarUtils.calculateTimezone(-80, true).getID());
        assertEquals("US/Atlantic", CalendarUtils.calculateTimezone(-45, false).getID());
    }
}
