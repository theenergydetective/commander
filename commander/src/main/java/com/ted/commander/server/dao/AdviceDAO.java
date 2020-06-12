/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.AdviceState;
import com.ted.commander.common.model.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class AdviceDAO extends SimpleAbstractDAO {

    Cache<Long, Advice> adviceCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    List<Advice> noPostCache = null;
    Cache<Long, List<Advice>> eccCache= CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<Long, List<Advice>> accountCache= CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();


    public void clearCache(){
        adviceCache.invalidateAll();
        if (noPostCache != null) noPostCache.clear();
        eccCache.invalidateAll();
        accountCache.invalidateAll();
    }

    static final String FIND_ONE_QUERY = "select * from advice where id=?";
    private static final String INSERT = "INSERT INTO advice " +
            "(adviceName, " +
            "virtualECC_id, " +
            "account_id, " +
            "adviceState) " +
            "VALUES " +
            "(:adviceName, " +
            ":virtualECC_id, " +
            ":account_id, " +
            ":adviceState); ";
    private static final String SAVE = "UPDATE advice" +
            "  SET " +
            "  adviceName =  :adviceName, " +
            "  virtualECC_id =  :virtualECC_id, " +
            "  adviceState =  :adviceState " +
            "  WHERE id =  :id ";
    private static final String DELETE_ADVICE = "DELETE from advice where id = ?";
    private static final String FIND_BY_VIRTUALECC = "select * from advice where virtualECC_id=?" ;
    private static final String FIND_BY_ACCOUNT = "select a.* from advice a where a.account_id = ?";
    private static final String FIND_BY_NOPOST = "select * from advice where id in (select advice_id from advice_trigger USE INDEX(TYPE) where triggerType = 8)";
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<Advice> rowMapper = new RowMapper<Advice>() {
        public Advice mapRow(ResultSet rs, int rowNum) throws SQLException {
            Advice dto = new Advice();

            dto.setId(rs.getLong("id"));
            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setAdviceName(rs.getString("adviceName"));
            dto.setAccountId(rs.getLong("account_id"));
            dto.setState(AdviceState.values()[rs.getInt("adviceState")]);
            return dto;
        }
    };

    private MapSqlParameterSource getNamedParameters(Advice dto) {
        //Map<String, Object> namedParameters = new HashMap(64);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", dto.getId());
        mapSqlParameterSource.addValue("adviceName", dto.getAdviceName());
        mapSqlParameterSource.addValue("virtualECC_id", dto.getVirtualECCId());
        mapSqlParameterSource.addValue("account_id", dto.getAccountId());
        mapSqlParameterSource.addValue("adviceState", dto.getState().ordinal());
        return mapSqlParameterSource;
    }

    public Advice findOne(Long id) {
        Advice advice = adviceCache.getIfPresent(id);
        if (advice == null) {
            try {
                advice = getJdbcTemplate().queryForObject(FIND_ONE_QUERY,
                        new Object[]{id},
                        rowMapper);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
            }
            if (advice != null) adviceCache.put(id, advice);
        }
        return advice;
    }

    public Advice insert(Advice dto) {
        clearCache();
        try {
            LOGGER.debug("RUNNING INSERT QUERY: {}", dto);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(dto), keyHolder);
            dto.setId(keyHolder.getKey().longValue());
            return dto;
        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", dto, ex);
            return null;
        }

    }

    public void save(Advice dto) {
        try {
            LOGGER.debug("RUNNING SAVE QUERY: {}", dto);
            getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(dto));
        } catch (Exception ex) {
            LOGGER.error("Error saving record: {}", dto, ex);
        }
        clearCache();
    }

    public void delete(Long id) {
        try {
            LOGGER.debug("RUNNING DELETE QUERY: {}", id);
            getJdbcTemplate().update(DELETE_ADVICE, new Object[]{id});
        } catch (Exception ex) {
            LOGGER.error("Error deleting record: {}", id, ex);
        }
        clearCache();
    }

    public List<Advice> findByVirtualECC(long virtualECCId) {
        List<Advice> list = eccCache.getIfPresent(virtualECCDAO);
        if (list == null) {
            try {
                list = getJdbcTemplate().query(FIND_BY_VIRTUALECC, new Object[]{virtualECCId}, rowMapper);
            } catch(EmptyResultDataAccessException ex){
                list = new ArrayList<>();
            }
            eccCache.put(virtualECCId, list);
        }
        return list;
    }

    public List<Advice> findByAccount(long userId) {
        List<Advice> list = accountCache.getIfPresent(userId);
        if (list == null) {
            try {
                list = getJdbcTemplate().query(FIND_BY_ACCOUNT, new Object[]{userId}, rowMapper);
            } catch (EmptyResultDataAccessException ex){
                list = new ArrayList<>();
            }
            accountCache.put(userId, list);
        }
        return list;
    }

    public List<Advice> findByNoPost() {
        if (noPostCache == null) {
            try {
                noPostCache = getJdbcTemplate().query(FIND_BY_NOPOST, new Object[]{}, rowMapper);
            } catch (EmptyResultDataAccessException ex){
                noPostCache = new ArrayList<>();
            }
        }
        return noPostCache;
    }

}
