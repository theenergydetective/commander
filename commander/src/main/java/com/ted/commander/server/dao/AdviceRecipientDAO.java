/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.AdviceRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class AdviceRecipientDAO extends SimpleAbstractDAO {


    static final String FIND_ONE_QUERY = "select * from advice_recipient ar straight_join user u where u.id=ar.user_id and advice_id=? and user_id=?";
    static final String DELETE_ONE_QUERY = "delete from advice_recipient where advice_id=? and user_id=?";
    static final String DELETE_BY_ADVICE = "delete from advice_recipient where advice_id=?";
    private static final String INSERT = "INSERT INTO advice_recipient " +
            "(advice_id, " +
            "user_id, " +
            "useEmail, " +
            "usePush) " +
            "VALUES " +
            "(:advice_id, " +
            ":user_id, " +
            ":useEmail, " +
            ":usePush); ";
    private static final String SAVE = "UPDATE advice_recipient" +
            "  SET " +
            "  useEmail =  :useEmail, " +
            "  usePush =  :usePush " +
            "  WHERE advice_id =  :advice_id " +
            "  and user_id = :user_id ";
    private static final String FIND_BY_ADVICE = "select * from advice_recipient ar straight_join user u where u.id=ar.user_id and advice_id=?";
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<AdviceRecipient> rowMapper = new RowMapper<AdviceRecipient>() {
        public AdviceRecipient mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdviceRecipient dto = new AdviceRecipient();
            dto.setAdviceId(rs.getLong("advice_id"));
            dto.setUserId(rs.getLong("user_id"));
            dto.setSendEmail(rs.getBoolean("useEmail"));
            dto.setSendPush(rs.getBoolean("usePush"));
            dto.setEmail(rs.getString("username"));
            String lastName = rs.getString("lastName");
            String firstName = rs.getString("firstName");
            String middleName = rs.getString("middleName");

            StringBuilder stringBuilder = new StringBuilder();
            if (lastName != null && !lastName.isEmpty()) stringBuilder.append(lastName);
            if (firstName != null && !firstName.isEmpty()) {
                if (stringBuilder.length() != 0) stringBuilder.append(", ");
                stringBuilder.append(firstName);
            }
            if (middleName != null && !middleName.isEmpty()) {
                if (stringBuilder.length() != 0) stringBuilder.append(" ");
                stringBuilder.append(middleName);
            }
            if (stringBuilder.length() == 0) stringBuilder.append(dto.getEmail()).append("*");
            dto.setDisplayName(stringBuilder.toString());

            return dto;
        }
    };

    private Map getNamedParameters(AdviceRecipient dto) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("advice_id", dto.getAdviceId());
        namedParameters.put("user_id", dto.getUserId());
        namedParameters.put("useEmail", dto.isSendEmail());
        namedParameters.put("usePush", dto.isSendPush());
        return namedParameters;
    }

    public AdviceRecipient findOne(Long adviceId, Long userId) {
        AdviceRecipient dto = null;
        try {
            dto = getJdbcTemplate().queryForObject(FIND_ONE_QUERY,
                    new Object[]{adviceId, userId},
                    rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
        }
        return dto;
    }

    public void deleteOne(Long adviceId, Long userId) {
        try {
            getJdbcTemplate().update(DELETE_ONE_QUERY, new Object[]{adviceId, userId});
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
        }
    }

    public void deleteByAdvice(Long adviceId) {
        try {
            getJdbcTemplate().update(DELETE_BY_ADVICE, new Object[]{adviceId});
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
        }
    }

    public void insert(AdviceRecipient dto) {
        try {
            LOGGER.debug("RUNNING INSERT QUERY: {}", dto);
            getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(dto));
        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", dto, ex);
        }
    }

    public void save(AdviceRecipient dto) {
        try {
            LOGGER.debug("RUNNING SAVE QUERY: {}", dto);
            getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(dto));
        } catch (Exception ex) {
            LOGGER.error("Error saving record: {}", dto, ex);
        }
    }

    public List<AdviceRecipient> findByAdvice(long adviceId) {
        List<AdviceRecipient> list = new ArrayList<>();
        list = getJdbcTemplate().query(FIND_BY_ADVICE, new Object[]{adviceId}, rowMapper);
        return list;
    }


}
