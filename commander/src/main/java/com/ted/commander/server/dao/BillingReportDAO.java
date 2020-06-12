/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.export.BillingRequest;
import com.ted.commander.common.model.history.BillingRecord;
import com.ted.commander.server.services.CostService;
import com.ted.commander.server.services.EnergyPlanService;
import com.ted.commander.server.services.EnergyPostService;
import com.ted.commander.server.util.CalendarUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BillingReportDAO extends SimpleAbstractDAO {

    @Autowired
    VirtualECCDAO virtualECCDAO;
    @Autowired
    EnergyPlanService energyPlanService;
    @Autowired
    CostService costService;
    @Autowired
    EnergyPostService energyPostService;
    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    static String MONT_COUNT =("((h.billingCycleYear * 12) + h.billingCycleMonth)");



    private RowMapper<BillingRecord> billingRecordRowMapper = new RowMapper<BillingRecord>() {
        public BillingRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillingRecord dto = new BillingRecord();
            dto.setTimeZone(rs.getString("timezone"));
            dto.setEccName(rs.getString("eccName"));
            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setBillingCycleMonth(rs.getInt("billingCycleMonth"));
            dto.setBillingCycleYear(rs.getInt("billingCycleYear"));
            dto.setStartEpoch(rs.getLong("startEpoch"));
            dto.setEndEpoch(rs.getLong("endEpoch"));
            dto.setNet(rs.getDouble("net"));
            dto.setDemandPeak(rs.getDouble("demandPeak"));
            dto.setDemandPeakTime(rs.getLong("demandPeakTime"));
            dto.setDemandCost(rs.getDouble("demandCost"));
            dto.setNetCost(rs.getDouble("netCost") + rs.getDouble("fixedCharge"));
            return dto;
        }
    };


    public List<BillingRecord> findByBillingRequest(BillingRequest billingRequest) {
        StringBuilder query = new StringBuilder();
        query.append(" select v.timezone, v.eccName, h.virtualECC_id, h.billingCycleMonth, h.billingCycleYear, h.startEpoch, h.endEpoch, h.net, h.demandPeak, h.demandPeakTime, h.demandCost, h.netCost, h.fixedCharge  ")
                .append(" from history_billingCycle h straight_join virtualECC v on v.id = h.virtualECC_id where virtualECC_id in ( ");

        StringBuilder eccList = new StringBuilder();
        for (long vid : billingRequest.getVirtualECCIdList()) {
            if (eccList.length() > 0) eccList.append(" ,");
            eccList.append(vid);
        }

        query.append(eccList).append(") ")
                .append(" and (")
                .append(MONT_COUNT).append(">= :startDate AND")
                .append(MONT_COUNT).append("<= :endDate ")
                .append(" ) ")
                .append(" order by v.eccName, startEpoch ");

        Map<String, Object> namedParameters = new HashMap(24);
        long startDate = (billingRequest.getStartDate().getYear() * 12) + billingRequest.getStartDate().getMonth();
        long endDate = (billingRequest.getEndDate().getYear() * 12) + billingRequest.getEndDate().getMonth();

        namedParameters.put("startDate", startDate);
        namedParameters.put("endDate", endDate);

        return getNamedParameterJdbcTemplate().query(query.toString(), namedParameters, billingRecordRowMapper);
    }


    public List<BillingRecord> findByBillingRequestDaily(BillingRequest billingRequest) {
        List<BillingRecord> billingRecords = new ArrayList<>();
        for (long vid : billingRequest.getVirtualECCIdList()) {
            VirtualECC virtualECC = virtualECCDAO.findOneFromCache(vid);
            EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
            StringBuilder query = new StringBuilder();
            query.append(" select h.startEpoch, h.endEpoch, h.net, h.demandPeak, h.demandPeakTime, h.netCost, 0 as fixedCharge")
                    .append(" from history_day2 h ")
                    .append(" where virtualECC_id = ").append(vid)
                    .append(" and h.startEpoch >= :startDate and h.startEpoch <= :endDate ")
                    .append(" order by startEpoch");

            Map<String, Object> namedParameters = new HashMap(24);
            long startDate = CalendarUtils.fromCalendarKey(billingRequest.getStartDate(), virtualECC.getTimezone()).getTimeInMillis()/1000;
            long endDate = CalendarUtils.fromCalendarKey(billingRequest.getEndDate(), virtualECC.getTimezone()).getTimeInMillis()/1000;
            namedParameters.put("startDate", startDate);
            namedParameters.put("endDate", endDate);

            LOGGER.debug(">>>>>>>>> " + query.toString().replace(":startDate", startDate + "").replace(":endDate", endDate + ""));

            DailyBillingCallbackHandler dailyBillingCallbackHandler = new DailyBillingCallbackHandler(startDate, endDate, virtualECC, energyPlan, costService, energyPostService,historyMinuteDAO);
            getNamedParameterJdbcTemplate().query(query.toString(), namedParameters, dailyBillingCallbackHandler);
            billingRecords.add(dailyBillingCallbackHandler.getBillingRecord());
        }
        return billingRecords;
    }
}
