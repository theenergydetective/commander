/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.services.*;
import com.ted.commander.server.util.CalendarUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api/mobile")
public class MobileController {

    final static Logger LOGGER = LoggerFactory.getLogger(MobileController.class);

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    CostService costService;

    @Autowired
    UserService userService;

    @Autowired
    VirtualECCService virtualECCService;

    @Autowired
    WeatherService weatherService;

    @Autowired
    ACLService aclService;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    HistoryService historyService;

    @Autowired
    HistoryHourDAO historyHourDAO;

    @Autowired
    HistoryMTUHourDAO historyMTUHourDAO;

    @Autowired
    PieGraphPointDAO pieGraphPointDAO;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");


    //@PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/{virtualECCId}", method = RequestMethod.GET)
    public
    @ResponseBody
    MobileDashboardResponse getRecentSummary(@PathVariable Long virtualECCId, @QueryParam("weather") String weather, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {

        ga.postAsync(new PageViewHit("/api/mobile/virtualECCid", "MobileController", "Get Recent Summary"));
        MobileDashboardResponse response = new MobileDashboardResponse();

        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(virtualECCId);
        Integer totalMTU = virtualECCMTUDAO.countNetMTU(virtualECCId, virtualECC.getAccountId());

        //Get most recent Day Value (but not ahead of the current time (in case someone posted way in the future by accident).
        response.setHistoryDay(historyDayDAO.findMostRecent(virtualECCId));
        if (response.getHistoryDay() != null) {
            response.setHistoryMinute(historyMinuteDAO.findMostRecent(virtualECCId, response.getHistoryDay().getStartEpoch(), totalMTU));
            Long dayStartEpoch = response.getHistoryDay().getStartEpoch();
            HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECCId, dayStartEpoch);
            response.setHistoryBillingCycle(historyBillingCycle);
            response.setEnergyPlan(energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId()));

            if (response.getHistoryMinute() != null && response.getEnergyPlan() != null && response.getEnergyPlan().getPlanType() != PlanType.FLAT){
                TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
                int season = costService.findSeason(response.getEnergyPlan(), timeZone, response.getHistoryMinute().getStartEpoch());
                response.setTier(costService.findTier(response.getEnergyPlan(), season, response.getHistoryBillingCycle().getNet()));
                TOUPeakType touPeakType = costService.findTOU(response.getEnergyPlan(), season, timeZone, response.getHistoryMinute().getStartEpoch());
                response.setTouName(costService.getTouName(response.getEnergyPlan(), touPeakType));
            }

            if (weather != null && weather.equals("true")) {
                LOGGER.debug("Including weather");
                long startEpoch = response.getHistoryDay().getStartEpoch();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(virtualECC.getTimezone()));
                calendar.setTimeInMillis(startEpoch * 1000);
                calendar.add(1, Calendar.DATE);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                long endEpoch = calendar.getTimeInMillis() / 1000;

                List<WeatherHistory> weatherHistoryList = weatherService.findHistory(HistoryType.DAILY, virtualECC, startEpoch, endEpoch);
                if (weatherHistoryList!=null &&  weatherHistoryList.size() > 0) {
                    response.setWeatherHistory(weatherHistoryList.get(weatherHistoryList.size()-1));
                }
            }
        }

        //Fix the billing cycle keys (Temp fix until this is removed from mobile)
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        mapKey(response.getHistoryDay(), response.getEnergyPlan(), timeZone);

        return response;
    }


    private void mapKey(HistoryDay historyDay, EnergyPlan energyPlan, TimeZone timeZone){
        historyDay.getKey().setBillingCycleMonth(historyDay.getBillingCycleMonth());
        if (historyDay.getBillingCycleMonth() == 11 && historyDay.getCalendarKey().getMonth() == 0){
            historyDay.getKey().setBillingCycleYear(historyDay.getCalendarKey().getYear() - 1);
        } else {
            historyDay.getKey().setBillingCycleYear(historyDay.getCalendarKey().getYear());
        }
        int mrd = energyPlan.getMeterReadDate();
        if (mrd == 0) mrd = 1;
        if (mrd == 1 && energyPlan.getMeterReadCycle().equals(MeterReadCycle.MONTHLY)) {
            historyDay.getKey().setBillingCycleDay(historyDay.getCalendarKey().getDate() - 1);
        } else {
            DateTimeZone jodaTimeZone = DateTimeZone.forID(timeZone.getID());
            Calendar mycal = new GregorianCalendar(historyDay.getKey().getBillingCycleYear(), historyDay.getBillingCycleMonth(), 1);
            int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
            if (mrd > daysInMonth) mrd = daysInMonth;
            DateTime start = new DateTime(historyDay.getKey().getBillingCycleYear(), historyDay.getBillingCycleMonth() + 1, mrd, 0, 0, 0, jodaTimeZone);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(historyDay.getStartEpoch() * 1000);
            DateTime end = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0, jodaTimeZone);
            int days = Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
            historyDay.getKey().setBillingCycleDay(days);
        }
    }


    @PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/{virtualECCId}/stats", method = RequestMethod.GET)
    public
    @ResponseBody
    DailySummary getSummary(@PathVariable Long virtualECCId, @QueryParam("startDate") String startDate) {
        ga.postAsync(new PageViewHit("/api/mobile/virtualECCid/stats", "MobileController", "Get Daily Stats"));

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

                mapKey(historyDay, energyPlan, timeZone);
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
        }
        return dailySummary;
    }


    @PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/{virtualECCId}/line", method = RequestMethod.GET)
    public
    @ResponseBody
    DailySummary getLine(@PathVariable Long virtualECCId, @QueryParam("startDate") String startDate) {

        ga.postAsync(new PageViewHit("/api/mobile/virtualECCid/line", "MobileController", "Get Line Graph"));

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
            dailySummary.setEnergyPlan(energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId()));
            if (historyDay != null){
                dailySummary.setHourlyData(historyHourDAO.findByDateRange(virtualECCId, historyDay.getStartEpoch(), historyDay.getEndEpoch()));
                dailySummary.setWeatherGraphPoints(weatherService.findHistory(HistoryType.HOURLY, virtualECC, startEpoch, historyDay.getEndEpoch()));
            }
        }
        return dailySummary;
    }



    @PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/{virtualECCId}/pie", method = RequestMethod.GET)
    public
    @ResponseBody
    PieSummary getPie(@PathVariable Long virtualECCId, @QueryParam("startDate") String startDate) {
        PieSummary pieSummary = new PieSummary();
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(virtualECCId);
        ga.postAsync(new PageViewHit("/api/mobile/virtualECCid/pie", "MobileController", "Get Pie Graph"));

        if (virtualECC != null) {
            TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
            long startEpoch = CalendarUtils.epochFromString(startDate, timeZone);
            CalendarKey calendarKey = CalendarUtils.keyFromMillis(startEpoch * 1000, timeZone);
            pieSummary.setDailyDate(calendarKey);
            pieSummary.setRateType(energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId()).getRateType());
            HistoryDay historyDay = historyDayDAO.findOne(virtualECC.getId(), startEpoch);
            if (historyDay != null){
                pieSummary.setPieGraphPoints(pieGraphPointDAO.findByHistoryDay(virtualECC.getAccountId(), virtualECCId, historyDay.getStartEpoch()));
            }
        }
        return pieSummary;
    }


}
