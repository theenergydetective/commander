/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.VirtualECCRole;
import com.ted.commander.common.model.VirtualECCPermission;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * DAO for accessing the Group object
 */
@Repository
public class VirtualECCPermissionDAO extends AbstractDAO<VirtualECCPermission> {

    public static final String CREATE_QUERY = "insert into commander.virtualecc_permission(virtualecc_id, user_id, role) values (?,?,?)";
    public static final String UPDATE_QUERY = "update commander.virtualecc_permission set role=? where id = ?";
    public static final String SELECT_BY_USER = "select id, virtualecc_id, user_id,  role from commander.virtualecc_permission where user_id=?";
    public static final String SELECT_BY_GROUP = "select id, virtualecc_id, user_id,  role from commander.virtualecc_permission where virtualecc_id=?";

    private RowMapper<VirtualECCPermission> rowMapper = new RowMapper<VirtualECCPermission>() {
        public VirtualECCPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
            VirtualECCPermission dto = new VirtualECCPermission();
            dto.setId(rs.getLong("id"));
            dto.setVirtualECCId(rs.getLong("virtualecc_id"));
            dto.setUserId(rs.getLong("user_id"));
            dto.setRole(VirtualECCRole.values()[rs.getInt("role")]);
            return dto;
        }
    };


    public VirtualECCPermissionDAO() {
        super("commander.virtualecc_permission");
    }

    @Override
    public RowMapper<VirtualECCPermission> getRowMapper() {
        return rowMapper;
    }

    public VirtualECCPermission update(final VirtualECCPermission gp) {
        if (gp.getId() == null || gp.getId() == 0) {
            LOGGER.info("CREATING NEW VIRTUALECC: {}", gp);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, gp.getVirtualECCId());
                    ps.setLong(2, gp.getUserId());
                    ps.setInt(3, gp.getRole().ordinal());
                    return ps;
                }
            }, keyHolder);

            gp.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL VIRTUALECC: {}", gp);
            getJdbcTemplate().update(UPDATE_QUERY,
                    gp.getRole().ordinal(),
                    gp.getId());
        }

        return gp;
    }

    public List<VirtualECCPermission> findByUser(Long userId) {
        return getJdbcTemplate().query(SELECT_BY_USER, new Object[]{userId}, getRowMapper());
    }

    public List<VirtualECCPermission> findByVirtualECC(Long virtualECCId) {
        return getJdbcTemplate().query(SELECT_BY_GROUP, new Object[]{virtualECCId}, getRowMapper());
    }

}
