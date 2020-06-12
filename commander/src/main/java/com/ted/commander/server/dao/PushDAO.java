/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.server.model.PushId;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for accessing the Push notification id's
 */
@Repository
public class PushDAO extends SimpleAbstractDAO {


    private static final String INSERT = "INSERT INTO push_id " +
            "(user_id, " +
            "registrationId," +
            "ios, adm) " +
            "VALUES " +
            "(:userId, " +
            ":registrationId," +
            ":ios, :adm); ";
    private static final String FIND_BY_USER = "select * from push_id where user_id=:userId";

    private static final String FIND_BY_KEY = "select * from push_id where registrationId = :registrationId";
    private static final String DELETE_BY_KEY = "delete from push_id where registrationId=:registrationId";


    private RowMapper<PushId> rowMapper = new RowMapper<PushId>() {
        public PushId mapRow(ResultSet rs, int rowNum) throws SQLException {
            PushId pushId = new PushId();
            pushId.setRegistrationId(rs.getString("registrationId"));
            pushId.setUserId(rs.getLong("user_id"));
            pushId.setIos(rs.getBoolean("ios"));
            pushId.setAdm(rs.getBoolean("adm"));
            return pushId;
        }
    };



    private Map getNamedParameters(PushId dto) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("userId", dto.getUserId());
        namedParameters.put("registrationId", dto.getRegistrationId());
        namedParameters.put("ios", dto.isIos());
        namedParameters.put("adm", dto.isAdm());
        return namedParameters;
    }

    public void insert(PushId pushId) {
        try {
            getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(pushId));
        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", pushId, ex);
        }
    }

    public PushId findByKey(PushId pushId) {
        Map<String, Object> namedParameters = getNamedParameters(pushId);
        try {
            return getNamedParameterJdbcTemplate().queryForObject(FIND_BY_KEY, namedParameters, rowMapper);
        } catch(Exception ex){
            return null;
        }
    }

    public List<PushId> findByUser(long userId) {
        Map<String, Object> namedParameters = new HashMap(24);
        namedParameters.put("userId", userId);
        List<PushId> list = new ArrayList<>();
        list = getNamedParameterJdbcTemplate().query(FIND_BY_USER, namedParameters, rowMapper);
        return list;
    }

    public void deleteByKey(PushId pushId) {
        Map<String, Object> namedParameters = getNamedParameters(pushId);
        getNamedParameterJdbcTemplate().update(DELETE_BY_KEY, namedParameters);
    }



}
