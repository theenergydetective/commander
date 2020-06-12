/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.export.GraphRequest;
import com.ted.commander.common.model.export.GraphResponse;
import com.ted.commander.common.model.export.HistoryDataPoint;
import com.ted.commander.common.model.history.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Interface for the data export service.
 */
@Service
public class GraphService {

    static Logger LOGGER = LoggerFactory.getLogger(GraphService.class);

    Long fileId = 0l;

    @Autowired
    WeatherHistoryDAO weatherHistoryDAO;

    @Autowired
    HazelcastService hazelcastService;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    HistoryService historyService;



    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    HistoryHourDAO historyHourDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    HistoryMTUHourDAO historyMTUHourDAO;

    @Autowired
    HistoryMTUMinuteDAO historyMTUMinuteDAO;

    @Autowired
    HistoryMTUDayDAO historyMTUDayDAO;

    @Autowired
    HistoryMTUBillingCycleDAO historyMTUBillingCycleDAO;


    private void addPoint(String key, HistoryDataPoint historyDataPoint, Map<String, List<HistoryDataPoint>> dataMap){
        List<HistoryDataPoint> historyDataPoints = getDataPointList(key, dataMap);
        historyDataPoints.add(historyDataPoint);
    }

    private List<HistoryDataPoint> getDataPointList(String key, Map<String, List<HistoryDataPoint>> dataMap){
        List<HistoryDataPoint> dataPointList = dataMap.get(key);
        if (dataPointList == null){
            dataPointList = new ArrayList<>();
            dataMap.put(key, dataPointList);
        }
        return dataPointList;
    }

    private HistoryDataPoint getMTUPoint(CalendarKey calendarKey, HistoryMTU historyMTU){
        return getMTUPoint(calendarKey, historyMTU, 1.0);
    }

    private HistoryDataPoint getMTUPoint(CalendarKey calendarKey, HistoryMTU historyMTU, double multiplier){
        HistoryDataPoint historyDataPoint = new HistoryDataPoint();
        historyDataPoint.setCalendarKey(calendarKey);
        historyDataPoint.setCostValue(((HistoryMTUCost)historyMTU).getCost() * multiplier);
        historyDataPoint.setEnergyValue(historyMTU.getEnergy() * multiplier);
        historyDataPoint.setDemandValue(historyMTU.getDemandPeak() * multiplier);
        return historyDataPoint;
    }

    private HistoryDataPoint getDemandDataPoint(CalendarKey calendarKey, HistoryBillingCycle dto){
        HistoryDataPoint historyDataPoint = new HistoryDataPoint();
        historyDataPoint.setCalendarKey(calendarKey);
        historyDataPoint.setCostValue(dto.getDemandCost());
        historyDataPoint.setEnergyValue(dto.getDemandCostPeak());
        historyDataPoint.setDemandValue(dto.getDemandCostPeak());
        return historyDataPoint;
    }

    private HistoryDataPoint getNetHistoryDataPoint(CalendarKey calendarKey, HistoryNetGenLoad historyNetGenLoad){
        HistoryDataPoint historyDataPoint = new HistoryDataPoint();
        historyDataPoint.setCalendarKey(calendarKey);

        if (historyNetGenLoad instanceof HistoryBillingCycle){
            double cost = ((HistoryCost)historyNetGenLoad).getNetCost() + ((HistoryBillingCycle)historyNetGenLoad).getFixedCharge();
            historyDataPoint.setCostValue(cost);
        } else {
            historyDataPoint.setCostValue(((HistoryCost)historyNetGenLoad).getNetCost());
        }
        historyDataPoint.setEnergyValue(historyNetGenLoad.getNet());
        if (historyNetGenLoad instanceof HistoryMinute){
            historyDataPoint.setCostValue(historyDataPoint.getCostValue() * 60.0);
            historyDataPoint.setEnergyValue(historyDataPoint.getEnergyValue() * 60.0);
            historyDataPoint.setDemandValue(((HistoryMinute)historyNetGenLoad).getDemandPeak());
        } else {
            historyDataPoint.setDemandValue(((HistoryDemandPeak)historyNetGenLoad).getDemandPeak());
        }

        return historyDataPoint;
    }

    private HistoryDataPoint getGenHistoryDataPoint(CalendarKey calendarKey, HistoryNetGenLoad historyNetGenLoad){
        HistoryDataPoint historyDataPoint = new HistoryDataPoint();
        historyDataPoint.setCalendarKey(calendarKey);
        historyDataPoint.setCostValue(((HistoryCost)historyNetGenLoad).getGenCost());
        historyDataPoint.setEnergyValue(historyNetGenLoad.getGeneration());
        if (historyNetGenLoad instanceof HistoryMinute){
            historyDataPoint.setCostValue(historyDataPoint.getCostValue() * 60.0);
            historyDataPoint.setEnergyValue(historyDataPoint.getEnergyValue() * 60.0);
            historyDataPoint.setDemandValue(((HistoryMinute)historyNetGenLoad).getGenerationPeak());
        } else {
            historyDataPoint.setDemandValue(((HistoryDemandPeak)historyNetGenLoad).getGenerationPeak());
        }

        return historyDataPoint;
    }

    private HistoryDataPoint getLoadHistoryDataPoint(CalendarKey calendarKey, HistoryNetGenLoad historyNetGenLoad){
        HistoryDataPoint historyDataPoint = new HistoryDataPoint();
        historyDataPoint.setCalendarKey(calendarKey);
        historyDataPoint.setCostValue(((HistoryCost)historyNetGenLoad).getLoadCost());
        historyDataPoint.setEnergyValue(historyNetGenLoad.getLoad());
        if (historyNetGenLoad instanceof HistoryMinute){
            historyDataPoint.setCostValue(historyDataPoint.getCostValue() * 60.0);
            historyDataPoint.setEnergyValue(historyDataPoint.getEnergyValue() * 60.0);
            historyDataPoint.setDemandValue(((HistoryMinute)historyNetGenLoad).getLoadPeak());
        } else {
            historyDataPoint.setDemandValue(((HistoryDemandPeak)historyNetGenLoad).getLoadPeak());
        }

        return historyDataPoint;
    }

    private void generateNetGenLoad(GraphRequest graphRequest, GraphResponse graphResponse, long startEpoch, long endEpoch){

        switch(graphRequest.getHistoryType()){
            case MINUTE:{
                List<HistoryMinute> minuteList = historyMinuteDAO.findByDateRange(graphRequest.getVirtualECCId(), startEpoch, endEpoch);
                for (HistoryMinute historyMinute: minuteList){
                    if (graphRequest.isExportNet()) addPoint("NET", getNetHistoryDataPoint(historyMinute.getCalendarKey(), historyMinute), graphResponse.getDataMap());
                    if (graphRequest.isExportGeneration()) addPoint("GENERATION", getGenHistoryDataPoint(historyMinute.getCalendarKey(), historyMinute), graphResponse.getDataMap());
                    if (graphRequest.isExportLoad()) addPoint("LOAD", getLoadHistoryDataPoint(historyMinute.getCalendarKey(), historyMinute), graphResponse.getDataMap());
                }
                break;
            }
            case HOURLY: {
                LOGGER.error("<<<<<<<<<<<< FIND HOUR: V:{} S:{} E:{}", graphRequest.getVirtualECCId(), startEpoch,endEpoch);
                List<HistoryHour> hourList = historyHourDAO.findByDateRange(graphRequest.getVirtualECCId(), startEpoch, endEpoch);

                for (HistoryHour dto: hourList){
                    if (graphRequest.isExportNet()) addPoint("NET", getNetHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                    if (graphRequest.isExportGeneration()) addPoint("GENERATION", getGenHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                    if (graphRequest.isExportLoad()) addPoint("LOAD", getLoadHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                }
                break;
            }
            case DAILY: {
                List<HistoryDay> dayList = historyDayDAO.findByDateRange(graphRequest.getVirtualECCId(), startEpoch, endEpoch);
                for (HistoryDay dto: dayList){
                    if (graphRequest.isExportNet()) addPoint("NET", getNetHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                    if (graphRequest.isExportGeneration()) addPoint("GENERATION", getGenHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                    if (graphRequest.isExportLoad()) addPoint("LOAD", getLoadHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                }
                break;
            }
            case BILLING_CYCLE:{
                List<HistoryBillingCycle> dayList = historyBillingCycleDAO.findByDateRange(graphRequest.getVirtualECCId(), startEpoch, endEpoch);
                for (HistoryBillingCycle dto: dayList){
                    if (graphRequest.isExportNet()) addPoint("NET", getNetHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                    if (graphRequest.isExportGeneration()) addPoint("GENERATION", getGenHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                    if (graphRequest.isExportLoad()) addPoint("LOAD", getLoadHistoryDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                }
                break;
            }
        }
    }

    private void generateStandAlone(GraphRequest graphRequest, GraphResponse graphResponse, long startEpoch, long endEpoch, VirtualECCMTU virtualECCMTU) {
        switch(graphRequest.getHistoryType()){
            case MINUTE:{

                EnergyPlan energyPlan = energyPlanService.loadEnergyPlanByVirtualECC(virtualECCMTU.getVirtualECCId());
                String demandField = "avg" + energyPlan.getDemandAverageTime();
                List<HistoryMTUMinute> minuteList = historyMTUMinuteDAO.findByDateRange(graphRequest.getVirtualECCId(), virtualECCMTU.getMtuId(), virtualECCMTU.getAccountId(),  startEpoch, endEpoch, demandField);
                for (HistoryMTUMinute dto: minuteList){
                    addPoint(virtualECCMTU.getMtuId().toString(), getMTUPoint(dto.getCalendarKey(), dto, 60.0), graphResponse.getDataMap());
                }
                break;
            }
            case HOURLY: {
                List<HistoryMTUHour> hourList = historyMTUHourDAO.findByDateRange(graphRequest.getVirtualECCId(), virtualECCMTU.getMtuId(), startEpoch, endEpoch);
                for (HistoryMTUHour dto: hourList){
                    addPoint(virtualECCMTU.getMtuId().toString(), getMTUPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                }
                break;
            }
            case DAILY: {
                List<HistoryMTUDay> dayList = historyMTUDayDAO.findByDateRange(graphRequest.getVirtualECCId(), virtualECCMTU.getMtuId(), startEpoch, endEpoch);

                for (HistoryMTUDay dto: dayList){
                    addPoint(virtualECCMTU.getMtuId().toString(), getMTUPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                }
                break;
            }
            case BILLING_CYCLE:{
                List<HistoryMTUBillingCycle> dayList = historyMTUBillingCycleDAO.findByDateRange(graphRequest.getVirtualECCId(),virtualECCMTU.getMtuId(), startEpoch, endEpoch);
                for (HistoryMTUBillingCycle dto: dayList){
                    addPoint(virtualECCMTU.getMtuId().toString(), getMTUPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
                }
                break;
            }
        }

    }


    public GraphResponse generateResponse(GraphRequest graphRequest){
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(graphRequest.getVirtualECCId());
        List<VirtualECCMTU> virtualECCMTUList = new ArrayList<>(); //Stand Alone List


        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        Calendar startCalendar = CalendarUtils.fromCalendarKey(graphRequest.getStartDate(), timeZone);
        Calendar endCalendar = CalendarUtils.fromCalendarKey(graphRequest.getEndDate(), timeZone);

        if (graphRequest.getHistoryType().equals(HistoryType.BILLING_CYCLE)){
            EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
            if (energyPlan.getMeterReadCycle().equals(MeterReadCycle.MONTHLY)){
                endCalendar.add(Calendar.MONTH, 1);
            } else {
                if (((endCalendar.get(Calendar.MONTH) %2 == 0) && energyPlan.getMeterReadCycle().equals(MeterReadCycle.BI_MONTHLY_ODD)) ||
                    ((endCalendar.get(Calendar.MONTH) %2 != 0) && energyPlan.getMeterReadCycle().equals(MeterReadCycle.BI_MONTHLY_EVEN))){
                    endCalendar.add(Calendar.MONTH, 1);
                } else {
                    endCalendar.add(Calendar.MONTH, 2);
                }
            }
        } else {
            endCalendar.add(Calendar.DATE, 1);
        }



        GraphResponse graphResponse = new GraphResponse();
        graphResponse.setVirtualECC(virtualECC);
        graphResponse.setVirtualECCMTUList(virtualECCMTUList);


        long startEpoch = startCalendar.getTimeInMillis() / 1000;
        long endEpoch = endCalendar.getTimeInMillis() / 1000;




        startEpoch = startCalendar.getTimeInMillis() / 1000;
        endEpoch = endCalendar.getTimeInMillis() / 1000;
        graphResponse.setStartCalendarKey(CalendarUtils.keyFromMillis(startEpoch * 1000, timeZone));
        graphResponse.setEndCalendarKey(CalendarUtils.keyFromMillis(endEpoch * 1000, timeZone));



        List<HistoryBillingCycle> historyBillingCycleList = null;
        if (graphRequest.getHistoryType().equals(HistoryType.BILLING_CYCLE) || graphRequest.isExportDemandCost()){

            historyBillingCycleList = historyBillingCycleDAO.findByDateRange(graphRequest.getVirtualECCId(), startEpoch, endEpoch);
            if (historyBillingCycleList.size() > 0) {
                graphResponse.setStartCalendarKey(CalendarUtils.keyFromMillis(historyBillingCycleList.get(0).getStartEpoch() * 1000, timeZone));
                graphResponse.setEndCalendarKey(CalendarUtils.keyFromMillis(historyBillingCycleList.get(historyBillingCycleList.size() - 1).getEndEpoch() * 1000, timeZone));
            }
        }

        generateNetGenLoad(graphRequest, graphResponse, startEpoch, endEpoch);

        if (graphRequest.isExportDemandCost()){
            for (HistoryBillingCycle dto: historyBillingCycleList){
                addPoint("DEMAND_COST", getDemandDataPoint(dto.getCalendarKey(), dto), graphResponse.getDataMap());
            }
        }

        for (Long mtuId: graphRequest.getMtuIdList()){
            VirtualECCMTU virtualECCMTU = virtualECCMTUDAO.findByMTUId(virtualECC.getId(), mtuId, virtualECC.getAccountId());
            if (virtualECCMTU != null){
                virtualECCMTUList.add(virtualECCMTU);
                generateStandAlone(graphRequest, graphResponse, startEpoch, endEpoch, virtualECCMTU);

            }
        }

        //TODO: Convert weather to cache by hour/day/billingCycle
        if (graphRequest.isExportWeather()) {
            LOGGER.debug("EXPORTING WEATHER");
            switch (graphRequest.getHistoryType()) {
                case MINUTE:
                    graphResponse.setWeatherHistory(weatherHistoryDAO.findMinuteHistory(virtualECC, startEpoch, endEpoch));
                    break;
                case HOURLY:
                    graphResponse.setWeatherHistory(weatherHistoryDAO.findHourHistory(virtualECC, startEpoch, endEpoch));
                    break;
                case DAILY:
                    graphResponse.setWeatherHistory(weatherHistoryDAO.findDayHistory(virtualECC, startEpoch, endEpoch));
                    break;
                case BILLING_CYCLE:
                    graphResponse.setWeatherHistory(weatherHistoryDAO.findBillingCycleHistory(virtualECC, startEpoch, endEpoch));
                    break;
            }
        }



        return graphResponse;
    }








}
