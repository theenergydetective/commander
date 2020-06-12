/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.export.ExportRequest;
import com.ted.commander.common.model.history.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.export.HistoryBillingCycleExportRow;
import com.ted.commander.server.model.export.HistoryDayExportRow;
import com.ted.commander.server.model.export.HistoryHourExportRow;
import com.ted.commander.server.model.export.HistoryMinuteExportRow;
import com.ted.commander.server.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


/**
 * Interface for the data export service.
 */
@Service
public class ExportService {

    static Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    Long fileId = 0l;


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
    WeatherService weatherService;


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


    public void append(Path path, String string){
        try {
            Files.write(path, string.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ex){
            LOGGER.error("Error writing {}", path, ex);
        }
    }

    WeatherHistory lookupWeather(Map<CalendarKey, WeatherHistory> weatherMap, CalendarKey calendarKey){
        return weatherMap.get(calendarKey);
    }

    public void generateFile(ExportRequest exportRequest, String fileId) {
        File dir = new File("/temp");
        if (!dir.exists()) dir.mkdirs();

        Path path = Paths.get("/temp/" + fileId);

        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(exportRequest.getVirtualECCId());
        List<VirtualECCMTU> virtualECCMTUList = new ArrayList<>(); //Stand Alone List
        for (Long mtuId: exportRequest.getMtuIdList()){
            VirtualECCMTU virtualECCMTU = virtualECCMTUDAO.findByMTUId(virtualECC.getId(), mtuId, virtualECC.getAccountId());
            if (virtualECCMTU != null){
                virtualECCMTUList.add(virtualECCMTU);
            }
        }

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        Calendar startCalendar = CalendarUtils.fromCalendarKey(exportRequest.getStartDate(), timeZone);
        Calendar endCalendar = CalendarUtils.fromCalendarKey(exportRequest.getEndDate(), timeZone);
        endCalendar.add(Calendar.DATE, 1);
        long startEpoch = startCalendar.getTimeInMillis() / 1000;
        long endEpoch = endCalendar.getTimeInMillis() / 1000;

        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
        String demandField = "avg" + energyPlan.getDemandAverageTime();

        Map<CalendarKey, WeatherHistory> weatherHistoryMap = new HashMap<>(1024);
        if (exportRequest.isExportWeather()){
            List<WeatherHistory> weatherHistoryList = weatherHistoryList= weatherService.findHistory(exportRequest.getHistoryType(), virtualECC, startEpoch, endEpoch);

            for (WeatherHistory weatherHistory : weatherHistoryList) {
                weatherHistoryMap.put(weatherHistory.getCalendarKey(), weatherHistory);
            }
        }

        Map<CalendarKey, HistoryBillingCycle> billingCycleMap = new HashMap<>(1024);
        if (exportRequest.isExportDemandCost()){
            List<HistoryBillingCycle> list = historyBillingCycleDAO.findByDateRange(exportRequest.getVirtualECCId(), startEpoch, endEpoch);
            if (exportRequest.isExportNet()) for (HistoryBillingCycle dto : list) {
                billingCycleMap.put(dto.getCalendarKey(), dto);
            }
        }

        switch (exportRequest.getDataExportFileType()) {
            case XML:
                append(path, "<ExportHistory>");
                break;
            case JSON:
                append(path, "\"exportHistory\":[");
                break;
            default:
                StringBuilder csvHeader = new StringBuilder("name, time, energy, cost");
                switch (exportRequest.getHistoryType()){
                    case MINUTE:
                        csvHeader = new StringBuilder("name, time, power, cost");
                        csvHeader.append(",voltage, power factor, demand");
                        if (exportRequest.isExportWeather()) csvHeader.append(", temp, wind, clouds");
                        break;
                    case HOURLY:
                        csvHeader.append(", peak voltage, peak voltage time, min voltage, min voltage time, power factor,  peak demand, peak demand time");
                        if (exportRequest.isExportWeather()) csvHeader.append(",temp, wind, clouds");
                        break;
                    case BILLING_CYCLE:
                        if (exportRequest.isExportDemandCost()) csvHeader.append(", demand cost, demand cost peak, demand cost time, demand cost tou");
                        csvHeader.append(", peak voltage, peak voltage time, min voltage, min voltage time, power factor,  peak demand, peak demand time");
                        if (exportRequest.isExportWeather()) csvHeader.append(", peak temp, peak temp time, low temp, low temp time, wind, clouds");
                        break;
                    default:
                        csvHeader.append(", peak voltage, peak voltage time, min voltage, min voltage time, power factor,  peak demand, peak demand time");
                        if (exportRequest.isExportWeather()) csvHeader.append(", peak temp, peak temp time, low temp, low temp time, wind, clouds");
                        break;
                }

                append(path, csvHeader.append(" \n").toString());
        }

        switch (exportRequest.getHistoryType()) {
            case BILLING_CYCLE: {
                List<HistoryBillingCycle> list = historyBillingCycleDAO.findByDateRange(exportRequest.getVirtualECCId(), startEpoch, endEpoch);
                if (exportRequest.isExportNet()) for (HistoryBillingCycle dto : list) {
                    append(path, HistoryBillingCycleExportRow.format(MTUType.NET, exportRequest.getDataExportFileType(),  exportRequest.isExportDemandCost(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportGeneration()) for (HistoryBillingCycle dto : list) {
                    append(path, HistoryBillingCycleExportRow.format(MTUType.GENERATION, exportRequest.getDataExportFileType(), exportRequest.isExportDemandCost(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportLoad()) for (HistoryBillingCycle dto : list) {
                    append(path, HistoryBillingCycleExportRow.format(MTUType.LOAD, exportRequest.getDataExportFileType(), exportRequest.isExportDemandCost(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                for (VirtualECCMTU mtu: virtualECCMTUList){
                    List<HistoryMTUBillingCycle> mtuList = historyMTUBillingCycleDAO.findByDateRange(virtualECC.getId(),mtu.getMtuId(), startEpoch, endEpoch);
                    for (HistoryMTUBillingCycle mtuDTO: mtuList){
                        append(path, HistoryBillingCycleExportRow.format(exportRequest.getDataExportFileType(), exportRequest.isExportDemandCost(), mtuDTO, billingCycleMap.get(mtuDTO.getCalendarKey()), mtu, lookupWeather(weatherHistoryMap, mtuDTO.getCalendarKey()),  timeZone));
                    }
                }
                break;
            }
            case DAILY: {
                List<HistoryDay> list = historyDayDAO.findByDateRange(exportRequest.getVirtualECCId(), startEpoch, endEpoch);
                if (exportRequest.isExportNet()) for (HistoryDay dto : list) {
                    append(path, HistoryDayExportRow.format(MTUType.NET, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportGeneration()) for (HistoryDay dto : list) {
                    append(path, HistoryDayExportRow.format(MTUType.GENERATION, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportLoad()) for (HistoryDay dto : list) {
                    append(path, HistoryDayExportRow.format(MTUType.LOAD, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                for (VirtualECCMTU mtu: virtualECCMTUList){
                    List<HistoryMTUDay> mtuList = historyMTUDayDAO.findByDateRange(virtualECC.getId(),mtu.getMtuId(), startEpoch, endEpoch);
                    for (HistoryMTUDay dto: mtuList){
                        append(path, HistoryDayExportRow.format(exportRequest.getDataExportFileType(), dto, mtu, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                    }
                }
                break;
            }
            case HOURLY: {
                List<HistoryHour> list = historyHourDAO.findByDateRange(exportRequest.getVirtualECCId(), startEpoch, endEpoch);

                if (exportRequest.isExportNet()) for (HistoryHour dto : list) {
                    append(path, HistoryHourExportRow.format(MTUType.NET, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap,  dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportGeneration()) for (HistoryHour dto : list) {
                    append(path, HistoryHourExportRow.format(MTUType.GENERATION, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportLoad()) for (HistoryHour dto : list) {
                    append(path, HistoryHourExportRow.format(MTUType.LOAD, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                for (VirtualECCMTU mtu: virtualECCMTUList){
                    List<HistoryMTUHour> mtuList = historyMTUHourDAO.findByDateRange(virtualECC.getId(),mtu.getMtuId(), startEpoch, endEpoch);
                    for (HistoryMTUHour dto: mtuList){
                        append(path, HistoryHourExportRow.format(exportRequest.getDataExportFileType(), dto, mtu, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                    }
                }
                break;
            }

            case MINUTE: {
                List<HistoryMinute> list = historyMinuteDAO.findByDateRange(exportRequest.getVirtualECCId(), startEpoch, endEpoch);
                if (exportRequest.isExportNet()) for (HistoryMinute dto : list) {
                    append(path, HistoryMinuteExportRow.format(MTUType.NET, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportGeneration()) for (HistoryMinute dto : list) {
                    append(path, HistoryMinuteExportRow.format(MTUType.GENERATION, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                if (exportRequest.isExportLoad()) for (HistoryMinute dto : list) {
                    append(path, HistoryMinuteExportRow.format(MTUType.LOAD, exportRequest.getDataExportFileType(), dto, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                }
                for (VirtualECCMTU mtu: virtualECCMTUList){
                    List<HistoryMTUMinute> mtuList = historyMTUMinuteDAO.findByDateRange(virtualECC.getId(),mtu.getMtuId(), virtualECC.getAccountId(), startEpoch, endEpoch, demandField);
                    for (HistoryMTUMinute dto: mtuList){
                        append(path, HistoryMinuteExportRow.format(exportRequest.getDataExportFileType(), dto, mtu, lookupWeather(weatherHistoryMap, dto.getCalendarKey()),  timeZone));
                    }
                }
                break;
            }
        }

        switch (exportRequest.getDataExportFileType()) {
            case XML:
                append(path, "</ExportHistory>");
                break;
            case JSON:
                append(path, "]");
                break;
        }

    }


}
