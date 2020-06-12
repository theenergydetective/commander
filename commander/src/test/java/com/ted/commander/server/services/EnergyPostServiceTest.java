package com.ted.commander.server.services;

import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.common.model.history.EnergyDifference;
import com.ted.commander.common.model.history.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.TimeZone;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * This is a unit test to test the new energy post service (DATABASE 2.0). This uses Account 52 as the 'playback'
 */
@RunWith(MockitoJUnitRunner.class)
public class EnergyPostServiceTest {


    @Mock
    PlaybackDAO playbackDAO;

    @Mock
    EnergyDataDAO energyDataDAO;

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
    CostService costService;

    VirtualECCMTU netMTU = new VirtualECCMTU();
    VirtualECCMTU loadMTU = new VirtualECCMTU();
    VirtualECCMTU genMTU = new VirtualECCMTU();
    VirtualECCMTU saMTU = new VirtualECCMTU();


    @InjectMocks
    EnergyPostService energyPostService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        reset(energyDataDAO);
        reset(historyBillingCycleDAO);
        reset(historyDayDAO);
        reset(historyHourDAO);
        reset(historyMinuteDAO);
        reset(historyMTUBillingCycleDAO);
        reset(historyMTUDayDAO);
        reset(historyMTUHourDAO);
        reset(playbackDAO);

        netMTU.setMtuType(MTUType.NET);
        loadMTU.setMtuType(MTUType.LOAD);
        genMTU.setMtuType(MTUType.GENERATION);
        saMTU.setMtuType(MTUType.STAND_ALONE);
    }

    @Test
    public void testFindBillingCycleEpoch(){
        EnergyPlan energyPlan = new EnergyPlan();
        HistoryBillingCycleKey historyBillingCycleKey = new HistoryBillingCycleKey();
        historyBillingCycleKey.setBillingCycleMonth(0);
        historyBillingCycleKey.setBillingCycleYear(2016);
        energyPlan.setMeterReadDate(1);
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setKey(historyBillingCycleKey);

        energyPostService.setHistoryBillingCycleEpoch(historyBillingCycle, energyPlan, TimeZone.getTimeZone("US/Eastern"));
        assertEquals(1451624400, historyBillingCycle.getStartEpoch().longValue());
        assertEquals(1454302800, historyBillingCycle.getEndEpoch().longValue());

        energyPostService.setHistoryBillingCycleEpoch(historyBillingCycle, energyPlan, TimeZone.getTimeZone("US/Central"));
        assertEquals(1451628000, historyBillingCycle.getStartEpoch().longValue());

        energyPlan.setMeterReadDate(14);
        energyPostService.setHistoryBillingCycleEpoch(historyBillingCycle, energyPlan, TimeZone.getTimeZone("US/Eastern"));
        assertEquals(1452747600, historyBillingCycle.getStartEpoch().longValue());

        historyBillingCycleKey.setBillingCycleMonth(1);
        energyPostService.setHistoryBillingCycleEpoch(historyBillingCycle, energyPlan, TimeZone.getTimeZone("US/Eastern"));
        assertEquals(1455426000, historyBillingCycle.getStartEpoch().longValue());
    }



    @Test
    public void testSingleFindBillingCycleKey() {
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setMeterReadDate(15);

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(1000l);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 9, 14, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");

        HistoryBillingCycleKey billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);

        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(8, (int) billingCycleKey.getBillingCycleMonth());

        calendar.set(2015, 9, 15, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(9, (int) billingCycleKey.getBillingCycleMonth());

        calendar.set(2015, 9, 16, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(9, (int) billingCycleKey.getBillingCycleMonth());


        energyPlan.setMeterReadDate(1);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(9, (int) billingCycleKey.getBillingCycleMonth());

        assertEquals(1000l, billingCycleKey.getVirtualECCId().longValue());


        //Specific test for Home system (energy plan, MRD of the 15th. Test with epochs).
        energyPlan.setMeterReadDate(16);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, 1454700360);
        Assert.assertEquals(0, billingCycleKey.getBillingCycleMonth().intValue());
        Assert.assertEquals(2016, billingCycleKey.getBillingCycleYear().intValue());
    }


    @Test
    public void testEvenMonthlyFindBillingCycleKey() {
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setMeterReadCycle(MeterReadCycle.BI_MONTHLY_EVEN);
        energyPlan.setMeterReadDate(15);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 9, 14, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(1000l);


        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        HistoryBillingCycleKey billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);

        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(7, (int) billingCycleKey.getBillingCycleMonth());

        calendar.set(2015, 9, 15, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(9, (int) billingCycleKey.getBillingCycleMonth());


        calendar.set(2015, 10, 1, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(9, (int) billingCycleKey.getBillingCycleMonth());
    }


    @Test
    public void testOddMonthlyFindBillingCycleKey() {
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setMeterReadCycle(MeterReadCycle.BI_MONTHLY_ODD);
        energyPlan.setMeterReadDate(15);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 9, 14, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setId(1000l);


        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        HistoryBillingCycleKey billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);

        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(8, (int) billingCycleKey.getBillingCycleMonth());

        calendar.set(2015, 9, 15, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(8, (int) billingCycleKey.getBillingCycleMonth());


        calendar.set(2015, 10, 14, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(8, (int) billingCycleKey.getBillingCycleMonth());

        calendar.set(2015, 10, 16, 0, 0, 0);
        billingCycleKey = energyPostService.findBillingCycleKey(virtualECC, energyPlan, timeZone, calendar.getTimeInMillis() / 1000);
        assertEquals(2015, (int) billingCycleKey.getBillingCycleYear());
        assertEquals(10, (int) billingCycleKey.getBillingCycleMonth());
    }


    @Test
    public void testNetOnlyUpdateEnergyValues() throws Exception {
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setSystemType(VirtualECCType.NET_ONLY);
        assertEquals(0.0, historyBillingCycle.getNet(), 0.01);
        EnergyDifference energyDifference;

        HistoryMinute historyMinute = new HistoryMinute();

        energyDifference = energyPostService.calcEnergyDifference(virtualECC, netMTU, 5.0, 100.0);
        energyDifference.addTo(historyBillingCycle);;
        assertEquals(5.0, historyBillingCycle.getNet(), .01);
        assertEquals(100.0, energyDifference.getNetTotal(), .01);
        historyMinute.setRunningNetTotal(historyMinute.getRunningNetTotal() + energyDifference.getNetTotal());


        energyDifference = energyPostService.calcEnergyDifference(virtualECC, netMTU, 5.0,200.0);
        energyDifference.addTo(historyBillingCycle);
        assertEquals(10.0, historyBillingCycle.getNet(), .01);
        assertEquals(0.0, historyBillingCycle.getLoad(), .01);
        assertEquals(0.0, historyBillingCycle.getGeneration(), .01);
        assertEquals(200.0, energyDifference.getNetTotal(), .01);
        historyMinute.setRunningNetTotal(historyMinute.getRunningNetTotal() + energyDifference.getNetTotal());
        assertEquals(300.0, historyMinute.getRunningNetTotal(), .01);
    }

    @Test
    public void testNetGenUpdateEnergyValues() throws Exception {
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setSystemType(VirtualECCType.NET_GEN);
        EnergyDifference energyDifference;

        assertEquals(0.0, historyBillingCycle.getNet(), 0.01);

        energyDifference = energyPostService.calcEnergyDifference(virtualECC, netMTU, 5.0, 100.0);
        energyDifference.addTo(historyBillingCycle);;
        assertEquals(5.0, historyBillingCycle.getNet(), .01);
        assertEquals(5.0, historyBillingCycle.getLoad(),.01);
        assertEquals(0.0, historyBillingCycle.getGeneration(), .01);
        assertEquals(100.0, energyDifference.getNetTotal(), .01);


        energyDifference = energyPostService.calcEnergyDifference(virtualECC, genMTU, -10.0, 200.0);
        energyDifference.addTo(historyBillingCycle);;
        assertEquals(5.0, historyBillingCycle.getNet(), .01);
        assertEquals(-10.0, historyBillingCycle.getGeneration(), .01);
        assertEquals(15.0, historyBillingCycle.getLoad(), .01);
        assertEquals(0.0, energyDifference.getNetTotal(), .01);


    }

    @Test
    public void testLoadGenUpdateEnergyValues() throws Exception {
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setSystemType(VirtualECCType.LOAD_GEN);
        EnergyDifference energyDifference;

        energyDifference = energyPostService.calcEnergyDifference(virtualECC, loadMTU, 5.0, 100.0);
        energyDifference.addTo(historyBillingCycle);;

        assertEquals(5.0,historyBillingCycle.getNet(), .01);
        assertEquals(5.0,historyBillingCycle.getLoad(), .01);
        assertEquals(0.0,historyBillingCycle.getGeneration(), .01);
        assertEquals(100.0,energyDifference.getNetTotal(), 01);

        energyDifference = energyPostService.calcEnergyDifference(virtualECC, genMTU, -10.0, -200.0);
        energyDifference.addTo(historyBillingCycle);;

        assertEquals(-5.0, historyBillingCycle.getNet(), .01);
        assertEquals(-10.0, historyBillingCycle.getGeneration(), .01);
        assertEquals(5.0, historyBillingCycle.getLoad(), .01);
        assertEquals(-200.0, energyDifference.getNetTotal(), .01);
    }




    @Test
    public void testGetBillingCycleInstance() {
        HistoryBillingCycleKey historyBillingCycleKey = new HistoryBillingCycleKey(1000l, new CalendarKey(2015, 1,1));
        when(historyBillingCycleDAO.findOne(historyBillingCycleKey)).thenReturn(null);
        EnergyPlan energyPlan = new EnergyPlan();
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        assertEquals(0.0, energyPostService.getBillingCycleInstance(historyBillingCycleKey, timeZone, energyPlan, 0).getNet(), 0.01);

        energyPlan.setNumberSeasons(2);
        energyPlan.getSeasonList().add(new EnergyPlanSeason(0, "summer", 0, 0));
        energyPlan.getSeasonList().add(new EnergyPlanSeason(1, "winder", 6, 0));
        energyPlan.getSeasonList().get(1).getAdditionalChargeList().add(new AdditionalCharge(AdditionalChargeType.MINIMUM, 0l, 1, 100.0));
        energyPlan.getSeasonList().get(1).getAdditionalChargeList().add(new AdditionalCharge(AdditionalChargeType.FIXED, 0l, 1, 10.0));

        assertEquals(100.0,energyPostService.getBillingCycleInstance(historyBillingCycleKey,timeZone, energyPlan, 1).getMinimumCharge(), .001);
        assertEquals(10.0,energyPostService.getBillingCycleInstance(historyBillingCycleKey, timeZone, energyPlan, 1).getFixedCharge(), .001);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        historyBillingCycle.setKey(historyBillingCycleKey);
        historyBillingCycle.setNet(1000.0);
        when(historyBillingCycleDAO.findOne(historyBillingCycleKey)).thenReturn(historyBillingCycle);
        assertEquals(1000.0, energyPostService.getBillingCycleInstance(historyBillingCycleKey, timeZone, energyPlan, 0).getNet(), 0.01);

    }

//    @Test
//    public void getDayInstance() {
//        HistoryDayKey historyDayKey = new HistoryDayKey(1000l, new CalendarKey(2015, 1,1));
//
//        when(historyDayDAO.findOne(historyDayKey)).thenReturn(null);
//        EnergyPlan energyPlan = new EnergyPlan();
//        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
//        assertEquals(0.0, energyPostService.getDayInstance(historyDayKey,  energyPlan,timeZone).getNet(), 0.01);
//
//        HistoryDay historyDay = new HistoryDay();
//        historyDay.setKey(historyDayKey);
//        historyDay.setNet(1000.0);
//        when(historyDayDAO.findOne(historyDayKey)).thenReturn(historyDay);
//
//        assertEquals(1000.0, energyPostService.getDayInstance(historyDayKey,  energyPlan,timeZone).getNet(), 0.01);
//
//    }

//    @Test
//    public void getHourInstance() {
//        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
//        when(historyHourDAO.findOne(1000l)).thenReturn(null);
//        EnergyPlan energyPlan = new EnergyPlan();
//        assertEquals(0.0, energyPostService.getHourInstance(historyHourKey,  energyPlan,timeZone).getNet(), 0.01);
//
//        HistoryHour historyHour = new HistoryHour();
//        historyHour.setKey(historyHourKey);
//        historyHour.setNet(1000.0);
//        when(historyHourDAO.findOne(historyHourKey)).thenReturn(historyHour);
//
//        assertEquals(1000.0, energyPostService.getHourInstance(historyHourKey, energyPlan, timeZone).getNet(), 0.01);
//
//    }

//    @Test
//    public void getMinuteInstance() {
//        HistoryMinuteKey historyMinuteKey = new HistoryMinuteKey(1000l, new CalendarKey(2015, 1,1,1,1));
//
//        when(historyMinuteDAO.findOne(historyMinuteKey)).thenReturn(null);
//        EnergyPlan energyPlan = new EnergyPlan();
//        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
//        assertEquals(0.0, energyPostService.getMinuteInstance(historyMinuteKey, energyPlan, timeZone).getNet(), 0.01);
//
//        HistoryMinute historyMinute = new HistoryMinute();
//        historyMinute.setKey(historyMinuteKey);
//        historyMinute.setNet(1000.0);
//        when(historyMinuteDAO.findOne(historyMinuteKey)).thenReturn(historyMinute);
//
//        assertEquals(1000.0, energyPostService.getMinuteInstance(historyMinuteKey, energyPlan, timeZone).getNet(), 0.01);
//
//    }

    @Test
    public void calcTOUDifference(){
        EnergyPlan energyPlan = new EnergyPlan();
        EnergyDifference energyDifference = new EnergyDifference();
        energyDifference.setNet(1000.0);
        assertNull(energyPostService.calcTOUDifference(energyPlan, 1234567890, TimeZone.getDefault(), TOUPeakType.PEAK, energyDifference));
        energyPlan.setPlanType(PlanType.TOU);
        assertEquals(1000.0, energyPostService.calcTOUDifference(energyPlan, 1234567890, TimeZone.getDefault(), TOUPeakType.PEAK, energyDifference).getNet(), 0.01);
    }

    @Test
    public void testCompareVoltage(){
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();


        energyPostService.compareVoltage(1, 120.0, historyBillingCycle);
        assertEquals(120.0, historyBillingCycle.getMinVoltage(), 0.01);
        assertEquals(120.0, historyBillingCycle.getPeakVoltage(), 0.01);
        assertEquals(1l, historyBillingCycle.getMinVoltageTime().longValue());
        assertEquals(1l, historyBillingCycle.getPeakVoltageTime().longValue());

        energyPostService.compareVoltage(2, 119.0, historyBillingCycle);
        assertEquals(119.0, historyBillingCycle.getMinVoltage(),0.01);
        assertEquals(120.0, historyBillingCycle.getPeakVoltage(),0.01);
        assertEquals(2l, historyBillingCycle.getMinVoltageTime().longValue());
        assertEquals(1l, historyBillingCycle.getPeakVoltageTime().longValue());


        energyPostService.compareVoltage(3, 121.0, historyBillingCycle);
        assertEquals(119.0, historyBillingCycle.getMinVoltage(),0.01);
        assertEquals(121.0, historyBillingCycle.getPeakVoltage(),0.01);
        assertEquals(2l, historyBillingCycle.getMinVoltageTime().longValue());
        assertEquals(3l, historyBillingCycle.getPeakVoltageTime().longValue());

    }

    @Test
    public void addPowerFactor(){
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();

        energyPostService.addPowerFactor(5.0, historyBillingCycle);
        energyPostService.addPowerFactor(5.0, historyBillingCycle);

        assertEquals(10.0, historyBillingCycle.getPfTotal(), .01);
        assertEquals(2, historyBillingCycle.getPfSampleCount());

    }


    @Test
    public void testCompareDemandPeak(){
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
//
//        EnergyDifference demandPeak = new EnergyDifference();
//        demandPeak.setNet(120.0);
//
//        energyPostService.compareDemandPeak(1, demandPeak, historyBillingCycle);
//        assertEquals(120.0, historyBillingCycle.getDemandPeak(), 0.01);
//        assertEquals(1l, historyBillingCycle.getDemandPeakTime().longValue());
//
//        demandPeak.setNet(100.0);
//        energyPostService.compareDemandPeak(2, demandPeak, historyBillingCycle);
//        assertEquals(120.0, historyBillingCycle.getDemandPeak(), 0.01);
//        assertEquals(1l, historyBillingCycle.getDemandPeakTime().longValue());
//
//        demandPeak.setNet(300.0);
//        energyPostService.compareDemandPeak(3, demandPeak, historyBillingCycle);
//        assertEquals(300.0, historyBillingCycle.getDemandPeak(), 0.01);
//        assertEquals(3l, historyBillingCycle.getDemandPeakTime().longValue());
//

    }

//    @Test
//    public void testCalcDemandKey(){
//        VirtualECC virtualECC = new VirtualECC();
//        virtualECC.setId(1l);
//        EnergyPlan energyPlan = new EnergyPlan();
//        energyPlan.setDemandAverageTime(5);
//
//        HistoryMinute historyMinute = new HistoryMinute();
//        historyMinute.setStartEpoch(1454130360l);
//
//        HistoryMinuteKey newMinute =  energyPostService.calcDemandKey(virtualECC, energyPlan, TimeZone.getTimeZone("US/Eastern"), historyMinute.getStartEpoch());
//        assertEquals(2016, newMinute.getBillingCycleYear().intValue());
//        assertEquals(0, newMinute.getBillingCycleMonth().intValue());
//        assertEquals(29, newMinute.getBillingCycleDay().intValue());
//        assertEquals(0, newMinute.getBillingCycleHour().intValue());
//        assertEquals(1, newMinute.getBillingCycleMinute().intValue());
//    }

//    @Test
//    public void testCalcDemandAverage(){
//        VirtualECC virtualECC = new VirtualECC();
//        virtualECC.setId(1l);
//        EnergyPlan energyPlan = new EnergyPlan();
//        energyPlan.setDemandAverageTime(5);
//
//        HistoryMinute historyMinute = new HistoryMinute();
//        historyMinute.setRunningNetTotal(60.0);
//        historyMinute.setStartEpoch(1451624760l);
//
//        HistoryMinute oldMinute = new HistoryMinute();
//        oldMinute.setRunningNetTotal(10.0);
//        oldMinute.setMtuCount(1);
//
//        when(historyMinuteDAO.findOne(any())).thenReturn(oldMinute);
//
//        VirtualECCMTU mtu = new VirtualECCMTU();
//        mtu.setMtuType(MTUType.NET);
//        mtu.setPowerMultiplier(1.0);
//
//        EnergyData energyData = new EnergyData();
//        energyData.setAvg5(10.0);
//        double demandAverage = energyPostService.calcDemandPeak(virtualECC, mtu, energyPlan, energyData).getNet();
//        assertEquals(600.0, demandAverage, 0.01);
//
//        energyPlan.setDemandUseActivePower(false); //KVA Test
//        energyData.setPowerFactor(98.0);
//        demandAverage = energyPostService.calcDemandPeak(virtualECC, mtu, energyPlan, energyData).getNet();
//        assertEquals(612.245, demandAverage, .01);
//    }


    @Test
    public void testIsDemandChargeApplicable(){
        EnergyPlan energyPlan = new EnergyPlan();
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1454094000l); //Friday

        assertFalse(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.SUPER_PEAK, historyMinute));
        energyPlan.setDemandPlanType(DemandPlanType.TOU);

        assertTrue(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.SUPER_PEAK, historyMinute));

        //Test Off peak
        energyPlan.setDemandApplicableOffPeak(false);
        assertFalse(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.OFF_PEAK, historyMinute));
        energyPlan.setDemandApplicableOffPeak(true);
        assertTrue(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.OFF_PEAK, historyMinute));


        //Test Saturday
        historyMinute.setStartEpoch(1454180400l);
        energyPlan.setDemandApplicableSaturday(false);
        assertFalse(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.PEAK, historyMinute));
        energyPlan.setDemandApplicableSaturday(true);
        assertTrue(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.PEAK, historyMinute));

        //Test Sunday
        historyMinute.setStartEpoch(1454266800l);
        energyPlan.setDemandApplicableSunday(false);
        assertFalse(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.PEAK, historyMinute));
        energyPlan.setDemandApplicableSunday(true);
        assertTrue(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.PEAK, historyMinute));

        //Test Holiday
        energyPlan.setDemandApplicableSaturday(true);
        energyPlan.setDemandApplicableSunday(true);
        historyMinute.setStartEpoch(1451070000l);
        energyPlan.setDemandApplicableHoliday(false);
        assertFalse(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.PEAK, historyMinute));
        energyPlan.setDemandApplicableHoliday(true);
        assertTrue(energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.PEAK, historyMinute));
    }


    @Test
    public void testCalculateTieredDemandCost(){

        double demandPeak = 75000.0; //watts



        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setAccountId(1l);
        energyPlan.setNumberSeasons(1);
        energyPlan.getSeasonList().add(new EnergyPlanSeason(0, "ALL", 0, 0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 0.0, 10.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 50.0, 20.1));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 100.0, 30.0));
        energyPlan.setNumberDemandSteps(3);
        energyPlan.setDemandPlanType(DemandPlanType.TIERED);
        assertEquals(1507.50, energyPostService.calculateTieredDemandCost(demandPeak, energyPlan, 0),0.001);
    }

    @Test
    public void testCalculateTieredPeakDemandCost(){


        double demandPeak = 75000.0; //watts

        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setAccountId(1l);
        energyPlan.setNumberSeasons(1);
        energyPlan.getSeasonList().add(new EnergyPlanSeason(0, "ALL", 0, 0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 0.0, 10.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 50.0, 20.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 100.0, 30.0));
        energyPlan.setNumberDemandSteps(3);
        energyPlan.setDemandPlanType(DemandPlanType.TIERED_PEAK);
        assertEquals(1000.00, energyPostService.calculateTieredPeakDemandCost(demandPeak, energyPlan, 0), 0.001);
    }


    @Test
    public void testCalcDemandCost(){


        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setAccountId(1l);
        energyPlan.setNumberSeasons(1);
        energyPlan.getSeasonList().add(new EnergyPlanSeason(0, "ALL", 0, 0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 0.0, 10.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 50.0, 20.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTierList().add(new DemandChargeTier(0, 1l, 0, 100.0, 30.0));

        energyPlan.setNumberDemandSteps(3);
        energyPlan.setDemandPlanType(DemandPlanType.TIERED);

        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1453917186l);

        historyMinute.setDemandPeak(75000.0);
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.SUPER_PEAK, timeZone, historyMinute);
        assertEquals(75000.0, historyBillingCycle.getDemandCostPeak(), .001);
        historyMinute.setDemandPeak(70000.0);
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.SUPER_PEAK, timeZone, historyMinute);
        assertEquals(75000.0, historyBillingCycle.getDemandCostPeak(), .001);


        energyPlan.setDemandPlanType(DemandPlanType.TIERED_PEAK);
        historyMinute.setDemandPeak(75000.0);
        historyBillingCycle = new HistoryBillingCycle();
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.SUPER_PEAK, timeZone, historyMinute);
        assertEquals(75000.0, historyBillingCycle.getDemandCostPeak(), .001);
        historyMinute.setDemandPeak(70000.0);
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.SUPER_PEAK, timeZone, historyMinute);
        assertEquals(75000.0, historyBillingCycle.getDemandCostPeak(), .001);

    }


    @Test
    public void testTOUCalcDemandCost(){


        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setId(1l);
        energyPlan.setAccountId(1l);
        energyPlan.setNumberSeasons(1);
        energyPlan.getSeasonList().add(new EnergyPlanSeason(0, "ALL", 0, 0));
        energyPlan.getSeasonList().get(0).getDemandChargeTOUList().add(new DemandChargeTOU(TOUPeakType.OFF_PEAK, 0l, 0, 1.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTOUList().add(new DemandChargeTOU(TOUPeakType.PEAK, 0l, 0, 2.0));
        energyPlan.getSeasonList().get(0).getDemandChargeTOUList().add(new DemandChargeTOU(TOUPeakType.SUPER_PEAK, 0l, 0, 3.0));

        energyPlan.setNumberDemandSteps(3);
        energyPlan.setDemandPlanType(DemandPlanType.TOU);
        energyPlan.setDemandApplicableOffPeak(true);

        HistoryMinute historyMinute = new HistoryMinute();
        historyMinute.setStartEpoch(1453917186l);
        historyMinute.setDemandPeak(10000.0);
        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.OFF_PEAK, timeZone, historyMinute);
        assertEquals(10, historyBillingCycle.getDemandCost(), .001);
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.SUPER_PEAK, timeZone, historyMinute);
        assertEquals(30, historyBillingCycle.getDemandCost(), .001);
        historyMinute.setDemandPeak(100000.0);
        energyPostService.calcDemandCost(historyBillingCycle, energyPlan, 0, TOUPeakType.PEAK, timeZone, historyMinute);
        assertEquals(200, historyBillingCycle.getDemandCost(), .001);

    }



    @Test
    public void testApplyCost(){
        CostDifference costDifference = new CostDifference();
        costDifference.setRate(1.0);
        costDifference.setNet(2.0);
        costDifference.setGeneration(3.0);
        costDifference.setLoad(4.0);

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();
        energyPostService.applyCost(historyBillingCycle, costDifference);

        Assert.assertEquals(1.0, historyBillingCycle.getRateInEffect(), .0001);
        Assert.assertEquals(2.0, historyBillingCycle.getNetCost(), .0001);
        Assert.assertEquals(3.0, historyBillingCycle.getGenCost(), .0001);
        Assert.assertEquals(4.0, historyBillingCycle.getLoadCost(), .0001);
    }



}
