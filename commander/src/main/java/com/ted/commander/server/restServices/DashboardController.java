/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.model.DashboardResponse;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.server.dao.HistoryBillingCycleDAO;
import com.ted.commander.server.dao.HistoryDayDAO;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.dao.VirtualECCMTUDAO;
import com.ted.commander.server.model.ProjectedCost;
import com.ted.commander.server.services.*;
import com.ted.commander.server.util.CalendarUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/dashboard")
public class DashboardController {

    final static Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;


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

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");


    //@PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/{virtualECCId}", method = RequestMethod.GET)
    public
    @ResponseBody
    DashboardResponse getSummary(@PathVariable Long virtualECCId, @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate, @QueryParam("weather") String weather, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {

        ga.postAsync(new PageViewHit("/api/dashboard", "DashboardRESTSerivce", "Get Dashboard"));

        LOGGER.debug("Request Dashboard Summary: v:{} s:{} e:{}", virtualECCId, startDate, endDate);
        DashboardResponse dashboardResponse = new DashboardResponse();

        VirtualECC virtualECC = null;

        if (virtualECCId > 0 & aclService.canViewLocation(activeUser.getUsername(), virtualECCId)){
            virtualECC = virtualECCDAO.findOneFromCache(virtualECCId);
        } else {
            virtualECC = virtualECCService.findDefault(activeUser.getUsername());
            LOGGER.info("Loaded default ECC of :{}", virtualECC);
        }

        if (virtualECC != null) {
            dashboardResponse.setVirtualECC(virtualECC);
            virtualECCId = virtualECC.getId();

            EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
            dashboardResponse.setEnergyPlan(energyPlan);

            int mtuCount = virtualECCMTUDAO.countNetMTU(virtualECC.getId(), virtualECC.getAccountId());

            if (mtuCount == 0) {
                dashboardResponse.setHasNetPoints(false);
                return dashboardResponse;
            }

            dashboardResponse.setHasNetPoints(true);

            TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
            long startEpoch = CalendarUtils.epochFromString(startDate, timeZone);
            long endEpoch = CalendarUtils.epochFromString(endDate, timeZone);

            //Find Billing Cycles for this range. Adjust the date selection so that all billing cycle dates are returned (used for gradient).
            List<HistoryBillingCycle> historyBillingCycleList = historyBillingCycleDAO.findByDateRange(virtualECCId, startEpoch, endEpoch);
            if (historyBillingCycleList != null) {
                for (HistoryBillingCycle historyBillingCycle : historyBillingCycleList) {
                    if (historyBillingCycle.getStartEpoch() < startEpoch)
                        startEpoch = historyBillingCycle.getStartEpoch();
                    if (historyBillingCycle.getEndEpoch() > endEpoch) endEpoch = historyBillingCycle.getEndEpoch();
                }
            }
            dashboardResponse.setHistoryDayList(historyDayDAO.findByDateRange(virtualECC.getId(), startEpoch, endEpoch));




            LOGGER.debug("DashBoard Response: size: {}", dashboardResponse.getHistoryDayList().size());
            if (dashboardResponse.getHistoryDayList().size() > 0) {
                long dayStartEpoch  = dashboardResponse.getHistoryDayList().get(dashboardResponse.getHistoryDayList().size() - 1).getStartEpoch();
                dashboardResponse.setCurrentBillingCycle(historyBillingCycleDAO.findActiveBillingCycle(virtualECCId, dayStartEpoch ));
            }
            LOGGER.debug("DashBoard Response: cbc: {}", dashboardResponse.getCurrentBillingCycle());
            if (dashboardResponse.getCurrentBillingCycle() != null) {

                //TODO: Remove when new mobile builds are out
                for (HistoryDay historyDay: dashboardResponse.getHistoryDayList()){
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

                //Only do this if its an active billing cycle.
                if (dashboardResponse.getCurrentBillingCycle().getEndEpoch() > System.currentTimeMillis() / 1000) {
                    int season = costService.findSeason(energyPlan, timeZone, System.currentTimeMillis() / 1000);
                    ProjectedCost projectedCost = costService.getProjectedCost(energyPlan, timeZone, dashboardResponse.getCurrentBillingCycle());
                    dashboardResponse.setProjectedCost(projectedCost.getCost());
                    dashboardResponse.setProjectedKWH(projectedCost.getEnergy());
                    dashboardResponse.setTier(costService.findTier(energyPlan, season, dashboardResponse.getCurrentBillingCycle().getNet()));
                    dashboardResponse.setTou(costService.findTOU(energyPlan, season, timeZone, System.currentTimeMillis() / 1000));
                }
            }

            //Do Weather
            if (weather != null && weather.equals("true")) {
                LOGGER.info("Including weather");
                dashboardResponse.setWeatherHistoryList(weatherService.findHistory(HistoryType.DAILY, virtualECC, startEpoch, endEpoch));
            }
        } else {
            LOGGER.warn("User {} does not have a virtual ECC on any of their accounts.", activeUser.getUsername());
        }
        return dashboardResponse;
    }

}
