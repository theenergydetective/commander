/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryKey;
import com.ted.commander.common.model.history.HistoryMTUHour;
import com.ted.commander.common.model.history.HistoryMTUKey;
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
public class HistoryMTUHourDAO extends SimpleAbstractDAO {

    private static final String INSERT = "INSERT INTO history_mtu_hour2 " +
            "(virtualECC_id, " +
            "mtu_id, " +
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
    private static final String SAVE = "UPDATE history_mtu_hour2" +
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
            "  AND startEpoch = :startEpoch ";
    private static final String FIND_BY_DATE_RANGE_QUERY = "select * from history_mtu_hour2 where virtualECC_id=? and mtu_id = ? and startEpoch >= ? and startEpoch <= ? order by startEpoch";
    @Autowired
    HazelcastService hazelcastService;
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<HistoryMTUHour> rowMapper = new RowMapper<HistoryMTUHour>() {
        public HistoryMTUHour mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryMTUHour dto = new HistoryMTUHour();

            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setMtuId(rs.getLong("mtu_id"));
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

    private Map getNamedParameters(HistoryMTUHour dto) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("virtualECC_id", dto.getVirtualECCId());
        namedParameters.put("mtu_id", dto.getMtuId());
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

        //namedParameters.put("peakVoltage", dto.getPeakVoltage());

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

    private void updateCalendarKeys(HistoryMTUHour dto) {
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(dto.getVirtualECCId());
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        dto.setCalendarKey(CalendarUtils.keyFromMillis(dto.getStartEpoch() * 1000, timeZone));
        dto.setDemandPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getDemandPeakTime() * 1000, timeZone));
        dto.setPeakVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getPeakVoltageTime() * 1000, timeZone));
        dto.setMinVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getMinVoltageTime() * 1000, timeZone));
    }

    public HistoryMTUHour findOne(Long virtualECCId, Long mtuId, Long startEpoch) {

        HistoryMTUKey key = new HistoryMTUKey(virtualECCId, mtuId, startEpoch);
        try {
            HistoryMTUHour historyMTUHour = hazelcastService.getHistoryMTUHourMap().get(key);
            if (historyMTUHour == null) {
                LOGGER.debug("DATABASE HIT<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                String query = "select *  from history_mtu_hour2 where virtualECC_id=? and mtu_id=? and startEpoch=?";
                historyMTUHour = getJdbcTemplate().queryForObject(query,
                        new Object[]{
                                key.getVirtualECCId(),
                                key.getMtuId(),
                                key.getStartEpoch()
                        },
                        rowMapper);
                if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {

                    HistoryMTUKey oldKey = new HistoryMTUKey(virtualECCId, mtuId, (startEpoch.longValue() - 7200L));
                    hazelcastService.getHistoryMTUHourMap().invalidate(oldKey);
                    hazelcastService.getHistoryMTUHourMap().put(key, historyMTUHour);
                }
            }
            return historyMTUHour;
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void insert(HistoryMTUHour dto) {
        processBatch();
        try {
            HistoryMTUKey key = new HistoryMTUKey(dto.getVirtualECCId(), dto.getMtuId(), dto.getStartEpoch());
            LOGGER.debug("RUNNING INSERT QUERY: {}", dto);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(dto);
                hazelcastService.getHistoryMTUHourMap().put(key, dto);
            }
            //getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(dto));
            queueForBatch(key, HistoryKey.BatchState.INSERT, dto);
        } catch (Exception ex) {
            LOGGER.error("Error inserting record:{}", dto, ex);
        }
    }

    public void save(HistoryMTUHour dto) {
        processBatch();
        HistoryMTUKey key = new HistoryMTUKey(dto.getVirtualECCId(), dto.getMtuId(), dto.getStartEpoch());
        try {
            LOGGER.debug("RUNNING SAVE QUERY: {}", dto);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(dto);
                hazelcastService.getHistoryMTUHourMap().put(key, dto);
            }
            queueForBatch(key, HistoryKey.BatchState.UPDATE, dto);
            //getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(dto));
        } catch (Exception ex) {
            LOGGER.error("Error updating record: {}", dto, ex);
        }
    }

    public List<HistoryMTUHour> findByDateRange(long virtualECCId, long mtu_id, long startEpoch, long endEpoch) {
        return getJdbcTemplate().query(FIND_BY_DATE_RANGE_QUERY, new Object[]{virtualECCId, mtu_id, startEpoch, endEpoch}, rowMapper);
    }

    static final int BATCH_SIZE = 20000;
    Cache<HistoryMTUKey,HistoryKey.BatchState> batchStateCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();
    Cache<HistoryMTUKey, HistoryMTUHour> batch = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();

    synchronized void queueForBatch(HistoryMTUKey key, HistoryKey.BatchState batchState, HistoryMTUHour dto){
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

        if (BATCH_SIZE != batch.size() && secondsSinceLastPost < (60*15) && serverService.isKeepRunning()) return; //Hold off o
        lastBatchPost = System.currentTimeMillis()/1000L;



        List<HistoryMTUHour> insertRecords = new ArrayList<>(BATCH_SIZE);
        List<HistoryMTUHour> updateRecords = new ArrayList<>(BATCH_SIZE);
        for (HistoryMTUKey key: batchStateCache.asMap().keySet()){
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


    private void batchUpdate(String query, List<HistoryMTUHour> batchList) {
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
        getJdbcTemplate().update("DELETE FROM history_mtu_hour2 where virtualECC_id=? and mtu_id=? ", new Object[]{virtualECCId,  mtuId});
    }
}
