/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

/**
 * DAO for accessing the AccountMember object
 */
@Repository
public class AccessControlDAO extends SimpleAbstractDAO {
    private final String VIEW_LOCATION_QUERY = "select count(*) from virtualECC v straight_join account_member am on v.account_id = am.account_id  straight_join user u on am.user_id = u.id where v.id = ? and username=?";
    private final String EDIT_LOCATION_QUERY = "select count(*) from virtualECC v straight_join account_member am on v.account_id = am.account_id  straight_join user u on am.user_id = u.id where v.id = ? and username=? and am.role <= 2";
    private final String VIEW_ACCOUNT_QUERY = "select count(*) from account_member am straight_join user u on am.user_id = u.id where account_id = ? and u.username = ?";
    private final String EDIT_ACCOUNT_QUERY = "select count(*) from account_member am straight_join user u on am.user_id = u.id where am.role < 2 and account_id = ? and u.username = ?";

    public boolean canViewLocation(Long virtualECCId, String userName) {
        try {
            return 1 == getJdbcTemplate().queryForObject(VIEW_LOCATION_QUERY, new Object[]{virtualECCId, userName}, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return false;
        }
    }

    public boolean canEditLocation(Long virtualECCId, String userName) {
        try {
            return 1 == getJdbcTemplate().queryForObject(EDIT_LOCATION_QUERY, new Object[]{virtualECCId, userName}, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return false;
        }
    }


    public boolean canViewAccount(Long accountId, String username) {
        try {
            return 1 == getJdbcTemplate().queryForObject(VIEW_ACCOUNT_QUERY, new Object[]{accountId, username}, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return false;
        }
    }

    public boolean canEditAccount(Long accountId, String username) {
        try {
            return 1 == getJdbcTemplate().queryForObject(EDIT_ACCOUNT_QUERY, new Object[]{accountId, username}, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return false;
        }
    }
}
