/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.DemandChargeTOU;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the DemandChargeTOU object
 */
@Repository
public class DemandChargeTOUDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.demandChargeTOU (rate, peakType, season_id, energyPlan_id) values (?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.demandChargeTOU set rate=? where peakType=? and season_id=? and energyPlan_id=?";
    public static final String SELECT_BY_ENERGY_PLAN = "select rate, peakType, season_id, energyPlan_id from commander.demandChargeTOU where energyPlan_id=? order by peakType";

    public static final String SELECT_BY_ID = "select rate, peakType, season_id, energyPlan_id from commander.demandChargeTOU where peakType=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_ID = "delete from commander.demandChargeTOU where peakType=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_PLAN = "delete from commander.demandChargeTOU where energyPlan_id=?";
    public static final String DELETE_BY_SEASON = "delete from commander.demandChargeTOU where energyPlan_id=? and season_id=?";
    public static final String DELETE_BY_TOU = "delete from commander.demandChargeTOU where energyPlan_id=? and peakType=?";


    private RowMapper<DemandChargeTOU> rowMapper = new RowMapper<DemandChargeTOU>() {
        public DemandChargeTOU mapRow(ResultSet rs, int rowNum) throws SQLException {
            DemandChargeTOU dto = new DemandChargeTOU();
            dto.setPeakType(TOUPeakType.values()[rs.getInt("peakType")]);
            dto.setSeasonId(rs.getInt("season_id"));
            dto.setEnergyPlanId(rs.getLong("energyPlan_id"));
            dto.setRate(rs.getDouble("rate"));
            return dto;
        }
    };


    public DemandChargeTOU update(final DemandChargeTOU demandChargeTOU) {
        if (null == findById(demandChargeTOU.getPeakType(), demandChargeTOU.getSeasonId(), demandChargeTOU.getEnergyPlanId())) {
            LOGGER.debug("CREATING NEW DEMAND CHARGE TOU: {}", demandChargeTOU);
            getJdbcTemplate().update(CREATE_QUERY,
                    demandChargeTOU.getRate(),
                    demandChargeTOU.getPeakType().ordinal(),
                    demandChargeTOU.getSeasonId(),
                    demandChargeTOU.getEnergyPlanId());

        } else {
            LOGGER.debug("UPDATING ADDITIONAL DEMAND CHARGE TOU: {}", demandChargeTOU);
            getJdbcTemplate().update(UPDATE_QUERY,
                    demandChargeTOU.getRate(),
                    demandChargeTOU.getPeakType().ordinal(),
                    demandChargeTOU.getSeasonId(),
                    demandChargeTOU.getEnergyPlanId());
        }

        return demandChargeTOU;
    }

    public List<DemandChargeTOU> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }

    public DemandChargeTOU findById(TOUPeakType peakType, int seasonId, long energyPlanId) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{peakType.ordinal(), seasonId, energyPlanId}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void deleteById(TOUPeakType peakType, int seasonId, long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{peakType.ordinal(), seasonId, energyPlanId});
    }

    public void deleteByPlan(Long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_PLAN, new Object[]{energyPlanId});
    }

    public void deleteBySeason(Long energyPlanId, Integer seasonId) {
        getJdbcTemplate().update(DELETE_BY_SEASON, new Object[]{energyPlanId, seasonId});
    }

    public void deleteByTOULevel(Long energyPlanId, Integer touLevelOrdinal) {
        getJdbcTemplate().update(DELETE_BY_TOU, new Object[]{energyPlanId, touLevelOrdinal});
    }
}
