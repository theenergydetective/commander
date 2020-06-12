/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.BillingRecord;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.services.CostService;
import com.ted.commander.server.services.EnergyPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pete on 12/18/2014.
 */
public class DailyBillingCallbackHandler implements RowCallbackHandler {

    final static Logger LOGGER = LoggerFactory.getLogger(DailyBillingCallbackHandler.class);

    final VirtualECC virtualECC;
    final EnergyPlan energyPlan;
    final CostService costService;
    final EnergyPostService energyPostService;
    final TimeZone timeZone;
    final HistoryMinuteDAO historyMinuteDAO;

    BillingRecord billingRecord = new BillingRecord();

    double peak = 0;
    long peakTime = 0;

    public DailyBillingCallbackHandler(long startEpoch, long endEpoch, VirtualECC virtualECC, EnergyPlan energyPlan, CostService costService, EnergyPostService energyPostService, HistoryMinuteDAO historyMinuteDAO) {
        this.virtualECC = virtualECC;
        this.timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        this.energyPlan = energyPlan;
        this.costService = costService;
        this.energyPostService = energyPostService;
        this.historyMinuteDAO = historyMinuteDAO;
        billingRecord.setEccName(virtualECC.getName());
        billingRecord.setStartEpoch(startEpoch);
        billingRecord.setEndEpoch(endEpoch);
        billingRecord.setVirtualECCId(virtualECC.getId());
        billingRecord.setTimeZone(virtualECC.getTimezone());
    }



    @Override
    public void processRow(ResultSet rs) throws SQLException {
        billingRecord.setNet(rs.getDouble("net") + billingRecord.getNet());
        billingRecord.setNetCost(rs.getDouble("netCost") + billingRecord.getNetCost());

        long startEpoch = rs.getLong("startEpoch");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(startEpoch * 1000);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long endEpoch = calendar.getTimeInMillis()/1000;

        if (peak < rs.getDouble("demandPeak")) {
            peak = rs.getDouble("demandPeak");
            peakTime = rs.getLong("demandPeakTime");
        }

        switch (energyPlan.getDemandPlanType()) {
            case TIERED_PEAK:
            case TIERED: {
                double demandPeak = rs.getDouble("demandPeak");
                long demandPeakTime = rs.getLong("demandPeakTime");
                HistoryMinute historyMinute = new HistoryMinute();
                historyMinute.setStartEpoch(startEpoch);
                if (energyPostService.isDemandChargeApplicable(energyPlan, timeZone, TOUPeakType.OFF_PEAK, historyMinute)) {
                    if (demandPeak > billingRecord.getDemandPeak()) {
                        billingRecord.setDemandPeak(demandPeak);
                        billingRecord.setDemandPeakTime(demandPeakTime);
                    }
                }
                break;
            }
            case TOU: {
                List<HistoryMinute> historyMinutes = historyMinuteDAO.findByDateRange(virtualECC.getId(), startEpoch, endEpoch);
                int season = costService.findSeason(energyPlan, timeZone, startEpoch);
                for (HistoryMinute historyMinute : historyMinutes) {
                    TOUPeakType peakType = costService.findTOU(energyPlan, season, timeZone, historyMinute.getStartEpoch());
                    if (energyPostService.isDemandChargeApplicable(energyPlan, timeZone, peakType, historyMinute)) {
                        double demandPeak = historyMinute.getDemandPeak();
                        if (demandPeak > billingRecord.getDemandPeak()){
                            billingRecord.setDemandPeak(demandPeak);
                            billingRecord.setDemandPeakTime(historyMinute.getDemandPeakTime());
                            billingRecord.setDemandPeakTOU(peakType);
                        }
                    }
                }
                break;
            }
        }

    }

    public BillingRecord getBillingRecord() {
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        if (billingRecord.getDemandPeak() > 0) {
            int season = costService.findSeason(energyPlan, timeZone, billingRecord.getDemandPeakTime());
            switch (energyPlan.getDemandPlanType()) {
                case TIERED:
                    billingRecord.setDemandCost(energyPostService.calculateTieredDemandCost(billingRecord.getDemandPeak(), energyPlan, season));
                    break;
                case TIERED_PEAK:
                    billingRecord.setDemandCost(energyPostService.calculateTieredPeakDemandCost(billingRecord.getDemandPeak(), energyPlan, season));
                    break;
                case TOU:
                    billingRecord.setDemandCost(energyPostService.calcTOUDemandCost(billingRecord.getDemandPeak(), energyPlan, season, billingRecord.getDemandPeakTOU()));
                    break;
            }
        }

        if (billingRecord.getDemandCost() < 0.01) {
            billingRecord.setDemandPeak(peak);
            billingRecord.setDemandPeakTime(peakTime);
        }
        return billingRecord;
    }

}
