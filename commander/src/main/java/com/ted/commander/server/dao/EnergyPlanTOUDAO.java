/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlanTOU;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the EnergyPlanTOU object
 */
@Repository
public class EnergyPlanTOUDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.energyPlanTOU (touStartHour, touStartMinute,touEndHour, touEndMinute,season_id, energyPlan_id, peakType, isAM ) values (?,?,?,?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.energyPlanTOU set touStartHour=?, touStartMinute=?,touEndHour=?, touEndMinute=? where season_id=? and energyPlan_id=? and peakType=? and isAM=?";
    public static final String SELECT_BY_ENERGY_PLAN = "select  isAM, season_id, energyPlan_id, peakType, touStartHour, touStartMinute,touEndHour, touEndMinute from commander.energyPlanTOU where energyPlan_id=?";
    public static final String SELECT_BY_ID = "select  isAM, season_id, energyPlan_id, peakType, touStartHour, touStartMinute,touEndHour, touEndMinute from commander.energyPlanTOU where season_id=? and energyPlan_id=? and peakType=? and isAM=?";
    public static final String DELETE_BY_ID = "delete from commander.energyPlanTOU where season_id=? and energyPlan_id=? and peakType=? and isAM=?";
    public static final String DELETE_BY_PLAN = "delete from commander.energyPlanTOU where energyPlan_id=?";
    public static final String DELETE_BY_SEASON = "delete from commander.energyPlanTOU where energyPlan_id=? and season_id=?";
    public static final String DELETE_BY_TOU_LEVEL = "delete from commander.energyPlanTOU where energyPlan_id=? and peakType=?";

    private RowMapper<EnergyPlanTOU> rowMapper = new RowMapper<EnergyPlanTOU>() {
        public EnergyPlanTOU mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyPlanTOU energyPlanTOU = new EnergyPlanTOU();
            energyPlanTOU.setSeasonId(rs.getInt("season_id"));
            energyPlanTOU.setEnergyPlanId(rs.getLong("energyPlan_id"));
            energyPlanTOU.setPeakType(TOUPeakType.values()[rs.getInt("peakType")]);
            energyPlanTOU.setTouStartHour(rs.getInt("touStartHour"));
            energyPlanTOU.setTouStartMinute(rs.getInt("touStartMinute"));
            energyPlanTOU.setTouEndHour(rs.getInt("touEndHour"));
            energyPlanTOU.setTouEndMinute(rs.getInt("touEndMinute"));
            energyPlanTOU.setMorningTou(rs.getBoolean("isAM"));
            return energyPlanTOU;
        }
    };


    public EnergyPlanTOU update(final EnergyPlanTOU energyPlanTOU) {
        LOGGER.debug("Updating {}", energyPlanTOU);
        if (null == findById(energyPlanTOU.getSeasonId(), energyPlanTOU.getEnergyPlanId(), energyPlanTOU.getPeakType(), energyPlanTOU.isMorningTou())) {
            LOGGER.debug("CREATING NEW EnergyPlanTOU: {}", energyPlanTOU);
            getJdbcTemplate().update(CREATE_QUERY,
                    energyPlanTOU.getTouStartHour(),
                    energyPlanTOU.getTouStartMinute(),
                    energyPlanTOU.getTouEndHour(),
                    energyPlanTOU.getTouEndMinute(),
                    energyPlanTOU.getSeasonId(),
                    energyPlanTOU.getEnergyPlanId(),
                    energyPlanTOU.getPeakType().ordinal(),
                    energyPlanTOU.isMorningTou()
            );
        } else {
            LOGGER.debug("UPDATING EnergyPlanTOU: {}", energyPlanTOU);
            getJdbcTemplate().update(UPDATE_QUERY,
                    energyPlanTOU.getTouStartHour(),
                    energyPlanTOU.getTouStartMinute(),
                    energyPlanTOU.getTouEndHour(),
                    energyPlanTOU.getTouEndMinute(),
                    energyPlanTOU.getSeasonId(),
                    energyPlanTOU.getEnergyPlanId(),
                    energyPlanTOU.getPeakType().ordinal(),
                    energyPlanTOU.isMorningTou()

            );
        }
        return energyPlanTOU;
    }


    public List<EnergyPlanTOU> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }

    public EnergyPlanTOU findById(int seasonId, long energyPlanId, TOUPeakType peakType, Boolean isAM) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{seasonId, energyPlanId, peakType.ordinal(), isAM}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void deleteById(int seasonId, long energyPlanId, TOUPeakType peakType, Boolean isAM) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{seasonId, energyPlanId, peakType.ordinal(), isAM});
    }

    public void deleteByPlan(Long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_PLAN, new Object[]{energyPlanId});
    }

    public void deleteBySeason(Long energyPlanId, Integer seasonId) {

        getJdbcTemplate().update(DELETE_BY_SEASON, new Object[]{energyPlanId, seasonId});
    }

    public void deleteByTOULevel(Long energyPlanId, Integer touLevelOrdinal) {
        getJdbcTemplate().update(DELETE_BY_TOU_LEVEL, new Object[]{energyPlanId, touLevelOrdinal});

    }
}
