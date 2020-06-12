/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.ECCState;
import com.ted.commander.common.model.ECC;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the Gateway object
 */
@Repository("eccDAO")
public class ECCDAO extends AbstractDAO<ECC> {


    public static final String CREATE_QUERY = "insert into commander.ecc (id, account_id, state, securityKey, version) values (?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.ecc set account_id=?, state=?, securityKey=?, version=? where id = ?";
    public static final String SELECT_USERID_ECC = "select id,account_id, state, securityKey, version from  commander.ecc where account_id = ? and id=?";
    public static final String SELECT_KEY = "select id, account_id, state, UPPER(securityKey) as securityKey, version from commander.ecc where securityKey = ?";

    Cache<String, ECC> eccKeyCache;

    private RowMapper<ECC> rowMapper = new RowMapper<ECC>() {
        public ECC mapRow(ResultSet rs, int rowNum) throws SQLException {
            ECC ecc = new ECC();
            ecc.setId(rs.getLong("id"));
            ecc.setUserAccountId(rs.getLong("account_id"));
            ecc.setState(ECCState.values()[rs.getInt("state")]);
            ecc.setSecurityKey(rs.getString("securityKey"));
            ecc.setVersion(rs.getString("version"));
            return ecc;
        }
    };


    public ECCDAO() {
        super("commander.ecc", false);
        eccKeyCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.DAYS).maximumSize(3000).build();

    }

    @Override
    public RowMapper<ECC> getRowMapper() {
        return rowMapper;
    }

    public ECC update(final ECC ecc) {
        //Check for an existing ECC.
        ECC existingECC = findById(ecc.getUserAccountId(), ecc.getId());

        if (existingECC == null) {
            LOGGER.info("CREATING NEW ECC: {}", ecc);

            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.NO_GENERATED_KEYS);
                    ps.setLong(1, ecc.getId());
                    ps.setLong(2, ecc.getUserAccountId());
                    ps.setInt(3, ecc.getState().ordinal());
                    ps.setString(4, ecc.getSecurityKey());
                    ps.setString(5, ecc.getVersion());
                    return ps;
                }
            });
        } else {
            LOGGER.debug("UPDATING ECC: {}", ecc);
            getJdbcTemplate().update(UPDATE_QUERY,
                    ecc.getUserAccountId(),
                    ecc.getState().ordinal(),
                    ecc.getSecurityKey(),
                    ecc.getVersion(),
                    ecc.getId());
        }

        eccKeyCache.put(ecc.getSecurityKey(), ecc);
        return ecc;
    }

    public ECC findById(Long accountId, Long eccId) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_USERID_ECC, new Object[]{accountId, eccId}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }


    public ECC findByKey(String securityKey) {
        ECC ecc = eccKeyCache.getIfPresent(securityKey);
        if (ecc == null) {
            try {
                ecc = getJdbcTemplate().queryForObject(SELECT_KEY, new Object[]{securityKey.toUpperCase()}, getRowMapper());
                eccKeyCache.put(securityKey, ecc);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return ecc;
    }
}
