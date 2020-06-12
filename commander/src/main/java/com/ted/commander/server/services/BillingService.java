/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.export.BillingRequest;
import com.ted.commander.common.model.history.BillingRecord;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.dao.BillingReportDAO;
import com.ted.commander.server.model.export.HistoryExportRow;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Interface for the data export service.
 */
@Service
public class BillingService {

    static Logger LOGGER = LoggerFactory.getLogger(BillingService.class);

    @Autowired
    BillingReportDAO billingReportDAO;

    static String MONTHS[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    static DecimalFormat ROUNDED_KWH_FORMAT = new DecimalFormat("0");
    static DecimalFormat KWH_FORMAT = new DecimalFormat("0.000");
    static DecimalFormat PEAK_FORMAT = new DecimalFormat("0.0");
    static DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
    static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/YYYY hh:mm:ss");


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


    //include DATE/KHW/COST/PEAK DEMAND  (I believe we’ll need TIME/DATE for Peak Demand)
    public void generateFile(BillingRequest billingRequest, String fileId) {
        File dir = new File("/temp");
        if (!dir.exists()) dir.mkdirs();

        Path path = Paths.get("/temp/" + fileId);

        List<BillingRecord> list;
        if (billingRequest.getHistoryType().equals(HistoryType.DAILY)){
            list = billingReportDAO.findByBillingRequestDaily(billingRequest);
        } else {
            list = billingReportDAO.findByBillingRequest(billingRequest);
        }

        switch (billingRequest.getDataExportFileType()) {
            case XML:
                append(path, "<ExportHistory>");
                break;
            case JSON:
                append(path, "\"exportHistory\":[");
                break;
            default:
                StringBuilder csvHeader;
                if (billingRequest.getHistoryType().equals(HistoryType.DAILY)) {
                    csvHeader =new StringBuilder("Location Name, Start Date, End Date, Net Usage (kwh), KWH Cost, Demand Cost, Demand Peak, Demand Peak Time");
                }  else {
                    csvHeader =new StringBuilder("Location Name, Billing Cycle Month, Billing Cycle Year, Net Usage (kwh), KWH Cost, Demand Cost, Demand Peak, Demand Peak Time");
                }
                append(path, csvHeader.append(" \n").toString());
        }

        ObjectMapper jsonObjectMapper = new ObjectMapper();

        for (BillingRecord billingRecord: list){
            switch (billingRequest.getDataExportFileType()){
                case XML:
                    StringWriter sw = new StringWriter();
                    try {
                        JAXBContext jaxbContext = JAXBContext.newInstance(HistoryExportRow.class);
                        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                        // output pretty printed
                        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                        jaxbMarshaller.marshal(billingRecord, sw);
                    } catch (Exception ex) {
                        //throw new SerializationException("Error saving " + userDir);
                        LoggerFactory.getLogger(HistoryExportRow.class).error("Exception creating xml", ex);
                    }
                    append(path, sw.toString());
                    break;
                case JSON:
                    try {
                        append(path, jsonObjectMapper.writeValueAsString(billingRecord));
                    } catch (Exception ex){
                        LOGGER.error("Error converting to JSON");
                    }
                    break;
                default:
                    //Append CSV
                    StringBuilder csvRow = new StringBuilder();
                    csvRow.append("\"").append(billingRecord.getEccName()).append("\",");
                    if (!billingRequest.getHistoryType().equals(HistoryType.DAILY)) {
                        csvRow.append(MONTHS[billingRecord.getBillingCycleMonth()]).append(",");
                        csvRow.append(billingRecord.getBillingCycleYear()).append(",");
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(billingRecord.getTimeZone()));
                        csvRow.append(simpleDateFormat.format(new Date(billingRecord.getStartEpoch() * 1000))).append(",");
                        csvRow.append(simpleDateFormat.format(new Date(billingRecord.getEndEpoch() * 1000))).append(",");
                    }
                    csvRow.append(ROUNDED_KWH_FORMAT.format(Math.round(billingRecord.getNet() /1000.0))).append(",");
                    csvRow.append(CURRENCY_FORMAT.format(billingRecord.getNetCost())).append(",");

                    csvRow.append(CURRENCY_FORMAT.format(billingRecord.getDemandCost())).append(",");
                    csvRow.append(PEAK_FORMAT.format(billingRecord.getDemandPeak() /1000.0)).append(",");

                    SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(billingRecord.getTimeZone()));
                    csvRow.append(SIMPLE_DATE_FORMAT.format(new Date(billingRecord.getDemandPeakTime() * 1000))).append("\n");

                    append(path, csvRow.toString());
            }
        }

        switch (billingRequest.getDataExportFileType()) {
            case XML:
                append(path, "</ExportHistory>");
                break;
            case JSON:
                append(path, "]");
                break;
        }

    }


}
