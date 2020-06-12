/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryQuery;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class HistoryServiceTest {

    @Mock
    VirtualECCDAO virtualECCDAO;

    @Mock
    EnergyPlanService energyPlanService;

    @Mock
    HazelcastService hazelcastService;

    @Mock
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Mock
    CostService costService;

    @Mock
    WeatherService weatherService;

    @InjectMocks
    HistoryService historyService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reset(weatherService);
        reset(costService);
        reset(virtualECCMTUDAO);
        reset(hazelcastService);
        reset(energyPlanService);
        reset(virtualECCDAO);
    }


    @Test
    public void testDefaultCalculateBillingCycles(){

        //Set up the billing cycle
        HistoryQuery historyQuery = new HistoryQuery();
        historyQuery.setVirtualECCId(1l);
        historyQuery.setStartDate(new CalendarKey(2015, 00, 10));
        historyQuery.setEndDate(new CalendarKey(2016, 00, 01));
        historyQuery.setHistoryType(HistoryType.HOURLY); //Not relevant for this test

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(1l);
        virtualECC.setAccountId(1l);

        //Set up location energy plans


        EnergyPlan ep1 = new EnergyPlan();
        ep1.setId(0l);
        ep1.setMeterReadDate(1);
        ep1.setMeterReadCycle(MeterReadCycle.MONTHLY);

        when(energyPlanService.loadEnergyPlan(0l)).thenReturn(ep1);

        List<BillingCycle>  billingCycleList =  historyService.calculateBillingCycles(virtualECC, historyQuery);

        assertEquals(13, billingCycleList.size());

    }


    @Test
    public void testDefaultCalculateBillingCycles2(){

        //Set up the billing cycle
        HistoryQuery historyQuery = new HistoryQuery();
        historyQuery.setVirtualECCId(1l);
        historyQuery.setStartDate(new CalendarKey(2015, 6, 26));
        historyQuery.setEndDate(new CalendarKey(2015, 6, 30));
        historyQuery.setHistoryType(HistoryType.DAILY); //Not relevant for this test

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(310l);
        virtualECC.setAccountId(73l);

        //Set up location energy plans
        EnergyPlan ep1 = new EnergyPlan();
        ep1.setId(0l);
        ep1.setMeterReadDate(16);
        ep1.setMeterReadCycle(MeterReadCycle.MONTHLY);

        when(energyPlanService.loadEnergyPlan(0l)).thenReturn(ep1);

        List<BillingCycle>  billingCycleList =  historyService.calculateBillingCycles(virtualECC, historyQuery);

        assertEquals(1, billingCycleList.size());
        assertEquals(1437019200, billingCycleList.get(0).getStartEpoch());
        assertEquals(1439697600, billingCycleList.get(0).getEndEpoch());


        historyQuery.setStartDate(new CalendarKey(2015, 6, 26));
        historyQuery.setEndDate(new CalendarKey(2015, 7, 1));
        billingCycleList =  historyService.calculateBillingCycles(virtualECC, historyQuery);

        assertEquals(1, billingCycleList.size());
        assertEquals(1437019200, billingCycleList.get(0).getStartEpoch());
        assertEquals(1439697600, billingCycleList.get(0).getEndEpoch());

    }

    @Test
    public void testMonthlyCalculateBillingCycles(){

        //Set up the billing cycle
        HistoryQuery historyQuery = new HistoryQuery();
        historyQuery.setVirtualECCId(1l);
        historyQuery.setStartDate(new CalendarKey(2015, 00, 10));
        historyQuery.setEndDate(new CalendarKey(2016, 00, 01));
        historyQuery.setHistoryType(HistoryType.HOURLY); //Not relevant for this test

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(1l);
        virtualECC.setAccountId(1l);

        //Set up location energy plans

        EnergyPlan ep1 = new EnergyPlan();
        ep1.setId(0l);
        ep1.setMeterReadDate(1);
        ep1.setMeterReadCycle(MeterReadCycle.MONTHLY);

        EnergyPlan ep2 = new EnergyPlan();
        ep2.setId(1l);
        ep2.setMeterReadDate(1);
        ep2.setMeterReadCycle(MeterReadCycle.MONTHLY);

        EnergyPlan ep3 = new EnergyPlan();
        ep3.setId(2l);
        ep3.setMeterReadDate(15);
        ep3.setMeterReadCycle(MeterReadCycle.MONTHLY);

        when(energyPlanService.loadEnergyPlan(0l)).thenReturn(ep1);
        when(energyPlanService.loadEnergyPlan(1l)).thenReturn(ep2);
        when(energyPlanService.loadEnergyPlan(2l)).thenReturn(ep3);

        List<BillingCycle>  billingCycleList =  historyService.calculateBillingCycles(virtualECC, historyQuery);

        assertEquals(13, billingCycleList.size());
        assertEquals(0l, billingCycleList.get(0).getEnergyPlanId()); // 1/1
        assertEquals(0l, billingCycleList.get(1).getEnergyPlanId()); // 2/1
        assertEquals(0l, billingCycleList.get(2).getEnergyPlanId()); // 3/1
        assertEquals(1l, billingCycleList.get(3).getEnergyPlanId()); // 4/1
        assertEquals(1l, billingCycleList.get(4).getEnergyPlanId()); // 5/1
        assertEquals(1l, billingCycleList.get(5).getEnergyPlanId()); // 6/1
        assertEquals(1l, billingCycleList.get(6).getEnergyPlanId()); // 7/1
        assertEquals(2l, billingCycleList.get(7).getEnergyPlanId()); // 7/15
        assertEquals(2l, billingCycleList.get(8).getEnergyPlanId());
        assertEquals(2l, billingCycleList.get(9).getEnergyPlanId());
        assertEquals(2l, billingCycleList.get(10).getEnergyPlanId());
        assertEquals(2l, billingCycleList.get(11).getEnergyPlanId());
        assertEquals(2l, billingCycleList.get(12).getEnergyPlanId());
        //assertEquals(2l, billingCycleList.get(13).getEnergyPlanId());

    }



    @Test
    public void testBiMonthlyCalculateBillingCycles(){

        //Set up the billing cycle
        HistoryQuery historyQuery = new HistoryQuery();
        historyQuery.setVirtualECCId(1l);
        historyQuery.setStartDate(new CalendarKey(2015, 00, 10));
        historyQuery.setEndDate(new CalendarKey(2016, 00, 01));
        historyQuery.setHistoryType(HistoryType.HOURLY); //Not relevant for this test

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(1l);
        virtualECC.setAccountId(1l);

        //Set up location energy plans

        EnergyPlan ep1 = new EnergyPlan();
        ep1.setId(0l);
        ep1.setMeterReadDate(1);
        ep1.setMeterReadCycle(MeterReadCycle.MONTHLY);

        EnergyPlan ep2 = new EnergyPlan();
        ep2.setId(1l);
        ep2.setMeterReadDate(1);
        ep2.setMeterReadCycle(MeterReadCycle.BI_MONTHLY_ODD);

        EnergyPlan ep3 = new EnergyPlan();
        ep3.setId(2l);
        ep3.setMeterReadDate(15);
        ep3.setMeterReadCycle(MeterReadCycle.MONTHLY);

        when(energyPlanService.loadEnergyPlan(0l)).thenReturn(ep1);
        when(energyPlanService.loadEnergyPlan(1l)).thenReturn(ep2);
        when(energyPlanService.loadEnergyPlan(2l)).thenReturn(ep3);


        List<BillingCycle>  billingCycleList =  historyService.calculateBillingCycles(virtualECC, historyQuery);

        assertEquals(12, billingCycleList.size());
//        assertEquals(0l, billingCycleList.get(0).getEnergyPlanId()); // 1/1
//        assertEquals(0l, billingCycleList.get(1).getEnergyPlanId()); // 2/1
//        assertEquals(0l, billingCycleList.get(2).getEnergyPlanId()); // 3/1
//        assertEquals(1l, billingCycleList.get(3).getEnergyPlanId()); // 4/1
//        assertEquals(1l, billingCycleList.get(4).getEnergyPlanId()); // 6/1
//        assertEquals(2l, billingCycleList.get(5).getEnergyPlanId()); // 7/5
//        assertEquals(2l, billingCycleList.get(6).getEnergyPlanId()); // 7/15
//        assertEquals(2l, billingCycleList.get(7).getEnergyPlanId());
//        assertEquals(2l, billingCycleList.get(8).getEnergyPlanId());
//        assertEquals(2l, billingCycleList.get(9).getEnergyPlanId());
//        assertEquals(2l, billingCycleList.get(10).getEnergyPlanId());

    }



}


