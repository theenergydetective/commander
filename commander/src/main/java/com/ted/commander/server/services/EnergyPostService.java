/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.StandAlonePost;
import com.ted.commander.server.model.TOUDifference;
import com.ted.commander.server.model.energyPost.EnergyCumulativePost;
import com.ted.commander.server.model.energyPost.EnergyMTUPost;
import com.ted.commander.server.model.energyPost.EnergyPost;
import com.ted.commander.server.util.CalendarUtils;
import com.ted.commander.server.util.HolidayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class EnergyPostService {
    static final Logger LOGGER = LoggerFactory.getLogger(EnergyPostService.class);


    @Autowired
    HistoryDayDAO historyDayDAO;
    @Autowired
    HistoryHourDAO historyHourDAO;
    @Autowired
    HistoryMinuteDAO historyMinuteDAO;
    @Autowired
    HistoryMTUBillingCycleDAO historyMTUBillingCycleDAO;
    @Autowired
    HistoryMTUDayDAO historyMTUDayDAO;
    @Autowired
    HistoryMTUHourDAO historyMTUHourDAO;
    @Autowired
    EnergyDataDAO energyDataDAO;

    @Autowired
    MTUDAO mtuDAO;

    @Autowired
    ServerService serverService;
    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    EnergyPostQueue energyPostQueue;

    @Autowired
    PollingService pollingService;

    @Autowired
    HazelcastService hazelcastService;

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    CostService costService;

    @Autowired
    AdvisorService advisorService;

    Stat averageEnergyPost = new Stat();
    Stat averageMTUPost = new Stat();
    Stat averageHistoryPost = new Stat();
    Stat averageLoad = new Stat();
    Stat averageSave = new Stat();
    Stat averageNet = new Stat();
    Stat averageHead = new Stat();
    Stat averageSA = new Stat();
    long postsProcessed = 0;


    @PostConstruct
    public void init() {
        for (int i = 0; i < PollingService.NUMBER_ENERGYPOST_THREADS; i++) {
            pollingService.startEnergyPostPoller(hazelcastService, this, i);
        }
        for (int i = 0; i < PollingService.NUMBER_STANDALONE_THREADS; i++) {
            pollingService.startStandAlonePoller(hazelcastService, this, i);
        }
    }


    public void applyCost(HistoryCost historyCost, CostDifference costDifference) {
        historyCost.setRateInEffect(costDifference.getRate());
        historyCost.setGenCost(historyCost.getGenCost() + costDifference.getGeneration());
        historyCost.setNetCost(historyCost.getNetCost() + costDifference.getNet());
        historyCost.setLoadCost(historyCost.getLoadCost() + costDifference.getLoad());
    }

    /**
     * This updates the energy value for the specified record
     *
     * @param location
     * @param mtu
     * @param energyDifference
     */
    public EnergyDifference calcEnergyDifference(VirtualECC location, VirtualECCMTU mtu, double energyDifference, double cumulativeTotal) {

        EnergyDifference result = new EnergyDifference();

        switch (location.getSystemType()) {
            case NET_GEN: {
                switch (mtu.getMtuType()) {
                    case NET: {
                        result.setNet(energyDifference);
                        result.setNetTotal(cumulativeTotal);
                        break;
                    }
                    case GENERATION: {
                        result.setGeneration(energyDifference);
                        break;
                    }
                }
                result.setLoad(result.getNet() - result.getGeneration());
                break;
            }

            case LOAD_GEN: {
                switch (mtu.getMtuType()) {
                    case LOAD: {
                        result.setLoad(Math.abs(energyDifference));
                        break;
                    }
                    case GENERATION: {
                        result.setGeneration(energyDifference);
                        break;
                    }
                }
                result.setNetTotal(cumulativeTotal);
                result.setNet(result.getLoad() + result.getGeneration());
                break;
            }

            case NET_ONLY: {
                switch (mtu.getMtuType()) {
                    case NET: {
                        result.setNet(energyDifference);
                        result.setNetTotal(cumulativeTotal);
                        break;
                    }
                }
            }
        }
        return result;
    }


    public HistoryBillingCycleKey findBillingCycleKey(VirtualECC virtualECC, EnergyPlan energyPlan, TimeZone timeZone, long epochTime) {
        LOGGER.debug("Looking up billing cycle for epoch: {} timezone: {} energyPlan: {}", epochTime, timeZone, energyPlan);

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(epochTime * 1000);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int meterReadDate = energyPlan.getMeterReadDate();

        switch (energyPlan.getMeterReadCycle()) {
            case MONTHLY: {
                if (dayOfMonth < meterReadDate) {
                    calendar.add(Calendar.MONTH, -1);
                }
                break;
            }

            case BI_MONTHLY_EVEN: {
                if (calendar.get(Calendar.MONTH) % 2 == 0) {
                    calendar.add(Calendar.MONTH, -1);
                } else {
                    if (dayOfMonth < meterReadDate) {
                        calendar.add(Calendar.MONTH, -2);
                    }
                }
                break;
            }

            case BI_MONTHLY_ODD: {
                if (calendar.get(Calendar.MONTH) % 2 != 0) {
                    calendar.add(Calendar.MONTH, -1);
                } else {
                    if (dayOfMonth < meterReadDate) {
                        calendar.add(Calendar.MONTH, -2);
                    }
                }
                break;
            }
        }

        return new HistoryBillingCycleKey(virtualECC.getId(), CalendarUtils.keyFromMillis(calendar.getTimeInMillis(), timeZone));
    }


    public Long findDayEpoch(long epochTime, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(epochTime * 1000);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }


    public Long findHourEpoch(long epochTime, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(epochTime * 1000);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }


    public Long findMinuteEpoch(long epochTime, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(epochTime * 1000);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }


    public HistoryBillingCycle getBillingCycleInstance(HistoryBillingCycleKey historyBillingCycleKey, TimeZone timeZone, EnergyPlan energyPlan, int season) {
        HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findOne(historyBillingCycleKey);

        if (historyBillingCycle == null) {
            double taxRate = 0.0;
            historyBillingCycle = new HistoryBillingCycle();
            historyBillingCycle.setKey(historyBillingCycleKey);
            historyBillingCycle.setMeterReadStartDate(energyPlan.getMeterReadDate());
            setHistoryBillingCycleEpoch(historyBillingCycle, energyPlan, timeZone);
            if (energyPlan.getSeasonList().size() > season) {
                EnergyPlanSeason energyPlanSeason = energyPlan.getSeasonList().get(season);

                //Get the tax Rate
                if (energyPlan.isTaxes()) {
                    for (AdditionalCharge additionalCharge : energyPlanSeason.getAdditionalChargeList()) {
                        if (additionalCharge.getAdditionalChargeType().equals(AdditionalChargeType.TAX)) {
                            taxRate = additionalCharge.getRate();
                            break;
                        }
                    }
                }
                for (AdditionalCharge additionalCharge : energyPlanSeason.getAdditionalChargeList()) {
                    if (additionalCharge.getAdditionalChargeType().equals(AdditionalChargeType.MINIMUM))
                        historyBillingCycle.setMinimumCharge(costService.applyTax(additionalCharge.getRate(), taxRate));
                    if (additionalCharge.getAdditionalChargeType().equals(AdditionalChargeType.FIXED))
                        historyBillingCycle.setFixedCharge(costService.applyTax(additionalCharge.getRate(), taxRate));
                }
            }
            historyBillingCycleDAO.insert(historyBillingCycle);
        } else {
            //Fix the end date of the billing cycle if the meter read date has changed.
            if (historyBillingCycle.getMeterReadStartDate() != energyPlan.getMeterReadDate().intValue()) {
                LOGGER.warn("Adjusting meter read date: {} {}", historyBillingCycle.getMeterReadStartDate(), historyBillingCycle);
                //Adjust the date range. Note: the values will be incorrect for this. Do we need a flag?
                historyBillingCycle.setMeterReadStartDate(energyPlan.getMeterReadDate());
                historyBillingCycle.setMeterReadDateChanged(true);
                updateEndEpoch(historyBillingCycle, energyPlan, timeZone);
                historyBillingCycleDAO.save(historyBillingCycle);
            }

        }
        return historyBillingCycle;
    }

    public HistoryMTUBillingCycle getMTUBillingCycleInstance(HistoryMTUBillingCycleKey historyBillingCycleKey, EnergyPlan energyPlan, TimeZone timeZone) {
        HistoryMTUBillingCycle historyBillingCycle = historyMTUBillingCycleDAO.findOne(historyBillingCycleKey);
        if (historyBillingCycle == null) {
            historyBillingCycle = new HistoryMTUBillingCycle();
            historyBillingCycle.setKey(historyBillingCycleKey);
            setHistoryMTUBillingCycleEpoch(historyBillingCycle, energyPlan, timeZone);
            historyMTUBillingCycleDAO.insert(historyBillingCycle);
        }
        return historyBillingCycle;
    }

    public HistoryDay getDayInstance(HistoryBillingCycleKey historyBillingCycleKey, Long virtualECCId, Long dayEpoch, EnergyPlan energyPlan, TimeZone timeZone) {
        HistoryDay historyDay = historyDayDAO.findOne(virtualECCId, dayEpoch);
        if (historyDay == null) {
            historyDay = new HistoryDay();
            historyDay.setVirtualECCId(virtualECCId);
            historyDay.setStartEpoch(dayEpoch);
            historyDay.setBillingCycleMonth(historyBillingCycleKey.getBillingCycleMonth());
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(dayEpoch * 1000);
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            historyDay.setEndEpoch(calendar.getTimeInMillis() / 1000);
            historyDayDAO.insert(historyDay);
        }
        return historyDay;
    }


    public HistoryMTUDay getMTUDayInstance(Long virtualECCId, Long mtuId, Long dayEpoch, TimeZone timeZone) {
        HistoryMTUDay historyDay = historyMTUDayDAO.findOne(virtualECCId, mtuId, dayEpoch);
        if (historyDay == null) {
            historyDay = new HistoryMTUDay();
            historyDay.setVirtualECCId(virtualECCId);
            historyDay.setMtuId(mtuId);
            historyDay.setStartEpoch(dayEpoch);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(dayEpoch * 1000);
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            historyDay.setEndEpoch(calendar.getTimeInMillis() / 1000);
            historyMTUDayDAO.insert(historyDay);
        }
        return historyDay;
    }

    public HistoryHour getHourInstance(Long virtualECCId, Long hourEpoch, TimeZone timeZone) {
        HistoryHour historyHour = historyHourDAO.findOne(virtualECCId, hourEpoch);
        if (historyHour == null) {
            historyHour = new HistoryHour();
            historyHour.setVirtualECCId(virtualECCId);
            historyHour.setStartEpoch(hourEpoch);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(hourEpoch * 1000);
            calendar.add(Calendar.HOUR, 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            historyHour.setEndEpoch(calendar.getTimeInMillis() / 1000);
            historyHourDAO.insert(historyHour);
        }
        return historyHour;
    }

    public HistoryMTUHour getMTUHourInstance(Long virtualECCId, Long mtuId, Long hourEpoch, TimeZone timeZone) {
        HistoryMTUHour historyHour = historyMTUHourDAO.findOne(virtualECCId, mtuId, hourEpoch);
        if (historyHour == null) {
            historyHour = new HistoryMTUHour();
            historyHour.setVirtualECCId(virtualECCId);
            historyHour.setMtuId(mtuId);
            historyHour.setStartEpoch(hourEpoch);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(hourEpoch * 1000);
            calendar.add(Calendar.HOUR, 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            historyHour.setEndEpoch(calendar.getTimeInMillis() / 1000);
            historyMTUHourDAO.insert(historyHour);
        }
        return historyHour;
    }

    public HistoryMinute getMinuteInstance(Long virtualECCId, Long minuteEpoch, TimeZone timeZone) {
        //Finds or creates the history minute.
        return historyMinuteDAO.findOne(virtualECCId, minuteEpoch, timeZone);
    }

    public void setHistoryBillingCycleEpoch(HistoryBillingCycle historyBillingCycle, EnergyPlan energyPlan, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, energyPlan.getMeterReadDate());
        calendar.set(Calendar.MONTH, historyBillingCycle.getKey().getBillingCycleMonth());
        calendar.set(Calendar.YEAR, historyBillingCycle.getKey().getBillingCycleYear());
        historyBillingCycle.setStartEpoch(calendar.getTimeInMillis() / 1000);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (energyPlan.getMeterReadCycle().equals(MeterReadCycle.MONTHLY)) {
            month++;
        } else {
            month += 2;
        }
        if (month > 11) {
            month -= 12;
            year++;
        }
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, energyPlan.getMeterReadDate());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        historyBillingCycle.setEndEpoch(calendar.getTimeInMillis() / 1000);
    }


    public void updateEndEpoch(HistoryBillingCycle historyBillingCycle, EnergyPlan energyPlan, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, energyPlan.getMeterReadDate());
        calendar.set(Calendar.MONTH, historyBillingCycle.getKey().getBillingCycleMonth());
        calendar.set(Calendar.YEAR, historyBillingCycle.getKey().getBillingCycleYear());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (energyPlan.getMeterReadCycle().equals(MeterReadCycle.MONTHLY)) {
            month++;
        } else {
            month += 2;
        }
        if (month > 11) {
            month -= 12;
            year++;
        }
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, energyPlan.getMeterReadDate());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        historyBillingCycle.setEndEpoch(calendar.getTimeInMillis() / 1000);
    }


    public void setHistoryMTUBillingCycleEpoch(HistoryMTUBillingCycle historyMTUBillingCycle, EnergyPlan energyPlan, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, historyMTUBillingCycle.getKey().getBillingCycleMonth());
        calendar.set(Calendar.YEAR, historyMTUBillingCycle.getKey().getBillingCycleYear());

        historyMTUBillingCycle.setStartEpoch(calendar.getTimeInMillis() / 1000);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (energyPlan.getMeterReadCycle().equals(MeterReadCycle.MONTHLY)) {
            month++;
        } else {
            month += 2;
        }
        if (month > 11) {
            month -= 12;
            year++;
        }
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, energyPlan.getMeterReadDate());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        historyMTUBillingCycle.setEndEpoch(calendar.getTimeInMillis() / 1000);
    }


    public TOUDifference calcTOUDifference(EnergyPlan energyPlan, long epochTime, TimeZone timeZone, TOUPeakType touPeakType, EnergyDifference energyDifference) {
        TOUDifference touDifference = null;

        //Check to make sure the energy plan is in effect
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(epochTime * 1000);

        if (energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            //Check applicable
            touDifference = new TOUDifference();
            touDifference.setTouPeakType(touPeakType);
            touDifference.setNet(energyDifference.getNet());
            touDifference.setLoad(energyDifference.getLoad());
            touDifference.setGeneration(energyDifference.getGeneration());
        }
        //Return the value
        return touDifference;
    }

    private boolean isZero(double value, double threshold) {
        return value >= -threshold && value <= threshold;
    }

    public void compareVoltage(long epochTime, double voltage, HistoryVoltage historyVoltage) {
        if (isZero(historyVoltage.getMinVoltage(), 0.01) || voltage < historyVoltage.getMinVoltage()) {
            historyVoltage.setMinVoltage(voltage);
            historyVoltage.setMinVoltageTime(epochTime);
        }

        if (isZero(historyVoltage.getPeakVoltage(), 0.01) || voltage > historyVoltage.getPeakVoltage()) {
            historyVoltage.setPeakVoltage(voltage);
            historyVoltage.setPeakVoltageTime(epochTime);
        }

    }

    public void compareDemandPeak(HistoryMinute historyMinute, HistoryDemandPeak historyDemandPeak) {
        if (historyMinute.getDemandPeak() > historyDemandPeak.getDemandPeak()) {
            historyDemandPeak.setDemandPeak(historyMinute.getDemandPeak());
            historyDemandPeak.setDemandPeakTime(historyMinute.getStartEpoch());
        }

        if (historyMinute.getLoadPeak() > historyDemandPeak.getLoadPeak()) {
            historyDemandPeak.setLoadPeak(historyMinute.getLoadPeak());
            historyDemandPeak.setLoadPeakTime(historyMinute.getStartEpoch());
        }

        if (historyMinute.getGenerationPeak() < historyDemandPeak.getGenerationPeak()) {
            historyDemandPeak.setGenerationPeak(historyMinute.getGenerationPeak());
            historyDemandPeak.setGenerationPeakTime(historyMinute.getStartEpoch());
        }
    }


    public void compareDemandPeak(long timeStamp, EnergyDifference peakDemand, HistoryDemandPeak historyDemandPeak) {
        if (peakDemand.getNet() > historyDemandPeak.getDemandPeak()) {
            historyDemandPeak.setDemandPeak(peakDemand.getNet());
            historyDemandPeak.setDemandPeakTime(timeStamp);
        }
    }

    public void addPowerFactor(double powerFactor, HistoryPowerFactor historyPowerFactor) {
        historyPowerFactor.setPfTotal(historyPowerFactor.getPfTotal() + powerFactor);
        historyPowerFactor.setPfSampleCount(historyPowerFactor.getPfSampleCount() + 1);
    }

//    public HistoryMinuteKey calcDemandKey(VirtualECC location, EnergyPlan energyPlan, TimeZone timeZone, long minuteEpoch) {
//        Calendar calendar = Calendar.getInstance(timeZone);
//        calendar.setTimeInMillis(minuteEpoch * 1000);
//        //Adjust to the previous record based on the energy plan
//        calendar.add(Calendar.MINUTE, (energyPlan.getDemandAverageTime() * -1));
//        long oldEpoch = calendar.getTimeInMillis() / 1000;
//        HistoryBillingCycleKey billingCycleKey = findBillingCycleKey(location, energyPlan, timeZone, oldEpoch);
//        HistoryDayKey dayKey = findDayKey(billingCycleKey, energyPlan, timeZone, oldEpoch);
//        HistoryHourKey hourKey = findHourKey(dayKey, timeZone, oldEpoch);
//        return findMinuteKey(hourKey, timeZone, oldEpoch);
//    }

    double roundDemand(double oldDemand) {
        long flooredDemand = (long) (oldDemand * 1000000.0);
        return ((double) flooredDemand) / 1000000.0;
    }

    public EnergyDifference calcDemandPeak(VirtualECC location, VirtualECCMTU mtu, EnergyPlan energyPlan, EnergyData energyData) {
        EnergyDifference demandPeak = new EnergyDifference();

        double value = 0.0;

        switch (energyPlan.getDemandAverageTime()) {
            case 5:
                value = energyData.getAvg5();
                break;
            case 10:
                value = energyData.getAvg10();
                break;
            case 20:
                value = energyData.getAvg20();
                break;
            case 30:
                value = energyData.getAvg30();
                break;
            default:
                value = energyData.getAvg15();
        }

        value *= mtu.getPowerMultiplier();
        value *= 60;

        if (!energyPlan.getDemandUseActivePower()) {
            //Convert to KVA
            double powerFactor = energyData.getPowerFactor();
            value *= (1.0 / powerFactor);
            value *= 100;
        }
        if (Double.isInfinite(value) || Double.isNaN(value)) return new EnergyDifference();


        roundDemand(value);

        switch (location.getSystemType()) {
            case NET_GEN: {
                switch (mtu.getMtuType()) {
                    case NET: {
                        demandPeak.setNet((value));
                        break;
                    }
                    case GENERATION: {
                        demandPeak.setGeneration((value));
                        break;
                    }
                }
                demandPeak.setLoad((demandPeak.getNet() - demandPeak.getGeneration()));
                break;
            }

            case LOAD_GEN: {

                switch (mtu.getMtuType()) {
                    case LOAD: {
                        demandPeak.setLoad((Math.abs(value)));
                        break;
                    }
                    case GENERATION: {
                        demandPeak.setGeneration((value));
                        break;
                    }
                }
                demandPeak.setNet(roundDemand(demandPeak.getLoad() + demandPeak.getGeneration()));
                break;
            }

            case NET_ONLY: {
                switch (mtu.getMtuType()) {
                    case NET: {
                        demandPeak.setNet(roundDemand(value));
                        break;
                    }
                }
            }
        }
        return demandPeak;
    }

    public String getTOUPeakName(EnergyPlan energyPlan, TOUPeakType touPeakType) {
        for (EnergyPlanTOULevel energyPlanTOULevel : energyPlan.getTouLevels()) {
            if (energyPlanTOULevel.getPeakType().equals(touPeakType)) {
                return energyPlanTOULevel.getTouLevelName();
            }
        }
        return touPeakType.name();
    }

    public void updateMTUEnergyDifference(HistoryMTU historyMTU, double diff) {
        double energy = historyMTU.getEnergy();
        energy += diff;
        historyMTU.setEnergy(energy);
    }

    public void addTOU(TOUPeakType peakType, HistoryMTU historyMTU, Double diff) {
        switch (peakType) {
            case OFF_PEAK: {
                historyMTU.setTouOffPeak(historyMTU.getTouOffPeak() + diff);
                break;
            }
            case PEAK: {
                historyMTU.setTouPeak(historyMTU.getTouPeak() + diff);
                break;
            }
            case MID_PEAK: {
                historyMTU.setTouMidPeak(historyMTU.getTouMidPeak() + diff);
                break;
            }
            case SUPER_PEAK: {
                historyMTU.setTouSuperPeak(historyMTU.getTouSuperPeak() + diff);
                break;
            }
        }
    }


    public boolean processEnergyPost(VirtualECC location, int totalMTU, VirtualECCMTU mtu, long timeStamp, boolean playback) {

        EnergyData energyData = findFromCache(mtu.getAccountId(), mtu.getMtuId(), timeStamp);

        if (energyData == null) {
            LOGGER.error("!!!Unable to find energy data for mtu:{} account:{} timestamp:{}", mtu.getHexId(), mtu.getAccountId(), timeStamp);
            return false;
        }
        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(location.getEnergyPlanId());
        processEnergyPost(location, totalMTU, mtu, energyPlan, timeStamp, energyData, playback);
        return true;
    }


    public void processEnergyPost(VirtualECC location, int totalMTU, VirtualECCMTU mtu, EnergyPlan energyPlan, long timeStamp, EnergyData energyData, boolean playback) {
        long s = System.currentTimeMillis();

        TimeZone timeZone = TimeZone.getTimeZone(location.getTimezone());
        int season = costService.findSeason(energyPlan, timeZone, timeStamp);


        LOGGER.debug("Generating keys");
        HistoryBillingCycleKey billingCycleKey = findBillingCycleKey(location, energyPlan, timeZone, timeStamp);
        HistoryBillingCycle historyBillingCycle = getBillingCycleInstance(billingCycleKey, timeZone, energyPlan, season);


        Long dayEpoch = findDayEpoch(timeStamp, timeZone);
        Long hourEpoch = findHourEpoch(timeStamp, timeZone);
        Long minuteEpoch = findMinuteEpoch(timeStamp, timeZone);
        HistoryMTUBillingCycleKey mtuBillingCycleKey = new HistoryMTUBillingCycleKey(billingCycleKey, mtu.getMtuId());
        EnergyDifference energyDifference = calcEnergyDifference(location, mtu, energyData.getEnergyDifference(), energyData.getEnergy());

        averageHead.add(System.currentTimeMillis() - s);

        if (!mtu.getMtuType().equals(MTUType.STAND_ALONE)) {
            //Get Instances for each type

            long startLoad = System.currentTimeMillis();
            HistoryMinute historyMinute = getMinuteInstance(location.getId(), minuteEpoch, timeZone);

            HistoryDay historyDay = getDayInstance(billingCycleKey, location.getId(), dayEpoch, energyPlan, timeZone);
            HistoryHour historyHour = getHourInstance(location.getId(), hourEpoch, timeZone);


            //Get instances for the mtu
            HistoryMTUBillingCycle historyMTUBillingCycle = getMTUBillingCycleInstance(mtuBillingCycleKey, energyPlan, timeZone);
            HistoryMTUDay historyMTUDay = getMTUDayInstance(location.getId(), mtu.getMtuId(), dayEpoch, timeZone);
            HistoryMTUHour historyMTUHour = getMTUHourInstance(location.getId(), mtu.getMtuId(), hourEpoch, timeZone);
            long endLoad = System.currentTimeMillis();
            averageLoad.add(endLoad - startLoad);

            long startNet = System.currentTimeMillis();
            //Update mtu count
            historyMinute.setMtuCount(historyMinute.getMtuCount() + 1);
            historyDay.setMtuCount(historyMinute.getMtuCount() + 1);
            historyHour.setMtuCount(historyMinute.getMtuCount() + 1);
            historyBillingCycle.setMtuCount(historyMinute.getMtuCount() + 1);

            //Update energy totals
            energyDifference.addTo(historyBillingCycle);
            energyDifference.addTo(historyDay);
            energyDifference.addTo(historyHour);
            energyDifference.addTo(historyMinute);

            //Update MTU row values
            updateMTUEnergyDifference(historyMTUBillingCycle, energyData.getEnergyDifference());
            updateMTUEnergyDifference(historyMTUDay, energyData.getEnergyDifference());
            updateMTUEnergyDifference(historyMTUHour, energyData.getEnergyDifference());


            //Update the running net total for minute (used for demand)
            historyMinute.setRunningNetTotal(historyMinute.getRunningNetTotal() + energyDifference.getNetTotal());


            LOGGER.debug("Updating TOU Values");
            TOUPeakType touPeakType = costService.findTOU(energyPlan, season, timeZone, historyMinute.getStartEpoch());

            TOUDifference touDifference = calcTOUDifference(energyPlan, historyMinute.getStartEpoch(), timeZone, touPeakType, energyDifference);
            if (touDifference != null) {
                touDifference.addTo(historyBillingCycle);
                touDifference.addTo(historyDay);
                touDifference.addTo(historyHour);
                touDifference.addTo(historyMinute);

                addTOU(touPeakType, historyMTUBillingCycle, energyData.getEnergyDifference());
                addTOU(touPeakType, historyMTUDay, energyData.getEnergyDifference());
                addTOU(touPeakType, historyMTUHour, energyData.getEnergyDifference());

            }

            CostDifference costDifference = costService.calcCost(historyBillingCycle, energyDifference, touDifference, energyPlan, season);
            applyCost(historyBillingCycle, costDifference);
            applyCost(historyDay, costDifference);
            applyCost(historyHour, costDifference);
            applyCost(historyMinute, costDifference);


            //Update MTU Cost values
            double rate = costDifference.getRate();
            double taxRate = costService.getTaxRate(energyPlan, season);
            rate = rate + (rate * (taxRate / 100.0));
            double cost = rate * (energyData.getEnergyDifference().doubleValue() / 1000.0);
            historyMTUHour.setCost(historyMTUHour.getCost() + cost);
            historyMTUDay.setCost(historyMTUDay.getCost() + cost);
            historyMTUBillingCycle.setCost(historyMTUBillingCycle.getCost() + cost);


            LOGGER.debug("Updating voltage");
            //DO Minute (different than other types)
            double voltageTotal = historyMinute.getVoltageTotal();
            voltageTotal += (energyData.getVoltage() * mtu.getVoltageMultiplier());
            historyMinute.setVoltageTotal(voltageTotal);
            double minVoltage = voltageTotal / (double) historyMinute.getMtuCount();


            compareVoltage(timeStamp, minVoltage, historyHour);
            compareVoltage(timeStamp, minVoltage, historyDay);
            compareVoltage(timeStamp, minVoltage, historyBillingCycle);


            compareVoltage(timeStamp, energyData.getVoltage(), historyMTUHour);
            compareVoltage(timeStamp, energyData.getVoltage(), historyMTUDay);
            compareVoltage(timeStamp, energyData.getVoltage(), historyMTUBillingCycle);

            LOGGER.debug("Updating Power Factor");
            addPowerFactor(energyData.getPowerFactor(), historyMinute);
            addPowerFactor(energyData.getPowerFactor(), historyDay);
            addPowerFactor(energyData.getPowerFactor(), historyHour);
            addPowerFactor(energyData.getPowerFactor(), historyBillingCycle);
            addPowerFactor(energyData.getPowerFactor(), historyMTUDay);
            addPowerFactor(energyData.getPowerFactor(), historyMTUHour);
            addPowerFactor(energyData.getPowerFactor(), historyMTUBillingCycle);


            LOGGER.debug("Updating Demand Peak");
            //Calculate demand average
            EnergyDifference peakDemand = calcDemandPeak(location, mtu, energyPlan, energyData);
            historyMinute.setDemandPeak(roundDemand(historyMinute.getDemandPeak() + peakDemand.getNet()));
            historyMinute.setGenerationPeak(roundDemand(historyMinute.getGenerationPeak() + peakDemand.getGeneration()));
            historyMinute.setLoadPeak(roundDemand(historyMinute.getLoadPeak() + peakDemand.getLoad()));


            if (historyMinute.getMtuCount() == totalMTU) {
                //Only do demand peak if all of the MTU's have been collected.
                compareDemandPeak(historyMinute, historyHour);
                compareDemandPeak(historyMinute, historyDay);
                compareDemandPeak(historyMinute, historyBillingCycle);
                LOGGER.debug("Calculating Demand Cost");
                calcDemandCost(historyBillingCycle, energyPlan, season, touPeakType, timeZone, historyMinute);

            }

            LOGGER.debug("Updating MTU Demand Peak");
            //This is for the per mtu only.
            compareDemandPeak(timeStamp, peakDemand, historyMTUHour);
            compareDemandPeak(timeStamp, peakDemand, historyMTUDay);
            compareDemandPeak(timeStamp, peakDemand, historyMTUBillingCycle);



            long endNet = System.currentTimeMillis();
            averageNet.add(endNet - startNet);

            //Save all instances
            LOGGER.debug("Saving history instances");

            long startSaveTime = System.currentTimeMillis();
            historyBillingCycleDAO.save(historyBillingCycle);
            historyDayDAO.save(historyDay);
            historyHourDAO.save(historyHour);
            historyMinuteDAO.save(historyMinute);

            historyMTUBillingCycleDAO.save(historyMTUBillingCycle);
            historyMTUDayDAO.save(historyMTUDay);
            historyMTUHourDAO.save(historyMTUHour);
            long endSaveTime = System.currentTimeMillis();
            averageSave.add(endSaveTime - startSaveTime);


            advisorService.advisorService(location, energyPlan, historyBillingCycle, historyDay, historyMinute, totalMTU);


        } else {
            StandAlonePost standAlonePost = new StandAlonePost();
            standAlonePost.setVirtualECC(location);
            standAlonePost.setVirtualECCMTU(mtu);
            standAlonePost.setMinuteEpoch(minuteEpoch);
            standAlonePost.setDayEpoch(dayEpoch);
            standAlonePost.setHourEpoch(hourEpoch);
            standAlonePost.setMtuBillingCycleKey(mtuBillingCycleKey);
            standAlonePost.setEnergyPlan(energyPlan);
            standAlonePost.setSeason(season);
            standAlonePost.setEnergyDifference(energyDifference);
            standAlonePost.setLastDifference(energyData.getEnergyDifference());
            standAlonePost.setEnergyData(energyData);
            standAlonePost.setTimestamp(timeStamp);
            postStandAlone(standAlonePost);
        }

        if (serverService.isDevelopment() && location.getId().equals(1429l) && !mtu.getMtuType().equals(MTUType.STAND_ALONE)) {
            LOGGER.info("PROCESSING COMPLETE: {} {} {}", location.getId(), mtu.getMtuId(), System.currentTimeMillis() - s);
        }

    }


    public void postStandAlone(StandAlonePost standAlonePost) {
        VirtualECC location = standAlonePost.getVirtualECC();
        VirtualECCMTU mtu = standAlonePost.getVirtualECCMTU();
        HistoryMTUBillingCycleKey mtuBillingCycleKey = standAlonePost.getMtuBillingCycleKey();
        TimeZone timeZone = TimeZone.getTimeZone(location.getTimezone());
        EnergyPlan energyPlan = standAlonePost.getEnergyPlan();
        int season = standAlonePost.getSeason();
        EnergyDifference energyDifference = standAlonePost.getEnergyDifference();
        Double lastDifference = standAlonePost.getLastDifference();
        EnergyData energyData = standAlonePost.getEnergyData();
        long timestamp = standAlonePost.getTimestamp();

        long standAloneStartTime = System.currentTimeMillis();
        LOGGER.debug("Calculating stats on STAND_ALONE");

        //Get instances for the mtu
        long startLoadTime = System.currentTimeMillis();


        HistoryMTUBillingCycle historyMTUBillingCycle = getMTUBillingCycleInstance(mtuBillingCycleKey, energyPlan, timeZone);
        HistoryMTUDay historyMTUDay = getMTUDayInstance(location.getId(), mtu.getMtuId(), standAlonePost.getDayEpoch(), timeZone);
        HistoryMTUHour historyMTUHour = getMTUHourInstance(location.getId(), mtu.getMtuId(), standAlonePost.getHourEpoch(), timeZone);
        long endLoadTime = System.currentTimeMillis();
        averageLoad.add(endLoadTime - startLoadTime);

        //Update energy totals
        LOGGER.debug("Updating energy values");
        //Update MTU row values
        updateMTUEnergyDifference(historyMTUBillingCycle, lastDifference);
        updateMTUEnergyDifference(historyMTUDay, lastDifference);
        updateMTUEnergyDifference(historyMTUHour, lastDifference);



        long minuteEpoch = standAlonePost.getMinuteEpoch();

        LOGGER.debug("Updating voltage");
        //DO Minute (different than other types)
        double mtuVoltage = (energyData.getVoltage() * mtu.getVoltageMultiplier());
        compareVoltage(timestamp, mtuVoltage, historyMTUHour);
        compareVoltage(timestamp, mtuVoltage, historyMTUDay);
        compareVoltage(timestamp, mtuVoltage, historyMTUBillingCycle);

        LOGGER.debug("Updating Power Factor");
        addPowerFactor(energyData.getPowerFactor(), historyMTUHour);
        addPowerFactor(energyData.getPowerFactor(), historyMTUDay);
        addPowerFactor(energyData.getPowerFactor(), historyMTUBillingCycle);

        LOGGER.debug("Updating TOU Values");
        TOUPeakType touPeakType = costService.findTOU(energyPlan, season, timeZone, minuteEpoch);

        TOUDifference touDifference = calcTOUDifference(energyPlan, minuteEpoch, timeZone, touPeakType, energyDifference);
        if (touDifference != null) {
            addTOU(touPeakType, historyMTUBillingCycle, lastDifference);
            addTOU(touPeakType, historyMTUDay, lastDifference);
            addTOU(touPeakType, historyMTUHour, lastDifference);
        }

        LOGGER.debug("Updating Cost");

        int tier = 0;
        if (energyPlan.getPlanType().equals(PlanType.TIER) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            //Calculate the tier
            HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(location.getId(), minuteEpoch);
            tier = costService.findTier(energyPlan, season, historyBillingCycle.getNet());
        }


        double rate = costService.getRate(energyPlan, season, tier, touPeakType);
        double taxRate = costService.getTaxRate(energyPlan, season);
        rate = rate + (rate * (taxRate / 100.0));
        double cost = rate * (lastDifference.doubleValue() / 1000.0);
        historyMTUHour.setCost(historyMTUHour.getCost() + cost);
        historyMTUDay.setCost(historyMTUDay.getCost() + cost);
        historyMTUBillingCycle.setCost(historyMTUBillingCycle.getCost() + cost);

        LOGGER.debug("Updating Demand Peak");


        //Calculate demand average
        EnergyDifference mtuPeakDemand = calcDemandPeak(location, mtu, energyPlan, energyData);
        compareDemandPeak(timestamp, mtuPeakDemand, historyMTUHour);
        compareDemandPeak(timestamp, mtuPeakDemand, historyMTUDay);
        compareDemandPeak(timestamp, mtuPeakDemand, historyMTUBillingCycle);


        //Save all instances
        LOGGER.debug("Saving history instances");
        long startSaveTime = System.currentTimeMillis();
        historyMTUBillingCycleDAO.save(historyMTUBillingCycle);
        historyMTUDayDAO.save(historyMTUDay);
        historyMTUHourDAO.save(historyMTUHour);
        long endSaveTime = System.currentTimeMillis();
        averageSave.add(endSaveTime - startSaveTime);



        long standAloneEndTime = System.currentTimeMillis();
        averageSA.add(standAloneEndTime - standAloneStartTime);
    }

    public boolean isDemandChargeApplicable(EnergyPlan energyPlan, TimeZone timeZone, TOUPeakType touPeakType, HistoryMinute historyMinute) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(historyMinute.getStartEpoch() * 1000);

        switch (energyPlan.getDemandPlanType()) {
            case TOU: {

                //Check if applicable
                if ((!energyPlan.getDemandApplicableOffPeak() && touPeakType.equals(TOUPeakType.OFF_PEAK)) ||
                        (!energyPlan.getDemandApplicableHoliday() && HolidayUtil.isHoliday(calendar, energyPlan.getHolidayScheduleId() == 1)) ||
                        (!energyPlan.getDemandApplicableSaturday() && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ||
                        (!energyPlan.getDemandApplicableSunday() && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
                    return false;
                }


                break;
            }
            case TIERED:
            case TIERED_PEAK: {
                if ((!energyPlan.getDemandApplicableHoliday() && HolidayUtil.isHoliday(calendar, energyPlan.getHolidayScheduleId() == 1)) ||
                        (!energyPlan.getDemandApplicableSaturday() && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ||
                        (!energyPlan.getDemandApplicableSunday() && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
                    return false;
                }
                break;
            }
            case NONE: {
                return false;
            }

        }

        return true;


    }


    public double calculateTieredPeakDemandCost(double demandPeak, EnergyPlan energyPlan, int season) {
        double taxRate = costService.getTaxRate(energyPlan, season);
        double peakValue = demandPeak / 1000.0;
        double maxRate = 0.0;
        if (peakValue > 0) {
            for (DemandChargeTier demandChargeTier : energyPlan.getSeasonList().get(season).getDemandChargeTierList()) {
                if (demandChargeTier.getPeak() < 0.05) continue;
                if (peakValue >= demandChargeTier.getPeak()) {
                    maxRate = demandChargeTier.getRate();
                } else {
                    break;
                }
            }
        }
        maxRate = maxRate * peakValue;
        return costService.applyTax(maxRate, taxRate);
    }

    public double calculateTieredDemandCost(double demandPeak, EnergyPlan energyPlan, int season) {
        double peakValue = demandPeak / 1000.0;
        double amount = 0.0;
        double remainingPeak = peakValue;
        double taxRate = costService.getTaxRate(energyPlan, season);

        if (remainingPeak > 0) {
            //Go through the list backwards
            for (int t = (energyPlan.getNumberDemandSteps() - 1); t >= 0; t--) {
                DemandChargeTier demandChargeTier = energyPlan.getSeasonList().get(season).getDemandChargeTierList().get(t);
                if (peakValue <= demandChargeTier.getPeak()) continue;
                Double billablePeak = remainingPeak - demandChargeTier.getPeak();
                amount += (billablePeak * demandChargeTier.getRate());
                remainingPeak -= billablePeak;
            }
        }
        return costService.applyTax(amount, taxRate);
    }

    public double calcTOUDemandCost(double demandPeak, EnergyPlan energyPlan, int season, TOUPeakType touPeakType) {
        double taxRate = costService.getTaxRate(energyPlan, season);

        for (DemandChargeTOU demandChargeTier : energyPlan.getSeasonList().get(season).getDemandChargeTOUList()) {
            if (demandChargeTier.getPeakType().equals(touPeakType)) {
                double amount = demandChargeTier.getRate() * (demandPeak / 1000.0);
                amount = costService.applyTax(amount, taxRate);
                return amount;
            }
        }
        return 0.0;
    }

    public void calcDemandCost(HistoryBillingCycle historyBillingCycle, EnergyPlan energyPlan, int season, TOUPeakType touPeakType, TimeZone timeZone, HistoryMinute historyMinute) {
        if (isDemandChargeApplicable(energyPlan, timeZone, touPeakType, historyMinute)) {
            switch (energyPlan.getDemandPlanType()) {
                case TIERED: {
                    if (isZero(historyBillingCycle.getDemandCostPeak(), 0.0001) || historyMinute.getDemandPeak() > historyBillingCycle.getDemandCostPeak()) {
                        historyBillingCycle.setDemandCostPeak(historyMinute.getDemandPeak());
                        historyBillingCycle.setDemandCostPeakTime(historyMinute.getStartEpoch());
                        historyBillingCycle.setDemandCost(calculateTieredDemandCost(historyMinute.getDemandPeak(), energyPlan, season));
                    }
                    break;
                }
                case TIERED_PEAK: {
                    if (isZero(historyBillingCycle.getDemandCostPeak(), 0.0001) || historyMinute.getDemandPeak() > historyBillingCycle.getDemandCostPeak()) {
                        historyBillingCycle.setDemandCostPeak(historyMinute.getDemandPeak());
                        historyBillingCycle.setDemandCostPeakTime(historyMinute.getStartEpoch());
                        historyBillingCycle.setDemandCost(calculateTieredPeakDemandCost(historyMinute.getDemandPeak(), energyPlan, season));
                    }
                    break;
                }
                case TOU: {
                    //Calculate demand cost
                    double touCost = calcTOUDemandCost(historyMinute.getDemandPeak(), energyPlan, season, touPeakType);
                    if (isZero(historyBillingCycle.getDemandCost(), 0.01) || touCost > historyBillingCycle.getDemandCost()) {
                        historyBillingCycle.setDemandCostPeak(historyMinute.getDemandPeak());
                        historyBillingCycle.setDemandCostPeakTime(historyMinute.getStartEpoch());
                        historyBillingCycle.setDemandCost(touCost);
                        historyBillingCycle.setDemandCostPeakTOU(touPeakType);
                        historyBillingCycle.setDemandCostPeakTOUName(getTOUPeakName(energyPlan, touPeakType));
                    }
                    break;
                }
            }
        }
    }

    private long UNIX_TIMESTAMP() {
        return System.currentTimeMillis() / 1000L;
    }

    Cache<Long, Cache<Long, Cache>> demandChargeAccountCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30,TimeUnit.DAYS).maximumSize(5000).removalListener(new CacheRemovalListener()).initialCapacity(5000).build();
    Cache<Long, Cache<Long, EnergyDataLastCur>> lastPostCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30,TimeUnit.DAYS).maximumSize(5000).removalListener(new CacheRemovalListener()).initialCapacity(5000).build();



    private Cache<Long, EnergyData> getDataCache(long accountId, long mtuId){
        Cache<Long, Cache> mtuCache = demandChargeAccountCache.getIfPresent(accountId);
        if (mtuCache == null){
            mtuCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30,TimeUnit.DAYS).maximumSize(5000).initialCapacity(500).build();
            demandChargeAccountCache.put(accountId, mtuCache);
        }

        Cache<Long, EnergyData> dataCache = mtuCache.getIfPresent(mtuId);
        if (dataCache == null){
            dataCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(35,TimeUnit.MINUTES).maximumSize(1000).initialCapacity(100).build();
            mtuCache.put(mtuId, dataCache);
        }

        return dataCache;
    }

    private  Cache<Long, EnergyDataLastCur> getLastPostCache(long accountId){
        Cache<Long, EnergyDataLastCur> mtuCache = lastPostCache.getIfPresent(accountId);
        if (mtuCache == null){
            mtuCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30,TimeUnit.MINUTES).maximumSize(2000).initialCapacity(500).build();
            lastPostCache.put(accountId, mtuCache);
        }
        return mtuCache;
    }


    private  EnergyData findFromCache(long accountId, long mtuId, long timeStamp){
        Cache<Long, EnergyData> dataCache = getDataCache(accountId, mtuId);
        EnergyData energyData = dataCache.getIfPresent(timeStamp);
        if (energyData == null){
            energyData = energyDataDAO.findById(timeStamp, accountId, mtuId);
            if (energyData != null){
                dataCache.put(timeStamp, energyData);;
            }
        }
        return energyData;
    }

    private  void addToCache(EnergyData energyData){
        Cache<Long, EnergyData> dataCache = getDataCache(energyData.getAccountId(), energyData.getMtuId());
        dataCache.put(energyData.getTimeStamp(), energyData);;
    }


    private Double calcDemandCharge(long newTimestamp, long newMtuId, long newAccountId, Double newEnergy, int sampleSizeMin){
        long sampleTime = newTimestamp - (60L * sampleSizeMin);
        EnergyData lastCur = findFromCache(newAccountId, newMtuId, sampleTime);
        if (lastCur == null) return 0.0;
        return (newEnergy - lastCur.getEnergy())/(double)sampleSizeMin;
    }



    private  void updateLastPost(long accountId, long mtuId, EnergyDataLastCur energyDataLastCur){
        getLastPostCache(accountId).put(mtuId, energyDataLastCur);
    }


    class DataExistsException extends Exception{

    }

    private EnergyDataLastCur findLast(long accountId, long mtuId, long timestamp) throws DataExistsException {
//        return energyDataDAO.findLast(accountId, mtuId, timestamp);
        EnergyDataLastCur energyDataLastCur = getLastPostCache(accountId).getIfPresent(mtuId);
        if (energyDataLastCur == null){
            energyDataLastCur = energyDataDAO.findLast(accountId, mtuId);
            if (energyDataLastCur != null && energyDataLastCur.getTimeStamp().longValue() >= timestamp){
                LOGGER.warn("[findLast] Throwing out record as there is already data past this post. Current:{} Last:{}", timestamp, energyDataLastCur.getTimeStamp());
                throw new DataExistsException();
            }
            if (energyDataLastCur != null) updateLastPost(accountId, mtuId, energyDataLastCur);
        }

      //  LOGGER.info("[findLast]  cache:{} elc:{}", cache, energyDataLastCur);

        return energyDataLastCur;
    }

    public boolean insertBatch(long newTimestamp, long newMtuId, long newAccountId, double newEnergy, double newPF, double newVoltage, long doValidation, boolean doNoNegative) {


        //Adjust Validation
        if ((newMtuId < 1245184L || newMtuId >= 1310720L) && doValidation == 0L) {
            doValidation = 77000L;
        }
        if ((newMtuId >= 1245184L || newMtuId < 1310720L) && doValidation == 0L) {
            doValidation = 5000000L;
        }



        if (newTimestamp < (UNIX_TIMESTAMP() + 900L) && newTimestamp > (UNIX_TIMESTAMP() - 2678400L)) {
            EnergyDataLastCur lastCur;


            try {
                lastCur=findLast(newAccountId, newMtuId, newTimestamp);
            } catch (DataExistsException e) {
                LOGGER.error("Data Already Exists: a:{} m:{}, t:{}", newAccountId, newMtuId, newTimestamp);
                return false;
            }

            boolean done = (lastCur == null);



            if (done) {
                EnergyData energyData = new EnergyData();
                energyData.setTimeStamp(newTimestamp);
                energyData.setMtuId(newMtuId);
                energyData.setAccountId(newAccountId);
                energyData.setEnergy(newEnergy);
                energyData.setPowerFactor(newPF);
                energyData.setVoltage(newVoltage);
                energyData.setEnergyDifference(0.0);
                energyData.setAvg5(0.0);
                energyData.setAvg10(0.0);
                energyData.setAvg15(0.0);
                energyData.setAvg20(0.0);
                energyData.setAvg30(0.0);
                LOGGER.trace("[insertBatch] ts:{} m:{}, a:{} INSERT SINGLE RECORD", newTimestamp, newMtuId, newAccountId);
                addToCache(energyData);
                energyDataDAO.insert(energyData);
                updateLastPost(newAccountId, newMtuId, new EnergyDataLastCur(energyData));

            } else if (newTimestamp > lastCur.getTimeStamp()) { //ignore data if its already been posted.Avoid the dupe error.
                long timeDiff = newTimestamp - lastCur.getTimeStamp();
                long numMin = timeDiff / 60L;
                double totalEnergyDiff = newEnergy - lastCur.getEnergy();
                double energyDiff = totalEnergyDiff / (double) numMin;


                //Negative Number Filter
                if (energyDiff < 0 && (newAccountId == 164L || doNoNegative)) {
                    LOGGER.warn("[insertBatch] Failed Validation (negative): mtu:{} account:{} ts: {}", newMtuId, newAccountId, new Date(newTimestamp * 1000));
                    return false;
                }

                //Validation Range Check
                if (doValidation > 0L && Math.abs(energyDiff) > (doValidation / 60L)) {
                    LOGGER.warn("[insertBatch] Failed Validation ({}): mtu:{} account:{} ts: {}", doValidation, newMtuId, newAccountId, new Date(newTimestamp * 1000));
                    return false;
                }

                double runningEnergydiff = 0;
                double smoothEnergy = lastCur.getEnergy();
                long smoothTimeStamp = lastCur.getTimeStamp() - (lastCur.getTimeStamp() % 60L); //Convert to even minute

                List<EnergyData> batchList = new ArrayList<>();

                while (numMin > 0) {
                    boolean smoothing = (numMin > 1);
                    smoothEnergy += energyDiff;
                    smoothTimeStamp += 60L;

                    if (numMin == 1L) {
                        //Even out the last one to eliminate rounding error. The larger the timespan, the larger
                        //this amount will be.
                        energyDiff = totalEnergyDiff - runningEnergydiff;
                    }


                    runningEnergydiff = runningEnergydiff + energyDiff;
                    if ((smoothTimeStamp < (UNIX_TIMESTAMP() + 900L)) && (smoothTimeStamp > (UNIX_TIMESTAMP() - 2678400L))) {
                        EnergyData energyData = new EnergyData();
                        energyData.setMtuId(newMtuId);
                        energyData.setAccountId(newAccountId);;
                        energyData.setTimeStamp(smoothTimeStamp);
                        energyData.setEnergy(smoothEnergy);
                        energyData.setEnergyDifference(energyDiff);
                        energyData.setPowerFactor(newPF);
                        energyData.setVoltage(newVoltage);
                        energyData.setSmoothing(smoothing);
                        energyData.setAvg5(calcDemandCharge(smoothTimeStamp, newMtuId, newAccountId, smoothEnergy, 5));
                        energyData.setAvg10(calcDemandCharge(smoothTimeStamp, newMtuId, newAccountId, smoothEnergy, 10));
                        energyData.setAvg15(calcDemandCharge(smoothTimeStamp, newMtuId, newAccountId, smoothEnergy, 15));
                        energyData.setAvg20(calcDemandCharge(smoothTimeStamp, newMtuId, newAccountId, smoothEnergy, 20));
                        energyData.setAvg30(calcDemandCharge(smoothTimeStamp, newMtuId, newAccountId, smoothEnergy, 30));
                        addToCache(energyData);
                        batchList.add(energyData);

                    }
                    numMin--;
                }

                if (batchList.size() > 0){
                    LOGGER.trace("[insertBatch] ts:{} m:{}, a:{} INSERT BATCH RECORD", newTimestamp, newMtuId, newAccountId);

                    if (!energyDataDAO.insert(batchList)) {
                        LOGGER.warn("MTU MISMATCH. Updating last post to a:{} m:{} t:{}", newAccountId, newMtuId, batchList.get(0).getTimeStamp());
                        mtuDAO.updateLastPost(newMtuId, newAccountId, batchList.get(0).getTimeStamp());
                        updateLastPost(newAccountId, newMtuId, new EnergyDataLastCur(batchList.get(0)));
                        return false;
                    } else {
                        updateLastPost(newAccountId, newMtuId, new EnergyDataLastCur(batchList.get(batchList.size()-1)));
                    }
                    LOGGER.trace("[insertBatch] ts:{} m:{}, a:{} INSERT BATCH RECORD COMPLETE", newTimestamp, newMtuId, newAccountId);
                }
            } else {
                LOGGER.debug("[insertBatch] Energy Row Already Exists: mtu:{} account:{} ts: {}", newMtuId, newAccountId, new Date(newTimestamp * 1000));
                return false;
            }
        } else {
            LOGGER.warn("[insertBatch]Timestamp Validation Failed: mtu:{} account:{} ts: {}", newMtuId, newAccountId, new Date(newTimestamp * 1000));
            return false;
        }

        return true;


    }


    public void processMTUPost(EnergyPost energyPost, EnergyMTUPost mtuPost, boolean isSpyder) {
        long startTime = System.currentTimeMillis();
        List<VirtualECC> virtualECCList = virtualECCDAO.findByMTU(energyPost.getAccount().getId(), mtuPost.getMTUId());

        try {

            LOGGER.debug(">>> MTU Processing {} spyder:{}", mtuPost, isSpyder);
            //Check to see if the MTU needs to be added
            MTU mtu = mtuDAO.findById(mtuPost.getMTUId(), energyPost.getAccount().getId());

            if (mtu == null) {
                LOGGER.info("MTU Not found. Adding {}", mtuPost.getMtuSerial());
                mtu = new MTU();
                mtu.setId(mtuPost.getMTUId());
                mtu.setAccountId(energyPost.getAccount().getId());
                mtu.setMtuType(MTUType.findByType(mtuPost.is5k(), mtuPost.getMtuTypeOrdinal()));
                mtu.setSpyder(isSpyder);

                if ((mtu.getValidation().equals(0l)) && (mtuPost.is5k() || (mtu.getHexId().startsWith("16") || mtu.getHexId().startsWith("17") || mtu.getHexId().startsWith("18")))) {
                    //LOGGER.warn("Forcing 5k/6k validation for {}", mtu);
                    mtu.setValidation(77000l);
                }

                if (isSpyder) {
                    mtu.setMtuType(MTUType.STAND_ALONE);
                    //LOGGER.warn("Forcing SPYDER validation for {}", mtu);
                    mtu.setValidation(77000l);
                }
                mtu = mtuDAO.update(mtu);
            }

            if (virtualECCList == null || virtualECCList.size() == 0){
                //LOGGER.debug("[processMTUPost} The MTU is not in a location: {}", mtu);
                return;
            }
            //Update last post
            LOGGER.debug("updating records for mtu {}", mtu);
            for (EnergyCumulativePost ccr : mtuPost.getCumulativePostList()) {

                if (energyPost.getAccount().getId().equals(296l)) {
                    LOGGER.info("Processing cumulative post for natoli: {}", ccr);
                }

                //Make sure the ccr is foored to the minute.
                ccr.setTimestamp(ccr.getTimestamp() - (ccr.getTimestamp() % 60));

                //Validate timestamp.
                long ts = System.currentTimeMillis() / 1000;
                ts -= ccr.getTimestamp();
                if (!serverService.isDevelopment()) {
                    if (ts > 57600 || ts < -57600) {
                        LOGGER.warn("Invalid timestamp received: ECC:{} TS:{}. Data is being skipped.", energyPost.getGateway(), ccr.getTimestamp());
                        continue;
                    }
                }
                if (ccr.getPowerFactor() == null) ccr.setPowerFactor(.98);
                if (ccr.getVoltage() == null) ccr.setVoltage(120.0);

                if (insertBatch(ccr.getTimestamp(), mtu.getId(), mtu.getAccountId(), ccr.getWatts(), ccr.getPowerFactor(), ccr.getVoltage(), mtu.getValidation(), mtu.isNoNegative())) {
                    LOGGER.debug("ENERGY DAO UPDATE: PASS");
                    processMTUEnergyPost(virtualECCList, mtu, ccr);
                } else {
                    LOGGER.debug("ENERGY DAO UPDATE: FAIL");
                }
                mtuDAO.updateLastPost(mtu.getId(), mtu.getAccountId(), ccr.getTimestamp());
                mtu.setLastPost(ccr.getTimestamp());
            }
            LOGGER.debug("Completed Processing {}", mtuPost);
        } catch (Exception ex) {
            LOGGER.error("ERROR PROCESSING MTU POST: {} {} {}", new Object[]{energyPost, mtuPost, isSpyder}, ex);
        }
        averageMTUPost.add(System.currentTimeMillis() - startTime);
    }

    private void processVirtualMTU(VirtualECC virtualECC, long virtualECCMTUId, int totalMTU, EnergyCumulativePost energyCumulativePost) {
        long s = System.currentTimeMillis();
        VirtualECCMTU virtualECCMTU = virtualECCMTUDAO.findByMTUId(virtualECC.getId(), virtualECCMTUId, virtualECC.getAccountId());

        if (virtualECCMTU.getLastPost() < energyCumulativePost.getTimestamp()) {
            if (processEnergyPost(virtualECC, totalMTU, virtualECCMTU, energyCumulativePost.getTimestamp(), false)) {
                virtualECCMTU.setLastPost(energyCumulativePost.getTimestamp());
                virtualECCMTUDAO.updateLastPost(virtualECCMTU, energyCumulativePost.getTimestamp());
            } else {
                LOGGER.warn("Missing Energy Post: mtu:{} ts:{}", virtualECCMTU.getMtuId(), energyCumulativePost.getTimestamp());
            }
        } else {
            LOGGER.trace("Skipping Already Processed: ecc:{} mtu:{} p:{}", virtualECC.getId(), virtualECCMTUId, energyCumulativePost);
        }
        averageHistoryPost.add(System.currentTimeMillis() - s);
    }

    public void processMTUEnergyPost(List<VirtualECC> virtualECCList, MTU mtu, EnergyCumulativePost cumulativePost) {
        //Run the standard energy post for all locations with this mtu.

        for (VirtualECC virtualECC : virtualECCList) {
            List<VirtualECCMTU> virtualECCMTUList = virtualECCMTUDAO.findByVirtualECC(virtualECC.getId(), virtualECC.getAccountId());
            int totalMTU = 0;
            for (VirtualECCMTU vm : virtualECCMTUList) {
                if (!vm.getMtuType().equals(MTUType.STAND_ALONE)) totalMTU++;
            }

            if (!mtu.getLastPost().equals(0l)) {
                if ((cumulativePost.getTimestamp() - mtu.getLastPost()) > 60) {
                    if (virtualECC.getId().equals(1429l) && !mtu.getMtuType().equals(MTUType.STAND_ALONE)) {
                        LOGGER.info("Smooth condition encountered: ts:{} lp:{} diff:{}", cumulativePost.getTimestamp(), mtu.getLastPost(), cumulativePost.getTimestamp() - mtu.getLastPost());
                    }
                    long epochTime = mtu.getLastPost() + 60;
                    while (epochTime < cumulativePost.getTimestamp()) {

                        if (virtualECC.getId().equals(1429l) && !mtu.getMtuType().equals(MTUType.STAND_ALONE)) {
                            LOGGER.info("Playing back smoothed values:{} epochTime:{} ccr:{}", mtu.getId(), epochTime, cumulativePost.getTimestamp());
                        }


                        EnergyData energyData = findFromCache(virtualECC.getAccountId(), mtu.getId(),epochTime);
                        if (energyData != null) {
                            EnergyCumulativePost energyCumulativePost = new EnergyCumulativePost(epochTime, energyData.getEnergy(), energyData.getPowerFactor(), energyData.getVoltage());
                            processVirtualMTU(virtualECC, mtu.getId(), totalMTU, energyCumulativePost);
                        }
                        epochTime += 60;
                    }
                }
            }
            //Playback the posted one.
            processVirtualMTU(virtualECC, mtu.getId(), totalMTU, cumulativePost);
        }
    }

    /**
     * Checks to see if we are already posting for this gateway. If so, we add this to the queue.
     *
     * @param energyPost
     * @return
     */
    public boolean checkPostQueue(EnergyPost energyPost) {
        if (energyPostQueue.isProcessing(energyPost.getGateway())) {
            LOGGER.warn("Post already processing. Queueing up. {} {}", energyPost.getGateway(), energyPostQueue.size(energyPost.getGateway()));
            energyPostQueue.addPost(energyPost);
            return true;
        }
        return false;
    }

    public void processEnergyPost(EnergyPost newPost) {


        //Check to see if we are already posting. if so, add this to the exsiting queue.
        if (checkPostQueue(newPost)) return;

        EnergyPost energyPost = newPost;
        energyPostQueue.addPost(energyPost);

        while (true) {
            try {
                long startTime = System.currentTimeMillis();

                for (EnergyMTUPost mtuPost : energyPost.getMtuList()) {
                    processMTUPost(energyPost, mtuPost, false);
                    Thread.sleep(10);
                }
                for (EnergyMTUPost mtuPost : energyPost.getSpyderList()) {
                    processMTUPost(energyPost, mtuPost, true);
                    Thread.sleep(10);
                }
                averageEnergyPost.add(System.currentTimeMillis() - startTime);

                //Metrics
                postsProcessed++;

                if (energyPost.getAccount().getId().equals(52L)) {
                    LOGGER.info("Processing Energy Post Time: {} a:{} t:{} mtu:{} spy:{}", energyPost.getGateway(), energyPost.getAccount().getId(), System.currentTimeMillis() - startTime, energyPost.getMtuList().size(), energyPost.getSpyderList().size());
                    LOGGER.info("averageEnergyPost: {}", averageEnergyPost);
                    LOGGER.info("averageMTUPost: {}", averageMTUPost);
                    LOGGER.info("averageHistoryPost: {}", averageHistoryPost);
                    LOGGER.info("averageHead: {}", averageHead);
                    LOGGER.info("averageLoad: {}", averageLoad);
                    LOGGER.info("averageNet: {}", averageNet);
                    LOGGER.info("averageSA: {}", averageSA);
                    LOGGER.info("averageSave: {}", averageSave);
                    LOGGER.info("total posts processed: {}", postsProcessed);
                }

            } catch (Exception ex) {
                LOGGER.error("Error processing energy post:{}", energyPost, ex);
            }
            energyPost = energyPostQueue.popPost(energyPost.getGateway());
            if (energyPost == null) {
                break;
            }
            LOGGER.info("Processing backlogged post: {} left:{}", energyPost.getGateway(), energyPostQueue.size(energyPost.getGateway()));

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }

        }
    }

    @Scheduled(fixedRate = 60000)
    public void forcePostCheck(){
        LOGGER.info("FORCEPOSTCHECK");
        mtuDAO.processLastUpdateBatch();
        virtualECCMTUDAO.processLastUpdateBatch();
        historyMinuteDAO.processBatch();
        historyHourDAO.processBatch();
        historyDayDAO.processBatch();
        historyBillingCycleDAO.processBatch();
        historyMTUHourDAO.processBatch();
        historyMTUDayDAO.processBatch();
        historyMTUBillingCycleDAO.processBatch();
    }



    class Stat {
        long sum = 0;
        long count = 0;
        long avg = 0;

        public void add(long val) {
            sum = sum + val;
            count++;

            if (count >= 1000000) {
                count = 0;
                sum = 0;
                avg = 0;
            } else {
                if (count != 0) {
                    avg = sum / count;
                }
            }
        }

        @Override
        public String toString() {
            return "avg: " + avg;
        }
    }



}
