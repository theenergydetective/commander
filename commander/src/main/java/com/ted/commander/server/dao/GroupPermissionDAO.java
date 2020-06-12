/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.GroupRole;
import com.ted.commander.common.model.GroupPermission;
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
public class GroupPermissionDAO extends AbstractDAO<GroupPermission> {

    public static final String CREATE_QUERY = "insert into commander.group_permission(group_id, user_id, role) values (?,?,?)";
    public static final String UPDATE_QUERY = "update commander.group_permission set role=? where id = ?";
    public static final String SELECT_BY_USER = "select id, group_id, user_id,  role from commander.group_permission where user_id=?";
    public static final String SELECT_BY_GROUP = "select id, group_id, user_id,  role from commander.group_permission where group_id=?";

    private RowMapper<GroupPermission> rowMapper = new RowMapper<GroupPermission>() {
        public GroupPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
            GroupPermission dto = new GroupPermission();
            dto.setId(rs.getLong("id"));
            dto.setGroupId(rs.getLong("group_id"));
            dto.setUserId(rs.getLong("user_id"));
            dto.setRole(GroupRole.values()[rs.getInt("role")]);
            return dto;
        }
    };


    public GroupPermissionDAO() {
        super("commander.group_permission");
    }

    @Override
    public RowMapper<GroupPermission> getRowMapper() {
        return rowMapper;
    }

    public GroupPermission update(final GroupPermission gp) {
        if (gp.getId() == null || gp.getId().equals(0L)) {
            LOGGER.debug("CREATING NEW GROUP: {}", gp);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, gp.getGroupId());
                    ps.setLong(2, gp.getUserId());
                    ps.setInt(3, gp.getRole().ordinal());
                    return ps;
                }
            }, keyHolder);

            gp.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL GROUP: {}", gp);
            getJdbcTemplate().update(UPDATE_QUERY,
                    gp.getRole().ordinal(),
                    gp.getId());
        }

        return gp;
    }

    public List<GroupPermission> findByUser(Long userId) {
        return getJdbcTemplate().query(SELECT_BY_USER, new Object[]{userId}, getRowMapper());
    }

    public List<GroupPermission> findByGroup(Long groupId) {
        return getJdbcTemplate().query(SELECT_BY_GROUP, new Object[]{groupId}, getRowMapper());
    }

}
