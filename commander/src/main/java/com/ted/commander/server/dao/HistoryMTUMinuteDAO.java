/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryMTUMinute;
import com.ted.commander.server.services.HazelcastService;
import com.ted.commander.server.util.CalendarUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class HistoryMTUMinuteDAO extends SimpleAbstractDAO {

    private static final String FIND_BY_DATE_RANGE_QUERY = "select hm.virtualECC_id, ed.mtu_id, voltage, pf, startEpoch, net, netCost, energyDifference, XXX as demandAverage from history_minute2 hm straight_join energydata ed on hm.startEpoch = ed.timestamp where ed.mtu_id = :mtuId and ed.account_id = :accountId and hm.virtualECC_id = :virtualECCId and startEpoch >= :startEpoch and startEpoch < :endEpoch order by startEpoch";
    @Autowired
    HazelcastService hazelcastService;
    @Autowired
    VirtualECCDAO virtualECCDAO;
    private RowMapper<HistoryMTUMinute> rowMapper = new RowMapper<HistoryMTUMinute>() {
        public HistoryMTUMinute mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryMTUMinute dto = new HistoryMTUMinute();

            double netCost = rs.getDouble("netCost");
            double net = rs.getDouble("net");
            double rate = netCost / net;
            rate *= 1000;

            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setMtuId(rs.getLong("mtu_id"));
            dto.setStartEpoch(rs.getLong("startEpoch"));
            dto.setEndEpoch(rs.getLong("startEpoch"));
            dto.setEnergy(rs.getDouble("energyDifference"));
            dto.setCost(rate * (dto.getEnergy() / 1000.0));
            dto.setDemandPeak(rs.getDouble("demandAverage"));
            dto.setVoltage(rs.getDouble("voltage"));
            dto.setPowerFactor(rs.getDouble("pf") / 100.0);

            VirtualECC virtualECC = virtualECCDAO.findOneFromCache(rs.getLong("virtualECC_id"));
            TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
            dto.setCalendarKey(CalendarUtils.keyFromMillis(dto.getStartEpoch() * 1000, timeZone));

            //LOGGER.error("CALENDAR: {}", dto.getCalendarKey());
            dto.setDemandPeakCalendarKey(CalendarUtils.keyFromMillis(dto.getDemandPeakTime() * 1000, timeZone));
            return dto;
        }
    };

    public List<HistoryMTUMinute> findByDateRange(long virtualECCId, long mtu_id, long accountId, long startEpoch, long endEpoch, String demandField) {
        Map<String, Object> namedParameters = new HashMap(24);
        namedParameters.put("virtualECCId", virtualECCId);
        namedParameters.put("mtuId", mtu_id);
        namedParameters.put("accountId", accountId);
        namedParameters.put("startEpoch", startEpoch);
        namedParameters.put("endEpoch", endEpoch);
        return getNamedParameterJdbcTemplate().query(FIND_BY_DATE_RANGE_QUERY.replace("XXX", demandField), namedParameters, rowMapper);

    }
}
