/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;


import java.util.Calendar;

public class HolidayUtil {


    public static boolean isHoliday(Calendar theDate, boolean isCanadian) {


        if (!isCanadian) {
            if (CalendarUtils.dateEquals(theDate, NewYearsDay(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, PresidentsDayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, MemorialDayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, IndependenceDay(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, LaborDayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, VeteransDay(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, ThanksgivingObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, ChristmasDay(theDate.get(Calendar.YEAR)))) return true;
        } else {
            if (CalendarUtils.dateEquals(theDate, NewYearsDay(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, FamilyDayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, GoodFridayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, VictoriaDayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, CanadaDay(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, CivicHolidayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, LaborDayObserved(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, CanadianThanksgivingObserved(theDate.get(Calendar.YEAR))))
                return true;
            if (CalendarUtils.dateEquals(theDate, ChristmasDay(theDate.get(Calendar.YEAR)))) return true;
            if (CalendarUtils.dateEquals(theDate, BoxingDay(theDate.get(Calendar.YEAR)))) return true;

        }


        return false;
    }

    public static java.util.Calendar BoxingDay(int nYear) {
        int nMonth = 11; // Decmeber
        // December 26th
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nMonth, 26);
        CalendarUtils.zeroTime(cal);
        return cal;
    }

    public static java.util.Calendar CanadaDay(int nYear) {
        int nMonth = 6; // July 1st
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nMonth, 1);
        CalendarUtils.zeroTime(cal);
        return cal;
    }

    public static java.util.Calendar ChristmasDay(int nYear) {
        int nMonth = 11; // Decmeber
        // December 25th
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nMonth, 25);
        CalendarUtils.zeroTime(cal);
        return cal;
    }


    public static java.util.Calendar CivicHolidayObserved(int nYear) {
        // The first Monday in August
        return findDayOfWeek(nYear, 7, Calendar.MONDAY, 1);
    }


    public static java.util.Calendar CanadianThanksgivingObserved(int nYear) {
        // Second Monday in October
        return findDayOfWeek(nYear, 9, Calendar.MONDAY, 2);
    }

    public static java.util.Calendar GoodFridayObserved(int nYear) {
        // Get Easter Sunday and subtract two days
        int nEasterMonth = 0;
        int nEasterDay = 0;
        int nGoodFridayMonth = 0;
        int nGoodFridayDay = 0;
        java.util.Calendar cEasterSunday;

        cEasterSunday = EasterSunday(nYear);
        nEasterMonth = cEasterSunday.get(Calendar.MONTH);
        nEasterDay = cEasterSunday.get(Calendar.DAY_OF_MONTH);
        if (nEasterDay <= 3 && nEasterMonth == 3) { // Check if <= April 3rd

            switch (nEasterDay) {
                case 3:
                    nGoodFridayMonth = nEasterMonth - 1;
                    nGoodFridayDay = nEasterDay - 2;
                    break;
                case 2:
                    nGoodFridayMonth = nEasterMonth - 1;
                    nGoodFridayDay = 31;
                    break;
                case 1:
                    nGoodFridayMonth = nEasterMonth - 1;
                    nGoodFridayDay = 31;
                    break;
                default:
                    nGoodFridayMonth = nEasterMonth;
                    nGoodFridayDay = nEasterDay - 2;
            }
        } else {
            nGoodFridayMonth = nEasterMonth;
            nGoodFridayDay = nEasterDay - 2;
        }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nGoodFridayMonth, nGoodFridayDay);
        CalendarUtils.zeroTime(cal);
        return cal;
    }

    public static java.util.Calendar EasterSunday(int nYear) {
/*  Calculate Easter Sunday

  Written by Gregory N. Mirsky

  Source: 2nd Edition by Peter Duffett-Smith. It was originally from
  Butcher's Ecclesiastical Calendar, published in 1876. This
  algorithm has also been published in the 1922 book General
  Astronomy by Spencer Jones; in The Journal of the British
  Astronomical Association (Vol.88, page 91, December 1977); and in
  Astronomical Algorithms (1991) by Jean Meeus.

  This algorithm holds for any year in the Gregorian Calendar, which
  (of course) means years including and after 1583.

        a=year%19
        b=year/100
        c=year%100
        d=b/4
        e=b%4
        f=(b+8)/25
        g=(b-f+1)/3
        h=(19*a+b-d-g+15)%30
        i=c/4
        k=c%4
        l=(32+2*e+2*i-h-k)%7
        m=(a+11*h+22*l)/451
        Easter Month =(h+l-7*m+114)/31  [3=March, 4=April]
        p=(h+l-7*m+114)%31
        Easter Date=p+1     (date in Easter Month)

  Note: Integer truncation is already factored into the
  calculations. Using higher percision variables will cause
  inaccurate calculations.
*/

        int nA = 0;
        int nB = 0;
        int nC = 0;
        int nD = 0;
        int nE = 0;
        int nF = 0;
        int nG = 0;
        int nH = 0;
        int nI = 0;
        int nK = 0;
        int nL = 0;
        int nM = 0;
        int nP = 0;
        int nEasterMonth = 0;
        int nEasterDay = 0;

        // Calculate Easter
        if (nYear < 1900) {
            // if year is in java format put it into standard
            // format for the calculation
            nYear += 1900;
        }
        nA = nYear % 19;
        nB = nYear / 100;
        nC = nYear % 100;
        nD = nB / 4;
        nE = nB % 4;
        nF = (nB + 8) / 25;
        nG = (nB - nF + 1) / 3;
        nH = (19 * nA + nB - nD - nG + 15) % 30;
        nI = nC / 4;
        nK = nC % 4;
        nL = (32 + 2 * nE + 2 * nI - nH - nK) % 7;
        nM = (nA + 11 * nH + 22 * nL) / 451;

        //  [3=March, 4=April]
        nEasterMonth = (nH + nL - 7 * nM + 114) / 31;
        --nEasterMonth;
        nP = (nH + nL - 7 * nM + 114) % 31;

        // Date in Easter Month.
        nEasterDay = nP + 1;

        // Uncorrect for our earlier correction.
        nYear -= 1900;

        // Populate the date object...
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nEasterMonth, nEasterDay);
        CalendarUtils.zeroTime(cal);
        return cal;
    }

    public static java.util.Calendar IndependenceDay(int nYear) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        CalendarUtils.zeroTime(cal);
        cal.set(nYear, 6, 4);
        CalendarUtils.zeroTime(cal);
        return cal;
    }


    public static java.util.Calendar LaborDayObserved(int nYear) {
        // The first Monday in September
        return findDayOfWeek(nYear, 8, Calendar.MONDAY, 1);
    }

    public static java.util.Calendar MemorialDayObserved(int nYear) {
        // Last Monday in May
        Calendar c = findDayOfWeek(nYear, 5, Calendar.MONDAY, 1); //1st monday in june
        c.add(Calendar.DATE, -7);
        CalendarUtils.zeroTime(c);
        return c;
    }


    public static java.util.Calendar VictoriaDayObserved(int nYear) {
        // Last Monday in May
        Calendar c = findDayOfWeek(nYear, 5, Calendar.MONDAY, 1); //last 1st Monday in June
        while (c.get(Calendar.MONTH) == 5) c.add(Calendar.DATE, -7); // Last Monday in May
        if (c.get(Calendar.DATE) >= 25) c.add(Calendar.DATE, -7);
        CalendarUtils.zeroTime(c);
        return c;
    }


    public static java.util.Calendar NewYearsDay(int nYear) {
        // January 1st
        int nMonth = 0; // January
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nMonth, 1);
        CalendarUtils.zeroTime(cal);
        return cal;
    }

    public static java.util.Calendar VeteransDay(int nYear) {
        //November 11th
        int nMonth = 10; // November
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(nYear, nMonth, 11);
        CalendarUtils.zeroTime(cal);
        return cal;
    }


    public static java.util.Calendar PresidentsDayObserved(int nYear) {
        //Third Monday in Feb
        return findDayOfWeek(nYear, 1, Calendar.MONDAY, 3);
    }


    public static java.util.Calendar FamilyDayObserved(int nYear) {
        //Second Monday in Feb
        return findDayOfWeek(nYear, 1, Calendar.MONDAY, 2);
    }

    public static java.util.Calendar ThanksgivingObserved(int nYear) {
        //4th thursday in November
        return findDayOfWeek(nYear, 10, Calendar.THURSDAY, 4);
    }


    public static java.util.Calendar findDayOfWeek(int nYear, int nMonth, int dow, int instance) {
        java.util.Calendar cal;
        cal = java.util.Calendar.getInstance();
        cal.set(nYear, nMonth, 1);
        //TODO: Find a better way to do this w/out a while loop (add the days?)
        while (cal.get(Calendar.DAY_OF_WEEK) != dow) cal.add(Calendar.DATE, 1);
        cal.add(Calendar.DATE, (instance - 1) * 7);
        CalendarUtils.zeroTime(cal);
        return cal;
    }
}


