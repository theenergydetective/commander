/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.EnergyPlanTier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the EnergyPlanTier object
 */
@Repository
public class EnergyPlanTierDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.energyPlanTier (id, season_id,energyPlan_id, kwh) values (?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.energyPlanTier set kwh=? where id = ? and season_id=? and energyPlan_id=?";
    public static final String SELECT_BY_ENERGY_PLAN = "select id, season_id,energyPlan_id, kwh from commander.energyPlanTier where energyPlan_id=? order by id";
    public static final String SELECT_BY_ID = "select id, season_id,energyPlan_id, kwh from commander.energyPlanTier where id=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_ID = "delete from commander.energyPlanTier where id=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_PLAN = "delete from commander.energyPlanTier where energyPlan_id=?";
    public static final String DELETE_BY_SEASON = "delete from commander.energyPlanTier where energyPlan_id=? and season_id=?";

    private RowMapper<EnergyPlanTier> rowMapper = new RowMapper<EnergyPlanTier>() {
        public EnergyPlanTier mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyPlanTier dto = new EnergyPlanTier();
            dto.setId(rs.getInt("id"));
            dto.setSeasonId(rs.getInt("season_id"));
            dto.setEnergyPlanId(rs.getLong("energyPlan_id"));
            dto.setKwh(rs.getLong("kwh"));
            return dto;
        }
    };


    public EnergyPlanTier update(final EnergyPlanTier energyPlanTier) {
        if (null == findById(energyPlanTier.getId(), energyPlanTier.getSeasonId(), energyPlanTier.getEnergyPlanId())) {
            LOGGER.debug("CREATING NEW EnergyPlanTier: {}", energyPlanTier);
            getJdbcTemplate().update(CREATE_QUERY,
                    energyPlanTier.getId(),
                    energyPlanTier.getSeasonId(),
                    energyPlanTier.getEnergyPlanId(),
                    energyPlanTier.getKwh());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL EnergyPlanTier: {}", energyPlanTier);
            getJdbcTemplate().update(UPDATE_QUERY,
                    energyPlanTier.getKwh(),
                    energyPlanTier.getId(),
                    energyPlanTier.getSeasonId(),
                    energyPlanTier.getEnergyPlanId());
        }

        return energyPlanTier;
    }

    public List<EnergyPlanTier> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }

    public EnergyPlanTier findById(int id, int seasonId, long energyPlanId) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{id, seasonId, energyPlanId}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void deleteById(int id, int seasonId, long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{id, seasonId, energyPlanId});
    }


    public void deleteByPlan(Long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_PLAN, new Object[]{energyPlanId});
    }

    public void deleteBySeason(Long energyPlanId, Integer seasonId) {
        getJdbcTemplate().update(DELETE_BY_SEASON, new Object[]{energyPlanId, seasonId});
    }


}
