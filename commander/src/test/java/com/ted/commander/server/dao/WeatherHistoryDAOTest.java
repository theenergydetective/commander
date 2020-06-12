/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class WeatherHistoryDAOTest {

    @Autowired
    protected WeatherHistoryDAO weatherHistoryDAO;


    @Test
    public void testMinute(){
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setWeatherId(44l);
        virtualECC.setTimezone("US/Eastern");

        List<WeatherHistory> weatherHistoryList =  weatherHistoryDAO.findMinuteHistory(virtualECC, 1428814800l, 1428818400l);
        Assert.assertEquals(60, weatherHistoryList.size());
        for (int i=0; i < 60; i++){
            WeatherHistory weatherHistory = weatherHistoryList.get(i);
            Assert.assertEquals(i, weatherHistory.getCalendarKey().getMin());
            Assert.assertEquals(3, weatherHistory.getCalendarKey().getMonth());
            Assert.assertEquals(12, weatherHistory.getCalendarKey().getDate());
            Assert.assertEquals(1, weatherHistory.getCalendarKey().getHour());
        }
    }


    @Test
    public void testHour(){
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setWeatherId(44l);
        virtualECC.setTimezone("US/Eastern");
        List<WeatherHistory> weatherHistoryList =  weatherHistoryDAO.findHourHistory(virtualECC, 1428811200l, 1428897600l);
        Assert.assertEquals(24, weatherHistoryList.size());
        for (int i=0; i < 24; i++){
            WeatherHistory weatherHistory = weatherHistoryList.get(i);
            Assert.assertEquals(0, weatherHistory.getCalendarKey().getMin());
            Assert.assertEquals(3, weatherHistory.getCalendarKey().getMonth());
            Assert.assertEquals(12, weatherHistory.getCalendarKey().getDate());
            Assert.assertEquals(i, weatherHistory.getCalendarKey().getHour());
        }
    }


    @Test
    public void testDay(){
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setWeatherId(44l);
        virtualECC.setTimezone("US/Eastern");
        List<WeatherHistory> weatherHistoryList =  weatherHistoryDAO.findDayHistory(virtualECC, 1428638400l, 1428897600l);
        Assert.assertEquals(3, weatherHistoryList.size());
        for (int i=0; i < 3; i++){
            WeatherHistory weatherHistory = weatherHistoryList.get(i);
            Assert.assertEquals(0, weatherHistory.getCalendarKey().getMin());
            Assert.assertEquals(3, weatherHistory.getCalendarKey().getMonth());
            Assert.assertEquals(10 + i, weatherHistory.getCalendarKey().getDate());
            //Assert.assertEquals(i, weatherHistory.getCalendarKey().getHour());
        }
    }


    @Test
    public void testBillingCycle(){
        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setWeatherId(44l);
        virtualECC.setTimezone("US/Eastern");
        List<WeatherHistory> weatherHistoryList =  weatherHistoryDAO.findBillingCycleHistory(virtualECC, 1428638400l, 1428897600l);
        Assert.assertEquals(1, weatherHistoryList.size());
        for (int i=0; i < 1; i++){
            WeatherHistory weatherHistory = weatherHistoryList.get(i);
            Assert.assertEquals(2015, weatherHistory.getCalendarKey().getYear());
            Assert.assertEquals(3, weatherHistory.getCalendarKey().getMonth());
            //Assert.assertEquals(i, weatherHistory.getCalendarKey().getHour());
        }
    }



}
