/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.model.ComparisonQueryRequest;
import com.ted.commander.common.model.ComparisonQueryResponse;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.services.EnergyPlanService;
import com.ted.commander.server.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Controller for handling Comparison Requests
 */

@Controller
@RequestMapping("/api/comparison")
public class ComparisonController {

    final static Logger LOGGER = LoggerFactory.getLogger(ComparisonController.class);

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    ACLService aclService;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    HistoryHourDAO historyHourDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");


    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    ComparisonQueryResponse execute(@RequestBody ComparisonQueryRequest comparisonQueryRequest, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser){
        ga.postAsync(new PageViewHit("/api/comparison", "ComparisonRestService", "Get Comparison"));

        ComparisonQueryResponse response = new ComparisonQueryResponse();
        response.setVirtualECCList(comparisonQueryRequest.getLocationList());
        EnergyPlan energyPlan = null;
        for (VirtualECC virtualECC: comparisonQueryRequest.getLocationList()){
            if (aclService.canViewLocation(activeUser.getUsername(), virtualECC.getId())){
                TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
                Calendar startCalendar = CalendarUtils.fromCalendarKey(comparisonQueryRequest.getStartDate(), timeZone);
                Calendar endCalendar = CalendarUtils.fromCalendarKey(comparisonQueryRequest.getEndDate(), timeZone);
                if (energyPlan == null) energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());

                if (!comparisonQueryRequest.getHistoryType().equals(HistoryType.BILLING_CYCLE)){
                    endCalendar.add(Calendar.DATE, 1);
                } else {
                    endCalendar.add(Calendar.MONTH, 1);
                    if (((endCalendar.get(Calendar.MONTH) % 2 == 0) && energyPlan.getMeterReadCycle().equals(MeterReadCycle.BI_MONTHLY_ODD)) ||
                            ((endCalendar.get(Calendar.MONTH) % 2 != 0) && energyPlan.getMeterReadCycle().equals(MeterReadCycle.BI_MONTHLY_EVEN))) {
                        endCalendar.add(Calendar.MONTH, 1);
                    }
                }


                long startEpoch = startCalendar.getTimeInMillis() / 1000;
                long endEpoch = endCalendar.getTimeInMillis() / 1000;

                switch (comparisonQueryRequest.getHistoryType()){
                    case MINUTE:
                        response.getMinuteHistoryList().put(virtualECC.getId(), historyMinuteDAO.findByDateRange(virtualECC.getId(), startEpoch, endEpoch));
                        for (HistoryMinute minute: response.getMinuteHistoryList().get(virtualECC.getId())){
                            minute.setNet(minute.getNet() * 60.0);
                            minute.setNetCost(minute.getNetCost() * 60.0);
                        }
                        break;
                    case HOURLY:
                        response.getHourHistoryList().put(virtualECC.getId(), historyHourDAO.findByDateRange(virtualECC.getId(), startEpoch, endEpoch));
                        break;
                    case DAILY:
                        response.getDayHistoryList().put(virtualECC.getId(), historyDayDAO.findByDateRange(virtualECC.getId(), startEpoch, endEpoch));
                        break;
                    case BILLING_CYCLE:
                        response.getBillingCycleHistoryList().put(virtualECC.getId(),historyBillingCycleDAO.findByDateRange(virtualECC.getId(), startEpoch, endEpoch));
                        if (response.getBillingCycleHistoryList().size() > 0) {
                            for (Long key: response.getBillingCycleHistoryList().keySet()){
                                if (response.getBillingCycleHistoryList().get(key).size() > 0){
                                    //Align all of the start epoch's together
                                    for(HistoryBillingCycle historyBillingCycle: response.getBillingCycleHistoryList().get(key)){
                                        historyBillingCycle.getCalendarKey().setDate(1);
                                        historyBillingCycle.setStartEpoch(CalendarUtils.fromCalendarKey(historyBillingCycle.getCalendarKey(), timeZone).getTimeInMillis()/1000);
                                      if (historyBillingCycle.getStartEpoch() < startEpoch) startEpoch = historyBillingCycle.getStartEpoch();
                                        if (historyBillingCycle.getEndEpoch().longValue() > endEpoch) {
                                            endEpoch = historyBillingCycle.getEndEpoch();
                                        }
                                    }
                                }
                            }
                        }
                        break;
                }

                response.setStartCalendarKey(CalendarUtils.keyFromMillis(startEpoch * 1000, timeZone));
                response.setEndCalendarKey(CalendarUtils.keyFromMillis(endEpoch * 1000, timeZone));
            } else {
                LOGGER.warn("Insufficient privs to view:{} by {}", virtualECC, activeUser.getUsername());
            }
        }
        return response;
    }
}
