/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.server.model.WeatherKey;
import com.ted.commander.server.model.WeatherKeyCoord;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the WeatherKey object
 */
@Repository
public class WeatherKeyDAO extends SimpleAbstractDAO {


    Cache<Long, WeatherKey> weatherKeyCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<Long, WeatherKey> weatherKeyCityCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<WeatherKeyCoord, WeatherKey> weatherKeyCoordCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();

    public void clearCache(){
        weatherKeyCache.invalidateAll();
        weatherKeyCityCache.invalidateAll();
        weatherKeyCoordCache.invalidateAll();
    }

    public void updateCache(WeatherKey weatherKey){
        weatherKeyCache.put(weatherKey.getId(), weatherKey);
        weatherKeyCityCache.put(weatherKey.getCityId(), weatherKey);
        weatherKeyCoordCache.put(new WeatherKeyCoord(weatherKey.getLat(), weatherKey.getLon()), weatherKey);
    }


    public static final String QUERY_ID = "select id, cityId, lat, lon, metric from commander.weatherKey where id=?";
    public static final String QUERY_CITY_ID = "select id, cityId, lat, lon, metric from commander.weatherKey where cityId=?";
    public static final String QUERY_COORD = "select id, cityId, lat, lon, metric from commander.weatherKey where lat=? and lon=?";
    public static final String INSERT = "insert into commander.weatherKey (cityId, lat, lon, metric) values (?,?,?,?)";
    public static final String UPDATE = "update commander.weatherKey set cityId=?, lat=?, lon=?, metric=? where id=?";

    public static final String QUERY_QUEUE = "select id, cityId, lat, lon, metric from commander.weatherQueue limit 30";


    private RowMapper<WeatherKey> rowMapper = new RowMapper<WeatherKey>() {
        public WeatherKey mapRow(ResultSet rs, int rowNum) throws SQLException {
            WeatherKey weatherKey = new WeatherKey();
            weatherKey.setId(rs.getLong("id"));
            weatherKey.setLat(rs.getBigDecimal("lat"));
            weatherKey.setLon(rs.getBigDecimal("lon"));
            weatherKey.setMetric(rs.getBoolean("metric"));
            return weatherKey;
        }
    };

    public WeatherKey update(final WeatherKey weatherKey) {
        clearCache();
        if (weatherKey.getId() == 0) {
            LOGGER.debug("CREATING NEW WeatherKey: {}", weatherKey);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, weatherKey.getCityId());
                    ps.setBigDecimal(2, weatherKey.getLat());
                    ps.setBigDecimal(3, weatherKey.getLon());
                    ps.setBoolean(4, weatherKey.isMetric());
                    return ps;
                }
            }, keyHolder);

            weatherKey.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING WeatherKey: {}", weatherKey);
            getJdbcTemplate().update(UPDATE,
                    weatherKey.getCityId(),
                    weatherKey.getLat(),
                    weatherKey.getLon(),
                    weatherKey.isMetric(),
                    weatherKey.getId());
        }

        return weatherKey;
    }


    public WeatherKey findById(long id) {
        WeatherKey weatherKey = weatherKeyCache.getIfPresent(id);
        if (weatherKey == null) {
            try {
                weatherKey = getJdbcTemplate().queryForObject(QUERY_ID, new Object[]{id}, rowMapper);
                updateCache(weatherKey);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return weatherKey;
    }

    public WeatherKey findByCityId(long id) {
        WeatherKey weatherKey = weatherKeyCityCache.getIfPresent(id);
        if (weatherKey == null) {
            try {
                weatherKey = getJdbcTemplate().queryForObject(QUERY_CITY_ID, new Object[]{id}, rowMapper);
                updateCache(weatherKey);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return weatherKey;
    }

    public WeatherKey findByCoord(BigDecimal lat, BigDecimal lon) {
        WeatherKeyCoord weatherKeyCoord = new WeatherKeyCoord(lat, lon);

        WeatherKey weatherKey = weatherKeyCoordCache.getIfPresent(weatherKeyCoord);

        if (weatherKey == null) {
            LOGGER.debug("findByCoord: {} {}", lat, lon);
            try {
                weatherKey = getJdbcTemplate().queryForObject(QUERY_COORD, new BigDecimal[]{lat, lon}, rowMapper);
                updateCache(weatherKey);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return weatherKey;
    }

    public List<WeatherKey> findInQueue() {
        return getJdbcTemplate().query(QUERY_QUEUE, rowMapper);
    }
}



