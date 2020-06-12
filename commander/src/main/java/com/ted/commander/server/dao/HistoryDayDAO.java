/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryKey;
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
public class HistoryDayDAO extends SimpleAbstractDAO {

    public static final String MOST_RECENT = "select * from history_day2 where virtualECC_id =? and startEpoch < ? order by startEpoch desc limit 1";
    private static final String INSERT = "INSERT INTO history_day2 " +
            "(virtualECC_id, " +
            "startEpoch, " +
            "endEpoch, " +
            "billingCycleMonth, " +
            "net, " +
            "loadValue, " +
            "generation, " +
            "demandPeak, " +
            "demandPeakTime, " +
            "loadPeak, " +
            "loadPeakTime, " +
            "generationPeak, " +
            "generationPeakTime, " +
            "minVoltage, " +
            "minVoltageTime, " +
            "peakVoltage, " +
            "peakVoltageTime, " +
            "pfTotal, " +
            "mtuCount, " +
            "touOffPeakNet, " +
            "touPeakNet, " +
            "touMidPeakNet, " +
            "touSuperPeakNet, " +
            "touOffPeakGen, " +
            "touPeakGen, " +
            "touMidPeakGen, " +
            "touSuperPeakGen, " +
            "touOffPeakLoad, " +
            "touPeakLoad, " +
            "touMidPeakLoad, " +
            "touSuperPeakLoad, " +
            "rateInEffect, " +
            "netCost, " +
            "loadCost, " +
            "genCost, " +
            "pfSampleCount) " +
            "VALUES " +
            "(:virtualECC_id, " +
            ":startEpoch, " +
            ":endEpoch, " +
            ":billingCycleMonth, " +
            ":net, " +
            ":loadValue, " +
            ":generation, " +
            ":demandPeak, " +
            ":demandPeakTime, " +
            ":loadPeak, " +
            ":loadPeakTime, " +
            ":generationPeak, " +
            ":generationPeakTime, " +
            ":minVoltage, " +
            ":minVoltageTime, " +
            ":peakVoltage, " +
            ":peakVoltageTime, " +
            ":pfTotal, " +
            ":mtuCount, " +
            ":touOffPeakNet, " +
            ":touPeakNet, " +
            ":touMidPeakNet, " +
            ":touSuperPeakNet, " +
            ":touOffPeakGen, " +
            ":touPeakGen, " +
            ":touMidPeakGen, " +
            ":touSuperPeakGen, " +
            ":touOffPeakLoad, " +
            ":touPeakLoad, " +
            ":touMidPeakLoad, " +
            ":touSuperPeakLoad, " +
            ":rateInEffect, " +
            ":netCost, " +
            ":loadCost, " +
            ":genCost, " +
            ":pfSampleCount)";
    private static final String SAVE = "UPDATE history_day2" +
            "  SET " +
            "  endEpoch =  :endEpoch, " +
            "  billingCycleMonth =  :billingCycleMonth, " +
            "  net =  :net, " +
            "  loadValue =  :loadValue, " +
            "  generation =  :generation, " +
            "  demandPeak =  :demandPeak, " +
            "  demandPeakTime =  :demandPeakTime, " +
            "  loadPeak =  :loadPeak, " +
            "  loadPeakTime =  :loadPeakTime, " +
            "  generationPeak =  :generationPeak, " +
            "  generationPeakTime =  :generationPeakTime, " +
            "  minVoltage =  :minVoltage, " +
            "  minVoltageTime =  :minVoltageTime, " +
            "  peakVoltage =  :peakVoltage, " +
            "  peakVoltageTime =  :peakVoltageTime, " +
            "  pfTotal =  :pfTotal, " +
            "  mtuCount =  :mtuCount, " +
            "  touOffPeakNet =  :touOffPeakNet, " +
            "  touPeakNet =  :touPeakNet, " +
            "  touMidPeakNet =  :touMidPeakNet, " +
            "  touSuperPeakNet =  :touSuperPeakNet, " +
            "  touOffPeakGen =  :touOffPeakGen, " +
            "  touPeakGen =  :touPeakGen, " +
            "  touMidPeakGen =  :touMidPeakGen, " +
            "  touSuperPeakGen =  :touSuperPeakGen, " +
            "  touOffPeakLoad =  :touOffPeakLoad, " +
            "  touPeakLoad =  :touPeakLoad, " +
            "  touMidPeakLoad =  :touMidPeakLoad, " +
            "  touSuperPeakLoad =  :touSuperPeakLoad, " +
            "  rateInEffect =  :rateInEffect, " +
            "  netCost =  :netCost, " +
            "  loadCost =  :loadCost, " +
            "  genCost =  :genCost, " +
            "  pfSampleCount =  :pfSampleCount " +
            "  WHERE virtualECC_id =  :virtualECC_id " +
            " AND startEpoch =  :startEpoch ";
    private static final String FIND_BY_DATE_RANGE_QUERY = "select * from history_day2 where virtualECC_id=? and startEpoch >= ? and startEpoch < ? order by startEpoch";
    @Autowired
    HazelcastService hazelcastService;
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<HistoryDay> rowMapper = new RowMapper<HistoryDay>() {
        public HistoryDay mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryDay dto = new HistoryDay();

            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setStartEpoch(rs.getLong("startEpoch"));
            dto.setEndEpoch(rs.getLong("endEpoch"));
            dto.setNet(rs.getDouble("net"));
            dto.setLoad(rs.getDouble("loadValue"));
            dto.setGeneration(rs.getDouble("generation"));

            dto.setDemandPeak(rs.getDouble("demandPeak"));
            dto.setDemandPeakTime(rs.getLong("demandPeakTime"));
            dto.setLoadPeak(rs.getDouble("loadPeak"));
            dto.setLoadPeakTime(rs.getLong("loadPeakTime"));
            dto.setGenerationPeak(rs.getDouble("generationPeak"));
            dto.setGenerationPeakTime(rs.getLong("generationPeakTime"));

            dto.setMinVoltage(rs.getDouble("minVoltage"));
            dto.setMinVoltageTime(rs.getLong("minVoltageTime"));
            dto.setPeakVoltage(rs.getDouble("peakVoltage"));
            dto.setPeakVoltageTime(rs.getLong("peakVoltageTime"));

            dto.setPfTotal(rs.getDouble("pfTotal"));
            dto.setPfSampleCount(rs.getLong("pfSampleCount"));
            dto.setMtuCount(rs.getInt("mtuCount"));

            dto.setTouOffPeakNet(rs.getDouble("touOffPeakNet"));
            dto.setTouOffPeakGen(rs.getDouble("touOffPeakGen"));
            dto.setTouOffPeakLoad(rs.getDouble("touOffPeakLoad"));

            dto.setTouPeakNet(rs.getDouble("touPeakNet"));
            dto.setTouPeakGen(rs.getDouble("touPeakGen"));
            dto.setTouPeakLoad(rs.getDouble("touPeakLoad"));

            dto.setTouMidPeakNet(rs.getDouble("touMidPeakNet"));
            dto.setTouMidPeakGen(rs.getDouble("touMidPeakGen"));
            dto.setTouMidPeakLoad(rs.getDouble("touMidPeakLoad"));

            dto.setTouSuperPeakNet(rs.getDouble("touSuperPeakNet"));
            dto.setTouSuperPeakGen(rs.getDouble("touSuperPeakGen"));
            dto.setTouSuperPeakLoad(rs.getDouble("touSuperPeakLoad"));

            dto.setRateInEffect(rs.getDouble("rateInEffect"));
            dto.setNetCost(rs.getDouble("netCost"));
            dto.setLoadCost(rs.getDouble("loadCost"));
            dto.setGenCost(rs.getDouble("genCost"));
            dto.setBillingCycleMonth(rs.getInt("billingCycleMonth"));
            updateCalendarKeys(dto);
            return dto;
        }
    };

    private Map getNamedParameters(HistoryDay dto) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("virtualECC_id", dto.getVirtualECCId());
        namedParameters.put("startEpoch", dto.getStartEpoch());
        namedParameters.put("endEpoch", dto.getEndEpoch());
        namedParameters.put("billingCycleMonth", dto.getBillingCycleMonth());
        namedParameters.put("net", dto.getNet());
        namedParameters.put("loadValue", dto.getLoad());
        namedParameters.put("generation", dto.getGeneration());
        namedParameters.put("demandPeak", dto.getDemandPeak());
        namedParameters.put("demandPeakTime", dto.getDemandPeakTime());
        namedParameters.put("loadPeak", dto.getLoadPeak());
        namedParameters.put("loadPeakTime", dto.getLoadPeakTime());
        namedParameters.put("generationPeak", dto.getGenerationPeak());
        namedParameters.put("generationPeakTime", dto.getGenerationPeakTime());

        if (dto.getMinVoltage() < 0) namedParameters.put("minVoltage", 0.0);
        else if (dto.getMinVoltage() > 999.0) namedParameters.put("minVoltage", 999.0);
        else namedParameters.put("minVoltage", dto.getMinVoltage());

        namedParameters.put("minVoltageTime", dto.getMinVoltageTime());

        if (dto.getPeakVoltage() < 0) namedParameters.put("peakVoltage", 120.0);
        else if (dto.getPeakVoltage() > 999.0) namedParameters.put("peakVoltage", 999.0);
        else namedParameters.put("peakVoltage", dto.getPeakVoltage());

        namedParameters.put("peakVoltageTime", dto.getPeakVoltageTime());
        namedParameters.put("pfTotal", dto.getPfTotal());
        namedParameters.put("pfSampleCount", dto.getPfSampleCount());
        namedParameters.put("mtuCount", dto.getMtuCount());
        namedParameters.put("touOffPeakNet", dto.getTouOffPeakNet());
        namedParameters.put("touOffPeakGen", dto.getTouOffPeakGen());
        namedParameters.put("touOffPeakLoad", dto.getTouOffPeakLoad());
        namedParameters.put("touPeakNet", dto.getTouPeakNet());
        namedParameters.put("touPeakGen", dto.getTouPeakGen());
        namedParameters.put("touPeakLoad", dto.getTouPeakLoad());
        namedParameters.put("touMidPeakNet", dto.getTouMidPeakNet());
        namedParameters.put("touMidPeakGen", dto.getTouMidPeakGen());
        namedParameters.put("touMidPeakLoad", dto.getTouMidPeakLoad());
        namedParameters.put("touSuperPeakNet", dto.getTouSuperPeakNet());
        namedParameters.put("touSuperPeakGen", dto.getTouSuperPeakGen());
        namedParameters.put("touSuperPeakLoad", dto.getTouSuperPeakLoad());

        namedParameters.put("rateInEffect", dto.getRateInEffect());
        namedParameters.put("netCost", dto.getNetCost());
        namedParameters.put("genCost", dto.getGenCost());
        namedParameters.put("loadCost", dto.getLoadCost());


        for (String s : namedParameters.keySet()) {
            Object o = namedParameters.get(s);
            if (o instanceof Double) namedParameters.put(s, forceZero((double) o));
        }

        return namedParameters;
    }

    private void updateCalendarKeys(HistoryDay dto) {
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(dto.getVirtualECCId());
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        dto.setCalendarKey(CalendarUtils.keyFromMillis(dto.getStartEpoch() * 1000, timeZone));
        dto.setDemandPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getDemandPeakTime() * 1000, timeZone));
        dto.setMinVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getMinVoltageTime() * 1000, timeZone));
        dto.setPeakVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getPeakVoltageTime() * 1000, timeZone));
        dto.setLoadPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getLoadPeakTime() * 1000, timeZone));
        dto.setGenerationPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getGenerationPeakTime() * 1000, timeZone));
    }

    public HistoryDay findOne(Long virtualECCId, Long startEpoch) {
        HistoryKey key = new HistoryKey(virtualECCId, startEpoch);
        try {
            HistoryDay historyDay = hazelcastService.getHistoryDayMap().get(key);
            if (historyDay == null) {
                LOGGER.debug("DATABASE HIT<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                String query = "select *  from history_day2 where virtualECC_id=? and startEpoch=?";
                historyDay = getJdbcTemplate().queryForObject(query,
                        new Object[]{virtualECCId, startEpoch},
                        rowMapper);
                if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0)
                    hazelcastService.getHistoryDayMap().put(key, historyDay);
            }
            return historyDay;
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void insert(HistoryDay historyDay) {
        processBatch();
        try {
            LOGGER.debug("RUNNING INSERT QUERY: {}", historyDay);
            HistoryKey key = new HistoryKey(historyDay.getVirtualECCId(), historyDay.getStartEpoch());

            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(historyDay);

                hazelcastService.getHistoryDayMap().put(key, historyDay);
            }
            //getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(historyDay));
            queueForBatch(key, HistoryKey.BatchState.INSERT, historyDay);

        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", historyDay, ex);
        }
    }

    public void save(HistoryDay historyDay) {
        processBatch();
        try {
            HistoryKey key = new HistoryKey(historyDay.getVirtualECCId(), historyDay.getStartEpoch());
            LOGGER.debug("RUNNING SAVE QUERY: {}", historyDay);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(historyDay);
                hazelcastService.getHistoryDayMap().put(key, historyDay);
            }
            //getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(historyDay));
            queueForBatch(key, HistoryKey.BatchState.UPDATE, historyDay);
        } catch (Exception ex) {
            LOGGER.error("Error saving record: {}", historyDay, ex);
        }
    }

    public List<HistoryDay> findByDateRange(long virtualECCId, long startEpoch, long endEpoch) {
        List<HistoryDay> list = new ArrayList<>();
        list = getJdbcTemplate().query(FIND_BY_DATE_RANGE_QUERY, new Object[]{virtualECCId, startEpoch, endEpoch}, rowMapper);
        return list;
    }

    public HistoryDay findOne(Long virtualECCid, long startEpoch) {
        try {
            HistoryDay historyDay = null; //TODO: Epoch to key
            if (historyDay == null) {
                String query = "select *  from history_day2 where virtualECC_id=? and startEpoch=? ";
                historyDay = getJdbcTemplate().queryForObject(query,
                        new Object[]{
                                virtualECCid,
                                startEpoch},
                        rowMapper);
                if (!PollingService.skipCache && historyDay != null) {
                    hazelcastService.getHistoryDayMap().put(new HistoryKey(historyDay.getVirtualECCId(), historyDay.getVirtualECCId()), historyDay);
                }
            }
            return historyDay;
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public HistoryDay findMostRecent(Long virtualECCId) {
        try {
            return getJdbcTemplate().queryForObject(MOST_RECENT, new Object[]{virtualECCId, System.currentTimeMillis() / 1000}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.info("Could not find recent record for {}-{}", virtualECCId, System.currentTimeMillis() / 1000);
            return null;
        }
    }

    final int BATCH_SIZE = 10000;
    Cache<HistoryKey,HistoryKey.BatchState> batchStateCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();
    Cache<HistoryKey, HistoryDay> batch = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();


    synchronized void queueForBatch(HistoryKey key, HistoryKey.BatchState batchState, HistoryDay dto){
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

        List<HistoryDay> insertRecords = new ArrayList<>(BATCH_SIZE);
        List<HistoryDay> updateRecords = new ArrayList<>(BATCH_SIZE);
        for (HistoryKey key: batchStateCache.asMap().keySet()){
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


    private void batchUpdate(String query, List<HistoryDay> batchList) {
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

    public void deleteByVirtualECC(Long virtualECCId) {
        getJdbcTemplate().update("DELETE FROM history_day2 where virtualECC_id=?", new Object[]{virtualECCId});
    }
}
