/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.server.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Interface for common date/time functions (also allows it to be manipulated via Mocks)
 */
@Service
public class ClockService {

    static Logger LOGGER = LoggerFactory.getLogger(ClockService.class);

    public long getCurrentTimeInMillis(){
        return System.currentTimeMillis();
    }

    public long getEpochTime(){
        return System.currentTimeMillis()/1000;
    }

    public Calendar getCalendar(TimeZone timeZone){
        return Calendar.getInstance(timeZone);
    }

    public void zeroTime(Calendar calendar) {
        CalendarUtils.zeroTime(calendar);;
    }


    public Calendar fromCalendarKey(CalendarKey calendarKey, TimeZone timeZone) {
        return CalendarUtils.fromCalendarKey(calendarKey, timeZone);
    }

    public Calendar fromCalendarKey(CalendarKey calendarKey, String tz) {
        return CalendarUtils.fromCalendarKey(calendarKey, tz);
    }

    public boolean dateEquals(Calendar c1, Calendar c2) {
        return CalendarUtils.dateEquals(c1, c2);

    }

    public TimeZone calculateTimezone(int offsetHours, boolean useDLST) {
        return CalendarUtils.calculateTimezone(offsetHours, useDLST);
    }

    public CalendarKey keyFromMillis(long epoch, TimeZone timeZone) {
        return CalendarUtils.keyFromMillis(epoch, timeZone);
    }

    public CalendarKey keyFromDate(Date date, String timeZoneId) {
        return CalendarUtils.keyFromDate(date, timeZoneId);
    }

    public long epochFromString(String startDateString, TimeZone timeZone) {
        return CalendarUtils.epochFromString(startDateString, timeZone);
    }

}
