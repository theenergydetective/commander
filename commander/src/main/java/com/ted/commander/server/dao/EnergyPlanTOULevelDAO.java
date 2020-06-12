/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlanTOULevel;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the EnergyPlanTOULevel object
 */
@Repository
public class EnergyPlanTOULevelDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.touLevel (energyPlan_id, touLevelName, peakType) values (?,?,?)";
    public static final String UPDATE_QUERY = "update commander.touLevel set touLevelName=? where energyPlan_id = ? and peakType=?";
    public static final String SELECT_BY_ENERGYPLAN = "select energyPlan_id, touLevelName, peakType from commander.touLevel where energyPlan_id=? order by peakType";
    public static final String SELECT_BY_ID = "select energyPlan_id, touLevelName, peakType from commander.touLevel where energyPlan_id=? and peakType=?";
    public static final String DELETE_BY_ID = "delete from commander.touLevel where energyPlan_id=? and peakType=?";
    public static final String DELETE_BY_PLAN = "delete from commander.touLevel where energyPlan_id=?";

    private RowMapper<EnergyPlanTOULevel> rowMapper = new RowMapper<EnergyPlanTOULevel>() {
        public EnergyPlanTOULevel mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyPlanTOULevel touLevel = new EnergyPlanTOULevel();
            touLevel.setEnergyPlanId(rs.getLong("energyPlan_id"));
            touLevel.setTouLevelName(rs.getString("touLevelName"));
            touLevel.setPeakType(TOUPeakType.values()[rs.getInt("peakType")]);
            return touLevel;
        }
    };


    public EnergyPlanTOULevel update(final EnergyPlanTOULevel touLevel) {
        if (findById(touLevel.getEnergyPlanId(), touLevel.getPeakType()) == null) {
            LOGGER.debug("CREATING NEW EnergyPlanTOULevel: {}", touLevel);
            getJdbcTemplate().update(CREATE_QUERY,
                    touLevel.getEnergyPlanId(),
                    touLevel.getTouLevelName(),
                    touLevel.getPeakType().ordinal());
        } else {
            LOGGER.debug("UPDATING EnergyPlanTOULevel: {}", touLevel);
            getJdbcTemplate().update(UPDATE_QUERY,
                    touLevel.getTouLevelName(),
                    touLevel.getEnergyPlanId(),
                    touLevel.getPeakType().ordinal());
        }
        return touLevel;
    }

    public EnergyPlanTOULevel findById(Long energyPlanId, TOUPeakType peakType) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{energyPlanId, peakType.ordinal()}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public List<EnergyPlanTOULevel> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGYPLAN, new Object[]{energyPlanId}, rowMapper);
    }

    public void deleteById(Long energyPlanId, TOUPeakType offPeak) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{energyPlanId, offPeak.ordinal()});
    }

    public void deleteByPlan(Long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_PLAN, new Object[]{energyPlanId});
    }


}
