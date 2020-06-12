/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryKey;
import com.ted.commander.common.model.history.HistoryMTUBillingCycle;
import com.ted.commander.common.model.history.HistoryMTUBillingCycleKey;
import com.ted.commander.server.services.HazelcastService;
import com.ted.commander.server.services.PollingService;
import com.ted.commander.server.services.ServerService;
import com.ted.commander.server.util.CalendarUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class HistoryMTUBillingCycleDAO extends SimpleAbstractDAO {

    private static final String INSERT = "INSERT INTO history_mtu_billingCycle " +
            "(virtualECC_id, " +
            "mtu_id, " +
            "billingCycleMonth, " +
            "billingCycleYear, " +
            "startEpoch, " +
            "endEpoch, " +
            "energy, " +
            "cost, " +
            "demandPeak, " +
            "demandPeakTime, " +
            "peakVoltage, " +
            "peakVoltageTime, " +
            "minVoltage, " +
            "minVoltageTime, " +
            "pfSampleCount, " +
            "pfTotal, " +

            "touOffPeak, " +
            "touPeak, " +
            "touMidPeak, " +
            "touSuperPeak) " +
            "VALUES " +
            "(:virtualECC_id, " +
            ":mtu_id, " +
            ":billingCycleMonth, " +
            ":billingCycleYear, " +
            ":startEpoch, " +
            ":endEpoch, " +
            ":energy, " +
            ":cost, " +
            ":demandPeak, " +
            ":demandPeakTime, " +
            ":peakVoltage, " +
            ":peakVoltageTime, " +
            ":minVoltage, " +
            ":minVoltageTime, " +
            ":pfSampleCount, " +
            ":pfTotal, " +

            ":touOffPeak, " +
            ":touPeak, " +
            ":touMidPeak, " +
            ":touSuperPeak)";
    private static final String SAVE = "UPDATE history_mtu_billingCycle" +
            "  SET " +
            "  energy =  :energy, " +
            "  cost =  :cost, " +
            "  demandPeak =  :demandPeak, " +
            "  demandPeakTime =  :demandPeakTime, " +
            "  peakVoltage =  :peakVoltage, " +
            "  peakVoltageTime =  :peakVoltageTime, " +
            "  minVoltage =  :minVoltage, " +
            "  minVoltageTime =  :minVoltageTime, " +
            "  pfSampleCount =  :pfSampleCount, " +
            "  pfTotal =  :pfTotal, " +
            "  touOffPeak =  :touOffPeak, " +
            "  touPeak =  :touPeak, " +
            "  touMidPeak =  :touMidPeak, " +
            "  touSuperPeak =  :touSuperPeak " +
            "  WHERE virtualECC_id =  :virtualECC_id " +
            "  AND mtu_id = :mtu_id " +
            "  AND billingCycleYear = :billingCycleYear " +
            "  AND billingCycleMonth = :billingCycleMonth ";
    private static final String FIND_BY_DATE_RANGE_QUERY = "select * from history_mtu_billingCycle where virtualECC_id=:virtualECCId and mtu_id = :mtuId and ((startEpoch >= :startEpoch and startEpoch < :endEpoch) or (:startEpoch >= startEpoch and :startEpoch < endEpoch) or (:endEpoch >= startEpoch and :endEpoch < endEpoch)) order by startEpoch";
    @Autowired
    HazelcastService hazelcastService;
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<HistoryMTUBillingCycle> rowMapper = new RowMapper<HistoryMTUBillingCycle>() {
        public HistoryMTUBillingCycle mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryMTUBillingCycle dto = new HistoryMTUBillingCycle();

            HistoryMTUBillingCycleKey key = new HistoryMTUBillingCycleKey();
            key.setVirtualECCId(rs.getLong("virtualECC_id"));
            key.setMtuId(rs.getLong("mtu_id"));
            key.setBillingCycleYear(rs.getInt("billingCycleYear"));
            key.setBillingCycleMonth(rs.getInt("billingCycleMonth"));
            dto.setKey(key);

            dto.setStartEpoch(rs.getLong("startEpoch"));
            dto.setEndEpoch(rs.getLong("endEpoch"));
            dto.setEnergy(rs.getDouble("energy"));
            dto.setCost(rs.getDouble("cost"));

            dto.setDemandPeak(rs.getDouble("demandPeak"));
            dto.setDemandPeakTime(rs.getLong("demandPeakTime"));


            dto.setTouOffPeak(rs.getDouble("touOffPeak"));
            dto.setTouPeak(rs.getDouble("touPeak"));
            dto.setTouMidPeak(rs.getDouble("touMidPeak"));
            dto.setTouSuperPeak(rs.getDouble("touSuperPeak"));


            dto.setPeakVoltage(rs.getDouble("peakVoltage"));
            dto.setPeakVoltageTime(rs.getLong("peakVoltageTime"));
            dto.setMinVoltage(rs.getDouble("minVoltage"));
            dto.setMinVoltageTime(rs.getLong("minVoltageTime"));
            dto.setPfSampleCount(rs.getLong("pfSampleCount"));
            dto.setPfTotal(rs.getDouble("pfTotal"));

            updateCalendarKeys(dto);


            return dto;
        }
    };

    private Map getNamedParameters(HistoryMTUBillingCycle dto) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("virtualECC_id", dto.getKey().getVirtualECCId());
        namedParameters.put("mtu_id", dto.getKey().getMtuId());
        namedParameters.put("billingCycleMonth", dto.getKey().getBillingCycleMonth());
        namedParameters.put("billingCycleYear", dto.getKey().getBillingCycleYear());
        namedParameters.put("startEpoch", dto.getStartEpoch());
        namedParameters.put("endEpoch", dto.getEndEpoch());
        namedParameters.put("energy", dto.getEnergy());
        namedParameters.put("cost", dto.getCost());
        namedParameters.put("demandPeak", dto.getDemandPeak());
        namedParameters.put("demandPeakTime", dto.getDemandPeakTime());

        namedParameters.put("touOffPeak", dto.getTouOffPeak());
        namedParameters.put("touPeak", dto.getTouPeak());
        namedParameters.put("touMidPeak", dto.getTouMidPeak());
        namedParameters.put("touSuperPeak", dto.getTouSuperPeak());

//        namedParameters.put("peakVoltage", dto.getPeakVoltage());
        if (dto.getPeakVoltage() < 0) namedParameters.put("peakVoltage", 120.0);
        else if (dto.getPeakVoltage() > 999.0) namedParameters.put("peakVoltage", 999.0);
        else namedParameters.put("peakVoltage", dto.getPeakVoltage());


        namedParameters.put("peakVoltageTime", dto.getPeakVoltageTime());

        if (dto.getMinVoltage() < 0) namedParameters.put("minVoltage", 0.0);
        else if (dto.getMinVoltage() > 999.0) namedParameters.put("minVoltage", 999.0);
        else namedParameters.put("minVoltage", dto.getMinVoltage());

        namedParameters.put("minVoltageTime", dto.getMinVoltageTime());
        namedParameters.put("pfSampleCount", dto.getPfSampleCount());
        namedParameters.put("pfTotal", dto.getPfTotal());


        for (String s : namedParameters.keySet()) {
            Object o = namedParameters.get(s);
            if (o instanceof Double) namedParameters.put(s, forceZero((double) o));


        }
        return namedParameters;
    }

    private void updateCalendarKeys(HistoryMTUBillingCycle dto) {
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(dto.getKey().getVirtualECCId());
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        dto.setCalendarKey(CalendarUtils.keyFromMillis(dto.getStartEpoch() * 1000, timeZone));
        dto.setDemandPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getDemandPeakTime() * 1000, timeZone));
        dto.setPeakVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getPeakVoltageTime() * 1000, timeZone));
        dto.setMinVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getMinVoltageTime() * 1000, timeZone));

    }

    public HistoryMTUBillingCycle findOne(HistoryMTUBillingCycleKey key) {
        try {
            HistoryMTUBillingCycle historyDay = hazelcastService.getHistoryMTUBillingCycleMap().get(key);
            if (historyDay == null) {
                LOGGER.debug("DATABASE HIT<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                String query = "select *  from history_mtu_billingCycle where virtualECC_id=? and mtu_id=? " +
                        " and billingCycleYear=? " +
                        " and billingCycleMonth = ? ";
                historyDay = getJdbcTemplate().queryForObject(query,
                        new Object[]{
                                key.getVirtualECCId(),
                                key.getMtuId(),
                                key.getBillingCycleYear(),
                                key.getBillingCycleMonth()
                        },
                        rowMapper);
                if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0)
                    hazelcastService.getHistoryMTUBillingCycleMap().put(key, historyDay);
            }
            return historyDay;
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void insert(HistoryMTUBillingCycle dto) {
        processBatch();
        try {
            LOGGER.debug("RUNNING INSERT QUERY: {}", dto);
            if (!PollingService.skipCache) {
                updateCalendarKeys(dto);
                hazelcastService.getHistoryMTUBillingCycleMap().put(dto.getKey(), dto);
            }
            //getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(dto));
            queueForBatch(dto.getKey(), HistoryKey.BatchState.INSERT, dto);
        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", dto, ex);
        }
    }

    public void save(HistoryMTUBillingCycle dto) {
        processBatch();
        try {
            LOGGER.debug("RUNNING SAVE QUERY: {}", dto);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(dto);
                hazelcastService.getHistoryMTUBillingCycleMap().put(dto.getKey(), dto);
            }
            //getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(dto));
            queueForBatch(dto.getKey(), HistoryKey.BatchState.UPDATE, dto);
        } catch (Exception ex) {
            LOGGER.error("Error updating record: {}", dto, ex);
        }
    }

    public List<HistoryMTUBillingCycle> findByDateRange(long virtualECCId, long mtu_id, long startEpoch, long endEpoch) {
        Map<String, Object> namedParameters = new HashMap(24);
        namedParameters.put("virtualECCId", virtualECCId);
        namedParameters.put("mtuId", mtu_id);
        namedParameters.put("startEpoch", startEpoch);
        namedParameters.put("endEpoch", endEpoch);
        return getNamedParameterJdbcTemplate().query(FIND_BY_DATE_RANGE_QUERY, namedParameters, rowMapper);
    }

    static final int BATCH_SIZE = 20000;
    Cache<HistoryMTUBillingCycleKey,HistoryKey.BatchState> batchStateCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();
    Cache<HistoryMTUBillingCycleKey, HistoryMTUBillingCycle> batch = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();

    synchronized void queueForBatch(HistoryMTUBillingCycleKey key, HistoryKey.BatchState batchState, HistoryMTUBillingCycle dto){
        if (batchStateCache.getIfPresent(key) == null){
            batchStateCache.put(key, batchState);
            batch.put(key, dto);
        } else {
            batch.put(key, dto);
        }
    }

    long lastBatchPost = System.currentTimeMillis()/1000L;

    @Autowired
    ServerService serverService;

    public synchronized void processBatch(){
        long secondsSinceLastPost = (System.currentTimeMillis()/1000L) - lastBatchPost;

        if (batch.size() != BATCH_SIZE && secondsSinceLastPost < (60*15) && serverService.isKeepRunning()) return; //Hold off o
        lastBatchPost = System.currentTimeMillis()/1000L;



        List<HistoryMTUBillingCycle> insertRecords = new ArrayList<>(BATCH_SIZE);
        List<HistoryMTUBillingCycle> updateRecords = new ArrayList<>(BATCH_SIZE);
        for (HistoryMTUBillingCycleKey key: batch.asMap().keySet()){
            HistoryKey.BatchState state = batchStateCache.getIfPresent(key);
            if (state != null){
                if (state == HistoryKey.BatchState.INSERT){
                    insertRecords.add(batch.getIfPresent(key));
                } else {
                    updateRecords.add(batch.getIfPresent(key));
                }


            } else {
                LOGGER.warn("KEY NOT FOUND: {}", key);
            }
        }

        //Batch Insert
        LOGGER.info("BULK INSERT:{}", insertRecords.size());
        batchUpdate(INSERT, insertRecords);


        //Batch Update
        LOGGER.info("BULK UPDATE:{}", updateRecords.size());
        batchUpdate(SAVE,updateRecords);

        batchStateCache.invalidateAll();
        batch.invalidateAll();
    }


    private void batchUpdate(String query, List<HistoryMTUBillingCycle> batchList) {
        Map<String, ?>[] maps = new Map[batchList.size()];
        for (int i=0; i < batchList.size(); i++){
            maps[i] = getNamedParameters(batchList.get(i));
        }
        try {
            getNamedParameterJdbcTemplate().batchUpdate(query, maps);
        } catch(DuplicateKeyException dex){
            LOGGER.error("Duplicate Key on insert: {}", dex.getMessage());
        }
    }

    public void deleteByVirtualEcc(Long virtualECCId, Long mtuId) {
        getJdbcTemplate().update("DELETE FROM history_mtu_billingCycle where virtualECC_id=? and mtu_id=? ", new Object[]{virtualECCId, mtuId});
    }
}
