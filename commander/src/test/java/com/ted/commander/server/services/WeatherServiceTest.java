/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.*;

import com.ted.commander.server.model.WeatherKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class WeatherServiceTest {


    @Autowired
    WeatherService weatherService;

    @Autowired
    WeatherKeyDAO weatherKeyDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    private DataSource commanderDataSource;


    @Test
    public void testFindByCity(){
        assertNotNull(weatherKeyDAO.findByCityId(4574324l));
        assertNotNull(weatherService.findWeatherKey("Charleston", "SC", "29401", "US"));
    }

    @Test
    public void testApi(){
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(1134l);
        //WeatherKey weatherKey = weatherService.findWeatherKey("Charleston", "SC", "29401", "US");
        virtualECC.setCity("Mt. Pleasant");
        WeatherKey weatherKey = weatherService.findWeatherKey(virtualECC);
        WeatherHistory weatherHistory = weatherService.getWeatherHistory(weatherKey);
        assertNotNull(weatherHistory);
    }

    @Test
    public void updateGap(){
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(1134l);
        //WeatherKey weatherKey = weatherService.findWeatherKey("Charleston", "SC", "29401", "US");
        WeatherKey weatherKey = weatherService.findWeatherKey(virtualECC);
        WeatherHistory weatherHistory = weatherService.getWeatherHistory(weatherKey);
        assertNotNull(weatherHistory);
    }

    @Test
    public void testUpdateWeather(){
        weatherService.updateWeather();
    }

    @Test
    public void testWeatherKey(){

        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);


        List<Long> idList = jdbcTemplate.queryForList("select id from commander.virtualECC where weatherId is null || weatherId = 0", Long.class);

        for (Long id: idList) {
            VirtualECC virtualECC = virtualECCDAO.findById(id);
            WeatherKey weatherKey = weatherService.findWeatherKey(virtualECC);
            if (weatherKey != null) {
                virtualECC.setWeatherId(weatherKey.getId());
                virtualECCDAO.update(virtualECC);
            }
        }
    }

}

