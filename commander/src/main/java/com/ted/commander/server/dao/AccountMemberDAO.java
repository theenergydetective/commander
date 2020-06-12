/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * DAO for accessing the AccountMember object
 */
@Repository
public class AccountMemberDAO extends AbstractDAO<AccountMember> {


    public static final String CREATE_QUERY = "insert into commander.account_member (account_id, user_id, role) values (?,?,?)";
    public static final String UPDATE_QUERY = "update commander.account_member set role=? where id = ?";

    public static final String FIND_BY_ACCOUNT_USERID = "select m.id, m.account_id, m.user_id, m.role,u.username, u.firstName, u.lastName, u.middleName, u.state, u.joinDate from commander.user u, commander.account_member m where u.id = m.user_id and account_id = ? and user_id=?";
    public static final String FIND_BY_ACCOUNT_USERNAME = "select m.id, m.account_id, m.user_id, m.role,u.username, u.firstName, u.lastName, u.middleName, u.state, u.joinDate from commander.user u, commander.account_member m where u.id = m.user_id and account_id = ? and u.username=?";
    public static final String FIND_BY_ID = "select m.id, m.account_id, m.user_id, m.role,u.username, u.firstName, u.lastName, u.middleName, u.state, u.joinDate from commander.user u, commander.account_member m where u.id = m.user_id and m.id = ?";
    public static final String FIND_MEMBERS = "select m.id, m.account_id, m.user_id, m.role, u.username, u.firstName, u.lastName, u.middleName, u.state, u.joinDate from commander.user u, commander.account_member m where u.id = m.user_id and u.state!=3 and m.account_id = ?";

    private RowMapper<AccountMember> rowMapper = new RowMapper<AccountMember>() {
        public AccountMember mapRow(ResultSet rs, int rowNum) throws SQLException {
            AccountMember account = new AccountMember();
            account.setId(rs.getLong("id"));
            account.setAccountId(rs.getLong("account_id"));
            account.setUserId(rs.getLong("user_id"));
            account.setAccountRole(AccountRole.values()[rs.getInt("role")]);

            User user = new User();
            user.setId(account.getUserId());
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setMiddleName(rs.getString("middleName"));
            user.setUserState(UserState.values()[rs.getInt("state")]);
            user.setJoinDate(rs.getLong("joinDate"));
            user.setUsername(rs.getString("username"));
            account.setUser(user);

            return account;
        }
    };

    public AccountMemberDAO() {
        super("commander.account_member");
    }

    @Override
    public AccountMember findById(Long id) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_ID, new Object[]{id}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    @Override
    public RowMapper<AccountMember> getRowMapper() {
        return rowMapper;
    }

    public AccountMember findByUser(Long accountId, Long userId) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_ACCOUNT_USERID, new Object[]{accountId, userId}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public List<AccountMember> findByAccount(Long accountId) {
        return getJdbcTemplate().query(FIND_MEMBERS, new Object[]{accountId}, getRowMapper());
    }


    public AccountMember update(final AccountMember accountMember) {
        if (accountMember.getId() == null || accountMember.getId() == 0) {
            LOGGER.info("CREATING NEW AccountMember: {}", accountMember);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, accountMember.getAccountId());
                    ps.setLong(2, accountMember.getUserId());
                    ps.setInt(3, accountMember.getAccountRole().ordinal());
                    return ps;
                }
            }, keyHolder);

            accountMember.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING AccountMember: {}", accountMember);
            getJdbcTemplate().update(UPDATE_QUERY,
                    accountMember.getAccountRole().ordinal(),
                    accountMember.getId());
        }

        return accountMember;
    }

    public AccountMember findByUserName(Long accountId, String username) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_ACCOUNT_USERNAME, new Object[]{accountId, username}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }
}
