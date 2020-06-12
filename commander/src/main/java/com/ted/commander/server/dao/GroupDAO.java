/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.Group;
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
public class GroupDAO extends AbstractDAO<Group> {

    public static final String CREATE_QUERY = "insert into commander.group(account_id,  description) values (?,?)";
    public static final String UPDATE_QUERY = "update commander.group set account_id=?, description=? where id = ?";
    public static final String SELECT_BY_OWNER = "select id, account_id,  description from commander.group where account_id=?";

    private RowMapper<Group> rowMapper = new RowMapper<Group>() {
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            Group dto = new Group();
            dto.setId(rs.getLong("id"));
            dto.setAccountId(rs.getLong("account_id"));
            dto.setDescription(rs.getString("description"));
            return dto;
        }
    };


    public GroupDAO() {
        super("commander.group");
    }

    @Override
    public RowMapper<Group> getRowMapper() {
        return rowMapper;
    }

    public Group update(final Group group) {
        if (group.getId() == null || group.getId().equals(0L)) {
            LOGGER.debug("CREATING NEW GROUP: {}", group);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, group.getAccountId());
                    ps.setString(2, group.getDescription());
                    return ps;
                }
            }, keyHolder);

            group.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL GROUP: {}", group);
            getJdbcTemplate().update(UPDATE_QUERY,
                    group.getAccountId(),
                    group.getDescription(),
                    group.getId());
        }

        return group;
    }

    public List<Group> findByOwner(Long ownerId) {
        return getJdbcTemplate().query(SELECT_BY_OWNER, new Object[]{ownerId}, getRowMapper());
    }

}
