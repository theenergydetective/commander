/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;

import com.ted.commander.common.model.CalendarKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by pete on 11/3/2014.
 */
public class CalendarUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarUtils.class);


    public static void zeroTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }


    public static Calendar fromCalendarKey(CalendarKey calendarKey, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.YEAR, calendarKey.getYear());
        calendar.set(Calendar.MONTH, calendarKey.getMonth());
        calendar.set(Calendar.DATE, calendarKey.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, calendarKey.getHour());
        calendar.set(Calendar.MINUTE, calendarKey.getMin());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar fromCalendarKey(CalendarKey calendarKey, String tz) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(tz);

            return fromCalendarKey(calendarKey, timeZone);
        } catch (Exception ex) {
            LOGGER.error("Error parsing calendar key: {} tz:{}", calendarKey, tz);
            return null;
        }
    }

    public static int daysLeft(Calendar nowCalendar, Calendar endCalendar) {
        int daysLeft = (int) ((endCalendar.getTimeInMillis() - nowCalendar.getTimeInMillis()) / 86400000l);
        daysLeft--;
        if (daysLeft < 0) daysLeft = 0;
        return daysLeft;
    }

    public static boolean dateEquals(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DATE) == c2.get(Calendar.DATE);
    }

    public static TimeZone calculateTimezone(int offsetHours, boolean useDLST) {
        //Try US Timezones first
        for (String id : TimeZone.getAvailableIDs()) {
            if (!id.startsWith("US/")) continue;
            ;
            TimeZone timeZone = TimeZone.getTimeZone(id);
            if (useDLST == timeZone.useDaylightTime()) {
                double offset = timeZone.getRawOffset();
                offset /= 3600000; //convert to hours
                int timeZoneHours = (int) (offset * 10);
                LOGGER.trace("{}: OFFSET: {}, OFFSETHOURS:{}", new Object[]{id, offset, offsetHours});
                if (timeZoneHours == offsetHours && !id.equals("US/East-Indiana")) {
                    LOGGER.debug("Timezone match found: {}", timeZone.getID());
                    return timeZone;
                }
            }
        }

        //Try global timezones
        for (String id : TimeZone.getAvailableIDs()) {
            TimeZone timeZone = TimeZone.getTimeZone(id);
            if (useDLST == timeZone.useDaylightTime()) {
                double offset = timeZone.getRawOffset();
                offset /= 3600000; //convert to hours
                int timeZoneHours = (int) (offset * 10);
                LOGGER.trace("{}: OFFSET: {}, OFFSETHOURS:{}", new Object[]{id, offset, offsetHours});
                if (timeZoneHours == offsetHours) {
                    LOGGER.debug("Timezone match found: {}", timeZone.getID());
                    return timeZone;
                }
            }
        }


        LOGGER.warn("Could not find timezone for H:{} D:{}", offsetHours, useDLST);
        return TimeZone.getDefault();
    }

    public static CalendarKey keyFromMillis(long epoch, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(epoch);
        return new CalendarKey(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }

    public static CalendarKey keyFromDate(Date date, String timeZoneId) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        calendar.setTime(date);
        return new CalendarKey(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }

    public static long epochFromString(String startDateString, TimeZone timeZone) {
        String[] dates = startDateString.split("-");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.YEAR, Integer.parseInt(dates[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]));
        calendar.set(Calendar.DATE, Integer.parseInt(dates[2]));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis()/1000;


    }
}
