/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.server.model.alexa.AlexaResponseAccount;
import com.ted.commander.server.model.alexa.AlexaResponseLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Service for handling Alexa commands
 */
@Service
public class AlexaDAO extends SimpleAbstractDAO {

    static final Logger LOGGER = LoggerFactory.getLogger(AlexaDAO.class);
    static final String GET_LOCATIONS = "select l.id as virtualECC_id, eccName, a.id as account_id, accountName, am.role " +
            "from virtualECC l " +
            "straight_join account a on l.account_id = a.id " +
            "straight_join account_member am on am.account_id = a.id  " +
            "where am.user_id = ? " +
            "order by am.role, accountName, eccName, l.id ";

    static final String UPDATE_TOKEN = "UPDATE user set alexaAccessToken=? where id=?";

    static final String CLEAR_LOCATIONS = "DELETE FROM alexa_location where user_id =?";
    static final String ADD_LOCATION = "INSERT INTO alexa_location (user_id, virtualECC_id, alexaName) values(?,?,?)";

    static final String ACTIVE_LOADS = "select mtuDescription from virtualECCMTU v " +
            "straight_join energydata e on v.mtu_id = e.mtu_id and v.account_id = e.account_id and v.lastPost = e.timestamp " +
            "where virtualECC_id = ? AND mtuType = 3 and energyDifference > 0 and v.lastPost > ? and v.mtu_id > 16777215";

    public class AlexaAccountCallbackHandler implements RowCallbackHandler{

    final List<AlexaResponseAccount> accountList = new ArrayList<>();
        AlexaResponseAccount account = null;

        public void processRow(ResultSet rs) throws SQLException {
            Long accountId = rs.getLong("account_id");
            if (account == null || !accountId.equals(account.getAccountId())){
                account = new AlexaResponseAccount();
                account.setAccountId(accountId);
                account.setAccountName(rs.getString("accountName"));
                accountList.add(account);
            }
            AlexaResponseLocation location = new AlexaResponseLocation();
            location.setLocationId(rs.getLong("virtualECC_id"));
            location.setLocationName(rs.getString("eccName"));
            account.getLocationList().add(location);
        }

        public List<AlexaResponseAccount> getAccountList() {
            return accountList;
        }
    };

    public List<String> findActiveLoads(long virtualECCId) {
        long window = System.currentTimeMillis();
        window /= 1000;
        window -= 900;

        try {
            List<String> activeList = getJdbcTemplate().queryForList(ACTIVE_LOADS, new Object[]{virtualECCId, window}, String.class);
            return activeList;
        } catch (Exception ex){
            return new ArrayList<>();
        }

    }

    public List<AlexaResponseAccount> findLocationsByUserId(long userId) {
        AlexaAccountCallbackHandler accountCallbackHandler =new AlexaAccountCallbackHandler();
        getJdbcTemplate().query(GET_LOCATIONS, new Object[]{userId}, accountCallbackHandler);
        return accountCallbackHandler.getAccountList();
    }

    public void updateToken(long userID, String token) {
        getJdbcTemplate().update(UPDATE_TOKEN, new Object[]{token,userID});
    }

    public void setAddLocation(long userId, long virtualECCId, String alexaName) {
        getJdbcTemplate().update(ADD_LOCATION, new Object[]{userId, virtualECCId, alexaName});
    }

    public void clearLocations(long userId) {
        getJdbcTemplate().update(CLEAR_LOCATIONS, new Object[]{userId});
    }
}
