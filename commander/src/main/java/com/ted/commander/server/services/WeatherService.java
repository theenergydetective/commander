/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.DailySummary;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.dao.WeatherHistoryDAO;
import com.ted.commander.server.dao.WeatherKeyDAO;
import com.ted.commander.server.model.WeatherKey;
import com.ted.commander.server.util.AverageUtils;
import com.ted.commander.server.util.CalendarUtils;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;
import net.aksingh.owmjapis.OpenWeatherMap.Units;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


/**
 * Interface for getting data from openweather.org
 */
@Service
public class WeatherService {

    static final String API_KEY = "<INSERT KEY>";
    static Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);
    OpenWeatherMap owm = new OpenWeatherMap(API_KEY);

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    WeatherKeyDAO weatherKeyDAO;

    @Autowired
    WeatherHistoryDAO weatherHistoryDAO;

    @Autowired
    ServerService serverService;

    boolean isUpdating = false;


    public WeatherKey findWeatherKey(VirtualECC virtualECC) {
        return findWeatherKey(virtualECC.getCity(), virtualECC.getState(), virtualECC.getPostal(), virtualECC.getCountry());
    }


    /**
     * This is used to look up the weather key. If it doesn't exist it is created. If it does exist and is missing some information, it is updated.
     *
     * @param city
     * @param state
     * @param postal
     * @param country
     * @return
     */
    public WeatherKey findWeatherKey(String city, String state, String postal, String country) {
        if (city != null && !city.trim().isEmpty()) {
            try {
                LOGGER.debug("Performing City weather key check: {} {} {}", new Object[]{city, state, country});
                CurrentWeather currentWeather = owm.currentWeatherByCityName(city + ", " + state, country);

                if (currentWeather == null || currentWeather.getCityId() == Long.MIN_VALUE) {
                    LOGGER.error("Could not look up weather for {} {} {}", new Object[]{city, state, country});
                    return findWeatherKey(null, null, postal, country);
                }
                //Look up by city id.
                WeatherKey weatherKey = null;

                if (currentWeather.getCityCode() != 0) {
                    LOGGER.debug("LOOKING UP BY CITY CODE: " + currentWeather.getCityCode());
                    weatherKey = weatherKeyDAO.findByCityId(currentWeather.getCityCode());
                }

                if (weatherKey == null && currentWeather != null && currentWeather.getCoordInstance() != null) {
                    BigDecimal lat = new BigDecimal(currentWeather.getCoordInstance().getLatitude());
                    lat = lat.setScale(2, BigDecimal.ROUND_FLOOR);

                    BigDecimal lng = new BigDecimal(currentWeather.getCoordInstance().getLongitude());
                    lng = lng.setScale(2, BigDecimal.ROUND_FLOOR);

                    weatherKey = weatherKeyDAO.findByCoord(lat, lng);
                    if (weatherKey == null) {
                        LOGGER.debug("CREATING NEW RECORD: {}", currentWeather.getRawResponse());
                        weatherKey = new WeatherKey();
                        weatherKey.setCityId(currentWeather.getCityCode());
                        weatherKey.setLat(lat);
                        weatherKey.setLon(lng);

                        if (country.equals(null) || country.equals("US") || country.isEmpty()){
                            weatherKey.setMetric(false);
                        }

                        weatherKey = weatherKeyDAO.update(weatherKey);
                        return weatherKey;
                    } else {
                        LOGGER.debug("UPDATING CITY RECORD");
                        weatherKey.setCityId(currentWeather.getCityCode());
                        weatherKey = weatherKeyDAO.update(weatherKey);
                        return weatherKey;
                    }
                } else {
                    return weatherKey;
                }

            } catch (Exception ex) {
                LOGGER.error("Error City Lookup: {} {} {}", new Object[]{city, state, country}, ex);
            }

        } else if (postal != null && !postal.isEmpty()) {
            try {
                LOGGER.debug("[findWeatherKey]  Performing Postal weatherKey check: {} {}", new Object[]{postal, country});
                CurrentWeather currentWeather = owm.currentWeatherByCityName(postal, country);
                if (currentWeather == null) {
                    LOGGER.error("[findWeatherKey] NULL returned for {} {}", new Object[]{postal, country});
                    return null;
                }

                BigDecimal lat = new BigDecimal(currentWeather.getCoordInstance().getLatitude());
                lat = lat.setScale(2, BigDecimal.ROUND_FLOOR);

                BigDecimal lng = new BigDecimal(currentWeather.getCoordInstance().getLongitude());
                lng = lng.setScale(2, BigDecimal.ROUND_FLOOR);

                //Look up by city id.
                WeatherKey weatherKey = weatherKeyDAO.findByCoord(lat, lng);
                if (weatherKey == null) {
                    LOGGER.debug("CREATING NEW RECORD");
                    weatherKey = new WeatherKey();
                    weatherKey.setCityId(0);


                    weatherKey.setLat(lat);
                    weatherKey.setLon(lng);
                    if (country.equals(null) || country.equals("US") || country.isEmpty()){
                        weatherKey.setMetric(false);
                    }

                    weatherKey = weatherKeyDAO.update(weatherKey);
                    return weatherKey;
                } else {
                    return weatherKey;
                }
            } catch (Exception ex) {
                LOGGER.error("Error City Lookup: {} {} {}", new Object[]{city, state, country}, ex);
            }
        }

        return null;

    }

    public WeatherHistory getWeatherHistory(WeatherKey weatherKey){
        WeatherHistory weatherHistory = null;
        LOGGER.debug("GET WEATHER HISTORY: {}", weatherKey);

        if (weatherKey.isMetric()) owm.setUnits(Units.METRIC);
        else owm.setUnits(Units.IMPERIAL);

        //boolean logWeather = (weatherKey.getId() == 46l) || (weatherKey.getId() == 124l);
        boolean logWeather = false;

        CurrentWeather currentWeather = null;
        try {
            if (weatherKey.getCityId() > 0) {
                LOGGER.debug("CITY ID LOOKUP: " + weatherKey.getCityId());
                currentWeather = owm.currentWeatherByCityCode(weatherKey.getCityId());
            } else {
                LOGGER.debug("COORD LOOKUP: " + weatherKey);
                currentWeather = owm.currentWeatherByCoordinates(weatherKey.getLat().floatValue(), weatherKey.getLon().floatValue());

            }
        } catch (Exception ex) {
            LOGGER.error("Exception getting weather: {}", weatherKey, ex);
        }



        if (logWeather) {
            StringBuilder weatherLog = new StringBuilder();
            if (currentWeather == null){
                weatherLog.append("NO DATE RETURNED " + new Date());
            } else {
                weatherLog.append(currentWeather.getRawResponse());
            }
            weatherLog.append("\n\n");
            LOGGER.info("PARSING WEATHER: {}", weatherLog);
            try {
                Files.write(Paths.get("/home/ec2-user/" + weatherKey.getId() + "-weather.txt"), weatherLog.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (Exception io) {
                LOGGER.error("Error writing weather log ", io);
            }
        }

        if (currentWeather != null) {
            if (logWeather) {
                StringBuilder weatherLog = new StringBuilder(currentWeather.getRawResponse());
                weatherLog.append("\n\n");
                LOGGER.info("PARSING WEATHER: {}", weatherLog);
                try {
                    Files.write(Paths.get("/home/ec2-user/" + weatherKey.getId() + "-weather.txt"), weatherLog.toString().getBytes(), StandardOpenOption.APPEND);
                } catch (Exception io){
                    LOGGER.error("Error writing weather log ", io);
                }
            }

            int clouds = 0;
            int windspeed = 0;
            double temp = 0;
            if (currentWeather.getMainInstance() != null) {
                temp = currentWeather.getMainInstance().getTemperature();
            }
            if (currentWeather.getCloudsInstance() != null) {
                clouds = (int) currentWeather.getCloudsInstance().getPercentageOfClouds();
            }
            if (currentWeather.getWindInstance() != null) {
                windspeed = (int) currentWeather.getWindInstance().getWindSpeed();
            }

            weatherHistory = new WeatherHistory();
            weatherHistory.setTemp(temp);
            //convert to IMERIAL for US


            weatherHistory.setClouds(clouds);
            weatherHistory.setWindspeed(windspeed);
        }

        LOGGER.debug("{}", weatherHistory);
        return weatherHistory;
    }

    long isUpdatingCount = 0;

    //Fix to make sure the block isn't stuck
    @Scheduled(fixedRate = 60000)
    public void updateCheck() {
        if (isUpdating) {
            isUpdatingCount++;
            if (isUpdatingCount > 5) {
                isUpdatingCount = 0;
                isUpdating = false;
            }
        } else {
            isUpdatingCount = 0;
        }
    }

    @Scheduled(fixedRate = 60000)
    public void updateWeather() {
        if (isUpdating) return;

        isUpdating = true;
        WeatherKey lastKey = null;
        try {

            if (serverService.isDevelopment() || PollingService.NUMBER_ENERGYPOST_THREADS == 0) {
                return;
            }


            LOGGER.debug("Updating weather");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long timeStamp = calendar.getTimeInMillis() / 1000;


            long startTime = System.currentTimeMillis();

            List<WeatherKey> weatherKeyList = weatherKeyDAO.findInQueue();
            if (weatherKeyList != null && weatherKeyList.size() > 0) {
                for (WeatherKey weatherKey : weatherKeyList) {
                    lastKey = weatherKey;
//                long nowTime = System.currentTimeMillis();
//                long diff = nowTime - startTime;
//                if (diff >= 3600000) {
//                    LOGGER.info("Killing weather thread: TIMEOUT");
//                    return; //Kill the thread if we are running more than a minute
//                }

                    try {
                        WeatherHistory weatherHistory = getWeatherHistory(weatherKey);
                        if (weatherHistory != null) {

                            weatherHistoryDAO.insert(timeStamp, weatherKey.getId(),
                                    weatherHistory.getTemp(),
                                    weatherHistory.getClouds(),
                                    weatherHistory.getWindspeed());

                        }
                    } catch (Exception ex){
                        LOGGER.error("[updateWeather] Exception Caught: {}", weatherKey, ex);

                    }
                }
            } else {
                LOGGER.debug("No weather needs updating.");
            }
        } catch(Exception ex){
            LOGGER.error("[updateWeather] Exception Caught: {}", lastKey, ex);
        }
        isUpdating = false;
    }


    public List<WeatherHistory> findHistory(HistoryType historyType, VirtualECC virtualECC, long startEpoch, long endEpoch) {
        switch (historyType) {
            case MINUTE:
                return (weatherHistoryDAO.findMinuteHistory(virtualECC, startEpoch, endEpoch));
            case HOURLY:
                return (weatherHistoryDAO.findHourHistory(virtualECC, startEpoch, endEpoch));
            case DAILY:
                return (weatherHistoryDAO.findDayHistory(virtualECC, startEpoch, endEpoch));
            default:
                return (weatherHistoryDAO.findBillingCycleHistory(virtualECC, startEpoch, endEpoch));
        }
    }



    public DailySummary getWeatherAverage(VirtualECC virtualECC, long dayStartEpoch) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(virtualECC.getTimezone()));
        calendar.setTimeInMillis(dayStartEpoch * 1000);
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.MONTH, -2);

        List<WeatherHistory> historyList = weatherHistoryDAO.findDayHistory(virtualECC, calendar.getTimeInMillis() / 1000, dayStartEpoch);
        List<Double> temps = new ArrayList<>();
        List<Integer> wind = new ArrayList<>();
        List<Integer> clouds = new ArrayList<>();

        for (WeatherHistory weatherHistory : historyList) {
            Calendar dowCalendar = CalendarUtils.fromCalendarKey(weatherHistory.getCalendarKey(), TimeZone.getTimeZone(virtualECC.getTimezone()));
            if (dowCalendar.get(Calendar.DAY_OF_WEEK) == dow) {
                temps.add(weatherHistory.getTemp());
                wind.add(weatherHistory.getWindspeed());
                clouds.add(weatherHistory.getClouds());
            }
        }
        DailySummary dailySummary = new DailySummary();
        dailySummary.setAvgCloud(AverageUtils.calculateIntAverage(clouds));
        dailySummary.setAvgWind(AverageUtils.calculateIntAverage(wind));
        dailySummary.setAvgTemp(AverageUtils.calculateAverage(temps));
        return dailySummary;
    }



    public DailySummary getTodayWeatherAverage(VirtualECC virtualECC, long timeOfDayEpoch, long dayStartEpoch){
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(virtualECC.getTimezone()));
        calendar.setTimeInMillis(dayStartEpoch * 1000);
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.MONTH, -2);
        int maxMinute = AverageUtils.getMaxMinute(timeOfDayEpoch, timeZone);


        List<WeatherHistory> historyList = weatherHistoryDAO.findHourHistory(virtualECC, calendar.getTimeInMillis() / 1000, dayStartEpoch);
        List<Double> temps = new ArrayList<>();
        List<Integer> wind = new ArrayList<>();
        List<Integer> clouds = new ArrayList<>();

        for (WeatherHistory weatherHistory : historyList) {
            Calendar dowCalendar = CalendarUtils.fromCalendarKey(weatherHistory.getCalendarKey(), TimeZone.getTimeZone(virtualECC.getTimezone()));
            if (dowCalendar.get(Calendar.DAY_OF_WEEK) == dow) {
                int hMaxMinute = AverageUtils.getMaxMinute(dowCalendar.getTimeInMillis() / 1000, timeZone);
                if (hMaxMinute <= maxMinute) {
                    temps.add(weatherHistory.getTemp());
                    wind.add(weatherHistory.getWindspeed());
                    clouds.add(weatherHistory.getClouds());
                }
            }
        }
        DailySummary dailySummary = new DailySummary();
        dailySummary.setAvgCloud(AverageUtils.calculateIntAverage(clouds));
        dailySummary.setAvgWind(AverageUtils.calculateIntAverage(wind));
        dailySummary.setAvgTemp(AverageUtils.calculateAverage(temps));
        return dailySummary;
    }



}
