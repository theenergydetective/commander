/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.dao.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdvisorServiceTest {

    @Mock
    EmailService emailService;

    @Mock
    PushService pushService;

    @Mock
    CostService costService;

    @Mock
    ClockService clockService;

    @Mock
    AdviceTriggerDAO adviceTriggerDAO;

    @Mock
    AdviceDAO adviceDAO;

    @Mock
    HistoryMinuteDAO historyMinuteDAO;

    @Mock
    HistoryHourDAO historyHourDAO;

    @InjectMocks
    AdvisorService advisorService;



    VirtualECC testLocation = new VirtualECC(1l, "Test Location", VirtualECCType.NET_ONLY, "US/Eastern");
    EnergyPlan testLocationEnergyPlan = new EnergyPlan();
    TimeZone testLocationTimeZone = TimeZone.getTimeZone(testLocation.getTimezone());

    @Before
    public void setup(){
        testLocation.setId(1l);
        testLocation.setSystemType(VirtualECCType.NET_GEN);
        MockitoAnnotations.initMocks(this);
        reset(emailService);
        reset(pushService);
        reset(costService);
        reset(clockService);
        reset(adviceTriggerDAO);
        reset(adviceDAO);
        reset(historyMinuteDAO);
        reset(historyHourDAO);
    }


    @Test
    public void testCheckTriggerTime() {
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        AdviceTrigger adviceTrigger = new AdviceTrigger();

        assertTrue(advisorService.checkTriggerTime(adviceTrigger, timeZone));


        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(720);
        assertTrue(advisorService.checkTriggerTime(adviceTrigger, timeZone));

        adviceTrigger.setEndTime(780);

        when(clockService.getCurrentTimeInMillis()).thenReturn(1451624400000l);
        assertFalse(advisorService.checkTriggerTime(adviceTrigger, timeZone));

        when(clockService.getCurrentTimeInMillis()).thenReturn(1451667600000l);
        assertTrue(advisorService.checkTriggerTime(adviceTrigger, timeZone));

        when(clockService.getCurrentTimeInMillis()).thenReturn(1451671200000l);
        assertFalse(advisorService.checkTriggerTime(adviceTrigger, timeZone));

        when(clockService.getCurrentTimeInMillis()).thenReturn(1451674800000l);
        assertFalse(advisorService.checkTriggerTime(adviceTrigger, timeZone));

    }

    @Test
    public void testCheckAdviceState() {

        Advice advice = new Advice();
        advice.setId(1l);
        advice.setState(AdviceState.NORMAL);
        advisorService.checkAdviceState(advice);
        verify(adviceDAO, times(0)).save(any());

        advice.setState(AdviceState.ALARM);
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);
        advisorService.checkAdviceState(advice);
        verify(adviceDAO, times(0)).save(any());


        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.NORMAL);
        advisorService.checkAdviceState(advice);
        verify(adviceDAO, times(1)).save(any());
        assertEquals(AdviceState.NORMAL, advice.getState());

    }

    @Test
    public void testCheckTOURate(){
        Advice advice = new Advice();
        advice.setId(1l);
        advice.setState(AdviceState.NORMAL);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setAdviceId(1l);
        adviceTrigger.setMinutesBefore(15);

        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);

        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);


        when(clockService.getEpochTime()).thenReturn(1451624400l);
        when(costService.findSeason(testLocationEnergyPlan, testLocationTimeZone, 1451624400l)).thenReturn(0);

        //No change
        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451624400l)).thenReturn(TOUPeakType.OFF_PEAK);
        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451625300l)).thenReturn(TOUPeakType.OFF_PEAK);
        advisorServiceSpy.checkTOURate(advice, adviceTrigger, testLocation, testLocationEnergyPlan);
        assertEquals(AdviceState.NORMAL, advice.getState());
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());

        //Change to Alarm
        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451625300l)).thenReturn(TOUPeakType.PEAK);
        when(costService.getTouName(testLocationEnergyPlan, TOUPeakType.PEAK)).thenReturn(TOUPeakType.PEAK.name());
        when(costService.getTouName(testLocationEnergyPlan, TOUPeakType.OFF_PEAK)).thenReturn(TOUPeakType.OFF_PEAK.name());
        when(costService.getTouName(testLocationEnergyPlan, TOUPeakType.SUPER_PEAK)).thenReturn(TOUPeakType.SUPER_PEAK.name());
        when(costService.getTouName(testLocationEnergyPlan, TOUPeakType.MID_PEAK)).thenReturn(TOUPeakType.MID_PEAK.name());

        advisorServiceSpy.checkTOURate(advice, adviceTrigger, testLocation, testLocationEnergyPlan);
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(1451625300l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(any()); //Only on a return to normal
        verify(emailService, times(1)).sendTOURateNotification(advice, adviceRecipient, adviceTrigger, testLocation, TOUPeakType.PEAK.name()); //Only on a return to normal
        verify(pushService, times(1)).sendTOURateNotification(advice, adviceRecipient, adviceTrigger, testLocation, TOUPeakType.PEAK.name()); //Only on a return to normal

        //Do not send if already in alarm state
        reset(adviceDAO);
        reset(emailService);
        reset(pushService);
        reset(advisorServiceSpy);
        advisorServiceSpy.checkTOURate(advice, adviceTrigger, testLocation, testLocationEnergyPlan);
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any()); //Only on a return to normal
        verify(emailService, times(0)).sendTOURateNotification(advice, adviceRecipient, adviceTrigger, testLocation, TOUPeakType.PEAK.name()); //Only on a return to normal
        verify(pushService, times(0)).sendTOURateNotification(advice, adviceRecipient, adviceTrigger, testLocation,  TOUPeakType.PEAK.name()); //Only on a return to normal

        //Check return to normal
        reset(adviceDAO);
        reset(emailService);
        reset(pushService);
        reset(advisorServiceSpy);
        reset(adviceTriggerDAO);
        when(clockService.getEpochTime()).thenReturn(1451625300l + (60 * 15));
        advisorServiceSpy.checkTOURate(advice, adviceTrigger, testLocation, testLocationEnergyPlan);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);


        //Check ignore if peak not applicable
        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451624400l)).thenReturn(TOUPeakType.OFF_PEAK);
        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451625300l)).thenReturn(TOUPeakType.MID_PEAK);
        adviceTrigger.setMidPeakApplicable(false);
        adviceTrigger.setSuperPeakApplicable(false);
        reset(adviceDAO);
        reset(emailService);
        reset(pushService);
        reset(advisorServiceSpy);
        reset(adviceTriggerDAO);
        when(clockService.getEpochTime()).thenReturn(1451624400l);
        advisorServiceSpy.checkTOURate(advice, adviceTrigger, testLocation, testLocationEnergyPlan);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);

        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451624400l)).thenReturn(TOUPeakType.OFF_PEAK);
        when(costService.findTOU(testLocationEnergyPlan, 0, testLocationTimeZone, 1451625300l)).thenReturn(TOUPeakType.SUPER_PEAK);
        reset(adviceDAO);
        reset(emailService);
        reset(pushService);
        reset(advisorServiceSpy);
        reset(adviceTriggerDAO);
        when(clockService.getEpochTime()).thenReturn(1451624400l);
        advisorServiceSpy.checkTOURate(advice, adviceTrigger, testLocation, testLocationEnergyPlan);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);

    }

    @Test
    public void testCheckRateChange(){

        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setRateInEffect(0.0001);
        historyMinute.setMtuCount(1);
        when(clockService.getEpochTime()).thenReturn(1451624400l);

        advisorService.checkRateChange(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyMinute, 1);

        assertEquals(0.0001, adviceTrigger.getAmount(), .000001);
        assertEquals(1451624400l, adviceTrigger.getLastSent());
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(emailService, times(0)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


        //Verify no rate change
        reset(adviceTriggerDAO);
        reset(emailService);
        reset(pushService);
        advisorService.checkRateChange(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyMinute, 1);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(emailService, times(0)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


        //Verify Rate Change
        historyMinute.setRateInEffect(0.0002);
        when(clockService.getEpochTime()).thenReturn(1451624460l);

        reset(adviceTriggerDAO);
        reset(emailService);
        reset(pushService);
        advisorService.checkRateChange(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyMinute, 1);
        assertEquals(0.0002, adviceTrigger.getAmount(), .000001);
        assertEquals(1451624460l, adviceTrigger.getLastSent());
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(emailService, times(1)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


        //Verify ignore if rate is different but invalid mtu count
        historyMinute.setRateInEffect(0.0003);
        when(clockService.getEpochTime()).thenReturn(1451624460l);

        reset(adviceTriggerDAO);
        reset(emailService);
        reset(pushService);
        advisorService.checkRateChange(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyMinute, 2);
        assertEquals(0.0002, adviceTrigger.getAmount(), .000001);
        assertEquals(1451624460l, adviceTrigger.getLastSent());
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(emailService, times(0)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendRateChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


    }


    @Test
    public void testCanReturnToNormal(){
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(1451624400l);

        //Test Minute
        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.MINUTE);
        adviceTrigger.setLastSent(1451624400l);
        when(clockService.getEpochTime()).thenReturn(1451624400l);
        assertFalse(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));
        when(clockService.getEpochTime()).thenReturn(1451624400l + 60l);
        assertTrue(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),timeZone));

        //Test Hour
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setLastSent(1451624400l);
        when(clockService.getEpochTime()).thenReturn(1451624400l);
        assertFalse(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern")));
        when(clockService.getEpochTime()).thenReturn(1451624400l + 3600l);
        assertTrue(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern")));

        //Test Daily
        adviceTrigger.setSendAtMost(SendAtMostType.DAILY);
        adviceTrigger.setLastSent(1451642400l); // 1-1-2016 5am
        when(clockService.getEpochTime()).thenReturn(1451642400l);  // 1-1-2016 5am
        assertFalse(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));
        when(clockService.getEpochTime()).thenReturn(1451707200l);  //1-1-2016 11pm
        assertFalse(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));

        //Check after midnight
        when(clockService.getEpochTime()).thenReturn(1451710800l);  // 1-2-2016 12am   TED-288 Fix
        assertFalse(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));

        when(clockService.getEpochTime()).thenReturn(1451710860l); // 1-2-2016 12:01am   TED-288 Fix
        assertTrue(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));

        when(clockService.getEpochTime()).thenReturn(1451642400l + 86400l);
        assertTrue(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));

        //Test Billing Cycle
        adviceTrigger.setSendAtMost(SendAtMostType.BILLING_CYCLE);
        adviceTrigger.setLastSent(1451624400l);
        assertFalse(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),timeZone));
        adviceTrigger.setLastSent(1451624400l - 60l);
        assertTrue(advisorService.canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone));

    }

    @Test
    public void testCheckDemandCharge(){


        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.MINUTE);
        adviceTrigger.setDelayMinutes(24);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(1451624400l);

        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);

        //Check Billing Cycle Delay
        historyBillingCycle.setDemandCost(10.0);
        when(clockService.getEpochTime()).thenReturn(1451624400l + (23l * 3600l));
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);

        assertEquals(0.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(0, adviceTrigger.getLastSent());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(0)).canReturnToNormal(any(), anyLong(), any());

        //Check Billing Cycle Delay with previous data
        adviceTrigger.setLastSent(1000l);
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);
        assertEquals(0.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(0, adviceTrigger.getLastSent());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(1)).checkAdviceState(any());
        verify(advisorServiceSpy, times(0)).canReturnToNormal(any(), anyLong(), any());


        //Check that we snag the first item by default w/out triggering alarm
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyBillingCycle.setDemandCost(100.0);
        when(clockService.getEpochTime()).thenReturn(1451624400l + (adviceTrigger.getDelayMinutes() * 60l));
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);
        assertEquals(100.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(1451624400l + (adviceTrigger.getDelayMinutes() * 60l), adviceTrigger.getLastSent());
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(1)).checkAdviceState(any());
        verify(advisorServiceSpy, times(0)).canReturnToNormal(any(), anyLong(),any());


        //Check Cost Change
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyBillingCycle.setDemandCost(200.0);
        long newTime = 1451624400l + (adviceTrigger.getDelayMinutes() * 60l);
        newTime += 60;
        when(clockService.getEpochTime()).thenReturn(newTime);
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);
        assertEquals(200.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(newTime, adviceTrigger.getLastSent());
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(0)).canReturnToNormal(any(), anyLong(),any());
        verify(emailService, times(1)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());



        //Check Igore Alarm because Send At most
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        when(clockService.getEpochTime()).thenReturn(newTime+60);
        historyBillingCycle.setDemandCost(300.0);
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);
        assertEquals(300.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(newTime, adviceTrigger.getLastSent());
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(any(), anyLong(),any());
        verify(emailService, times(0)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


        //Check Return to Normal
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        when(clockService.getEpochTime()).thenReturn(newTime+3600);
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);
        assertEquals(300.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(newTime, adviceTrigger.getLastSent());
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(1)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(any(), anyLong(),any());
        verify(emailService, times(0)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());



        //Test outside trigger

        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        historyBillingCycle.setDemandCost(300.0);
        when(clockService.getEpochTime()).thenReturn(1452875400l);
        advisorServiceSpy.checkDemandCharge(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle);
        assertEquals(300.0, adviceTrigger.getAmount(), 0.01);
        assertEquals(newTime, adviceTrigger.getLastSent());
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(1)).checkAdviceState(any());
        verify(advisorServiceSpy, times(0)).canReturnToNormal(any(), anyLong(),any());
        verify(emailService, times(0)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendDemandCostChangeNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
    }


    @Test
    public void testCheckMoneySpent(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setAmount(100.00);

        HistoryDay historyDay = new HistoryDay();
        historyDay.setNetCost(10.0);
        historyDay.setStartEpoch(1451624400l);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setNetCost(200.0);
        historyBillingCycle.setStartEpoch(1451624400l);

        when(clockService.getEpochTime()).thenReturn(1451624400l);


        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkMoneySpent(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyDay.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyDay.setNetCost(100.0);
        advisorServiceSpy.checkMoneySpent(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(1451624400l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyDay.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        when(clockService.getEpochTime()).thenReturn(1451624460l);
        historyDay.setNetCost(200.0);
        advisorServiceSpy.checkMoneySpent(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        when(clockService.getEpochTime()).thenReturn(1451624400l + 3600l);
        historyDay.setNetCost(300.0);
        advisorServiceSpy.checkMoneySpent(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        when(clockService.getEpochTime()).thenReturn(1451624400l + 7200l);
        historyDay.setNetCost(1.0);
        advisorServiceSpy.checkMoneySpent(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);

        advisorServiceSpy.checkMoneySpent(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendMoneySpentNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

    }


    @Test
    public void testCheckEnergyConsumed(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setAmount(100.00);

        HistoryDay historyDay = new HistoryDay();
        historyDay.setNet(10.0);
        historyDay.setStartEpoch(1451624400l);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setNet(200.0);
        historyBillingCycle.setStartEpoch(1451624400l);

        when(clockService.getEpochTime()).thenReturn(1451624400l);


        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkEnergyConsumed(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyDay.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyDay.setNet(100.0 * 1000.0);
        advisorServiceSpy.checkEnergyConsumed(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(1451624400l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyDay.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        when(clockService.getEpochTime()).thenReturn(1451624460l);
        historyDay.setNet(200.0 * 1000.0);
        advisorServiceSpy.checkEnergyConsumed(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        when(clockService.getEpochTime()).thenReturn(1451624400l + 3600l);
        historyDay.setNet(300.0 * 1000.0);
        advisorServiceSpy.checkEnergyConsumed(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        when(clockService.getEpochTime()).thenReturn(1451624400l + 7200l);
        historyDay.setNet(1.0 * 1000.0);
        advisorServiceSpy.checkEnergyConsumed(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);

        advisorServiceSpy.checkEnergyConsumed(advice, adviceTrigger, testLocation, testLocationEnergyPlan, historyBillingCycle, historyDay);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendEnergyConsumedNotification(any(), any(), any(), any(), any(), anyDouble(), anyDouble());

    }



    @Test
    public void testCheckDemandExceeds(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setAmount(500.00);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(1451624400l);
        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1451625000l);
        historyMinute.setRunningNetTotal(50.0 * 1000.0);
        historyMinute.setMtuCount(1);

        HistoryMinute oldHistoryMinute = new HistoryMinute();
        oldHistoryMinute.setStartEpoch(1451624400l);
        oldHistoryMinute.setRunningNetTotal(0.0);
        oldHistoryMinute.setMtuCount(1);
        when(historyMinuteDAO.findOne(testLocation.getId(), 1451624400l)).thenReturn(oldHistoryMinute);

        when(clockService.getEpochTime()).thenReturn(1451625000l);
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkDemandExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
          adviceTrigger.setAmount(300.0);
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        advisorServiceSpy.checkDemandExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(1451625000l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        advisorServiceSpy.checkDemandExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        long newTime = 1451624400l + 7200l;
        when(clockService.getEpochTime()).thenReturn(newTime);
        historyMinute.setStartEpoch(newTime);
        oldHistoryMinute.setStartEpoch(newTime - 600);
        when(historyMinuteDAO.findOne(testLocation.getId(), newTime - 600)).thenReturn(oldHistoryMinute);
        advisorServiceSpy.checkDemandExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        oldHistoryMinute.setRunningNetTotal(historyMinute.getRunningNetTotal());
        when(clockService.getEpochTime()).thenReturn(newTime+3600);
        advisorServiceSpy.checkDemandExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);

        advisorServiceSpy.checkDemandExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendKWAverageNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

    }


    @Test
    public void testCheckGenerationExceeds(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setDelayMinutes(8);
        adviceTrigger.setAmount(-1000.00);

        long baseEpoch =1451624400l;
        long currentEpoch = baseEpoch + (3600 * 8);
        when(clockService.getEpochTime()).thenReturn(currentEpoch);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(currentEpoch);

        when(historyHourDAO.getAverageEnergyGenerated(anyLong(), anyLong(), anyInt())).thenReturn(((-1000.00) * 1000.0));
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        HistoryMinute historyMinute = new HistoryMinute();


        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkGeneratedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
        when(historyHourDAO.getAverageEnergyGenerated(anyLong(),anyLong(), anyInt())).thenReturn(((-500.0) * 1000.0));
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        advisorServiceSpy.checkGeneratedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(currentEpoch, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        advisorServiceSpy.checkGeneratedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        currentEpoch += 7200l;
        when(clockService.getEpochTime()).thenReturn(currentEpoch);
        advisorServiceSpy.checkGeneratedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(currentEpoch, adviceTrigger.getLastSent());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        currentEpoch += 3600;
        when(clockService.getEpochTime()).thenReturn(currentEpoch);
        when(historyHourDAO.getAverageEnergyGenerated(anyLong(),anyLong(), anyInt())).thenReturn(((-1500.0 ) * 1000.0));
        advisorServiceSpy.checkGeneratedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);
        when(historyHourDAO.getAverageEnergyGenerated(anyLong(),anyLong(), anyInt())).thenReturn(((-500.0 ) * 1000.0));
        advisorServiceSpy.checkGeneratedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendAverageGenerationNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

    }


    @Test
    public void testCheckConsumedExceeds(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);



        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setDelayMinutes(8);
        adviceTrigger.setAmount(1000.00);

        long baseEpoch =1451624400l;
        long currentEpoch = baseEpoch + (3600 * 8);
        when(clockService.getEpochTime()).thenReturn(currentEpoch);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(currentEpoch);

        when(historyHourDAO.getAverageEnergyConsumed(anyLong(), anyLong(), anyInt())).thenReturn(((1000.00) * 1000.0));
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkConsumedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
        when(historyHourDAO.getAverageEnergyConsumed(anyLong(),anyLong(), anyInt())).thenReturn(((500.0) * 1000.0));
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        advisorServiceSpy.checkConsumedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(currentEpoch, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        advisorServiceSpy.checkConsumedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        currentEpoch += 7200l;
        when(clockService.getEpochTime()).thenReturn(currentEpoch);
        advisorServiceSpy.checkConsumedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(currentEpoch, adviceTrigger.getLastSent());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        currentEpoch += 3600;
        when(clockService.getEpochTime()).thenReturn(currentEpoch);
        when(historyHourDAO.getAverageEnergyConsumed(anyLong(),anyLong(), anyInt())).thenReturn(((1500.0 ) * 1000.0));
        advisorServiceSpy.checkConsumedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);
        when(historyHourDAO.getAverageEnergyConsumed(anyLong(),anyLong(), anyInt())).thenReturn(((-500.0 ) * 1000.0));
        advisorServiceSpy.checkConsumedAverage(advice, adviceTrigger, testLocation, historyBillingCycle, new HistoryMinute(), 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendAverageConsumedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

    }



    @Test
    public void testRealTimePowerExceeds(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setAmount(10.0);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(1451624400l);
        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1451625000l);


        historyMinute.setMtuCount(1);

        HistoryMinute oldHistoryMinute = new HistoryMinute();
        oldHistoryMinute.setStartEpoch(1451624400l);
        oldHistoryMinute.setMtuCount(1);
        historyMinute.setNet(8.0 * 16.66666666666667);
        when(clockService.getEpochTime()).thenReturn(1451625000l);
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkRealTimePowerExceeds (advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyMinute.setNet(11.0 * 16.66666666666667);
        advisorServiceSpy.checkRealTimePowerExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(1451625000l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        advisorServiceSpy.checkRealTimePowerExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        long newTime = 1451624400l + 7200l;
        when(clockService.getEpochTime()).thenReturn(newTime);
        historyMinute.setStartEpoch(newTime);
        oldHistoryMinute.setStartEpoch(newTime - 600);
        when(historyMinuteDAO.findOne(testLocation.getId(), newTime - 600)).thenReturn(oldHistoryMinute);
        advisorServiceSpy.checkRealTimePowerExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        historyMinute.setNet(8.0 * 16.66666666666667);
        when(clockService.getEpochTime()).thenReturn(newTime+3600);
        advisorServiceSpy.checkRealTimePowerExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);

        advisorServiceSpy.checkRealTimePowerExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendRealTimePowerNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

    }



    @Test
    public void testVoltageExceeds(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setAmount(120.0);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(1451624400l);
        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1451625000l);


        historyMinute.setMtuCount(1);

        HistoryMinute oldHistoryMinute = new HistoryMinute();
        oldHistoryMinute.setStartEpoch(1451624400l);
        oldHistoryMinute.setMtuCount(1);
        historyMinute.setVoltageTotal(100.0);
        when(clockService.getEpochTime()).thenReturn(1451625000l);
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkVoltageExceeds (advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Exceeds
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyMinute.setVoltageTotal(200.0);
        advisorServiceSpy.checkVoltageExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(1451625000l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        advisorServiceSpy.checkVoltageExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        long newTime = 1451624400l + 7200l;
        when(clockService.getEpochTime()).thenReturn(newTime);
        historyMinute.setStartEpoch(newTime);
        oldHistoryMinute.setStartEpoch(newTime - 600);
        when(historyMinuteDAO.findOne(testLocation.getId(), newTime - 600)).thenReturn(oldHistoryMinute);
        advisorServiceSpy.checkVoltageExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        historyMinute.setVoltageTotal(100.0);
        when(clockService.getEpochTime()).thenReturn(newTime+3600);
        advisorServiceSpy.checkVoltageExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);

        advisorServiceSpy.checkVoltageExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendVoltageExceedsNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

    }




    @Test
    public void testVoltageDoesNotExceed(){
        Advice advice = new Advice();
        advice.setId(1l);
        AdviceRecipient adviceRecipient = new AdviceRecipient();
        adviceRecipient.setSendEmail(true);
        adviceRecipient.setSendPush(true);
        advice.getAdviceRecipientList().add(adviceRecipient);

        AdviceTrigger adviceTrigger = new AdviceTrigger();
        adviceTrigger.setSendAtMost(SendAtMostType.HOURLY);
        adviceTrigger.setSinceStart(HistoryType.DAILY);
        adviceTrigger.setAmount(120.0);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setStartEpoch(1451624400l);
        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1451625000l);


        historyMinute.setMtuCount(1);

        HistoryMinute oldHistoryMinute = new HistoryMinute();
        oldHistoryMinute.setStartEpoch(1451624400l);
        oldHistoryMinute.setMtuCount(1);
        historyMinute.setVoltageTotal(200.0);
        when(clockService.getEpochTime()).thenReturn(1451625000l);
        when(adviceTriggerDAO.findAdviceState(1l)).thenReturn(AdviceState.ALARM);

        //Check not exceeds
        AdvisorService advisorServiceSpy = Mockito.spy(advisorService);
        advisorServiceSpy.checkVoltageDoesNotExceeds (advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.NORMAL, advice.getState());
        verify(adviceDAO, times(0)).save(any());
        verify(adviceTriggerDAO, times(0)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger,historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));

        //Check Does not Exceed
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        historyMinute.setVoltageTotal(100.0);
        advisorServiceSpy.checkVoltageDoesNotExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        assertEquals(1451625000l, adviceTrigger.getLastSent());
        verify(adviceDAO, times(1)).save(any());
        verify(adviceTriggerDAO, times(1)).save(any());
        verify(advisorServiceSpy, times(0)).checkAdviceState(any());
        verify(advisorServiceSpy, times(1)).canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(),TimeZone.getTimeZone("US/Eastern"));
        verify(emailService, times(1)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Check Exceeds before Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        advisorServiceSpy.checkVoltageDoesNotExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(0)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(0)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());


        //Test exceeds after expiration
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        long newTime = 1451624400l + 7200l;
        when(clockService.getEpochTime()).thenReturn(newTime);
        historyMinute.setStartEpoch(newTime);
        oldHistoryMinute.setStartEpoch(newTime - 600);
        when(historyMinuteDAO.findOne(testLocation.getId(), newTime - 600)).thenReturn(oldHistoryMinute);
        advisorServiceSpy.checkVoltageDoesNotExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.ALARM, adviceTrigger.getTriggerState());
        assertEquals(AdviceState.ALARM, advice.getState());
        verify(adviceDAO, times(1)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(0)).checkAdviceState(advice);
        verify(emailService, times(1)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(1)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Return to normal
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        historyMinute.setVoltageTotal(200.0);
        when(clockService.getEpochTime()).thenReturn(newTime+3600);
        advisorServiceSpy.checkVoltageDoesNotExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

        //Test Trigger disabled
        reset(adviceDAO);
        reset(adviceTriggerDAO);
        reset(advisorServiceSpy);
        reset(emailService);
        reset(pushService);
        adviceTrigger.setStartTime(720);
        adviceTrigger.setEndTime(780);
        adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
        when(clockService.getEpochTime()).thenReturn(1452875400l);

        advisorServiceSpy.checkVoltageDoesNotExceeds(advice, adviceTrigger, testLocation, historyBillingCycle, historyMinute, 1);
        assertEquals(AdviceTriggerState.NORMAL, adviceTrigger.getTriggerState());
        verify(adviceDAO, times(0)).save(advice);
        verify(adviceTriggerDAO, times(1)).save(adviceTrigger);
        verify(advisorServiceSpy, times(1)).checkAdviceState(advice);
        verify(emailService, times(0)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());
        verify(pushService, times(0)).sendVoltageDoesNotExceedNotification(any(), any(), any(), any(), anyDouble(), anyDouble());

    }


    @Test
    public void testLastPost(){

    }


}



