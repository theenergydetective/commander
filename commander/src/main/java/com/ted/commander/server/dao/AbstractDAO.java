/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Common abstract class for all Aggredata DAO's. Contains common methods and utilities.
 *
 * @param <T>
 */
@Repository
public abstract class AbstractDAO<T> {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource commanderDataSource;

    private String tableName;
    private JdbcTemplate jdbcTemplate = null;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
    Cache<Long, T> objectCache;

    boolean useIdCache =false;

    public AbstractDAO(String tableName) {
        this.tableName = tableName;
        objectCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(60, TimeUnit.MINUTES).maximumSize(10000).build();
    }

    public AbstractDAO(String tableName, boolean useIdCache) {
        this.tableName = tableName;
        this.useIdCache = useIdCache;
        objectCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.MINUTES).maximumSize(10000).build();
    }

    public void clearCache(){
        objectCache.invalidateAll();
    }


    /**
     * Returns the rowmapper for the DAO type
     *
     * @return
     */
    public abstract RowMapper<T> getRowMapper();




    public T findById(Long id) {
        T obj = null;
        if (useIdCache) obj = objectCache.getIfPresent(id);
        if (obj == null) {

            try {
                String query = "select * from " + tableName + " where id= ?";
                obj = getJdbcTemplate().queryForObject(query, new Object[]{id}, getRowMapper());
                objectCache.put(id, obj);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }

        return obj;
    }

    public void deleteById(Long id) {
        String query = "delete from " + tableName + " where id= ?";
        getJdbcTemplate().update(query, new Object[]{id});
        if (useIdCache) objectCache.invalidate(id);
    }


    protected JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(commanderDataSource);
        }
        return jdbcTemplate;
    }


    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(commanderDataSource);
        }
        return namedParameterJdbcTemplate;
    }


}
