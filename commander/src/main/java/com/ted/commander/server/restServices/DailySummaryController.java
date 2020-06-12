/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMTUHour;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.services.EnergyPlanService;
import com.ted.commander.server.services.HistoryService;
import com.ted.commander.server.services.UserService;
import com.ted.commander.server.services.WeatherService;
import com.ted.commander.server.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.TimeZone;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api/dailySummary")
public class DailySummaryController {

    final static Logger LOGGER = LoggerFactory.getLogger(DailySummaryController.class);

    @Autowired
    UserService userService;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;


    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    WeatherService weatherService;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    WeatherHistoryDAO weatherHistoryDAO;

    @Autowired
    HistoryHourDAO historyHourDAO;

    @Autowired
    HistoryMTUHourDAO historyMTUHourDAO;

    @Autowired
    HistoryService historyService;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");

    @PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/{virtualECCId}", method = RequestMethod.GET)
    public
    @ResponseBody
    DailySummary getSummary(@PathVariable Long virtualECCId, @QueryParam("startDate") String startDate) {

        ga.postAsync(new PageViewHit("/api/dailySummary", "DailyRestSevice", "Get Daily"));


        DailySummary dailySummary = new DailySummary();

        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(virtualECCId);
        List<VirtualECCMTU> virtualECCMTUList = virtualECCMTUDAO.findByVirtualECC(virtualECCId, virtualECC.getAccountId());
        int mtuCount = 0;
        for (VirtualECCMTU virtualECCMTU: virtualECCMTUList){
            if (!virtualECCMTU.getMtuType().equals(MTUType.STAND_ALONE)) {
                mtuCount++;
            }
        }

        if (virtualECC != null) {
            TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
            long startEpoch = CalendarUtils.epochFromString(startDate, timeZone);
            CalendarKey calendarKey = CalendarUtils.keyFromMillis(startEpoch * 1000, timeZone);
            dailySummary.setDailyDate(calendarKey);
            dailySummary.setVirtualECCType(virtualECC.getSystemType());

            HistoryDay historyDay = historyDayDAO.findOne(virtualECC.getId(), startEpoch);
            EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());

            //See if its today
            long nowTime = System.currentTimeMillis()/1000;
            boolean today = false;
            if (historyDay != null){
                today = nowTime >= startEpoch && nowTime < historyDay.getEndEpoch();
            }

            //Set day values
            if (historyDay != null) {
                dailySummary.setCost(historyDay.getNetCost());
                dailySummary.setEnergy(historyDay.getNet());
                dailySummary.setDemand(historyDay.getDemandPeak());
                dailySummary.setDemandTime(historyDay.getDemandPeakCalendarKey());
                dailySummary.setPeakVoltage(historyDay.getPeakVoltage());
                dailySummary.setPeakVoltageTime(historyDay.getPeakVoltageCalendarKey());
                dailySummary.setLowVoltage(historyDay.getMinVoltage());
                dailySummary.setLowVoltageTime(historyDay.getMinVoltageCalendarKey());
                dailySummary.setPowerFactor(historyDay.getPfTotal() / (double) historyDay.getPfSampleCount());
                dailySummary.setEnergyPlan(energyPlan);
                dailySummary.setIsMetric(virtualECC.getCountry() != null && virtualECC.getCountry().trim().length() > 0 &&  !virtualECC.getCountry().equals("US"));
            }

            //Set Average values
            HistoryDay avgDay;
            long timeOfDayEpoch = System.currentTimeMillis() / 1000;
            if (today) {

                HistoryMinute historyMinute = historyMinuteDAO.findMostRecent(virtualECC.getId(), startEpoch, mtuCount);
                if (historyMinute != null) {
                    timeOfDayEpoch = historyMinute.getStartEpoch();
                    dailySummary.setRecentPower(historyMinute.getNet() * 60.0);
                    dailySummary.setAveragePower(60.0 * historyMinuteDAO.findAveragePower(virtualECC.getId(), historyMinute.getStartEpoch(), timeZone, mtuCount));
                }

                avgDay = historyService.getTodayAverageHistory(virtualECC, timeOfDayEpoch, startEpoch, mtuCount);
            }
            else {
                avgDay = historyService.getAverageHistory(virtualECC,startEpoch);
            }
            dailySummary.setAvgEnergy(avgDay.getNet());
            dailySummary.setAvgCost(avgDay.getNetCost());
            dailySummary.setAvgDemand(avgDay.getDemandPeak());
            dailySummary.setAvgPeakVoltage(avgDay.getPeakVoltage());
            dailySummary.setAvgLowVoltage(avgDay.getMinVoltage());
            dailySummary.setAvgPowerFactor(avgDay.getPfTotal());

            if (historyDay != null){
                dailySummary.setWeatherGraphPoints(weatherService.findHistory(HistoryType.HOURLY, virtualECC, startEpoch, historyDay.getEndEpoch()));
            }

            if (historyDay != null) {
                if (dailySummary.getWeatherGraphPoints().size() > 0) {
                    dailySummary.setPeakTemp(Double.MIN_VALUE);
                    dailySummary.setPeakWind(Integer.MIN_VALUE);
                    dailySummary.setPeakClouds(Integer.MIN_VALUE);
                    dailySummary.setMinTemp(Double.MAX_VALUE);
                    dailySummary.setMinWind(Integer.MAX_VALUE);
                    dailySummary.setMinClouds(Integer.MAX_VALUE);
                    for (WeatherHistory weatherHistory : dailySummary.getWeatherGraphPoints()) {
                        if (dailySummary.getPeakTemp() < weatherHistory.getTemp()) dailySummary.setPeakTemp(weatherHistory.getTemp());
                        if (dailySummary.getPeakWind() < weatherHistory.getWindspeed()) dailySummary.setPeakWind(weatherHistory.getWindspeed());
                        if (dailySummary.getPeakClouds() < weatherHistory.getClouds()) dailySummary.setPeakClouds(weatherHistory.getClouds());
                        if (dailySummary.getMinTemp() > weatherHistory.getTemp()) dailySummary.setMinTemp(weatherHistory.getTemp());
                        if (dailySummary.getMinWind() > weatherHistory.getWindspeed()) dailySummary.setMinWind(weatherHistory.getWindspeed());
                        if (dailySummary.getMinClouds() > weatherHistory.getClouds()) dailySummary.setMinClouds(weatherHistory.getClouds());
                    }
                }

                DailySummary weatherSummary;
                if (!today){
                    weatherSummary = weatherService.getWeatherAverage(virtualECC, startEpoch);
                } else {
                    weatherSummary = weatherService.getTodayWeatherAverage(virtualECC, timeOfDayEpoch, startEpoch);
                }
                dailySummary.setAvgTemp(weatherSummary.getAvgTemp());
                dailySummary.setAvgWind(weatherSummary.getAvgWind());
                dailySummary.setAvgTemp(weatherSummary.getAvgTemp());

            }

            if (historyDay != null){
                dailySummary.setHourlyData(historyHourDAO.findByDateRange(virtualECCId, historyDay.getStartEpoch(), historyDay.getEndEpoch()));

                //Load up the spyder totals

                for (VirtualECCMTU virtualECCMTU : virtualECCMTUList) {
                    if (virtualECCMTU.getMtuType().equals(MTUType.STAND_ALONE)) {
                        //Add the stand alone load to the hour query.
                        List<HistoryMTUHour> historyMTUHourList = historyMTUHourDAO.findByDateRange(virtualECCId, virtualECCMTU.getMtuId(), historyDay.getStartEpoch(), historyDay.getEndEpoch());
                        dailySummary.getStandAloneData().put(virtualECCMTU.getHexId(), historyMTUHourList);
                        dailySummary.getMtuDescription().put(virtualECCMTU.getHexId(), virtualECCMTU.getMtuDescription());
                    }
                }
            }
        }
        return dailySummary;
    }






}
