/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.model.BillingCycle;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.common.model.history.HistoryQuery;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.util.AverageUtils;
import com.ted.commander.server.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by pete on 10/28/2014.
 */
@Service
public class HistoryService {
    static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);


    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    HazelcastService hazelcastService;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    CostService costService;

    @Autowired
    WeatherService weatherService;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;


    public HistoryDay getTodayAverageHistory(VirtualECC virtualECC, long timeOfDayEpoch, long dayStartEpoch, int mtuCount){
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());

        List<HistoryMinute> historyMinuteList = historyMinuteDAO.findPastMinutesForAverage(virtualECC.getId(), timeOfDayEpoch, dayStartEpoch, timeZone, mtuCount );
        HistoryDay avgDay = new HistoryDay();


        List<Double> energyTotals = new ArrayList<>();
        List<Double> costTotals = new ArrayList();
        List<Double> minVoltages = new ArrayList<>();
        List<Double> peakVoltages = new ArrayList<>();
        List<Double> demandPeaks = new ArrayList<>();
        List<Double> avgPowerFactors = new ArrayList<>();
        List<Integer> avgPowerFactorPoints = new ArrayList<>();

        int lastDate = -1;



        for (HistoryMinute historyMinute: historyMinuteList){
            if (historyMinute.getMtuCount() == mtuCount){
                int day = new Date(historyMinute.getStartEpoch() * 1000).getDay();
                if (day != lastDate){
                    lastDate = day;
                    minVoltages.add(Double.MAX_VALUE);
                    peakVoltages.add(Double.MIN_VALUE);
                    demandPeaks.add(Double.MIN_VALUE);
                    energyTotals.add(0.0);
                    costTotals.add(0.0);
                    avgPowerFactors.add(0.0);
                    avgPowerFactorPoints.add(0);
                }

                energyTotals.set(energyTotals.size()-1, energyTotals.get(energyTotals.size()-1) + historyMinute.getNet());
                costTotals.set(costTotals.size()-1, costTotals.get(energyTotals.size()-1) + historyMinute.getNetCost());

                double powerFactor = historyMinute.getPfTotal() / historyMinute.getPfSampleCount();
                avgPowerFactors.set(avgPowerFactors.size()-1, avgPowerFactors.get(avgPowerFactors.size()-1) + powerFactor);
                avgPowerFactorPoints.set(avgPowerFactorPoints.size()-1, avgPowerFactorPoints.get(avgPowerFactorPoints.size()-1) + 1);


                //Peaks
                double voltage = historyMinute.getVoltageTotal()/ historyMinute.getMtuCount();
                if (voltage < minVoltages.get(minVoltages.size() - 1)) minVoltages.set(minVoltages.size()-1, voltage);
                if (voltage > peakVoltages.get(peakVoltages.size() - 1)) peakVoltages.set(peakVoltages.size()-1, voltage);
                if (historyMinute.getDemandPeak() > demandPeaks.get(demandPeaks.size()-1)) demandPeaks.set(demandPeaks.size()-1, historyMinute.getDemandPeak());
            }
        }

        for (int i=0; i < avgPowerFactorPoints.size(); i++){
            double avgPowerFactor = avgPowerFactors.get(i);
            int total = avgPowerFactorPoints.get(i);
            avgPowerFactor /= total;
            avgPowerFactors.set(i, avgPowerFactor);
        }

        avgDay.setNet(AverageUtils.calculateAverage(energyTotals));
        avgDay.setNetCost(AverageUtils.calculateAverage(costTotals));
        avgDay.setDemandPeak(AverageUtils.calculateAverage(demandPeaks));
        avgDay.setPeakVoltage(AverageUtils.calculateAverage(peakVoltages));
        avgDay.setMinVoltage(AverageUtils.calculateAverage(minVoltages));
        avgDay.setPfTotal(AverageUtils.calculateAverage(avgPowerFactors));

        return avgDay;
    }


    public HistoryDay getAverageHistory(VirtualECC virtualECC, long dayStartEpoch){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(virtualECC.getTimezone()));
        calendar.setTimeInMillis(dayStartEpoch * 1000);
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.MONTH, -2);

        List<HistoryDay> historyDayList = historyDayDAO.findByDateRange(virtualECC.getId(), calendar.getTimeInMillis() /1000, dayStartEpoch - 3600);
        List<Double> energyTotals = new ArrayList<>();
        List<Double> costTotals = new ArrayList();
        List<Double> minVoltages = new ArrayList<>();
        List<Double> peakVoltages = new ArrayList<>();
        List<Double> demandPeaks = new ArrayList<>();
        List<Double> avgPowerFactors = new ArrayList<>();


        for (HistoryDay historyDay: historyDayList){
            Calendar dowCalendar = Calendar.getInstance(TimeZone.getTimeZone(virtualECC.getTimezone()));
            dowCalendar.setTimeInMillis(historyDay.getStartEpoch() * 1000);
            if (dowCalendar.get(Calendar.DAY_OF_WEEK) == dow) {
                energyTotals.add(historyDay.getNet());
                costTotals.add(historyDay.getNetCost());
                minVoltages.add(historyDay.getMinVoltage());
                peakVoltages.add(historyDay.getPeakVoltage());
                demandPeaks.add(historyDay.getDemandPeak());
                avgPowerFactors.add(historyDay.getPfTotal() / historyDay.getPfSampleCount());
            }
        }

        HistoryDay avgDay = new HistoryDay();
        avgDay.setNet(AverageUtils.calculateAverage(energyTotals));
        avgDay.setNetCost(AverageUtils.calculateAverage(costTotals));
        avgDay.setDemandPeak(AverageUtils.calculateAverage(demandPeaks));
        avgDay.setPeakVoltage(AverageUtils.calculateAverage(peakVoltages));
        avgDay.setMinVoltage(AverageUtils.calculateAverage(minVoltages));
        avgDay.setPfTotal(AverageUtils.calculateAverage(avgPowerFactors));
        return avgDay;
    }





    /**
     * Generates a list of billing cycles
     *
     * @return
     */
    public List<BillingCycle> calculateBillingCycles(VirtualECC virtualECC, CalendarKey startDate, CalendarKey endDate) {
        List<BillingCycle> billingCycleList = new ArrayList<BillingCycle>();

        LOGGER.debug("CALCULATING BILLING CYCLES FOR {} {} {} ", virtualECC.getId(), startDate, endDate);
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());



        //Find the Energy plan at the start of the query
        Calendar startCalendar = CalendarUtils.fromCalendarKey(startDate, timeZone);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = CalendarUtils.fromCalendarKey(endDate, timeZone);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        endCalendar.add(Calendar.DATE, 1);

        LOGGER.debug("Base Start Date:{}", startCalendar.getTime());
        LOGGER.debug("Base End Date:{}", endCalendar.getTime());



        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
        //Adjust the range to the beginning of the billing cycle.

        int meterReadDate = energyPlan.getMeterReadDate();
        MeterReadCycle meterReadCycle = energyPlan.getMeterReadCycle();

        //Find the first billing cycle start date prior to the startCalendar.
        if (startCalendar.get(Calendar.DAY_OF_MONTH) < meterReadDate) {
            startCalendar.add(Calendar.MONTH, -1);
        }
        startCalendar.set(Calendar.DAY_OF_MONTH, meterReadDate);


        if (endCalendar.get(Calendar.DAY_OF_MONTH) > meterReadDate) {
            endCalendar.add(Calendar.MONTH, 1);
        }
        endCalendar.set(Calendar.DAY_OF_MONTH, meterReadDate);

        if (meterReadCycle.equals(MeterReadCycle.BI_MONTHLY_ODD) && (startCalendar.get(Calendar.MONTH) % 2) == 0)
            startCalendar.add(Calendar.MONTH, -1);
        if (meterReadCycle.equals(MeterReadCycle.BI_MONTHLY_EVEN) && (startCalendar.get(Calendar.MONTH) % 2) != 0)
            startCalendar.add(Calendar.MONTH, -1);

        LOGGER.debug("Adjusted Start Date:{}", startCalendar.getTime());

        LOGGER.debug("END DATE: {}", endCalendar.getTime());


        while (startCalendar.before(endCalendar)) {

            BillingCycle billingCycle = new BillingCycle();
            billingCycle.setStartEpoch(startCalendar.getTimeInMillis() / 1000);
            LOGGER.debug("NEW START KEY:" + startCalendar.get(Calendar.MONTH) + " " + startCalendar.get(Calendar.DATE) + " " + startCalendar.getTime());
            billingCycle.setStartKey(new CalendarKey(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE)));


            //Calculate the end of the plan
            LOGGER.debug("Timezone: " + timeZone.getID());
            LOGGER.debug("SCTimezone: " + startCalendar.getTimeZone().getID());
            Calendar planEndDate = Calendar.getInstance(timeZone);
            planEndDate.setTimeInMillis(startCalendar.getTimeInMillis());
            meterReadCycle = energyPlan.getMeterReadCycle();
            if (meterReadCycle.equals(MeterReadCycle.MONTHLY)) {
                planEndDate.add(Calendar.MONTH, 1);
            } else {
                planEndDate.add(Calendar.MONTH, 2);
            }

            startCalendar.setTimeInMillis(planEndDate.getTimeInMillis());

//            LOGGER.debug("ACTIVE ENERGY PLAN: {}", activeEnergyPlan);
            billingCycle.setEnergyPlanId(energyPlan.getId());
            billingCycle.setEndEpoch(startCalendar.getTimeInMillis() / 1000);
            LOGGER.debug("END SCTimezone: " + startCalendar.getTimeZone().getID());
            LOGGER.debug("NEW END KEY:" + startCalendar.get(Calendar.MONTH) + " " + startCalendar.get(Calendar.DATE) + " " + startCalendar.getTime());
            billingCycle.setEndKey(new CalendarKey(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE)));

            if (billingCycle.getStartEpoch() <= (System.currentTimeMillis() / 1000)) {


                if (billingCycleList.size() > 0) {
                    BillingCycle lastBillingCycle = billingCycleList.get(billingCycleList.size() - 1);
                    if (lastBillingCycle.getStartKey().getMonth() == billingCycle.getStartKey().getMonth()) {
                        LOGGER.debug("-----> PLAN CHANGED: {}", lastBillingCycle);
                        lastBillingCycle.setEndKey(billingCycle.getEndKey());
                        lastBillingCycle.setEndEpoch(billingCycle.getEndEpoch());
                        lastBillingCycle.setEnergyPlanId(billingCycle.getEnergyPlanId());
                    } else {
                        LOGGER.debug("------>Billing Cycle {}", billingCycle);
                        billingCycleList.add(billingCycle);
                    }
                } else {
                    LOGGER.debug("------>Billing Cycle {}", billingCycle);
                    billingCycleList.add(billingCycle);
                }
            }

        }

        return billingCycleList;
    }


    /**
     * Generates a list of billing cycles
     *
     * @param query
     * @return
     */
    public List<BillingCycle> calculateBillingCycles(VirtualECC virtualECC, HistoryQuery query) {
        return calculateBillingCycles(virtualECC, query.getStartDate(), query.getEndDate());
    }




}
