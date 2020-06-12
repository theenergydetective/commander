/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.NonPostingMTU;
import com.ted.commander.server.util.CalendarUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Implementation of TED Advisor Rules
 */
@Service
public class AdvisorService {

    static Logger LOGGER = LoggerFactory.getLogger(AdvisorService.class);

    @Autowired
    EmailService emailService;

    @Autowired
    PushService pushService;

    @Autowired
    CostService costService;

    @Autowired
    ClockService clockService;

    @Autowired
    AdviceService adviceService;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    HistoryHourDAO historyHourDAO;

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    LastPostDAO lastPostDAO;

    @Autowired
    VirtualECCService virtualECCService;

    @Autowired
    AdviceDAO adviceDAO;

    @Autowired
    AdviceTriggerDAO adviceTriggerDAO;

    @Autowired
    MTUDAO mtudao;

    @Autowired
    ServerService serverService;


    public void logEvent(String eventName){
        GoogleAnalytics ga = new GoogleAnalytics("<KEY>");
        ga.postAsync(new EventHit("Advisor", eventName));
    }

    public void advisorService(VirtualECC location, EnergyPlan energyPlan,  HistoryBillingCycle historyBillingCycle, HistoryDay historyDay, HistoryMinute historyMinute, int totalMTU) {
        if (historyMinute.getMtuCount() == totalMTU) {
            LOGGER.debug("Processing advisor settings for location {}", location.getId());
            List<Advice> adviceList = adviceService.findForLocation(location.getId());
            for (Advice advice : adviceList) {
                if (advice.getTriggerList() == null || advice.getTriggerList().size()==0) continue;
                if (location.getAccountId().equals(52l)){
                    LOGGER.error(">>>>>>Processing advisor settings for {} {}", location.getId(), advice);
                }


                for (AdviceTrigger adviceTrigger : advice.getTriggerList()) {

                    switch (adviceTrigger.getTriggerType()) {
                        case NEW_TOU_RATE: {
                            checkTOURate(advice, adviceTrigger, location, energyPlan);
                            break;
                        }
                        case RATE_CHANGE: {
                            checkRateChange(advice, adviceTrigger, location, energyPlan, historyMinute, totalMTU);
                            break;
                        }
                        case NEW_DEMAND_CHARGE: {
                            checkDemandCharge(advice, adviceTrigger, location, energyPlan, historyBillingCycle);
                            break;
                        }
                        case MONEY_SPENT: {
                            checkMoneySpent(advice, adviceTrigger, location, energyPlan, historyBillingCycle, historyDay);
                            break;
                        }
                        case ENERGY_CONSUMED: {
                            checkEnergyConsumed(advice, adviceTrigger, location, energyPlan, historyBillingCycle, historyDay);
                            break;
                        }
                        case TEN_MINUTE_DEMAND:
                            checkDemandExceeds(advice, adviceTrigger, location, historyBillingCycle, historyMinute, totalMTU);
                            break;
                        case AVG_GENERATED:
                            checkGeneratedAverage(advice, adviceTrigger, location, historyBillingCycle, historyMinute, totalMTU);
                            break;
                        case AVG_CONSUMED:
                            checkConsumedAverage(advice, adviceTrigger, location, historyBillingCycle, historyMinute, totalMTU);
                            break;
                        case VOLTAGE_EXCEEDS:
                            checkVoltageExceeds(advice, adviceTrigger, location, historyBillingCycle, historyMinute, totalMTU);
                            break;
                        case VOLTAGE_DOES_NOT_EXCEED:
                            checkVoltageDoesNotExceeds(advice, adviceTrigger, location, historyBillingCycle, historyMinute, totalMTU);
                            break;
                        case REAL_TIME_POWER:
                            checkRealTimePowerExceeds(advice, adviceTrigger, location, historyBillingCycle, historyMinute, totalMTU);
                            break;
                    }

                }

            }
        }
    }



    //Check every  hour
    @Scheduled(cron = "00 * * * * * ")
    public void checkNoPost() {
        if (serverService.getUptimeInSeconds() < 3600) return; //Delay on startup/

        LOGGER.info("*** Checking Advisor No Post **");
        List<Advice> adviceList = adviceService.findCommanderAdvice();

        //Fix for concurrent access exception
        List<Advice> clonedList = new ArrayList<>();
        for (Advice advice: adviceList){
            clonedList.add((Advice) SerializationUtils.clone(advice));
        }
        adviceList = clonedList;

        for (Advice advice: adviceList){
//            if (!advice.getAccountId().equals(52L)) continue;;

            List<AdviceTrigger> adviceTriggerList = advice.getTriggerList();
            for (AdviceTrigger adviceTrigger: adviceTriggerList){
                try {
                    checkLastPost(advice, adviceTrigger);
                } catch (Exception ex) {
                    LOGGER.error("Error cloning advice: {} {}", advice, adviceTrigger, ex);
                }
            }
        }
    }



    public boolean checkTriggerTime(AdviceTrigger adviceTrigger, TimeZone timeZone){
        if (adviceTrigger.getStartTime() == adviceTrigger.getEndTime()) return true;

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(clockService.getCurrentTimeInMillis());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        CalendarUtils.zeroTime(calendar);

        calendar.set(Calendar.MINUTE, adviceTrigger.getStartTime() % 60);
        calendar.set(Calendar.HOUR_OF_DAY, adviceTrigger.getStartTime() / 60);

        Long convertedStartTime = calendar.getTimeInMillis();

        calendar.set(Calendar.MINUTE, adviceTrigger.getEndTime() % 60);
        calendar.set(Calendar.HOUR_OF_DAY, adviceTrigger.getEndTime() / 60);
        Long convertedEndTime = calendar.getTimeInMillis();

        Long currentTime = clockService.getCurrentTimeInMillis();
        return (currentTime.longValue() >= convertedStartTime.longValue() &&
                currentTime.longValue() < convertedEndTime.longValue());

    }

    public void checkAdviceState(Advice advice){
        if (advice.getState().equals(AdviceState.ALARM)){
            if (AdviceState.NORMAL.equals(adviceTriggerDAO.findAdviceState(advice.getId()))){
                advice.setState(AdviceState.NORMAL);
                adviceDAO.save(advice);
            }
        }
    }


    public void checkTOURate(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan){
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL) && checkTriggerTime(adviceTrigger, timeZone)) {

            int season = costService.findSeason(energyPlan, timeZone, currentEpoch);
            TOUPeakType touPeakType = costService.findTOU(energyPlan, season, timeZone, currentEpoch);

            currentEpoch += (60 * adviceTrigger.getMinutesBefore());

            TOUPeakType newTOUPeak = costService.findTOU(energyPlan, season, timeZone, currentEpoch);

            if (!touPeakType.equals(newTOUPeak)) {
                LOGGER.debug("Current Peak Does not Equal New Peak: {} {}", touPeakType, newTOUPeak);
                if ((newTOUPeak.equals(TOUPeakType.OFF_PEAK) && adviceTrigger.isOffPeakApplicable()) ||
                        (newTOUPeak.equals(TOUPeakType.PEAK) && adviceTrigger.isPeakApplicable()) ||
                        (newTOUPeak.equals(TOUPeakType.MID_PEAK) && adviceTrigger.isMidPeakApplicable()) ||
                        (newTOUPeak.equals(TOUPeakType.SUPER_PEAK) && adviceTrigger.isSuperPeakApplicable())) {


                    adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
                    advice.setState(AdviceState.ALARM);
                    adviceDAO.save(advice);
                    adviceTrigger.setLastSent(currentEpoch);
                    adviceTriggerDAO.save(adviceTrigger);

                    //Get the energy TOU Name

                    String touName = costService.getTouName(energyPlan, newTOUPeak);

                    //Check last sent
                    logEvent("sendTOURateNotification");
                    for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                        if (adviceRecipient.isSendEmail()) {
                            emailService.sendTOURateNotification(advice, adviceRecipient, adviceTrigger, virtualECC, touName);
                        }
                        if (adviceRecipient.isSendPush()) {
                            pushService.sendTOURateNotification(advice, adviceRecipient, adviceTrigger, virtualECC, touName);
                        }
                    }
                }
            }
        } else if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
            //See if we can return the trigger to normal
            long resetTime = adviceTrigger.getLastSent();
            resetTime += (60 * adviceTrigger.getMinutesBefore());

            if (resetTime <= currentEpoch){
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                adviceTriggerDAO.save(adviceTrigger);
                checkAdviceState(advice);
            }
        }
    }

    public static boolean doubleEquals(double a, double b, double epsilon){
        return a == b ? true : Math.abs(a - b) < epsilon;
    }

    public void checkRateChange(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, HistoryMinute historyMinute, int totalNumberMTU){
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        if (totalNumberMTU != historyMinute.getMtuCount()){
            return;
        }

        if (adviceTrigger.getLastSent() == 0){
            LOGGER.debug("Rate not set. Using current rate as the last rate: {} epoch:{}", historyMinute.getRateInEffect(), currentEpoch);
            adviceTrigger.setAmount(historyMinute.getRateInEffect());
            adviceTrigger.setLastSent(currentEpoch);
            adviceTriggerDAO.save(adviceTrigger);
        } else {
            double oldRate = adviceTrigger.getAmount();
            boolean rateChange = false;
            if (!doubleEquals(historyMinute.getRateInEffect(), adviceTrigger.getAmount(), 0.00001)) {
                adviceTrigger.setAmount(historyMinute.getRateInEffect());
                adviceTrigger.setLastSent(currentEpoch);
                adviceTriggerDAO.save(adviceTrigger);
                rateChange = true;
            }

            if (rateChange && checkTriggerTime(adviceTrigger, timeZone)) {
                //Check last sent
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendRateChangeNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, oldRate, adviceTrigger.getAmount());
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendRateChangeNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, oldRate, adviceTrigger.getAmount());
                    }
                }
            }
        }
    }

    public boolean canReturnToNormal(AdviceTrigger adviceTrigger, long startEpoch, TimeZone timeZone){


        long currentEpoch = clockService.getEpochTime();
        long diff = currentEpoch - adviceTrigger.getLastSent();


        switch(adviceTrigger.getSendAtMost()){
            case MINUTE:
                return diff >= 60;
            case HOURLY:
                return diff >= 3600;
            case DAILY: {
                //TED-288 Workaround.
                Calendar currentTime = Calendar.getInstance(timeZone);
                currentTime.setTimeInMillis(currentEpoch * 1000);
                boolean isMidnight = (currentTime.get(Calendar.HOUR_OF_DAY) == 0 && currentTime.get(Calendar.MINUTE) == 0);
                if (isMidnight) return false;


                if (diff >= 86400) {
                    return true;
                }
                else {
                    //Check to make sure we are on a different day
                    Calendar calendar = Calendar.getInstance(timeZone);
                    calendar.setTimeInMillis(adviceTrigger.getLastSent() * 1000);
                    int startEpochDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                    return (currentTime.get(Calendar.DAY_OF_YEAR) != startEpochDayOfYear);
                }
            }
            default: //Billing Cycle
                return adviceTrigger.getLastSent() < startEpoch;
        }
    }


    public void checkDemandCharge(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, HistoryBillingCycle historyBillingCycle){
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        //Check Billing Cycle Delay
        if ((currentEpoch - historyBillingCycle.getStartEpoch()) < (adviceTrigger.getDelayMinutes() * 60l)) {
            LOGGER.debug("Billing Cycle Delay");
            if (adviceTrigger.getLastSent() != 0) {
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                adviceTrigger.setAmount(0.0);
                adviceTrigger.setLastSent(0l);
                adviceTriggerDAO.save(adviceTrigger);
                checkAdviceState(advice);
            }
            return;
        }

        //Check if this is the first value we can check
        if (adviceTrigger.getLastSent() == 0) {
            LOGGER.debug("Rate not set. Using current rate as the last rate: {} epoch:{}", historyBillingCycle.getDemandCost(), currentEpoch);
            adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
            adviceTrigger.setAmount(historyBillingCycle.getDemandCost());
            adviceTrigger.setLastSent(currentEpoch);
            adviceTriggerDAO.save(adviceTrigger);
            checkAdviceState(advice);
            return;
        }


        //Check if a cost change has occured.
        boolean costChange = !doubleEquals(historyBillingCycle.getDemandCost(), adviceTrigger.getAmount(), 0.00001);
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);


        if (isTriggerActive){
            if (costChange && adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL)){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
                adviceTrigger.setLastSent(currentEpoch);
                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);

                logEvent("sendDemandCostChangeNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendDemandCostChangeNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, adviceTrigger.getAmount(), historyBillingCycle.getDemandCost());
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendDemandCostChangeNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, adviceTrigger.getAmount(), historyBillingCycle.getDemandCost());
                    }
                }
            } else if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                //Check if we can return to normal.
                if (canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone)){
                    LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                    costChange = true; //Force the save
                    adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                }
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                costChange = true; //Forces the save on a return to normal.
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
            }
        }
        if (costChange){
            adviceTrigger.setAmount(historyBillingCycle.getDemandCost());
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                checkAdviceState(advice);
            }
        }
    }



    public void checkMoneySpent(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, HistoryBillingCycle historyBillingCycle, HistoryDay historyDay){

        double moneySpent = adviceTrigger.getSinceStart().equals(HistoryType.DAILY)?historyDay.getNetCost():historyBillingCycle.getNetCost();
        long startEpoch = adviceTrigger.getSinceStart().equals(HistoryType.DAILY)?historyDay.getStartEpoch():historyBillingCycle.getStartEpoch();

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();




        //Check if a cost change has occured.
        boolean exceeds = moneySpent >= adviceTrigger.getAmount();
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);

        if (isTriggerActive){

            //TED-288: Make sure its the same day
            if (adviceTrigger.getSinceStart().equals(HistoryType.DAILY) && !isSameDay(timeZone, historyDay.getStartEpoch(), currentEpoch)){
                return;
            }


            if (exceeds && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);

                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendMoneySpentNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendMoneySpentNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, adviceTrigger.getAmount(), moneySpent);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendMoneySpentNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, adviceTrigger.getAmount(), moneySpent);
                    }
                }
            } else if (expired && !exceeds) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }


    //TED-288 Fix
    private boolean isSameDay(TimeZone timeZone, long historyEpoch, long systemEpoch){
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(historyEpoch * 1000);
        int historyDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(systemEpoch * 1000);
        int systemDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        return historyDayOfYear == systemDayOfYear;
    }


    public void checkEnergyConsumed(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, HistoryBillingCycle historyBillingCycle, HistoryDay historyDay){

        double energyConsumed = adviceTrigger.getSinceStart().equals(HistoryType.DAILY)?historyDay.getNet():historyBillingCycle.getNet();
        energyConsumed /= 1000.0; //Convert to KW

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());


        long currentEpoch = clockService.getEpochTime();



        //Check if a cost change has occured.
        boolean exceeds = energyConsumed >= adviceTrigger.getAmount();
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);


        if (isTriggerActive){
            //TED-288: Make sure its the same day when comparing records for daily (prevent the midnight error)
            if (adviceTrigger.getSinceStart().equals(HistoryType.DAILY) && !isSameDay(timeZone, historyDay.getStartEpoch(), currentEpoch)){
                return;
            }


            if (exceeds && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);

                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendEnergyConsumedNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendEnergyConsumedNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, adviceTrigger.getAmount(), energyConsumed);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendEnergyConsumedNotification(advice, adviceRecipient, adviceTrigger, virtualECC, energyPlan, adviceTrigger.getAmount(), energyConsumed);
                    }
                }
            } else if (expired && !exceeds) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }



    public void checkDemandExceeds(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, HistoryBillingCycle historyBillingCycle, HistoryMinute historyMinute, int totalNumberMTU){

        if (historyMinute.getMtuCount() != totalNumberMTU){
            LOGGER.debug("Not all MTU information has been collected. Found {} expected {}", totalNumberMTU, historyMinute.getMtuCount());
            return;
        }

        long startEpoch = historyMinute.getStartEpoch();



        HistoryMinute tenMinuteHistory = historyMinuteDAO.findOne(virtualECC.getId(), startEpoch - 600);

        if (tenMinuteHistory.getMtuCount() != totalNumberMTU){
            LOGGER.debug("Not all MTU information has been collected for tenMinuteHistory. Found {} expected {}", totalNumberMTU, historyMinute.getMtuCount());
            return;
        }


        double demandAverage = historyMinute.getRunningNetTotal() -  tenMinuteHistory.getRunningNetTotal();
        demandAverage /= 10.0;
        demandAverage *= 60.0;
        demandAverage /= 1000.0; //Convert to kWH

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        //Check if a cost change has occured.
        boolean exceeds = demandAverage >= adviceTrigger.getAmount();
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);



        if (isTriggerActive){
            if (exceeds && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);

                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendKWAverageNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendKWAverageNotification(advice, adviceRecipient, adviceTrigger, virtualECC, adviceTrigger.getAmount(), demandAverage);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendKWAverageNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  adviceTrigger.getAmount(), demandAverage);
                    }
                }
            } else if (expired && !exceeds) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }




    public void checkGeneratedAverage(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, HistoryBillingCycle historyBillingCycle, HistoryMinute historyMinute, int totalMTUCount){


        long startEpoch = clockService.getEpochTime();
        startEpoch -= (60l * adviceTrigger.getDelayMinutes());


        Double DA = historyHourDAO.getAverageEnergyGenerated(virtualECC.getId(), startEpoch, totalMTUCount);
        if (DA == null) {
            LOGGER.error("No Data found");
            return;
        }
        double demandAverage = DA.doubleValue();
        demandAverage /= 1000.0;

        double recentValue = Math.abs(historyMinute.getGeneration() * 60.0);
        recentValue /= 1000.0;



        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        double expectedAmount = adviceTrigger.getAmount();
        if (expectedAmount > 0) expectedAmount *= -1;

        boolean doesNotExceed =  false;
        if (!doubleEquals(demandAverage, expectedAmount, .001)){
          doesNotExceed = (demandAverage > adviceTrigger.getAmount());
        }

        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);


        if (isTriggerActive){
            if (doesNotExceed && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendAverageGenerationNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendAverageGenerationNotification(advice, adviceRecipient, adviceTrigger, virtualECC, adviceTrigger.getAmount(), recentValue);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendAverageGenerationNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  adviceTrigger.getAmount(), recentValue);
                    }
                }
            } else if (expired && !doesNotExceed) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }



    public void checkConsumedAverage(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, HistoryBillingCycle historyBillingCycle,HistoryMinute historyMinute, int totalMTUCount){

        long startEpoch = clockService.getEpochTime();
        startEpoch -= (60l * adviceTrigger.getDelayMinutes());

        Double DA = null;
        double lastValue = 0;

        if (virtualECC.getSystemType().equals(VirtualECCType.NET_ONLY)){
            DA = historyHourDAO.getAverageNetEnergyConsumed(virtualECC.getId(), startEpoch, totalMTUCount);
            lastValue = historyMinute.getNet() * 60.0;

        } else {
            DA = historyHourDAO.getAverageEnergyConsumed(virtualECC.getId(), startEpoch, totalMTUCount);
            lastValue = historyMinute.getLoad() * 60.0;
        }

        lastValue /= 1000.0;

        if (DA== null) return;
        double demandAverage = DA.doubleValue();

        demandAverage /= 1000.0;
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        boolean doesNotExceed =  false;
        if (!doubleEquals(demandAverage, adviceTrigger.getAmount(), .001)){
            doesNotExceed = (demandAverage < adviceTrigger.getAmount());
        }

        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);



        if (isTriggerActive){

            if (doesNotExceed && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendAverageConsumedNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendAverageConsumedNotification(advice, adviceRecipient, adviceTrigger, virtualECC, adviceTrigger.getAmount(), lastValue);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendAverageConsumedNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  adviceTrigger.getAmount(), lastValue);
                    }
                }
            } else if (expired && !doesNotExceed) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }


    public void checkLastPost(Advice advice, AdviceTrigger adviceTrigger){
        LOGGER.info("[checkLastPost] Called for {} {}", advice, adviceTrigger);;
        VirtualECC virtualECC = virtualECCService.findOneFromCache(advice.getVirtualECCId());

        long rtnEpoch = 0;
        if (adviceTrigger.getSendAtMost().equals(SendAtMostType.BILLING_CYCLE)){
            //Find the start of the current billing cycle
            HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(advice.getVirtualECCId(), clockService.getEpochTime());
            if (historyBillingCycle != null) {
                rtnEpoch = historyBillingCycle.getStartEpoch();
            }
        }

        long startEpoch = clockService.getEpochTime();
        startEpoch -= (60l * adviceTrigger.getDelayMinutes());

        List<VirtualECCMTU> virtualECCMTUList = virtualECCMTUDAO.findByVirtualECC(virtualECC.getId(), virtualECC.getAccountId());
        List<NonPostingMTU> nonPostingMTUList = new ArrayList<>();
        for (VirtualECCMTU virtualECCMTU: virtualECCMTUList){
            MTU mtu = mtudao.findById(virtualECCMTU.getMtuId(), virtualECCMTU.getAccountId());
            if (mtu == null || mtu.getLastPost() < startEpoch){
                NonPostingMTU nonPostingMTU = new NonPostingMTU();
                nonPostingMTU.setLastPost((mtu==null)?0:mtu.getLastPost());
                nonPostingMTU.setDescription(virtualECCMTU.getMtuDescription());
                nonPostingMTU.setHex(virtualECCMTU.getHexId());
                nonPostingMTUList.add(nonPostingMTU);
            }
        }

//        List<NonPostingMTU> nonPostingMTUList = lastPostDAO.findNonPostingMTU(advice.getVirtualECCId(), startEpoch);




        boolean notPosting = false;
        if (nonPostingMTUList.size() > 0){
            notPosting = true;
            if (adviceTrigger.isAllMTUs()){
                //if (nonPostingMTUList.size() != lastPostDAO.getMTUCount(advice.getVirtualECCId())){
                if (nonPostingMTUList.size() != virtualECCMTUList.size()){
                    //We only care if its all the MTU's posting
                    notPosting = false;
                }
            }
        }


        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, rtnEpoch, timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);




        if (isTriggerActive){
            if (notPosting && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){

                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);
                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;
                advice.setState(AdviceState.ALARM);
                if (!advice.getId().equals(0L)) {
                    adviceDAO.save(advice);
                }

                LOGGER.info("[checkLastPost] Triggered: {} {}", advice, adviceTrigger);

                logEvent("sendNotPostingNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendNotPostingNotification(advice, adviceRecipient, adviceTrigger, virtualECC, nonPostingMTUList);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendNotPostingNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  nonPostingMTUList);
                    }
                }
            } else if (!notPosting && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                //Check if we can return to normal.
                    LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                    adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                    saveTrigger = true;

                    logEvent("sendPostingNotification");
                    for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                        if (adviceRecipient.isSendEmail()) {
                            emailService.sendPostingNotification(adviceRecipient, adviceTrigger, virtualECC);
                        }
                        if (adviceRecipient.isSendPush()) {
                            pushService.sendPostingNotification(adviceRecipient, adviceTrigger, virtualECC);
                        }
                    }
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;

//              We don't post here because this is an RTN that occured outside the timer window
//                logEvent("sendPostingNotification");
//                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
//                    if (adviceRecipient.isSendEmail()) {
//                        emailService.sendPostingNotification(advice, adviceRecipient, adviceTrigger, virtualECC, nonPostingMTUList);
//                    }
//                    if (adviceRecipient.isSendPush()) {
//                        pushService.sendPostingNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  nonPostingMTUList);
//                    }
//                }
            }
        }



        if (!advice.getId().equals(0L) && saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }

    public void checkRealTimePowerExceeds(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, HistoryBillingCycle historyBillingCycle, HistoryMinute historyMinute, int totalNumberMTU) {
        if (historyMinute.getMtuCount() != totalNumberMTU){
            LOGGER.info("Not all MTU information has been collected. Found {} expected {}", totalNumberMTU, historyMinute.getMtuCount());
            return;
        }
        LOGGER.debug("CHECKING REAL TIME POWER EXCEEDS!!!");
        double realTimePower = historyMinute.getNet();
        realTimePower *= 60.0;
        realTimePower /= 1000.0; //Convert to kW



        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        //Check if a cost change has occured.
        boolean exceeds = realTimePower >= adviceTrigger.getAmount();
        LOGGER.debug("rtp: {} at:{} V:{}", realTimePower, adviceTrigger.getAmount(), (realTimePower >= adviceTrigger.getAmount()));
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);

        if (isTriggerActive){
            if (exceeds && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);

                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendRealTimePowerNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendRealTimePowerNotification(advice, adviceRecipient, adviceTrigger, virtualECC, adviceTrigger.getAmount(), realTimePower);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendRealTimePowerNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  adviceTrigger.getAmount(), realTimePower);
                    }
                }
            } else if (expired && !exceeds) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }

    public void checkVoltageDoesNotExceeds(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, HistoryBillingCycle historyBillingCycle, HistoryMinute historyMinute, int totalNumberMTU) {
        if (historyMinute.getMtuCount() != totalNumberMTU){
            LOGGER.debug("Not all MTU information has been collected. Found {} expected {}", totalNumberMTU, historyMinute.getMtuCount());
            return;
        }

        double voltage = historyMinute.getVoltageTotal();
        voltage /= historyMinute.getMtuCount();

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        //Check if a cost change has occured.
        boolean doesNotExceed = voltage < adviceTrigger.getAmount();
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);

        if (isTriggerActive){
            if (doesNotExceed && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);

                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendVoltageDoesNotExceedNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendVoltageDoesNotExceedNotification(advice, adviceRecipient, adviceTrigger, virtualECC, adviceTrigger.getAmount(), voltage);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendVoltageDoesNotExceedNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  adviceTrigger.getAmount(), voltage);
                    }
                }
            } else if (expired && !doesNotExceed) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }

    public void checkVoltageExceeds(Advice advice, AdviceTrigger adviceTrigger, VirtualECC virtualECC, HistoryBillingCycle historyBillingCycle, HistoryMinute historyMinute, int totalNumberMTU) {
        if (historyMinute.getMtuCount() != totalNumberMTU){
            LOGGER.debug("Not all MTU information has been collected. Found {} expected {}", totalNumberMTU, historyMinute.getMtuCount());
            return;
        }


        double voltage = historyMinute.getVoltageTotal();
        voltage /= historyMinute.getMtuCount();

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        long currentEpoch = clockService.getEpochTime();

        //Check if a cost change has occured.
        boolean exceeds = voltage >= adviceTrigger.getAmount();
        boolean isTriggerActive = checkTriggerTime(adviceTrigger, timeZone);
        boolean saveTrigger = false;
        boolean expired = canReturnToNormal(adviceTrigger, historyBillingCycle.getStartEpoch(), timeZone) && adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM);

        if (isTriggerActive){
            if (exceeds && (expired || adviceTrigger.getTriggerState().equals(AdviceTriggerState.NORMAL))){
                adviceTrigger.setTriggerState(AdviceTriggerState.ALARM);

                adviceTrigger.setLastSent(currentEpoch);
                saveTrigger = true;

                advice.setState(AdviceState.ALARM);
                adviceDAO.save(advice);
                logEvent("sendVoltageExceedsNotification");
                for (AdviceRecipient adviceRecipient: advice.getAdviceRecipientList()) {
                    if (adviceRecipient.isSendEmail()) {
                        emailService.sendVoltageExceedsNotification(advice, adviceRecipient, adviceTrigger, virtualECC, adviceTrigger.getAmount(), voltage);
                    }
                    if (adviceRecipient.isSendPush()) {
                        pushService.sendVoltageExceedsNotification(advice, adviceRecipient, adviceTrigger, virtualECC,  adviceTrigger.getAmount(), voltage);
                    }
                }
            } else if (expired && !exceeds) {
                //Check if we can return to normal.
                LOGGER.debug("RETURN TO NORMAL  " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        } else {
            if (adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)) {
                LOGGER.debug("RETURN TO NORMAL (Outside of Trigger Range Check) " + adviceTrigger);
                adviceTrigger.setTriggerState(AdviceTriggerState.NORMAL);
                saveTrigger = true;
            }
        }
        if (saveTrigger){
            adviceTriggerDAO.save(adviceTrigger);
            LOGGER.debug("Saving {}", adviceTrigger);
            if (!adviceTrigger.getTriggerState().equals(AdviceTriggerState.ALARM)){
                checkAdviceState(advice);
            }
        }
    }

}
