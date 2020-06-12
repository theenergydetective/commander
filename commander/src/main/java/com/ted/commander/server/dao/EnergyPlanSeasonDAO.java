/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.EnergyPlanSeason;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Set;

/**
 * DAO for accessing the EnergyPlanSeason object
 */
@Repository
public class EnergyPlanSeasonDAO extends SimpleAbstractDAO {


    public static final String CREATE_QUERY = "insert into commander.energyPlanSeason (id, energyPlan_id, seasonName, seasonMonth, seasonDayOfMonth) values (?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.energyPlanSeason set seasonName=?, seasonMonth=?, seasonDayOfMonth=? where id = ? and energyPlan_id=?";
    public static final String SELECT_BY_PLAN = "select id, energyPlan_id, seasonName, seasonMonth, seasonDayOfMonth from commander.energyPlanSeason where energyPlan_id=? order by seasonMonth, seasonDayOfMonth";
    public static final String SELECT_BY_ID = "select id, energyPlan_id, seasonName, seasonMonth, seasonDayOfMonth from commander.energyPlanSeason where id=? and energyPlan_id=?";
    public static final String DELETE_BY_PLAN = "delete from commander.energyPlanSeason where energyPlan_id=?";
    public static final String DELETE_BY_ID = "delete from commander.energyPlanSeason where energyPlan_id=? and id=?";
    public static final String DELETE_ORPHANS = "delete from commander.energyPlanSeason where energyPlan_id=? and id not in (:ids)";

    private RowMapper<EnergyPlanSeason> rowMapper = new RowMapper<EnergyPlanSeason>() {
        public EnergyPlanSeason mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyPlanSeason energyPlanSeason = new EnergyPlanSeason();
            energyPlanSeason.setId(rs.getInt("id"));
            energyPlanSeason.setSeasonName(rs.getString("seasonName"));
            energyPlanSeason.setSeasonMonth(rs.getInt("seasonMonth"));
            energyPlanSeason.setSeasonDayOfMonth(rs.getInt("seasonDayOfMonth"));
            return energyPlanSeason;
        }
    };


    public EnergyPlanSeason update(final long energyPlanId, final EnergyPlanSeason energyPlan) {

        if (findById(energyPlan.getId(), energyPlanId) == null) {
            LOGGER.debug("CREATING NEW EnergyPlanSeason: {}", energyPlan);

            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.NO_GENERATED_KEYS);
                    ps.setLong(1, energyPlan.getId());
                    ps.setLong(2, energyPlanId);
                    ps.setString(3, energyPlan.getSeasonName());
                    ps.setInt(4, energyPlan.getSeasonMonth());
                    ps.setInt(5, energyPlan.getSeasonDayOfMonth());
                    return ps;
                }
            });


        } else {
            LOGGER.debug("UPDATING EnergyPlanSeason: {}", energyPlan);
            getJdbcTemplate().update(UPDATE_QUERY,
                    energyPlan.getSeasonName(),
                    energyPlan.getSeasonMonth(),
                    energyPlan.getSeasonDayOfMonth(),
                    energyPlan.getId(),
                    energyPlanId);
        }
        return energyPlan;
    }

    public List<EnergyPlanSeason> findByPlan(Long energyPlanId) {
        return getJdbcTemplate().query(SELECT_BY_PLAN, new Object[]{energyPlanId}, rowMapper);
    }


    public void deleteByPlan(Long energyPlanId) {
        getJdbcTemplate().update(DELETE_BY_PLAN, new Object[]{energyPlanId});
    }

    public void deleteById(Long energyPlanId, int seasonId) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{energyPlanId, seasonId});
    }

    public void deleteOrphans(Long energyPlanId, Set<Integer> ids) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{energyPlanId}, parameters);
    }

    public EnergyPlanSeason findById(Integer id, Long energyPlanId) {
        try {


            return getJdbcTemplate().queryForObject(SELECT_BY_ID, new Object[]{id, energyPlanId}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

}
