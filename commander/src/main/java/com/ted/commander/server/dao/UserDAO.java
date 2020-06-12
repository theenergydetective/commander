/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the user object
 */
@Repository
public class UserDAO extends AbstractDAO<User> {

    public static final String GET_BY_USERNAME_QUERY_SESSION = "select * from commander.user where username= ?";
    public static final String CREATE_USER_QUERY = "insert into commander.user (username, state, firstName, lastName, middleName, joinDate, emailActivationKey) values (?,?,?,?,?,?,?)";
    public static final String UPDATE_USERNAME = "update commander.user set username=? where id = ?";
    public static final String UPDATE_PASSWORD = "update commander.user set password=? where id = ?";
    public static final String UPDATE_ACTIVATION_KEY = "update commander.user set emailActivationKey=? where id = ?";
    public static final String SAVE_USER_QUERY_SESSION = "update commander.user set username=?, state=?, firstName=?, lastName=?, middleName=? where id=?";
    public static final String GET_AUTHORIZED_USERNAME_QUERY_SESSION = "select * from  commander.user where username=? AND password=?";
    public static final String GET_FIND_BY_ACTIVATION_KEY = "select * from  commander.user where username=? AND emailActivationKey=?";
    public static final String GET_FIND_BY_ACTIVATION_KEY_ONLY = "select * from  commander.user where emailActivationKey=?";
    public static final String FIND_BY_USERNAME = "select * from commander.user where LOWER(username) like ? order by username limit ?";

    private RowMapper<User> rowMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setMiddleName(rs.getString("middleName"));
            user.setJoinDate(rs.getLong("joinDate"));
            user.setUserState(UserState.values()[rs.getInt("state")]);
            //user.setBillingEnabled(rs.getBoolean("billingEnabled"));
            user.setBillingEnabled(true);
            return user;
        }
    };

    Cache<String, User> userEmailCache;


    public UserDAO() {
        super("commander.user", true);
        userEmailCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(60, TimeUnit.MINUTES).maximumSize(2000).build();
    }

    @Override
    public void clearCache(){
        super.clearCache();
        userEmailCache.invalidateAll();
    }

    public User findByUsername(String username) {

        User user = userEmailCache.getIfPresent(username);
        if (user == null) {
            try {
                if (LOGGER.isTraceEnabled()) LOGGER.trace("looking up user object for username " + username);
                user = getJdbcTemplate().queryForObject(GET_BY_USERNAME_QUERY_SESSION, new Object[]{username}, rowMapper);
                userEmailCache.put(username, user);
                objectCache.put(user.getId(), user);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return user;
    }


    @Override
    public RowMapper<User> getRowMapper() {
        return rowMapper;
    }


    public User update(final User user) {

        LOGGER.debug("UPDATING ACCOUNT: {}", user);
        getJdbcTemplate().update(SAVE_USER_QUERY_SESSION, user.getUsername(),
                user.getUserState().ordinal(),
                user.getFirstName(), user.getLastName(), user.getMiddleName(),
                user.getId());
        userEmailCache.put(user.getUsername(), user);
        objectCache.put(user.getId(), user);


        return user;
    }

    public User create(final User user, final String emailActivationKey) {
        if (user.getId() == null || user.getId().equals(0L)) {
            LOGGER.info("CREATING NEW ACCOUNT: {}", user);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getUsername());
                    ps.setInt(2, user.getUserState().ordinal());
                    ps.setString(3, user.getFirstName());
                    ps.setString(4, user.getLastName());
                    ps.setString(5, user.getMiddleName());
                    ps.setLong(6, user.getJoinDate());
                    ps.setString(7, emailActivationKey);
                    return ps;
                }
            }, keyHolder);
            user.setId(keyHolder.getKey().longValue());
        }

        userEmailCache.put(user.getUsername(), user);
        objectCache.put(user.getId(), user);
        return user;
    }

    public void setUpdateUsername(User user, String username) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Saving user " + user);
        userEmailCache.invalidate(user.getUsername());
        objectCache.invalidate(user.getId());
        getJdbcTemplate().update(UPDATE_USERNAME, username, user.getId());
    }

    public void updatePassword(User user, String password) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Saving user " + user);
        userEmailCache.invalidate(user.getUsername());
        objectCache.invalidate(user.getId());
        getJdbcTemplate().update(UPDATE_PASSWORD, password, user.getId());
    }

    public void updateActivationKey(User user, String password) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Saving user " + user);
        userEmailCache.invalidate(user.getUsername());
        objectCache.invalidate(user.getId());
        getJdbcTemplate().update(UPDATE_ACTIVATION_KEY, password, user.getId());
    }


    /**
     * Used to authenticate the user against the database.
     *
     * @param username
     * @param password
     * @return
     */
    public User getAuthorizedUserByUserName(String username, String password) {
        try {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("looking up AUTHORIZED user object for username " + username);
            return getJdbcTemplate().queryForObject(GET_AUTHORIZED_USERNAME_QUERY_SESSION, new Object[]{username, password}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    /**
     * Used to authenticate the user against the database.
     *
     * @param username
     * @param activationKey
     * @return
     */
    public User getByActivationKey(String username, String activationKey) {
        try {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("looking up AUTHORIZED user object for username " + username);
            return getJdbcTemplate().queryForObject(GET_FIND_BY_ACTIVATION_KEY, new Object[]{username, activationKey}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    /**
     * Used to authenticate the user against the database.
     *
     * @param activationKey
     * @return
     */
    public User getByActivationKey(String activationKey) {
        try {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("looking up AUTHORIZED user object for username " + activationKey);
            return getJdbcTemplate().queryForObject(GET_FIND_BY_ACTIVATION_KEY_ONLY, new Object[]{activationKey}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public List<User> findByUsername(String username, Integer limit) {
        return getJdbcTemplate().query(FIND_BY_USERNAME, new Object[]{"%" + username.toLowerCase() + "%", limit}, getRowMapper());
    }


    public List<User> findSumter() {
        return getJdbcTemplate().query("SELECT * FROM commander.user  where lastName = 'Sumter' order by id", getRowMapper());
    }

    @Override
    public User findById(Long id) {
        User user =  super.findById(id);
        userEmailCache.put(user.getUsername(), user);
        return user;
    }
}
