/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.AdviceTrigger;
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
public class AdviceTriggerDAO extends SimpleAbstractDAO {

    Cache<Long, AdviceTrigger> adviceTriggerCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<Long, List<AdviceTrigger>> adviceTriggerByAdviceCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<Long, List<AdviceTrigger>> adviceTriggerCommanderCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<Long, List<AdviceTrigger>> adviceTriggerNonCommanderCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();


    public void clearCache(){
        adviceTriggerCache.invalidateAll();
        adviceTriggerByAdviceCache.invalidateAll();
        adviceTriggerCommanderCache.invalidateAll();
        adviceTriggerNonCommanderCache.invalidateAll();
    }


    static final String FIND_ONE_QUERY = "select * from advice_trigger where id=?";
    static final String DELETE_ONE_QUERY = "delete from advice_trigger where id=?";
    static final String DELETE_BY_ADVICE = "delete from advice_trigger where advice_id=?";

    private static final String FIND_BY_ADVICE = "select * from advice_trigger where advice_id=?";
    private static final String FIND_NON_COMMANDER_BY_ADVICE = "select * from advice_trigger where advice_id=? and triggerType != " + TriggerType.COMMANDER_NO_POST.ordinal();
    private static final String FIND_COMMANDER_BY_ADVICE = "select * from advice_trigger where advice_id=? and triggerType = " + TriggerType.COMMANDER_NO_POST.ordinal();


    private static final String INSERT = "INSERT INTO advice_trigger " +
            "(advice_id, " +
            "startTime, " +
            "endTime, " +
            "triggerType, " +
            "sendAtMost, " +
            "delayMinutes, " +
            "minutesBefore, " +
            "amount, " +
            "allMTUs, " +
            "lastSent," +
            "triggerState," +
            "offPeakApplicable, " +
            "peakApplicable, " +
            "midPeakApplicable, " +
            "superPeakApplicable, " +
            "sinceStart) " +
            "VALUES " +
            "(:advice_id, " +
            ":startTime, " +
            ":endTime, " +
            ":triggerType, " +
            ":sendAtMost, " +
            ":delayMinutes, " +
            ":minutesBefore, " +
            ":amount, " +
            ":allMTUs, " +
            ":lastSent," +
            ":triggerState," +
            ":offPeakApplicable, " +
            ":peakApplicable, " +
            ":midPeakApplicable, " +
            ":superPeakApplicable, " +
            ":sinceStart); ";
    private static final String SAVE = "UPDATE advice_trigger" +
            "  SET " +
            "  startTime =  :startTime , " +
            "  endTime =  :endTime , " +
            "  triggerType =  :triggerType , " +
            "  sendAtMost =  :sendAtMost , " +
            "  delayMinutes =  :delayMinutes , " +
            "  minutesBefore =  :minutesBefore , " +
            "  amount =  :amount , " +
            "  allMTUs =  :allMTUs , " +
            "  offPeakApplicable =  :offPeakApplicable , " +
            "  peakApplicable =  :peakApplicable , " +
            "  midPeakApplicable =  :midPeakApplicable , " +
            "  superPeakApplicable =  :superPeakApplicable , " +
            "  lastSent =  :lastSent , " +
            "  triggerState =  :triggerState , " +
            "  sinceStart =  :sinceStart " +
            "  WHERE id =  :id ";


    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<AdviceTrigger> rowMapper = new RowMapper<AdviceTrigger>() {
        public AdviceTrigger mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdviceTrigger dto = new AdviceTrigger();
            dto.setId(rs.getLong("id"));
            dto.setAdviceId(rs.getLong("advice_id"));
            dto.setStartTime(rs.getInt("startTime"));
            dto.setEndTime(rs.getInt("endTime"));
            dto.setTriggerType(TriggerType.values()[rs.getInt("triggerType")]);
            dto.setSendAtMost(SendAtMostType.values()[rs.getInt("sendAtMost")]);
            dto.setDelayMinutes(rs.getInt("delayMinutes"));
            dto.setMinutesBefore(rs.getInt("minutesBefore"));
            dto.setAmount(rs.getDouble("amount"));
            dto.setAllMTUs(rs.getBoolean("allMTUs"));
            dto.setOffPeakApplicable(rs.getBoolean("offPeakApplicable"));
            dto.setPeakApplicable(rs.getBoolean("peakApplicable"));
            dto.setMidPeakApplicable(rs.getBoolean("midPeakApplicable"));
            dto.setSuperPeakApplicable(rs.getBoolean("superPeakApplicable"));
            dto.setSinceStart(HistoryType.values()[rs.getInt("sinceStart")]);
            dto.setLastSent(rs.getLong("lastSent"));
            dto.setTriggerState(AdviceTriggerState.values()[rs.getInt("triggerState")]);

            return dto;
        }
    };

    private MapSqlParameterSource getNamedParameters(AdviceTrigger dto) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", dto.getId());
        namedParameters.addValue("advice_id", dto.getAdviceId());
        namedParameters.addValue("startTime", dto.getStartTime());
        namedParameters.addValue("endTime", dto.getEndTime());
        namedParameters.addValue("triggerType", dto.getTriggerType().ordinal());
        namedParameters.addValue("sendAtMost", dto.getSendAtMost().ordinal());
        namedParameters.addValue("delayMinutes", dto.getDelayMinutes());
        namedParameters.addValue("minutesBefore", dto.getMinutesBefore());
        namedParameters.addValue("amount", dto.getAmount());
        namedParameters.addValue("allMTUs", dto.isAllMTUs());
        namedParameters.addValue("offPeakApplicable", dto.isOffPeakApplicable());
        namedParameters.addValue("peakApplicable", dto.isPeakApplicable());
        namedParameters.addValue("midPeakApplicable", dto.isMidPeakApplicable());
        namedParameters.addValue("superPeakApplicable", dto.isSuperPeakApplicable());
        namedParameters.addValue("sinceStart", dto.getSinceStart().ordinal());
        namedParameters.addValue("lastSent", dto.getLastSent());
        namedParameters.addValue("triggerState", dto.getTriggerState().ordinal());

        return namedParameters;
    }

    public AdviceTrigger findOne(Long id) {
        if (id==null) return null;
        AdviceTrigger dto = adviceTriggerCache.getIfPresent(id);
        if (dto == null) {
            try {
                dto = getJdbcTemplate().queryForObject(FIND_ONE_QUERY,
                        new Object[]{id},
                        rowMapper);
                adviceTriggerCache.put(id, dto);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
            }
        }
        return dto;
    }

    public void deleteOne(Long id) {
        clearCache();
        try {
            getJdbcTemplate().update(DELETE_ONE_QUERY, new Object[]{id});
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
        }
    }

    public void deleteByAdvice(Long adviceId) {
        clearCache();
        try {
            getJdbcTemplate().update(DELETE_BY_ADVICE, new Object[]{adviceId});
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
        }
    }

    public AdviceTrigger insert(AdviceTrigger dto) {
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

    public void save(AdviceTrigger dto) {
        clearCache();
        try {
            getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(dto));
        } catch (Exception ex) {
            LOGGER.error("Error saving record: {}", dto, ex);
        }
    }

    public List<AdviceTrigger> findByAdvice(long adviceId) {
        List<AdviceTrigger> list = adviceTriggerByAdviceCache.getIfPresent(adviceId);
        if (list == null) {
            try {
                list = getJdbcTemplate().query(FIND_BY_ADVICE, new Object[]{adviceId}, rowMapper);
            } catch (EmptyResultDataAccessException ex){
                list = new ArrayList<>();
            }
            adviceTriggerByAdviceCache.put(adviceId, list);
        }
        return list;

    }

    public List<AdviceTrigger> findNoPostAdvice(long adviceId) {
        List<AdviceTrigger> list = adviceTriggerCommanderCache.getIfPresent(adviceId);
        if (list == null) {
            try {
                list = getJdbcTemplate().query(FIND_COMMANDER_BY_ADVICE, new Object[]{adviceId}, rowMapper);
            } catch(EmptyResultDataAccessException ex){
                list =new ArrayList<>();
            }
            adviceTriggerCommanderCache.put(adviceId, list);
        }
        return list;
    }

    public List<AdviceTrigger> findPostAdvice(long adviceId) {
        List<AdviceTrigger> list = adviceTriggerNonCommanderCache.getIfPresent(adviceId);
        if (list == null) {
            try {
                list = getJdbcTemplate().query(FIND_NON_COMMANDER_BY_ADVICE, new Object[]{adviceId}, rowMapper);
            } catch(EmptyResultDataAccessException ex){
                list =new ArrayList<>();
            }
            adviceTriggerNonCommanderCache.put(adviceId, list);
        }
        return list;

    }


    public AdviceState findAdviceState(long adviceId){
        AdviceTrigger adviceTrigger = findOne(adviceId);
        if (adviceTrigger == null || adviceTrigger.getTriggerState() == AdviceTriggerState.NORMAL ) return AdviceState.NORMAL;
        return AdviceState.ALARM;
    }



}
