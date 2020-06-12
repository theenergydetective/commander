/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyRate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the EnergyRate object
 */
@Repository
public class EnergyRateDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.energyRate (rate,season_id,energyPlan_id, peakType, tier) values (?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.energyRate set rate=? where season_id=? and energyPlan_id=? and peakType=? and tier=?";
    public static final String SELECT_BY_ENERGY_PLAN = "select season_id, energyPlan_id, peakType,tier , rate from commander.energyRate where energyPlan_id=? order by tier, peakType";
    public static final String SELECT_BY_ID = "select season_id, energyPlan_id, peakType,tier , rate from commander.energyRate where season_id=? and energyPlan_id=? and peakType=? and tier=?";
    public static final String DELETE_BY_ID = "delete from commander.energyRate where season_id=? and energyPlan_id=? and peakType=? and tier=?";
    public static final String DELETE_BY_PLAN = "delete from commander.energyRate where energyPlan_id=?";
    public static final String DELETE_BY_SEASON = "delete from commander.energyRate where energyPlan_id=? and season_id=?";
    public static final String DELETE_BY_TOU = "delete from commander.energyRate where energyPlan_id=? and peakType=?";
    public static final String DELETE_BY_TIER = "delete from commander.energyRate where energyPlan_id=? and tier=?";

    private RowMapper<EnergyRate> rowMapper = new RowMapper<EnergyRate>() {
        public EnergyRate mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyRate dto = new EnergyRate();
            dto.setSeasonId(rs.getInt("season_id"));
            dto.setEnergyPlanId(rs.getLong("energyPlan_id"));
            dto.setPeakType(TOUPeakType.values()[rs.getInt("peakType")]);
            dto.setTierId(rs.getInt("tier"));
            dto.setRate(rs.getDouble("rate"));
            return dto;
        }
    };


    public EnergyRate update(final EnergyRate energyRate) {

        if (null == findById(energyRate.getSeasonId(), energyRate.getEnergyPlanId(), energyRate.getPeakType(), energyRate.getTierId())) {
            LOGGER.error("CREATING NEW EnergyRate: {}", energyRate);
            getJdbcTemplate().update(CREATE_QUERY,
                    energyRate.getRate(),
                    energyRate.getSeasonId(),
                    energyRate.getEnergyPlanId(),
                    energyRate.getPeakType().ordinal(),
                    energyRate.getTierId());

        } else {
            LOGGER.error("UPDATING ADDITIONAL EnergyRate: {}", energyRate);
            getJdbcTemplate().update(UPDATE_QUERY,
                    energyRate.getRate(),
                    energyRate.getSeasonId(),
                    energyRate.getEnergyPlanId(),
                    energyRate.getPeakType().ordinal(),
                    energyRate.getTierId());

        }

        return energyRate;
    }

    public List<EnergyRate> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }


    public EnergyRate findById(int seasonId, long energyPlanId, TOUPeakType peakType, int tier) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{seasonId, energyPlanId, peakType.ordinal(), tier}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void deleteById(int seasonId, long energyPlanId, TOUPeakType peakType, int tier) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{seasonId, energyPlanId, peakType.ordinal(), tier});
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

    public void deleteByTier(Long energyPlanId, Integer tierId) {
        getJdbcTemplate().update(DELETE_BY_TIER, new Object[]{energyPlanId, tierId});
    }
}
