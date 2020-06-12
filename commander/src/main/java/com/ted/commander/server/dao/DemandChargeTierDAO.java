/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.DemandChargeTier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the DemandChargeTier object
 */
@Repository
public class DemandChargeTierDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.demandChargeTier (peak, rate, id, season_id, energyPlan_id) values (?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.demandChargeTier set peak=?, rate=? where id = ? and season_id=? and energyPlan_id=?";
    public static final String SELECT_BY_ENERGY_PLAN = "select id, season_id,energyPlan_id, peak, rate from commander.demandChargeTier where energyPlan_id=? order by id";

    public static final String SELECT_BY_ID = "select id, season_id, energyPlan_id, peak, rate  from commander.demandChargeTier where id=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_ID = "delete from commander.demandChargeTier where id=? and season_id=? and energyPlan_id=?";
    public static final String DELETE_BY_PLAN = "delete from commander.demandChargeTier where energyPlan_id=?";
    public static final String DELETE_BY_SEASON = "delete from commander.demandChargeTier where energyPlan_id=? and season_id=?";

    private RowMapper<DemandChargeTier> rowMapper = new RowMapper<DemandChargeTier>() {
        public DemandChargeTier mapRow(ResultSet rs, int rowNum) throws SQLException {
            DemandChargeTier dto = new DemandChargeTier();
            dto.setId(rs.getInt("id"));
            dto.setSeasonId(rs.getInt("season_id"));
            dto.setEnergyPlanId(rs.getLong("energyPlan_id"));
            dto.setPeak(rs.getDouble("peak"));
            dto.setRate(rs.getDouble("rate"));
            return dto;
        }
    };


    public DemandChargeTier update(final DemandChargeTier demandChargeTier) {
        if (null == findById(demandChargeTier.getId(), demandChargeTier.getSeasonId(), demandChargeTier.getEnergyPlanId())) {
            LOGGER.debug("CREATING NEW DEMAND CHARGE TIER: {}", demandChargeTier);
            getJdbcTemplate().update(CREATE_QUERY,
                    demandChargeTier.getPeak(),
                    demandChargeTier.getRate(), demandChargeTier.getId(),
                    demandChargeTier.getSeasonId(),
                    demandChargeTier.getEnergyPlanId());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL DEMAND CHARGE TER: {}", demandChargeTier);
            getJdbcTemplate().update(UPDATE_QUERY,
                    demandChargeTier.getPeak(),
                    demandChargeTier.getRate(), demandChargeTier.getId(),
                    demandChargeTier.getSeasonId(),
                    demandChargeTier.getEnergyPlanId());
        }

        return demandChargeTier;
    }

    public List<DemandChargeTier> findByEnergyPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_ENERGY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }


    public DemandChargeTier findById(int id, int seasonId, long energyPlanId) {
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
