/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.GroupVirtualECC;
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
public class GroupVirtualECCDAO extends AbstractDAO<GroupVirtualECC> {

    public static final String CREATE_QUERY = "insert into commander.group_virtualecc(group_id, virtualECC_id) values (?,?)";
    public static final String UPDATE_QUERY = "update commander.group_virtualecc set group_id=?, virtualECC_id=? where id = ?";
    public static final String SELECT_BY_GROUP = "select id, group_id,  virtualECC_id from commander.group_virtualecc where group_id=?";

    private RowMapper<GroupVirtualECC> rowMapper = new RowMapper<GroupVirtualECC>() {
        public GroupVirtualECC mapRow(ResultSet rs, int rowNum) throws SQLException {
            GroupVirtualECC dto = new GroupVirtualECC();
            dto.setId(rs.getLong("id"));
            dto.setGroupId(rs.getLong("group_id"));
            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            return dto;
        }
    };


    public GroupVirtualECCDAO() {
        super("commander.group_virtualecc");
    }

    @Override
    public RowMapper<GroupVirtualECC> getRowMapper() {
        return rowMapper;
    }

    public GroupVirtualECC update(final GroupVirtualECC groupMember) {
        if (groupMember.getId() == null || groupMember.getId().equals(0L)) {
            LOGGER.debug("CREATING NEW GROUP_VIRTUALECC: {}", groupMember);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, groupMember.getGroupId());
                    ps.setLong(2, groupMember.getVirtualECCId());


                    return ps;
                }
            }, keyHolder);

            groupMember.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING ADDITIONAL GROUP_VIRTUALECC: {}", groupMember);
            getJdbcTemplate().update(UPDATE_QUERY,
                    groupMember.getGroupId(),
                    groupMember.getVirtualECCId(),
                    groupMember.getId());
        }

        return groupMember;
    }

    public List<GroupVirtualECC> findByGroup(Long groupId) {
        return getJdbcTemplate().query(SELECT_BY_GROUP, new Object[]{groupId}, getRowMapper());
    }

}
