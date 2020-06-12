/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.EnergyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service for handling Alexa commands
 */
@Service
public class BigNumberDAO extends SimpleAbstractDAO {

    static final Logger LOGGER = LoggerFactory.getLogger(BigNumberDAO.class);
    static final String GET_BIG_NUMBERS = "select * " +
            " from energydata " +
            " where account_id = :account_id " +
            " and mtu_id = :mtu_id " +
            " and timestamp >= :start and timestamp <= :stop " +
            " order by energydifference desc limit 100";

    static final String UPDATE_RECORD = "update energydata set" +
            " energy = :energy," +
            " energyDifference = :energyDifference," +
            " pf = :pf," +
            " voltage = :voltage," +
            " avg5 = :avg5," +
            " avg10 = :avg10," +
            " avg15 = :avg15," +
            " avg20 = :avg20," +
            " avg30 = :avg30 " +
            " where  account_id = :account_id and mtu_id = :mtu_id and timestamp = :timestamp";

    public void update(EnergyData ed){
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("mtu_id", ed.getMtuId());
        parameterSource.addValue("account_id", ed.getAccountId());
        parameterSource.addValue("timestamp", ed.getTimeStamp());
        parameterSource.addValue("energy", ed.getEnergy());
        parameterSource.addValue("energyDifference", ed.getEnergyDifference());
        parameterSource.addValue("pf", ed.getPowerFactor());
        parameterSource.addValue("voltage", ed.getVoltage());
        parameterSource.addValue("avg5", ed.getAvg5());
        parameterSource.addValue("avg10", ed.getAvg10());
        parameterSource.addValue("avg15", ed.getAvg15());
        parameterSource.addValue("avg20", ed.getAvg20());
        parameterSource.addValue("avg30", ed.getAvg30());
        getNamedParameterJdbcTemplate().update(UPDATE_RECORD, parameterSource);
    }

    public List<EnergyData> findBigNumber(long accountId, long mtuId, long start, long stop) {
        try{
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("mtu_id", mtuId);
            parameterSource.addValue("account_id", accountId);
            parameterSource.addValue("start", start);
            parameterSource.addValue("stop", stop);

            return getNamedParameterJdbcTemplate().query(GET_BIG_NUMBERS, parameterSource, EnergyDataDAO.rowMapper);

        } catch (EmptyResultDataAccessException ex){
            LOGGER.warn("NO ENERGY DATA FOUND");
            return null;
        }

    }


}
