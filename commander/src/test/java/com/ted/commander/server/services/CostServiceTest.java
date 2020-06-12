/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.*;

import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.TOUDifference;
import com.ted.commander.common.model.history.*;
import com.ted.commander.server.util.CalendarUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;

@RunWith(MockitoJUnitRunner.class)
public class CostServiceTest {

    @Mock
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Mock
    HistoryDayDAO historyDayDAO;

    @Mock
    HistoryHourDAO historyHourDAO;

    @Mock
    HistoryMinuteDAO historyMinuteDAO;

    @Mock
    HistoryMTUBillingCycleDAO historyMTUBillingCycleDAO;

    @Mock
    HistoryMTUDayDAO historyMTUDayDAO;

    @Mock
    HistoryMTUHourDAO historyMTUHourDAO;

    @Mock
    EnergyPlanService energyPlanService;

    @Mock
    VirtualECCDAO virtualECCDAO;

    @Mock
    VirtualECCMTUDAO virtualECCMTUDAO;

    @InjectMocks
    CostService costService;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        reset(historyBillingCycleDAO);
        reset(historyDayDAO);
        reset(historyHourDAO);
        reset(historyMinuteDAO);
        reset(historyMTUBillingCycleDAO);
        reset(historyMTUDayDAO);
        reset(historyMTUHourDAO);

        reset(energyPlanService);
        reset(virtualECCDAO);
        reset(virtualECCMTUDAO);
    }


    @Test
    public void testGetSeason() {
        EnergyPlan energyPlan = new EnergyPlan();
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        long epochTime = 1453532100l;
        assertEquals(0, costService.findSeason(energyPlan, timeZone, epochTime));

        energyPlan.setNumberSeasons(3);
        energyPlan.getSeasonList().add(new EnergyPlanSeason(0, "Spring", 3, 15));
        energyPlan.getSeasonList().add(new EnergyPlanSeason(1, "Summer", 6, 15));
        energyPlan.getSeasonList().add(new EnergyPlanSeason(2, "Winter", 7, 15));

        assertEquals(0, costService.findSeason(energyPlan, timeZone, new Date(115, 0, 23).getTime() / 1000));
        assertEquals(0, costService.findSeason(energyPlan, timeZone, new Date(115, 3, 14).getTime() / 1000));
        assertEquals(0, costService.findSeason(energyPlan, timeZone, new Date(115, 3, 15).getTime() / 1000));
        assertEquals(1, costService.findSeason(energyPlan, timeZone, new Date(115, 3, 16).getTime() / 1000));
        assertEquals(1, costService.findSeason(energyPlan, timeZone, new Date(115, 6, 14).getTime() / 1000));
        assertEquals(1, costService.findSeason(energyPlan, timeZone, new Date(115, 6, 15).getTime() / 1000));
        assertEquals(2, costService.findSeason(energyPlan, timeZone, new Date(115, 6, 16).getTime() / 1000));
        assertEquals(0, costService.findSeason(energyPlan, timeZone, new Date(115, 7, 23).getTime() / 1000));

    }

    @Test
    public void testFindTOU() {
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setNumberSeasons(1);
        EnergyPlanSeason season1 = new EnergyPlanSeason(0, "Season 1", 3, 15);
        energyPlan.getSeasonList().add(season1);
        energyPlan.setPlanType(PlanType.TOU);
        energyPlan.setNumberTOU(3);
        season1.getTouList().add(new EnergyPlanTOU(TOUPeakType.PEAK, true, 0, 0l, 8, 0, 9, 45));
        season1.getTouList().add(new EnergyPlanTOU(TOUPeakType.PEAK, false, 0, 0l, 18, 0, 19, 45));
        season1.getTouList().add(new EnergyPlanTOU(TOUPeakType.MID_PEAK, true, 0, 0l, 9, 45, 10, 30));
        season1.getTouList().add(new EnergyPlanTOU(TOUPeakType.MID_PEAK, false, 0, 0l, 19, 45, 21, 0));



        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        calendar.set(2014, 11, 3, 0, 0, 0);
        CalendarUtils.zeroTime(calendar);

        energyPlan.setPlanType(PlanType.FLAT);
        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        energyPlan.setPlanType(PlanType.TIER);
        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        energyPlan.setPlanType(PlanType.TOU);


        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        calendar.set(2014, 11, 3, 8, 0, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        calendar.set(2014, 11, 3, 9, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        calendar.set(2014, 11, 3, 9, 45, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.MID_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        calendar.set(2014, 11, 3, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        calendar.set(2014, 11, 3, 19, 45, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.MID_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));
        calendar.set(2014, 11, 3, 21, 0, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));


        energyPlan.setTouApplicableSaturday(false);
        energyPlan.setTouApplicableSunday(false);
        energyPlan.setTouApplicableHoliday(false);

        calendar.set(2016, 11, 25, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));

        calendar.set(2016, 0, 16, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));

        calendar.set(2016, 0, 17, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.OFF_PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));

        energyPlan.setTouApplicableSaturday(true);
        energyPlan.setTouApplicableSunday(true);
        energyPlan.setTouApplicableHoliday(true);

        calendar.set(2016, 11, 25, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));

        calendar.set(2016, 0, 16, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));

        calendar.set(2016, 0, 17, 19, 44, 0);
        junit.framework.Assert.assertEquals(TOUPeakType.PEAK, costService.findTOU(energyPlan, 0, TimeZone.getTimeZone("US/Eastern"), calendar.getTimeInMillis() / 1000));


    }


    @Test
    public void testFindTier(){
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setNumberSeasons(1);
        assertEquals(0, costService.findTier(energyPlan, 0, 1000000000000.00));


        EnergyPlanSeason season1 = new EnergyPlanSeason(0, "Season 1", 3, 15);
        energyPlan.getSeasonList().add(season1);
        energyPlan.setPlanType(PlanType.TIER);
        energyPlan.setNumberTier(3);
        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.OFF_PEAK, .15));
        season1.getEnergyRateList().add(new EnergyRate(0,1, TOUPeakType.OFF_PEAK, .25));
        season1.getEnergyRateList().add(new EnergyRate(0,2, TOUPeakType.OFF_PEAK, .35));
        season1.getTierList().add(new EnergyPlanTier(0, 0, 1l, 10l));
        season1.getTierList().add(new EnergyPlanTier(1, 0, 1l, 20l));

        assertEquals(0, costService.findTier(energyPlan, 0, 0.0));
        assertEquals(0, costService.findTier(energyPlan, 0, 10000));
        assertEquals(1, costService.findTier(energyPlan, 0, 10001));

    }


    @Test
    public void testTiers(){
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setNumberSeasons(1);
        EnergyPlanSeason season1 = new EnergyPlanSeason(0, "Season 1", 3, 15);
        energyPlan.getSeasonList().add(season1);
        energyPlan.setPlanType(PlanType.TIER);
        energyPlan.setNumberTier(3);
        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.OFF_PEAK, .15));
        season1.getEnergyRateList().add(new EnergyRate(0,1, TOUPeakType.OFF_PEAK, .25));
        season1.getEnergyRateList().add(new EnergyRate(0,2, TOUPeakType.OFF_PEAK, .35));
        season1.getTierList().add(new EnergyPlanTier(0, 0, 1l, 10l));
        season1.getTierList().add(new EnergyPlanTier(1, 0, 1l, 20l));

        assertEquals(0.15, costService.findTierCost(energyPlan, 0,TOUPeakType.OFF_PEAK,  1000, 0 ,0), .01);
        assertEquals(1.50, costService.findTierCost(energyPlan, 0,TOUPeakType.OFF_PEAK,  10000, 0 , 0), .01);
        assertEquals(2.75, costService.findTierCost(energyPlan, 0,TOUPeakType.OFF_PEAK,  15000, 0 , 0), .01);
        assertEquals(5.75, costService.findTierCost(energyPlan, 0, TOUPeakType.OFF_PEAK, 25000, 0, 0), .01);

        //Test tax
        assertEquals(1.575, costService.findTierCost(energyPlan, 0,TOUPeakType.OFF_PEAK,  10000, 5 , 0), .01);

        //Test Surcharge
        assertEquals(2.5, costService.findTierCost(energyPlan, 0,TOUPeakType.OFF_PEAK,  10000, 0 , .1), .01);

    }




    @Test
    public void testFlatPlans(){
        EnergyPlan energyPlan = new EnergyPlan();

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setNet(10000.0);

        EnergyDifference energyDifference = new EnergyDifference();
        energyDifference.setNet(100000.0);
        energyDifference.setLoad(150000.0);
        energyDifference.setGeneration(-50000.0);

        TOUDifference touDifference = new TOUDifference();

        assertEquals(10.00, costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0).getNet(),.001);
        assertEquals(15.00, costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0).getLoad(), .001);
        assertEquals(-5.00, costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0).getGeneration(), .001);

        EnergyPlanSeason energyPlanSeason = new EnergyPlanSeason();
        EnergyRate energyRate = new EnergyRate(0, 0, TOUPeakType.OFF_PEAK, .20);
        energyPlanSeason.getEnergyRateList().add(energyRate);
        energyPlan.getSeasonList().add(energyPlanSeason);

        assertEquals(20.00, costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0).getNet(),.001);
        assertEquals(30.00, costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0).getLoad(), .001);
        assertEquals(-10.00, costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0).getGeneration(), .001);

    }



    @Test
    public void testTierPlans() {
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setNumberSeasons(1);
        EnergyPlanSeason season1 = new EnergyPlanSeason(0, "Season 1", 3, 15);
        energyPlan.getSeasonList().add(season1);
        energyPlan.setPlanType(PlanType.TIER);
        energyPlan.setNumberTier(3);
        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.OFF_PEAK, .10));
        season1.getEnergyRateList().add(new EnergyRate(0,1, TOUPeakType.OFF_PEAK, .20));
        season1.getEnergyRateList().add(new EnergyRate(0,2, TOUPeakType.OFF_PEAK, .30));
        season1.getTierList().add(new EnergyPlanTier(0, 0, 1l, 10l));
        season1.getTierList().add(new EnergyPlanTier(1, 0, 1l, 20l));

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setNet(5000.00);
        historyBillingCycle.setLoad(10000.00);
        historyBillingCycle.setGeneration(-5000.00);



        TOUDifference touDifference = new TOUDifference();
        EnergyDifference energyDifference = new EnergyDifference();
        energyDifference.setNet(10000.0);
        energyDifference.setLoad(15000.0);
        energyDifference.setGeneration(-5000.0);

        CostDifference costDifference = costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0);
        assertEquals(.20, costDifference.getRate(), .01);
        assertEquals(1.5, costDifference.getNet(),.01);
        assertEquals(2.0, costDifference.getLoad(), .01);
        assertEquals(-0.50, costDifference.getGeneration(), .01);


    }


    @Test
    public void testTOUPlans(){
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setNumberSeasons(1);
        EnergyPlanSeason season1 = new EnergyPlanSeason(0, "Season 1", 3, 15);
        energyPlan.getSeasonList().add(season1);
        energyPlan.setPlanType(PlanType.TOU);
        energyPlan.setNumberTOU(2);

        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.OFF_PEAK, .10));
        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.PEAK, .20));


        season1.getTouList().add(new EnergyPlanTOU());
        season1.getTouList().add(new EnergyPlanTOU());

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setNet(5000.00);
        historyBillingCycle.setLoad(10000.00);
        historyBillingCycle.setGeneration(-5000.00);

        EnergyDifference energyDifference = new EnergyDifference();
        energyDifference.setNet(10000.0);
        energyDifference.setLoad(15000.0);
        energyDifference.setGeneration(-5000.0);

        TOUDifference touDifference = new TOUDifference();
        touDifference.setTouPeakType(TOUPeakType.OFF_PEAK);
        touDifference.setNet(10000.0);
        touDifference.setLoad(15000.0);
        touDifference.setGeneration(-5000.0);

        CostDifference costDifference = costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0);
        assertEquals(.10, costDifference.getRate(), .01);
        assertEquals(1.0, costDifference.getNet(),.01);
        assertEquals(1.5, costDifference.getLoad(), .01);
        assertEquals(-0.50, costDifference.getGeneration(), .01);

        touDifference.setTouPeakType(TOUPeakType.PEAK);
        costDifference = costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0);
        assertEquals(.20, costDifference.getRate(), .01);
        assertEquals(2.0, costDifference.getNet(),.01);
        assertEquals(3.0, costDifference.getLoad(), .01);
        assertEquals(-1.00, costDifference.getGeneration(), .01);

    }




    @Test
    public void testTierTOUPlans(){
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setNumberSeasons(1);
        EnergyPlanSeason season1 = new EnergyPlanSeason(0, "Season 1", 3, 15);
        energyPlan.getSeasonList().add(season1);
        energyPlan.setPlanType(PlanType.TIERTOU);
        energyPlan.setNumberTOU(2);
        energyPlan.setNumberTier(3);
        season1.getTouList().add(new EnergyPlanTOU());
        season1.getTouList().add(new EnergyPlanTOU());

        energyPlan.setNumberTier(3);
        season1.getTierList().add(new EnergyPlanTier(0, 0, 1l, 10l));
        season1.getTierList().add(new EnergyPlanTier(1, 0, 1l, 20l));


        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.OFF_PEAK, .10));
        season1.getEnergyRateList().add(new EnergyRate(0,1, TOUPeakType.OFF_PEAK, .20));
        season1.getEnergyRateList().add(new EnergyRate(0,2, TOUPeakType.OFF_PEAK, .30));

        season1.getEnergyRateList().add(new EnergyRate(0,0, TOUPeakType.PEAK, 1.00));
        season1.getEnergyRateList().add(new EnergyRate(0,1, TOUPeakType.PEAK, 2.00));
        season1.getEnergyRateList().add(new EnergyRate(0,2, TOUPeakType.PEAK, 3.00));


        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setNet(5000.00);
        historyBillingCycle.setLoad(10000.00);
        historyBillingCycle.setGeneration(-5000.00);

        EnergyDifference energyDifference = new EnergyDifference();
        energyDifference.setNet(10000.0);
        energyDifference.setLoad(15000.0);
        energyDifference.setGeneration(-5000.0);

        TOUDifference touDifference = new TOUDifference();
        touDifference.setTouPeakType(TOUPeakType.OFF_PEAK);
        touDifference.setNet(10000.0);
        touDifference.setLoad(15000.0);
        touDifference.setGeneration(-5000.0);

        CostDifference costDifference = costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0);
        assertEquals(.20, costDifference.getRate(), .01);
        assertEquals(1.5, costDifference.getNet(),.01);
        assertEquals(2.0, costDifference.getLoad(), .01);
        assertEquals(-0.50, costDifference.getGeneration(), .01);

        touDifference.setTouPeakType(TOUPeakType.PEAK);
        costDifference = costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, 0);
        assertEquals(2.00, costDifference.getRate(), .01);
        assertEquals(15.0, costDifference.getNet(),.01);
        assertEquals(20.0, costDifference.getLoad(), .01);
        assertEquals(-5.00, costDifference.getGeneration(), .01);

    }


}

