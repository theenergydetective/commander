/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.AdditionalChargeType;
import com.ted.commander.common.model.AdditionalCharge;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the AdditionalCharge object
 */
@Repository
public class AdditionalChargeDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.additionalCharge (rate, additionalChargeType,season_id,energyPlan_id) values (?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.additionalCharge set  rate=? where additionalChargeType=? and season_id=? and energyPlan_id=?";
    public static final String SELECT_BY_ID = "select additionalChargeType,season_id,energyPlan_id,rate from commander.additionalCharge where additionalChargeType=? and season_id=? and energyPlan_id=?";
    public static final String SELECT_BY_ENERGY_PLAN = "select  additionalChargeType,season_id,energyPlan_id, rate from commander.additionalCharge where energyPlan_id=?";
    public static final String SELECT_BY_ENERGY_PLAN_SEASON = "select  additionalChargeType,season_id,energyPlan_id, rate from commander.additionalCharge where season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_ID = "delete from commander.additionalCharge where additionalChargeType=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_PLAN = "delete from commander.additionalCharge where energyPlan_id=?";
    public static final String DELETE_BY_SEASON = "delete from commander.additionalCharge where energyPlan_id=? and season_id=?";

    private RowMapper<AdditionalCharge> rowMapper = new RowMapper<AdditionalCharge>() {
        public AdditionalCharge mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdditionalCharge additionalCharge = new AdditionalCharge();
            additionalCharge.setAdditionalChargeType(AdditionalChargeType.values()[rs.getInt("additionalChargeType")]);
            additionalCharge.setSeasonId(rs.getInt("season_id"));
            additionalCharge.setEnergyPlanId(rs.getLong("energyPlan_id"));
            additionalCharge.setRate(rs.getDouble("rate"));
            return additionalCharge;
        }
    };


    public AdditionalCharge update(final AdditionalCharge additionalCharge) {
        if (null == findById(additionalCharge.getAdditionalChargeType(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId())) {
            LOGGER.info("CREATING NEW ADDITIONAL CHARGE: {}", additionalCharge);
            getJdbcTemplate().update(CREATE_QUERY, additionalCharge.getRate(), additionalCharge.getAdditionalChargeType().ordinal(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL CHARGE: {}", additionalCharge);
            getJdbcTemplate().update(UPDATE_QUERY, additionalCharge.getRate(), additionalCharge.getAdditionalChargeType().ordinal(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId());
        }

        return additionalCharge;
    }

    public AdditionalCharge findById(AdditionalChargeType additionalChargeType, Integer energyPlanSeasonId, Long energyPlanSeasonEnergyPlanId) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{additionalChargeType.ordinal(), energyPlanSeasonId, energyPlanSeasonEnergyPlanId}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void deleteById(AdditionalChargeType additionalChargeType, Integer energyPlanSeasonId, Long energyPlanSeasonEnergyPlanId) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{additionalChargeType.ordinal(), energyPlanSeasonId, energyPlanSeasonEnergyPlanId});
    }


    public List<AdditionalCharge> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }

    public List<AdditionalCharge> findBySeasonAndEnergyPlan(Integer seasonId, Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN_SEASON, new Object[]{seasonId, energyPlanId}, rowMapper);
    }

    public void deleteByPlan(Long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_PLAN, new Object[]{energyPlanId});
    }

    public void deleteBySeason(Long energyPlanId, Integer seasonId) {
        getJdbcTemplate().update(DELETE_BY_SEASON, new Object[]{energyPlanId, seasonId});
    }
}
