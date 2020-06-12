/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by pete on 11/3/2014.
 */
public class HolidayUtilTest {


    @Test
    public void testIndependenceDay(){
        assertEquals(6, HolidayUtil.IndependenceDay(2014).get(Calendar.MONTH));
        assertEquals(4, HolidayUtil.IndependenceDay(2014).get(Calendar.DATE));
    }


    @Test
    public void testBoxingDay(){
        assertEquals(11, HolidayUtil.BoxingDay(2014).get(Calendar.MONTH));
        assertEquals(26, HolidayUtil.BoxingDay(2014).get(Calendar.DATE));
    }


    @Test
    public void testNewYearsDay(){
        assertEquals(0, HolidayUtil.NewYearsDay(2014).get(Calendar.MONTH));
        assertEquals(1, HolidayUtil.NewYearsDay(2014).get(Calendar.DATE));
    }


    @Test
    public void testVeteransDay(){
        assertEquals(10, HolidayUtil.VeteransDay(2014).get(Calendar.MONTH));
        assertEquals(11, HolidayUtil.VeteransDay(2014).get(Calendar.DATE));
    }


    @Test
    public void testCanadaDay(){
        assertEquals(6, HolidayUtil.CanadaDay(2014).get(Calendar.MONTH));
        assertEquals(1, HolidayUtil.CanadaDay(2014).get(Calendar.DATE));
    }

    @Test
    public void testChristmasDay(){
        assertEquals(11, HolidayUtil.ChristmasDay(2014).get(Calendar.MONTH));
        assertEquals(25, HolidayUtil.ChristmasDay(2014).get(Calendar.DATE));
    }


    @Test
    public void testCivicHolidayDay(){
        assertEquals(7, HolidayUtil.CivicHolidayObserved(2013).get(Calendar.MONTH));assertEquals(5, HolidayUtil.CivicHolidayObserved(2013).get(Calendar.DATE));
        assertEquals(7, HolidayUtil.CivicHolidayObserved(2014).get(Calendar.MONTH));assertEquals(4, HolidayUtil.CivicHolidayObserved(2014).get(Calendar.DATE));
        assertEquals(7, HolidayUtil.CivicHolidayObserved(2015).get(Calendar.MONTH));assertEquals(3, HolidayUtil.CivicHolidayObserved(2015).get(Calendar.DATE));
        assertEquals(7, HolidayUtil.CivicHolidayObserved(2016).get(Calendar.MONTH));assertEquals(1, HolidayUtil.CivicHolidayObserved(2016).get(Calendar.DATE));

    }

    @Test
    public void testCanadianThanksgiving(){
        assertEquals(9, HolidayUtil.CanadianThanksgivingObserved(2013).get(Calendar.MONTH));assertEquals(14, HolidayUtil.CanadianThanksgivingObserved(2013).get(Calendar.DATE));
        assertEquals(9, HolidayUtil.CanadianThanksgivingObserved(2014).get(Calendar.MONTH));assertEquals(13, HolidayUtil.CanadianThanksgivingObserved(2014).get(Calendar.DATE));
        assertEquals(9, HolidayUtil.CanadianThanksgivingObserved(2015).get(Calendar.MONTH));assertEquals(12, HolidayUtil.CanadianThanksgivingObserved(2015).get(Calendar.DATE));
        assertEquals(9, HolidayUtil.CanadianThanksgivingObserved(2016).get(Calendar.MONTH));assertEquals(10, HolidayUtil.CanadianThanksgivingObserved(2016).get(Calendar.DATE));
    }


    @Test
    public void testGoodFridayObserved(){
        assertEquals(2, HolidayUtil.GoodFridayObserved(2013).get(Calendar.MONTH));assertEquals(29, HolidayUtil.GoodFridayObserved(2013).get(Calendar.DATE));
        assertEquals(3, HolidayUtil.GoodFridayObserved(2014).get(Calendar.MONTH));assertEquals(18, HolidayUtil.GoodFridayObserved(2014).get(Calendar.DATE));
        assertEquals(3, HolidayUtil.GoodFridayObserved(2015).get(Calendar.MONTH));assertEquals(3, HolidayUtil.GoodFridayObserved(2015).get(Calendar.DATE));
        assertEquals(2, HolidayUtil.GoodFridayObserved(2016).get(Calendar.MONTH));assertEquals(25, HolidayUtil.GoodFridayObserved(2016).get(Calendar.DATE));
    }


    @Test
    public void testLaborDay(){
        assertEquals(8, HolidayUtil.LaborDayObserved(2013).get(Calendar.MONTH));assertEquals(2, HolidayUtil.LaborDayObserved(2013).get(Calendar.DATE));
        assertEquals(8, HolidayUtil.LaborDayObserved(2014).get(Calendar.MONTH));assertEquals(1, HolidayUtil.LaborDayObserved(2014).get(Calendar.DATE));
        assertEquals(8, HolidayUtil.LaborDayObserved(2015).get(Calendar.MONTH));assertEquals(7, HolidayUtil.LaborDayObserved(2015).get(Calendar.DATE));
        assertEquals(8, HolidayUtil.LaborDayObserved(2016).get(Calendar.MONTH));assertEquals(5, HolidayUtil.LaborDayObserved(2016).get(Calendar.DATE));
    }
    @Test
    public void testMemorialDay(){
        assertEquals(4, HolidayUtil.MemorialDayObserved(2013).get(Calendar.MONTH));assertEquals(27, HolidayUtil.MemorialDayObserved(2013).get(Calendar.DATE));
        assertEquals(4, HolidayUtil.MemorialDayObserved(2014).get(Calendar.MONTH));assertEquals(26, HolidayUtil.MemorialDayObserved(2014).get(Calendar.DATE));
        assertEquals(4, HolidayUtil.MemorialDayObserved(2015).get(Calendar.MONTH));assertEquals(25, HolidayUtil.MemorialDayObserved(2015).get(Calendar.DATE));
        assertEquals(4, HolidayUtil.MemorialDayObserved(2016).get(Calendar.MONTH));assertEquals(30, HolidayUtil.MemorialDayObserved(2016).get(Calendar.DATE));
    }

    @Test
    public void testVictoriaDay(){
        assertEquals(4, HolidayUtil.VictoriaDayObserved(2013).get(Calendar.MONTH));assertEquals(20, HolidayUtil.VictoriaDayObserved(2013).get(Calendar.DATE));
        assertEquals(4, HolidayUtil.VictoriaDayObserved(2014).get(Calendar.MONTH));assertEquals(19, HolidayUtil.VictoriaDayObserved(2014).get(Calendar.DATE));
        assertEquals(4, HolidayUtil.VictoriaDayObserved(2015).get(Calendar.MONTH));assertEquals(18, HolidayUtil.VictoriaDayObserved(2015).get(Calendar.DATE));
        assertEquals(4, HolidayUtil.VictoriaDayObserved(2016).get(Calendar.MONTH));assertEquals(23, HolidayUtil.VictoriaDayObserved(2016).get(Calendar.DATE));
    }


    @Test
    public void testPresidentsDay(){
        assertEquals(1, HolidayUtil.PresidentsDayObserved(2013).get(Calendar.MONTH));assertEquals(18, HolidayUtil.PresidentsDayObserved(2013).get(Calendar.DATE));
        assertEquals(1, HolidayUtil.PresidentsDayObserved(2014).get(Calendar.MONTH));assertEquals(17, HolidayUtil.PresidentsDayObserved(2014).get(Calendar.DATE));
        assertEquals(1, HolidayUtil.PresidentsDayObserved(2015).get(Calendar.MONTH));assertEquals(16, HolidayUtil.PresidentsDayObserved(2015).get(Calendar.DATE));
        assertEquals(1, HolidayUtil.PresidentsDayObserved(2016).get(Calendar.MONTH));assertEquals(15, HolidayUtil.PresidentsDayObserved(2016).get(Calendar.DATE));
    }

    @Test
    public void testFamilyDay(){
        assertEquals(1, HolidayUtil.FamilyDayObserved(2013).get(Calendar.MONTH));assertEquals(11, HolidayUtil.FamilyDayObserved(2013).get(Calendar.DATE));
        assertEquals(1, HolidayUtil.FamilyDayObserved(2014).get(Calendar.MONTH));assertEquals(10, HolidayUtil.FamilyDayObserved(2014).get(Calendar.DATE));
        assertEquals(1, HolidayUtil.FamilyDayObserved(2015).get(Calendar.MONTH));assertEquals(9, HolidayUtil.FamilyDayObserved(2015).get(Calendar.DATE));
        assertEquals(1, HolidayUtil.FamilyDayObserved(2016).get(Calendar.MONTH));assertEquals(8, HolidayUtil.FamilyDayObserved(2016).get(Calendar.DATE));
    }


    @Test
    public void testThanksgiving(){
        assertEquals(10, HolidayUtil.ThanksgivingObserved(2013).get(Calendar.MONTH));assertEquals(28, HolidayUtil.ThanksgivingObserved(2013).get(Calendar.DATE));
        assertEquals(10, HolidayUtil.ThanksgivingObserved(2014).get(Calendar.MONTH));assertEquals(27, HolidayUtil.ThanksgivingObserved(2014).get(Calendar.DATE));
        assertEquals(10, HolidayUtil.ThanksgivingObserved(2015).get(Calendar.MONTH));assertEquals(26, HolidayUtil.ThanksgivingObserved(2015).get(Calendar.DATE));
        assertEquals(10, HolidayUtil.ThanksgivingObserved(2016).get(Calendar.MONTH));assertEquals(24, HolidayUtil.ThanksgivingObserved(2016).get(Calendar.DATE));
    }


    @Test
    public void testIsHoliday(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 8, 10);
        assertFalse(HolidayUtil.isHoliday(calendar, false));
        assertFalse(HolidayUtil.isHoliday(calendar, true));

        calendar.set(2014, 11, 25);
        assertTrue(HolidayUtil.isHoliday(calendar, false));
        assertTrue(HolidayUtil.isHoliday(calendar, true));

        calendar.set(2014, 6, 1);
        assertFalse(HolidayUtil.isHoliday(calendar, false));
        assertTrue(HolidayUtil.isHoliday(calendar, true));
    }
}
