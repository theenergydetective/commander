/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryBillingCycleKey;
import com.ted.commander.common.model.history.HistoryKey;
import com.ted.commander.server.services.HazelcastService;
import com.ted.commander.server.services.PollingService;
import com.ted.commander.server.services.ServerService;
import com.ted.commander.server.util.CalendarUtils;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
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
public class HistoryBillingCycleDAO extends SimpleAbstractDAO {

    public static final String MINUTES_SO_FAR = "select startEpoch from history_minute2 where virtualECC_id = ? and startEpoch >=? and startEpoch < ? order by startEpoch desc limit 1";
    private static final String INSERT = "INSERT INTO history_billingCycle " +
            "(virtualECC_id, " +
            "billingCycleMonth, " +
            "billingCycleYear, " +
            "startEpoch, " +
            "endEpoch, " +
            "net, " +
            "loadValue, " +
            "generation, " +
            "demandPeak, " +
            "demandPeakTime, " +
            "loadPeak, " +
            "loadPeakTime, " +
            "generationPeak, " +
            "generationPeakTime, " +
            "demandCost, " +
            "demandCostPeakTime, " +
            "demandCostPeak, " +
            "demandCostPeakTOU, " +
            "demandCostPeakTOUName, " +
            "minimumCharge, " +
            "fixedCharge, " +
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
            "pfSampleCount," +
            "meterReadDateChanged," +
            "meterReadStartDate) " +
            "VALUES " +
            "(:virtualECC_id, " +
            ":billingCycleMonth, " +
            ":billingCycleYear, " +
            ":startEpoch, " +
            ":endEpoch, " +
            ":net, " +
            ":loadValue, " +
            ":generation, " +
            ":demandPeak, " +
            ":demandPeakTime, " +
            ":loadPeak, " +
            ":loadPeakTime, " +
            ":generationPeak, " +
            ":generationPeakTime, " +
            ":demandCost, " +
            ":demandCostPeakTime, " +
            ":demandCostPeak, " +
            ":demandCostPeakTOU, " +
            ":demandCostPeakTOUName, " +
            ":minimumCharge, " +
            ":fixedCharge, " +
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
            ":pfSampleCount," +
            ":meterReadDateChanged," +
            ":meterReadStartDate)";
    private static final String SAVE = "UPDATE history_billingCycle " +
            "  SET " +
            "  net =  :net, " +
            "  loadValue =  :loadValue, " +
            "  generation =  :generation, " +
            "  demandPeak =  :demandPeak, " +
            "  demandPeakTime =  :demandPeakTime, " +
            "  loadPeak =  :loadPeak, " +
            "  loadPeakTime =  :loadPeakTime, " +
            "  generationPeak =  :generationPeak, " +
            "  generationPeakTime =  :generationPeakTime, " +
            "  demandCost =  :demandCost, " +
            "  demandCostPeakTime =  :demandCostPeakTime, " +
            "  demandCostPeak =  :demandCostPeak, " +
            "  demandCostPeakTOU =  :demandCostPeakTOU, " +
            "  demandCostPeakTOUName =  :demandCostPeakTOUName, " +
            "  minimumCharge =  :minimumCharge, " +
            "  fixedCharge =  :fixedCharge, " +
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
            "  pfSampleCount =  :pfSampleCount, " +
            "  endEpoch = :endEpoch, " +
            "  meterReadDateChanged = :meterReadDateChanged, " +
            "  meterReadStartDate = :meterReadStartDate " +
            "  WHERE virtualECC_id =  :virtualECC_id  AND billingCycleMonth =  :billingCycleMonth  AND billingCycleYear =  :billingCycleYear";
    private static final String FIND_BY_DATE_RANGE_QUERY = "select * from history_billingCycle where virtualECC_id=:virtualECCId and ((startEpoch >= :startEpoch and startEpoch < :endEpoch) or (:startEpoch >= startEpoch and :startEpoch < endEpoch) or (:endEpoch >= startEpoch and :endEpoch < endEpoch)) order by startEpoch";
    @Autowired
    HazelcastService hazelcastService;
    @Autowired
    VirtualECCDAO virtualECCDAO;


    private RowMapper<HistoryBillingCycle> rowMapper = new RowMapper<HistoryBillingCycle>() {
        public HistoryBillingCycle mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryBillingCycle dto = new HistoryBillingCycle();

            HistoryBillingCycleKey historyBillingCycleKey = new HistoryBillingCycleKey();
            historyBillingCycleKey.setVirtualECCId(rs.getLong("virtualECC_id"));
            historyBillingCycleKey.setBillingCycleYear(rs.getInt("billingCycleYear"));
            historyBillingCycleKey.setBillingCycleMonth(rs.getInt("billingCycleMonth"));
            dto.setKey(historyBillingCycleKey);


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


            dto.setDemandCost(rs.getDouble("demandCost"));
            dto.setDemandCostPeakTime(rs.getLong("demandCostPeakTime"));
            dto.setDemandCostPeak(rs.getDouble("demandCostPeak"));
            dto.setDemandCostPeakTOU(TOUPeakType.values()[rs.getInt("demandCostPeakTOU")]);
            dto.setDemandCostPeakTOUName(rs.getString("demandCostPeakTOUName"));

            dto.setMinimumCharge(rs.getDouble("minimumCharge"));
            dto.setFixedCharge(rs.getDouble("fixedCharge"));

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

            dto.setMeterReadStartDate(rs.getInt("meterReadStartDate"));
            dto.setMeterReadDateChanged(rs.getBoolean("meterReadDateChanged"));
            updateCalendarKeys(dto);
            return dto;
        }
    };

    private Map getNamedParameters(HistoryBillingCycle historyBillingCycle) {
        Map<String, Object> namedParameters = new HashMap(128);
        namedParameters.put("virtualECC_id", historyBillingCycle.getKey().getVirtualECCId());
        namedParameters.put("billingCycleMonth", historyBillingCycle.getKey().getBillingCycleMonth());
        namedParameters.put("billingCycleYear", historyBillingCycle.getKey().getBillingCycleYear());
        namedParameters.put("startEpoch", historyBillingCycle.getStartEpoch());
        namedParameters.put("endEpoch", historyBillingCycle.getEndEpoch());
        namedParameters.put("net", historyBillingCycle.getNet());
        namedParameters.put("loadValue", historyBillingCycle.getLoad());
        namedParameters.put("generation", historyBillingCycle.getGeneration());
        namedParameters.put("demandPeak", historyBillingCycle.getDemandPeak());
        namedParameters.put("demandPeakTime", historyBillingCycle.getDemandPeakTime());

        namedParameters.put("loadPeak", historyBillingCycle.getLoadPeak());
        namedParameters.put("loadPeakTime", historyBillingCycle.getLoadPeakTime());
        namedParameters.put("generationPeak", historyBillingCycle.getGenerationPeak());
        namedParameters.put("generationPeakTime", historyBillingCycle.getGenerationPeakTime());


        namedParameters.put("demandCost", historyBillingCycle.getDemandCost());
        namedParameters.put("demandCostPeakTime", historyBillingCycle.getDemandCostPeakTime());
        namedParameters.put("demandCostPeak", historyBillingCycle.getDemandCostPeak());
        namedParameters.put("demandCostPeakTOU", historyBillingCycle.getDemandCostPeakTOU().ordinal());
        namedParameters.put("demandCostPeakTOUName", historyBillingCycle.getDemandCostPeakTOUName());
        namedParameters.put("minimumCharge", historyBillingCycle.getMinimumCharge());
        namedParameters.put("fixedCharge", historyBillingCycle.getFixedCharge());
//        namedParameters.put("minVoltage", historyBillingCycle.getMinVoltage());

        if (historyBillingCycle.getMinVoltage() < 0) namedParameters.put("minVoltage", 0.0);
        else if (historyBillingCycle.getMinVoltage() > 999.0) namedParameters.put("minVoltage", 999.0);
        else namedParameters.put("minVoltage", historyBillingCycle.getMinVoltage());


        namedParameters.put("minVoltageTime", historyBillingCycle.getMinVoltageTime());

        if (historyBillingCycle.getPeakVoltage() < 0) namedParameters.put("peakVoltage", 120.0);
        else if (historyBillingCycle.getPeakVoltage() > 999.0) namedParameters.put("peakVoltage", 999.0);
        else namedParameters.put("peakVoltage", historyBillingCycle.getPeakVoltage());


        namedParameters.put("peakVoltageTime", historyBillingCycle.getPeakVoltageTime());
        namedParameters.put("pfTotal", historyBillingCycle.getPfTotal());
        namedParameters.put("pfSampleCount", historyBillingCycle.getPfSampleCount());
        namedParameters.put("mtuCount", historyBillingCycle.getMtuCount());
        namedParameters.put("touOffPeakNet", historyBillingCycle.getTouOffPeakNet());
        namedParameters.put("touOffPeakGen", historyBillingCycle.getTouOffPeakGen());
        namedParameters.put("touOffPeakLoad", historyBillingCycle.getTouOffPeakLoad());
        namedParameters.put("touPeakNet", historyBillingCycle.getTouPeakNet());
        namedParameters.put("touPeakGen", historyBillingCycle.getTouPeakGen());
        namedParameters.put("touPeakLoad", historyBillingCycle.getTouPeakLoad());
        namedParameters.put("touMidPeakNet", historyBillingCycle.getTouMidPeakNet());
        namedParameters.put("touMidPeakGen", historyBillingCycle.getTouMidPeakGen());
        namedParameters.put("touMidPeakLoad", historyBillingCycle.getTouMidPeakLoad());
        namedParameters.put("touSuperPeakNet", historyBillingCycle.getTouSuperPeakNet());
        namedParameters.put("touSuperPeakGen", historyBillingCycle.getTouSuperPeakGen());
        namedParameters.put("touSuperPeakLoad", historyBillingCycle.getTouSuperPeakLoad());

        namedParameters.put("rateInEffect", historyBillingCycle.getRateInEffect());
        namedParameters.put("netCost", historyBillingCycle.getNetCost());
        namedParameters.put("genCost", historyBillingCycle.getGenCost());
        namedParameters.put("loadCost", historyBillingCycle.getLoadCost());

        namedParameters.put("meterReadStartDate", historyBillingCycle.getMeterReadStartDate());
        namedParameters.put("meterReadDateChanged", historyBillingCycle.isMeterReadDateChanged());

        for (String s : namedParameters.keySet()) {
            Object o = namedParameters.get(s);
            if (o instanceof Double) namedParameters.put(s, forceZero((double) o));
        }

        return namedParameters;
    }

    private void updateCalendarKeys(HistoryBillingCycle dto) {
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(dto.getKey().getVirtualECCId());
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        dto.setCalendarKey(CalendarUtils.keyFromMillis(dto.getStartEpoch() * 1000, timeZone));
        dto.setDemandPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getDemandPeakTime() * 1000, timeZone));
        dto.setMinVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getMinVoltageTime() * 1000, timeZone));
        dto.setPeakVoltageCalendarKey(CalendarUtils.keyFromMillis(dto.getPeakVoltageTime() * 1000, timeZone));
        dto.setDemandCostPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getDemandCostPeakTime() * 1000, timeZone));
        dto.setLoadPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getLoadPeakTime() * 1000, timeZone));
        dto.setGenerationPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getGenerationPeakTime() * 1000, timeZone));
    }

    public HistoryBillingCycle findOne(HistoryBillingCycleKey key) {
        try {
            HistoryBillingCycle historyBillingCycle = hazelcastService.getHistoryBillingCycleMap().get(key);
            if (historyBillingCycle == null) {
                String query = "select *  from history_billingCycle where virtualECC_id=? and billingCycleYear=?  and billingCycleMonth = ?";
                historyBillingCycle = getJdbcTemplate().queryForObject(query, new Object[]{key.getVirtualECCId(), key.getBillingCycleYear(), key.getBillingCycleMonth()}, rowMapper);
                if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                    hazelcastService.getHistoryBillingCycleMap().put(key, historyBillingCycle);
                }
            }
            return historyBillingCycle;
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    Cache<Long, HistoryBillingCycle> activeBillingCycleCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(10, TimeUnit.DAYS).maximumSize(20000).build();

    public void updateActiveBillingCycle(HistoryBillingCycle hbc){
        long virtualECCId = hbc.getKey().getVirtualECCId();
        HistoryBillingCycle active = activeBillingCycleCache.getIfPresent(virtualECCId);
        if (active != null && active.getKey().equals(hbc.getKey())){
            activeBillingCycleCache.put(virtualECCId, hbc);
        }
    }

    public HistoryBillingCycle findActiveBillingCycle(Long virtualECCId, Long startEpoch) {
        HistoryBillingCycle historyBillingCycle = activeBillingCycleCache.getIfPresent(virtualECCId);

        boolean loadBillingCycle = (historyBillingCycle == null ||
                startEpoch.longValue() < historyBillingCycle.getStartEpoch().longValue() ||
                startEpoch.longValue() >= historyBillingCycle.getEndEpoch().longValue()
        );

        if (loadBillingCycle){
            historyBillingCycle = null;
            activeBillingCycleCache.invalidate(virtualECCId);

            try {
                String query = "select *  from history_billingCycle where virtualECC_id=? and startEpoch <=? order by startEpoch desc limit 1";
                historyBillingCycle = getJdbcTemplate().queryForObject(query, new Object[]{virtualECCId, startEpoch}, rowMapper);
                if (historyBillingCycle != null) {
                    activeBillingCycleCache.put(virtualECCId, historyBillingCycle);
                }
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
            }
        }
        return historyBillingCycle;
    }

    public int getMinutesSoFar(HistoryBillingCycle historyBillingCycle) {
        try {
            Long maxEpoch = getJdbcTemplate().queryForObject(MINUTES_SO_FAR,
                    new Object[]{historyBillingCycle.getKey().getVirtualECCId(),
                            historyBillingCycle.getStartEpoch(),
                            historyBillingCycle.getEndEpoch()}, Long.class);
            Long diff = maxEpoch - historyBillingCycle.getStartEpoch();
            return (int) (diff.longValue() / 60);
        } catch (Exception ex) {
            return 0;
        }
    }

    public void insert(HistoryBillingCycle historyBillingCycle) {
        processBatch();
        try {
            LOGGER.debug("RUNNING INSERT QUERY: {}", historyBillingCycle);
            //getNamedParameterJdbcTemplate().update(INSERT, getNamedParameters(historyBillingCycle));

            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                //Force a reload and put it in the cache.
                updateCalendarKeys(historyBillingCycle);
                hazelcastService.getHistoryBillingCycleMap().put(historyBillingCycle.getKey(), historyBillingCycle);
            }

            queueForBatch(historyBillingCycle.getKey(), HistoryKey.BatchState.INSERT, historyBillingCycle);
            updateActiveBillingCycle(historyBillingCycle);

        } catch (Exception ex) {
            LOGGER.error("Error inserting record: {}", historyBillingCycle, ex);
        }
    }

    public void save(HistoryBillingCycle historyBillingCycle) {
        processBatch();
        try {
            LOGGER.debug("RUNNING SAVE QUERY: {}", historyBillingCycle);
            if (!PollingService.skipCache && PollingService.NUMBER_ENERGYPOST_THREADS != 0) {
                updateCalendarKeys(historyBillingCycle);
                hazelcastService.getHistoryBillingCycleMap().put(historyBillingCycle.getKey(), historyBillingCycle);
            }
            //getNamedParameterJdbcTemplate().update(SAVE, getNamedParameters(historyBillingCycle));
            queueForBatch(historyBillingCycle.getKey(), HistoryKey.BatchState.UPDATE, historyBillingCycle);
            updateActiveBillingCycle(historyBillingCycle);

        } catch (Exception ex) {
            LOGGER.error("Error saving record: {}", historyBillingCycle, ex);
        }
    }

    public List<HistoryBillingCycle> findByDateRange(long virtualECCId, long startEpoch, long endEpoch) {

        Map<String, Object> namedParameters = new HashMap(24);
        namedParameters.put("virtualECCId", virtualECCId);
        namedParameters.put("startEpoch", startEpoch);
        namedParameters.put("endEpoch", endEpoch);

        //LOGGER.info(">>>>>>>>> find by date range: v:{} s:{} e:{}", virtualECCId, startEpoch, endEpoch);

        List<HistoryBillingCycle> list = getNamedParameterJdbcTemplate().query(FIND_BY_DATE_RANGE_QUERY, namedParameters, rowMapper);

        //Put it in the cache for quicker lookups
        for (HistoryBillingCycle dto : list) {
            if (!PollingService.skipCache && hazelcastService.getHistoryBillingCycleMap().get(dto.getKey()) == null) {
                hazelcastService.getHistoryBillingCycleMap().put(dto.getKey(), dto);
            }
        }
        return list;
    }


    static final int BATCH_SIZE = 10000;
    Cache<HistoryBillingCycleKey,HistoryKey.BatchState> batchStateCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();
    Cache<HistoryBillingCycleKey, HistoryBillingCycle> batch = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(BATCH_SIZE).initialCapacity(BATCH_SIZE).build();

    synchronized void queueForBatch(HistoryBillingCycleKey key, HistoryKey.BatchState batchState, HistoryBillingCycle dto){

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

        List<HistoryBillingCycle> insertRecords = new ArrayList<>(BATCH_SIZE);
        List<HistoryBillingCycle> updateRecords = new ArrayList<>(BATCH_SIZE);
        for (HistoryBillingCycleKey key: batchStateCache.asMap().keySet()){
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


    private void batchUpdate(String query, List<HistoryBillingCycle> batchList) {
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
        getJdbcTemplate().update("DELETE FROM history_billingCycle where virtualECC_id=?", new Object[]{virtualECCId});
    }
}
