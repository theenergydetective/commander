/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * Simple DAO
 */
@Repository
public abstract class SimpleAbstractDAO {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource commanderDataSource;

    private JdbcTemplate jdbcTemplate = null;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

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


    protected double forceZero(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) return 0.0;
        return d;
    }



}
