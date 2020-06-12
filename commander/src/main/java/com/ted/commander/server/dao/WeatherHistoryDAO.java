/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.WeatherHistory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for accessing the WeatherKey object
 */
@Repository
public class WeatherHistoryDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.weather_history (timestamp, weatherId, temp, windspeed, clouds) values (?,?,?,?,?)";

    static final String HOUR_QUERY = "select YEAR(wiq.convertedDate) as theYear, MONTH(wiq.convertedDate) as theMonth, DAY(wiq.convertedDate) as theDay, HOUR(wiq.convertedDate) as theHour, wiq.temp, wiq.windspeed, wiq.clouds from (select  CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', ?) as convertedDate, temp, windspeed, clouds from weather_history where weather_history.weatherId=? and weather_history.timestamp>=? and weather_history.timestamp < ? order by timestamp) wiq";

    static final String DAY_QUERY = "select a.theYear, a.theMonth, a.theDay, temp, windspeed, clouds, lowHour, lowTemp, peakHour, peakTemp from " +
            "(select theYear, theMonth, theDay, avg(temp) as temp, avg(windspeed) as windspeed, avg(clouds) as clouds from (select YEAR(wiq.convertedDate) as theYear, MONTH(wiq.convertedDate) as theMonth, DAY(wiq.convertedDate) as theDay, wiq.temp, wiq.windspeed, wiq.clouds from (select  CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', ?) as convertedDate, temp, windspeed, clouds from weather_history where weather_history.weatherId=? and weather_history.timestamp>=? and weather_history.timestamp <= ? order by timestamp) wiq)bq group by theYear, theMonth, theDay) a, " +
            "(select theYear, theMonth, theDay, theHour as lowHour, temp as  lowTemp from (select YEAR(wiq.convertedDate) as theYear, MONTH(wiq.convertedDate) as theMonth, DAY(wiq.convertedDate) as theDay,  HOUR(wiq.convertedDate) as theHour, wiq.temp, wiq.windspeed, wiq.clouds from (select  CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', ?) as convertedDate, temp, windspeed, clouds from weather_history where weather_history.weatherId=? and weather_history.timestamp>=? and weather_history.timestamp <= ? order by temp asc) wiq )bq group by theYear, theMonth, theDay) b, " +
            "(select theYear, theMonth, theDay, theHour as peakHour, temp as peakTemp from (select YEAR(wiq.convertedDate) as theYear, MONTH(wiq.convertedDate) as theMonth, DAY(wiq.convertedDate) as theDay,  HOUR(wiq.convertedDate) as theHour, wiq.temp, wiq.windspeed, wiq.clouds from (select  CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', ?) as convertedDate, temp, windspeed, clouds from weather_history where weather_history.weatherId=? and weather_history.timestamp>=? and weather_history.timestamp <= ? order by temp desc) wiq )bq group by theYear, theMonth, theDay) c " +
            "where a.theYear = b.theYear  and a.theMonth = b.theMonth and a.theDay = b.theDay and a.theYear = c.theYear and a.theMonth = c.theMonth and a.theDay = c.theDay";

    static final String AVG_QUERY = " select avg(temp) as temp, avg(windspeed) as windspeed, avg(clouds) as clouds " +
            " from ( " +
            "           select dayOfWeek(convertedDate) as dow, temp, windspeed, clouds " +
            "            from ( " +
            "                       select CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', ?) as convertedDate, " +
            "                       temp, windspeed, clouds " +
            "                        from weather_history " +
            "                        where weatherId = ? and timestamp >= ? and timestamp <= ? " +
            "                  )iq " +
            "  where (HOUR(convertedDate)*60) < ? ) sq " +
            " where dow = ? group by dow ";
    final static String BILLING_CYCLE_QUERY = "select billingCycleMonth, billingCycleYear, temp, windspeed, clouds, startEpoch, " +
            " YEAR(lowTempTime) as lowYear, MONTH(lowTempTime) as lowMonth, DAY(lowTempTime) as lowDay, HOUR(lowTempTime) as lowHour, lowTemp, " +
            " YEAR(peakTempTime) as peakYear, MONTH(peakTempTime) as peakMonth, DAY(peakTempTime) as peakDay, HOUR(peakTempTime) as peakHour, peakTemp " +
            " from ( " +
            " select " +
            " bc.startEpoch , " +
            " bc.billingCycleMonth, " +
            " bc.billingCycleYear, " +
            " avg(temp) as temp, avg(windspeed) as windspeed, avg(clouds) as clouds, min(temp) as lowTemp, max(temp) as peakTemp, " +
            " (select CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', :timezone)  from weather_history where weatherId = :weatherId and timestamp >= bc.startEpoch and timestamp < bc.endEpoch order by temp asc limit 1) as lowTempTime, " +
            " (select CONVERT_TZ(FROM_UNIXTIME(timestamp),'SYSTEM', :timezone)  from weather_history where weatherId = :weatherId and timestamp >= bc.startEpoch and timestamp < bc.endEpoch order by temp asc limit 1) as peakTempTime " +
            " from history_billingCycle bc " +
            " straight_join weather_history wh on wh.weatherId = :weatherId and wh.timestamp >= bc.startEpoch and wh.timestamp < bc.endEpoch " +
            " where virtualECC_id = :virtualECCId and startEpoch >= :startEpoch and startEpoch < :endEpoch " +
            " group by billingCyclemonth, billingCycleYear ) iq " +
            " order by startEpoch";
    private RowMapper<WeatherHistory> billingCycleRowMapper = new RowMapper<WeatherHistory>() {
        public WeatherHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            WeatherHistory weatherHistory = new WeatherHistory();
            CalendarKey calendarKey = new CalendarKey(rs.getInt("billingCycleYear"), rs.getInt("billingCycleMonth"));
            CalendarKey lowTime = new CalendarKey(rs.getInt("lowYear"), rs.getInt("lowMonth") - 1, rs.getInt("lowDay"), rs.getInt("lowHour"), 0);
            CalendarKey peakTime = new CalendarKey(rs.getInt("peakYear"), rs.getInt("peakMonth") - 1, rs.getInt("peakDay"), rs.getInt("peakHour"), 0);


            weatherHistory.setCalendarKey(calendarKey);
            weatherHistory.setWindspeed(rs.getInt("windspeed"));
            weatherHistory.setClouds(rs.getInt("clouds"));
            weatherHistory.setTemp(rs.getInt("temp"));

            weatherHistory.setPeakTempTime(peakTime);
            weatherHistory.setLowTempTime(lowTime);

            weatherHistory.setLowTemperature(rs.getInt("lowTemp"));
            weatherHistory.setPeakTemperature(rs.getInt("peakTemp"));

            return weatherHistory;
        }
    };
    private RowMapper<WeatherHistory> dayRowMapper = new RowMapper<WeatherHistory>() {
        public WeatherHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            WeatherHistory weatherHistory = new WeatherHistory();
            CalendarKey calendarKey = new CalendarKey(rs.getInt("theYear"), rs.getInt("theMonth") - 1, rs.getInt("theDay"), 0, 0);
            weatherHistory.setCalendarKey(calendarKey);
            weatherHistory.setWindspeed(rs.getInt("windspeed"));
            weatherHistory.setClouds(rs.getInt("clouds"));
            weatherHistory.setTemp(rs.getInt("temp"));

            CalendarKey peakTime = calendarKey.clone();
            peakTime.setHour(rs.getInt("peakHour"));
            weatherHistory.setPeakTempTime(peakTime);

            CalendarKey lowTime = calendarKey.clone();
            lowTime.setHour(rs.getInt("lowHour"));
            weatherHistory.setLowTempTime(lowTime);

            weatherHistory.setLowTemperature(rs.getInt("lowTemp"));
            weatherHistory.setPeakTemperature(rs.getInt("peakTemp"));

            return weatherHistory;
        }
    };
    private RowMapper<WeatherHistory> hourRowMapper = new RowMapper<WeatherHistory>() {
        public WeatherHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            WeatherHistory weatherHistory = new WeatherHistory();
            weatherHistory.setCalendarKey(new CalendarKey(rs.getInt("theYear"), rs.getInt("theMonth") - 1, rs.getInt("theDay"), rs.getInt("theHour"), 0));
            weatherHistory.setTemp(rs.getInt("temp"));
            weatherHistory.setWindspeed(rs.getInt("windspeed"));
            weatherHistory.setClouds(rs.getInt("clouds"));
            return weatherHistory;
        }
    };
    private RowMapper<WeatherHistory> avgRowMapper = new RowMapper<WeatherHistory>() {
        public WeatherHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            WeatherHistory weatherHistory = new WeatherHistory();
            weatherHistory.setTemp(rs.getInt("temp"));
            weatherHistory.setWindspeed(rs.getInt("windspeed"));
            weatherHistory.setClouds(rs.getInt("clouds"));
            return weatherHistory;
        }
    };

    public List<WeatherHistory> findBillingCycleHistory(VirtualECC virtualECC, long startEpoch, long endEpoch) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("virtualECCId", virtualECC.getId());
        namedParameters.put("weatherId", virtualECC.getWeatherId());
        namedParameters.put("timezone", virtualECC.getTimezone());
        namedParameters.put("startEpoch", startEpoch);
        namedParameters.put("endEpoch", endEpoch);

        return getNamedParameterJdbcTemplate().query(BILLING_CYCLE_QUERY, namedParameters, billingCycleRowMapper);
    }

    public List<WeatherHistory> findDayHistory(VirtualECC virtualECC, long startEpoch, long endEpoch) {
        return getJdbcTemplate().query(DAY_QUERY, new Object[]{
                virtualECC.getTimezone(),
                virtualECC.getWeatherId(),
                startEpoch,
                endEpoch,
                virtualECC.getTimezone(),
                virtualECC.getWeatherId(),
                startEpoch,
                endEpoch,
                virtualECC.getTimezone(),
                virtualECC.getWeatherId(),
                startEpoch,
                endEpoch
        }, dayRowMapper);
    }

    public List<WeatherHistory> findHourHistory(VirtualECC virtualECC, long startEpoch, long endEpoch) {
        return getJdbcTemplate().query(HOUR_QUERY, new Object[]{virtualECC.getTimezone(), virtualECC.getWeatherId(), startEpoch, endEpoch}, hourRowMapper);
    }


    public List<WeatherHistory> findDayAvg(VirtualECC virtualECC, long firstAverageDay, long lastAverageDay, int dayOfWeek, int maxMinutes) {
        return getJdbcTemplate().query(AVG_QUERY, new Object[]{virtualECC.getTimezone(),
                virtualECC.getWeatherId(),
                firstAverageDay,
                lastAverageDay,
                maxMinutes,
                dayOfWeek}, avgRowMapper);
    }


    public List<WeatherHistory> findMinuteHistory(VirtualECC virtualECC, long startEpoch, long endEpoch) {
        List<WeatherHistory> hourHistoryList = findHourHistory(virtualECC, startEpoch, endEpoch);
        ArrayList<WeatherHistory> minuteHistory = new ArrayList<>();
        for (WeatherHistory hourHistory : hourHistoryList) {
            for (int i = 0; i < 60; i++) {
                CalendarKey calendarKey = hourHistory.getCalendarKey().clone();
                calendarKey.setMin(i);
                WeatherHistory weatherHistory = new WeatherHistory();
                weatherHistory.setCalendarKey(calendarKey);
                weatherHistory.setPeakTemperature(hourHistory.getPeakTemperature());
                weatherHistory.setWindspeed(hourHistory.getWindspeed());
                weatherHistory.setTemp(hourHistory.getTemp());
                minuteHistory.add(weatherHistory);
            }
        }
        return minuteHistory;
    }


    public void insert(long timestamp, long weatherId, double temp, int clouds, int windspeed) {
        try {
            getJdbcTemplate().update(CREATE_QUERY,
                    timestamp,
                    weatherId,
                    temp,
                    windspeed,
                    clouds);
        } catch (Exception ex) {
            LOGGER.error("Error inserting weather: {}", ex);
        }
    }


}



