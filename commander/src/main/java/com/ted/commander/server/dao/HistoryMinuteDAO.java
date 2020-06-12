/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryKey;
import com.ted.commander.common.model.history.HistoryMinute;
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
public class HistoryMinuteDAO extends SimpleAbstractDAO {

    public static final String FIND_AFTER_DATE = "select * from history_minute2 where virtualECC_id = ? and startEpoch > ? order by startEpoch limit 1";
    public static final String FIND_MOST_RECENT = "select * from history_minute2  where virtualECC_id = ?  and mtuCount = ? and startEpoch >= ? order by startEpoch desc limit 1";
    public static final String FIND_LAST_POST = "select * from history_minute2  where virtualECC_id = ? and startEpoch < ? order by startEpoch desc limit 1";
    public static final String FIND_LAST_COMPLETED_POST = "select * from history_minute2  where virtualECC_id = ? and startEpoch < ? and mtuCount = ? order by startEpoch desc limit 1";
    public static final String FIND_AVERAGE_POWER = "select avg(net) as avgPower from history_minute2 where virtualECC_id = ?  and dow = ? and startEpoch >= ? and startEpoch < ? and mtuCount=?";
    public static final String FIND_TODAY_MINUTES = "select * from history_minute2 where virtualECC_id = ? and minutesInDay(startEpoch, ?) <= ? and dow = ? and startEpoch >= ? and startEpoch < ? and mtuCount=? order by startEpoch";

    static final int BATCH_SIZE = 20000;
    Cache<HistoryKey,HistoryKey.BatchState> minuteStateCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();
    Cache<HistoryKey, HistoryMinute> batch = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();
    

    synchronized void queueForBatch(HistoryKey key, HistoryKey.BatchState batchState, HistoryMinute historyMinute){
        if (minuteStateCache.getIfPresent(key) == null){
            minuteStateCache.put(key, batchState);
            batch.put(key, historyMinute);
        } else {
            batch.put(key, historyMinute);
        }
    }

    long lastBatchPost = System.currentTimeMillis()/1000L;

    @Autowired
    ServerService serverService;

    public synchronized void processBatch(){
        long secondsSinceLastPost = (System.currentTimeMillis()/1000L) - lastBatchPost;

        if (batch.size() != BATCH_SIZE  && secondsSinceLastPost < 300 && serverService.isKeepRunning()) return; //Hold off o
        lastBatchPost = System.currentTimeMillis()/1000L;

        List<HistoryMinute> insertRecords = new ArrayList<>(BATCH_SIZE);
        List<HistoryMinute> updateRecords = new ArrayList<>(BATCH_SIZE);
        for (HistoryKey key: minuteStateCache.asMap().keySet()){
            HistoryKey.BatchState state = minuteStateCache.getIfPresent(key);
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

        minuteStateCache.invalidateAll();
        batch.invalidateAll();
    }


    private void batchUpdate(String query, List<HistoryMinute> batchList) {
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


    private static final String INSERT = "INSERT INTO history_minute2 " +
            "(virtualECC_id, " +
            "startEpoch, " +
            "net, " +
            "loadValue, " +
            "generation, " +
            "demandPeak, " +
            "loadPeak, " +
            "generationPeak, " +
            "voltageTotal, " +
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
            "pfSampleCount," +
            "rateInEffect, " +
            "netCost, " +
            "loadCost, " +
            "genCost, " +
            "dow, " +
            "runningNetTotal) " +
            "VALUES " +
            "(:virtualECC_id, " +
            ":startEpoch, " +
            ":net, " +
            ":loadValue, " +
            ":generation, " +
            ":demandPeak, " +
            ":loadPeak, " +
            ":generationPeak, " +
            ":voltageTotal, " +
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
            ":pfSampleCount," +
            ":rateInEffect, " +
            ":netCost, " +
            ":loadCost, " +
            ":genCost, " +
            ":dow, " +
            ":runningNetTotal)";



    
    private static final String SAVE = "UPDATE history_minute2" +
            "  SET " +
            "  net =  :net, " +
            "  loadValue =  :loadValue, " +
            "  generation =  :generation, " +
            "  demandPeak =  :demandPeak, " +
            "  loadPeak =  :loadPeak, " +
            "  generationPeak =  :generationPeak, " +
            "  voltageTotal =  :voltageTotal, " +
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
            "  pfSampleCount =  :pfSampleCount, " +
            "  rateInEffect =  :rateInEffect, " +
            "  netCost =  :netCost, " +
            "  loadCost =  :loadCost, " +
            "  genCost =  :genCost, " +
            "  dow =  :dow, " +
            "  runningNetTotal =  :runningNetTotal " +
            "  WHERE virtualECC_id =  :virtualECC_id " +
            "  AND startEpoch =  :startEpoch ";




    
    private static final String FIND_BY_DATE_RANGE_QUERY = "select * from history_minute2 where virtualECC_id=? and startEpoch >= ? and startEpoch < ? order by startEpoch";
    @Autowired
    HazelcastService hazelcastService;
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<HistoryMinute> rowMapper = new RowMapper<HistoryMinute>() {
        public HistoryMinute mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryMinute dto = new HistoryMinute();

            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setStartEpoch(rs.getLong("startEpoch"));
            dto.setNet(rs.getDouble("net"));
            dto.setLoad(rs.getDouble("loadValue"));
            dto.setGeneration(rs.getDouble("generation"));

            dto.setDemandPeak(rs.getDouble("demandPeak"));
            dto.setDemandPeakTime(rs.getLong("startEpoch"));
            dto.setLoadPeak(rs.getDouble("loadPeak"));
            dto.setLoadPeakTime(rs.getLong("startEpoch"));
            dto.setGenerationPeak(rs.getDouble("generationPeak"));
            dto.setGenerationPeakTime(rs.getLong("startEpoch"));


            dto.setVoltageTotal(rs.getDouble("voltageTotal"));

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

            dto.setRunningNetTotal(rs.getDouble("runningNetTotal"));

            dto.setRateInEffect(rs.getDouble("rateInEffect"));
            dto.setNetCost(rs.getDouble("netCost"));
            dto.setLoadCost(rs.getDouble("loadCost"));
            dto.setGenCost(rs.getDouble("genCost"));
            dto.setDow(rs.getInt("dow"));


            updateCalendarKeys(dto);

            return dto;
        }
    };

    private Map getNamedParameters(HistoryMinute dto) {
        Map<String, Object> namedParameters = new HashMap(64);
        namedParameters.put("virtualECC_id", dto.getVirtualECCId());
        namedParameters.put("startEpoch", dto.getStartEpoch());
        namedParameters.put("net", dto.getNet());
        namedParameters.put("loadValue", dto.getLoad());
        namedParameters.put("generation", dto.getGeneration());
        namedParameters.put("demandPeak", dto.getDemandPeak());
        namedParameters.put("loadPeak", dto.getLoadPeak());
        namedParameters.put("generationPeak", dto.getGenerationPeak());
        namedParameters.put("voltageTotal", dto.getVoltageTotal());
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
        namedParameters.put("runningNetTotal", dto.getRunningNetTotal());

        namedParameters.put("rateInEffect", dto.getRateInEffect());
        namedParameters.put("netCost", dto.getNetCost());
        namedParameters.put("genCost", dto.getGenCost());
        namedParameters.put("loadCost", dto.getLoadCost());

        namedParameters.put("dow", dto.getDow());

        for (String s : namedParameters.keySet()) {
            Object o = namedParameters.get(s);
            if (o instanceof Double) namedParameters.put(s, forceZero((double) o));
        }

        return namedParameters;
    }

    private void updateCalendarKeys(HistoryMinute dto) {
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(dto.getVirtualECCId());
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        dto.setCalendarKey(CalendarUtils.keyFromMillis(dto.getStartEpoch() * 1000, timeZone));
        dto.setDemandPeakCalendarKey(dto.getCalendarKey());
        dto.setLoadPeakCalendarKey(dto.getCalendarKey());
        dto.setGenerationPeakCalendarKey(dto.getCalendarKey());
    }

    public HistoryMinute findOne(Long virtualECCId, Long startEpoch) {
        try {
            HistoryKey key = new HistoryKey(virtualECCId, startEpoch);
            HistoryMinute dto = hazelcastService.getHistoryMinuteMap().get(key);
            if (dto == null) {
                LOGGER.debug("DATABASE HIT<<<: {}", key);
                String query = "select *  from history_minute2 where virtualECC_id=? and startEpoch=?";
                dto = getJdbcTemplate().queryForObject(query, new Object[]{virtualECCId, startEpoch}, rowMapper);
                if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                    if (dto != null) {
                        //Clear out old key if we are looking for a new one.
                        HistoryKey oldKey = new HistoryKey(virtualECCId, startEpoch - 600L);
                        hazelcastService.getHistoryMinuteMap().invalidate(oldKey);
                        hazelcastService.getHistoryMinuteMap().put(key, dto);
                    }
                }
            } else {
                LOGGER.debug("CACHE HIT<<<: {}", key);
            }
            return dto;
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public void insert(HistoryMinute dto) {
        processBatch();
        HistoryKey key = new HistoryKey(dto.getVirtualECCId(), dto.getStartEpoch());

        try {
            LOGGER.debug("RUNNING INSERT QUERY: {}", dto);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(dto);
                hazelcastService.getHistoryMinuteMap().put(key, dto);
            }
            queueForBatch(key, HistoryKey.BatchState.INSERT, dto);
            //getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(dto));
        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", dto, ex);
        }
    }

    public void save(HistoryMinute dto) {
        processBatch();
        HistoryKey key = new HistoryKey(dto.getVirtualECCId(), dto.getStartEpoch());
        try {
            LOGGER.debug("RUNNING SAVE QUERY: {}", dto);
            if (PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(dto);
                hazelcastService.getHistoryMinuteMap().put(key, dto);
            }
            //getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(dto));
            queueForBatch(key, HistoryKey.BatchState.UPDATE, dto);
            LOGGER.debug("COMPLETED SAVE QUERY: {}", dto);
        } catch (Exception ex) {
            LOGGER.error("Error updating {}", dto, ex);
        }
    }

    public HistoryMinute findFirstAfterDate(Long virtualECCId, long startDate) {
        try {
            return getJdbcTemplate().queryForObject(FIND_AFTER_DATE, new Object[]{virtualECCId, startDate}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public List<HistoryMinute> findByDateRange(long virtualECCId, long startEpoch, long endEpoch) {
        List<HistoryMinute> list = new ArrayList<>();
        list = getJdbcTemplate().query(FIND_BY_DATE_RANGE_QUERY, new Object[]{virtualECCId, startEpoch, endEpoch}, rowMapper);
        return list;
    }

    public HistoryMinute findMostRecent(long virtualECCId, long startEpoch, int mtuCount) {
        try {
            return getJdbcTemplate().queryForObject(FIND_MOST_RECENT, new Object[]{virtualECCId, mtuCount, startEpoch}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned: v:{} m:{} se:{}", virtualECCId, mtuCount, startEpoch);
            return null;
        }
    }

    public HistoryMinute findLastPost(long virtualECCId, long startEpoch) {
        try {
            return getJdbcTemplate().queryForObject(FIND_LAST_POST, new Object[]{virtualECCId, startEpoch}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public HistoryMinute findLastCompletedPost(long virtualECCId, long startEpoch, int mtuCount) {
        try {
            return getJdbcTemplate().queryForObject(FIND_LAST_COMPLETED_POST, new Object[]{virtualECCId, startEpoch, mtuCount}, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    public double findAveragePower(Long virtualEccId, long startEpoch, TimeZone timeZone, int mtuCount) {
        Calendar avgDayCalender = Calendar.getInstance(timeZone);
        avgDayCalender.setTimeInMillis(startEpoch * 1000);
        int dow = avgDayCalender.get(Calendar.DAY_OF_WEEK);
        int hour = avgDayCalender.get(Calendar.HOUR_OF_DAY);
        int minute = avgDayCalender.get(Calendar.MINUTE);
        avgDayCalender.add(Calendar.MONTH, -2);
        long startAvgEpoch = avgDayCalender.getTimeInMillis() / 1000;
        try {
            //LOGGER.info(">>>>>>>>>>>>>>FIND AVERAGE POWER: {} {} {} {} {} {} {}", virtualEccId, minute, hour, dow, startAvgEpoch, startEpoch, mtuCount);
            return getJdbcTemplate().queryForObject(FIND_AVERAGE_POWER, new Object[]{virtualEccId, dow, startAvgEpoch, startEpoch, mtuCount}, Double.class).doubleValue();
        } catch (Exception ex) {
            LOGGER.error("EX: ", ex);
            return 0.0;
        }
    }

    public List<HistoryMinute> findPastMinutesForAverage(long virtualECCId, long timeOfDayEpoch, long dayStartEpoch, TimeZone timeZone, int mtuCount) {
        Calendar dowCalendar = Calendar.getInstance(timeZone);
        dowCalendar.setTimeInMillis(dayStartEpoch * 1000);
        int dow = dowCalendar.get(Calendar.DAY_OF_WEEK);
        dowCalendar.add(Calendar.MONTH, -2);
        long queryStartDate = dowCalendar.getTimeInMillis() / 1000;
        Calendar maxMinuteCalendar = Calendar.getInstance(timeZone);
        maxMinuteCalendar.setTimeInMillis(timeOfDayEpoch * 1000);
        int maxMin = (maxMinuteCalendar.get(Calendar.HOUR_OF_DAY) * 60) + maxMinuteCalendar.get(Calendar.MINUTE);
        return getJdbcTemplate().query(FIND_TODAY_MINUTES, new Object[]{virtualECCId, timeZone.getID(), maxMin, dow, queryStartDate, dayStartEpoch, mtuCount}, rowMapper);
    }

    //Synchronized in case multiple ECC's are posting ot the same location at the exact same time so we don't loose the record.
    public synchronized HistoryMinute findOne(Long virtualECCId, Long startEpoch, TimeZone timeZone) {
        HistoryMinute historyMinute = findOne(virtualECCId, startEpoch);
        if (historyMinute == null) {
            LOGGER.debug("Creating new HM:v{} s:{}", virtualECCId, startEpoch);
            historyMinute = new HistoryMinute();
            historyMinute.setVirtualECCId(virtualECCId);
            historyMinute.setStartEpoch(startEpoch);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(historyMinute.getStartEpoch() * 1000);
            historyMinute.setDow(calendar.get(Calendar.DAY_OF_WEEK));
            insert(historyMinute);

            //Add to Cache
            HistoryKey key = new HistoryKey(virtualECCId, startEpoch);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                hazelcastService.getHistoryMinuteMap().put(key, historyMinute);
            }
        } else {
            LOGGER.debug("found HM:{}", historyMinute);
        }
        return historyMinute;
    }

    public void deleteByVirtualECC(Long virtualECCId) {
        getJdbcTemplate().update("DELETE FROM history_minute2 where virtualECC_id=?", new Object[]{virtualECCId});
    }
}