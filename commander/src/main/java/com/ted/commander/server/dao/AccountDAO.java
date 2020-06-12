/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.AccountState;
import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.AccountMembership;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * DAO for accessing the Account object
 */
@Repository
public class AccountDAO extends AbstractDAO<Account> {


    public static final String CREATE_QUERY = "insert into commander.account (accountName, createDate, activationKey, phoneNumber, accountState) values (?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.account set accountName=?, createDate=?, activationKey=?, phoneNumber=?, accountState=? where id = ?";
    public static final String FIND_BY_ACTIVATIONCODE = "select id, accountName, createDate, activationKey, phoneNumber, accountState from commander.account where activationKey=?";
    public static final String FIND_MEMBERSHIPS = "select a.id, accountName, createDate, activationKey, phoneNumber, accountState, am.role from commander.account a, commander.account_member am  where am.account_id = a.id and am.user_id=? order by am.role asc";
    public static final String FIND_BY_MEMBER = "select a.id, accountName, createDate, activationKey, phoneNumber, accountState, am.role from commander.account a, commander.account_member am  where am.account_id = a.id and am.user_id=?  and am.account_id= ? order by am.role asc limit 1";
    public static final String FIND_BY_ENERGY_PLAN = "select a.id, accountName, createDate, activationKey, phoneNumber, accountState, am.role from commander.account a, commander.account_member am, commander.energyPlan ep where am.account_id = a.id and am.user_id=? and am.account_id= ep.account_id  and ep.id=? order by am.role asc limit 1";

    public void activateMore(int number){
        getJdbcTemplate().update(" update account set accountState = 0 where id < " + number);
        clearCache();
    }

    public int findMaxAccount(){
        return getJdbcTemplate().queryForObject(" select max(id) from account where accountState = 0", Integer.class);
    }


    private RowMapper<Account> rowMapper = new RowMapper<Account>() {
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();
            account.setId(rs.getLong("id"));
            account.setName(rs.getString("accountName"));
            account.setCreateDate(rs.getLong("createDate"));
            account.setActivationKey(rs.getString("activationKey"));
            account.setPhoneNumber(rs.getString("phoneNumber"));
            account.setAccountState(AccountState.values()[rs.getInt("accountState")]);
            return account;
        }
    };

    private RowMapper<AccountMembership> membershipRowMapper = new RowMapper<AccountMembership>() {
        public AccountMembership mapRow(ResultSet rs, int rowNum) throws SQLException {
            AccountMembership accountMembership = new AccountMembership();
            Account account = rowMapper.mapRow(rs, rowNum);
            accountMembership.setAccount(account);
            accountMembership.setAccountRole(AccountRole.values()[rs.getInt("role")]);
            return accountMembership;
        }
    };


    public AccountDAO() {
        super("commander.account", true);
    }

    @Override
    public RowMapper<Account> getRowMapper() {
        return rowMapper;
    }

    public Account update(final Account account) {

        if (account.getId() == null || account.getId() == 0) {
            LOGGER.debug("CREATING NEW ACCOUNT: {}", account);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, account.getName());
                    ps.setLong(2, account.getCreateDate());
                    ps.setString(3, account.getActivationKey());
                    ps.setString(4, account.getPhoneNumber());
                    ps.setInt(5, account.getAccountState().ordinal());
                    return ps;
                }
            }, keyHolder);

            account.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.debug("UPDATING ACCOUNT: {}", account);
            getJdbcTemplate().update(UPDATE_QUERY,
                    account.getName(),
                    account.getCreateDate(),
                    account.getActivationKey(),
                    account.getPhoneNumber(),
                    account.getAccountState().ordinal(),
                    account.getId());
        }

        objectCache.put(account.getId(), account);
        return account;
    }


    public Account findByActivationKey(String accountKey) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_ACTIVATIONCODE, new Object[]{accountKey}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public List<AccountMembership> findMemberships(long userId) {
        return getJdbcTemplate().query(FIND_MEMBERSHIPS, new Object[]{userId}, membershipRowMapper);
    }


    public AccountMembership findByMember(Long userId, long accountId) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_MEMBER, new Object[]{userId, accountId}, membershipRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public AccountMembership findByEnergyPlan(Long userId, Long energyPlanId) {
        try {
            return getJdbcTemplate().queryForObject(FIND_BY_ENERGY_PLAN, new Object[]{userId, energyPlanId}, membershipRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public List<Account> findAll() {
        return getJdbcTemplate().query("select * from account", new Object[]{}, getRowMapper());
    }
}
