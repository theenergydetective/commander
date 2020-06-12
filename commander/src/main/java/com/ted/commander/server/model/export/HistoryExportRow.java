package com.ted.commander.server.model.export;

import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.util.CalendarUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by pete on 2/2/2015.
 */
public class HistoryExportRow implements Serializable {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final DecimalFormat kwFormat = new DecimalFormat("0.000");
    static final DecimalFormat costFormat = new DecimalFormat("0.00");
    protected static final Logger LOGGER = LoggerFactory.getLogger(CalendarUtils.class);
    protected String name;
    protected String timeZoneId;
    protected Date date;
    protected double watts;
    protected double cost;
    protected double peakVoltage;
    protected Date peakVoltageTime;
    protected double minVoltage;
    protected Date minVoltageTime;
    protected double peakDemand;
    protected Date peakDemandTime;
    protected Double powerFactor;
    protected boolean isSpyder;
    protected WeatherHistory weather;

    protected Double demandCost = null;
    protected TOUPeakType demandCostPeakTOU;
    protected Double demandCostPeak;
    protected Long demandCostPeakTime;
    protected String demandCostPeakTOUName;




    public HistoryExportRow() {
    }

    public HistoryExportRow(MTUType mtuType) {
        switch (mtuType) {
            case NET:
                name = "Location Net";
                break;
            case LOAD:
                name = "Location Load";
                break;
            case GENERATION:
                name = "Location Generation";
                break;
        }
    }

    public boolean isSpyder() {
        return isSpyder;
    }

    public void setSpyder(boolean isSpyder) {
        this.isSpyder = isSpyder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public double getWatts() {
        return watts;
    }

    public void setWatts(double watts) {
        this.watts = watts;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPeakVoltage() {
        return peakVoltage;
    }

    public void setPeakVoltage(double peakVoltage) {
        this.peakVoltage = peakVoltage;
    }

    public Date getPeakVoltageTime() {
        return peakVoltageTime;
    }

    public void setPeakVoltageTime(Date peakVoltageTime) {
        this.peakVoltageTime = peakVoltageTime;
    }

    public double getMinVoltage() {
        return minVoltage;
    }

    public void setMinVoltage(double minVoltage) {
        this.minVoltage = minVoltage;
    }

    public Date getMinVoltageTime() {
        return minVoltageTime;
    }

    public void setMinVoltageTime(Date minVoltageTime) {
        this.minVoltageTime = minVoltageTime;
    }

    public double getPeakDemand() {
        return peakDemand;
    }

    public void setPeakDemand(double peakDemand) {
        this.peakDemand = peakDemand;
    }

    public Date getPeakDemandTime() {
        return peakDemandTime;
    }

    public void setPeakDemandTime(Date peakDemandTime) {
        this.peakDemandTime = peakDemandTime;
    }

    public Double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(Double powerFactor) {
        this.powerFactor = powerFactor;
    }

    public Double getDemandCost() {
        return demandCost;
    }

    public void setDemandCost(Double demandCost) {
        this.demandCost = demandCost;
    }

    public TOUPeakType getDemandCostPeakTOU() {
        return demandCostPeakTOU;
    }

    public void setDemandCostPeakTOU(TOUPeakType demandCostPeakTOU) {
        this.demandCostPeakTOU = demandCostPeakTOU;
    }

    public Double getDemandCostPeak() {
        return demandCostPeak;
    }

    public void setDemandCostPeak(Double demandCostPeak) {
        this.demandCostPeak = demandCostPeak;
    }

    public Long getDemandCostPeakTime() {
        return demandCostPeakTime;
    }

    public void setDemandCostPeakTime(Long demandCostPeakTime) {
        this.demandCostPeakTime = demandCostPeakTime;
    }

    public String getDemandCostPeakTOUName() {
        return demandCostPeakTOUName;
    }

    public void setDemandCostPeakTOUName(String demandCostPeakTOUName) {
        this.demandCostPeakTOUName = demandCostPeakTOUName;
    }

    public String toCSV(TimeZone timezone, boolean exportWeather, boolean hourlyWeather) {
        dateFormat.setTimeZone(timezone);
        StringBuilder output = new StringBuilder();
        String n = name.replace("\"", "");
        output.append("\"").append(n).append("\",");
        output.append(dateFormat.format(date)).append(",");
        output.append(kwFormat.format(watts / 1000.0)).append(",");
        output.append(costFormat.format(cost)).append(",");

        if (demandCost != null){
            output.append(costFormat.format(demandCost)).append(",");
            output.append(kwFormat.format(demandCostPeak / 1000.0)).append(",");
            output.append(dateFormat.format(demandCostPeakTime * 1000)).append(", ");
            output.append(demandCostPeakTOU).append(", ");
        }


        if (!isSpyder) {
            output.append(peakVoltage).append(",");
            output.append(dateFormat.format(peakVoltageTime)).append(",");
            output.append(minVoltage).append(",");
            output.append(dateFormat.format(minVoltageTime)).append(",");
            output.append(powerFactor).append(",");
        }
        else {
            output.append("NA,");
            output.append("NA,");
            output.append("NA,");
            output.append("NA,");
            output.append("NA,");
        }

        output.append(kwFormat.format(peakDemand / 1000)).append(",");
        output.append(dateFormat.format(peakDemandTime));


        if (exportWeather) {
            output.append(",");

            if (weather == null) {
                if (hourlyWeather) {
                    output.append(",");
                    output.append(",");
                    output.append(",");
                    output.append(",");
                    output.append(",");
                } else {
                    output.append(",");
                    output.append(",");
                }
            } else {
                if (hourlyWeather) {
                    output.append(weather.getTemp()).append(",");
                    output.append(weather.getWindspeed()).append(",");
                    output.append(weather.getClouds());
                } else {
                    output.append(weather.getPeakTemperature()).append(",");

                    Calendar peakTempCalendar = CalendarUtils.fromCalendarKey(weather.getPeakTempTime(), timezone.getID());
                    try {
                        if (peakTempCalendar != null) output.append(dateFormat.format(peakTempCalendar.getTime()));
                    } catch (Exception ex) {
                        LOGGER.error("Error parsing peak date: {}", peakTempCalendar.getTime(), ex);
                    }
                    output.append(",");

                    output.append(weather.getLowTemperature()).append(",");

                    Calendar lowTempCalendar = CalendarUtils.fromCalendarKey(weather.getLowTempTime(), timezone.getID());
                    try {
                        if (lowTempCalendar != null) output.append(dateFormat.format(lowTempCalendar.getTime()));
                    } catch (Exception ex) {
                        LOGGER.error("Error parsing min date: {}", peakTempCalendar.getTime(), ex);
                    }
                    output.append(",");

                    output.append(weather.getWindspeed()).append(",");
                    output.append(weather.getClouds());
                }
            }

        }

        output.append("\n");
        return output.toString();
    }

    public void toXLS(Workbook wb, Sheet sheet, Row row) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));


        CellStyle percentStyle = wb.createCellStyle();
        percentStyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));


        XSSFDataFormat decimalFormat = (XSSFDataFormat) wb.createDataFormat();
        CellStyle decimalStyle = wb.createCellStyle();
        decimalStyle.setDataFormat(decimalFormat.getFormat("0.000"));
        CellStyle currencyStyle = wb.createCellStyle();
        currencyStyle.setDataFormat((short) 7);
        row.createCell(0).setCellValue(date);
        row.createCell(1).setCellValue(watts / 1000.0);
        row.createCell(2).setCellValue(cost);
        row.createCell(3).setCellValue(peakVoltage);
        row.createCell(4).setCellValue(peakVoltageTime);
        row.createCell(5).setCellValue(minVoltage);
        row.createCell(6).setCellValue(minVoltageTime);
        row.createCell(7).setCellValue(peakDemand / 1000.0);
        row.createCell(8).setCellValue(peakDemandTime);
        if (!isSpyder) row.createCell(9).setCellValue(powerFactor / 100.0);

        row.getCell(0).setCellStyle(dateCellStyle);
        row.getCell(4).setCellStyle(dateCellStyle);
        row.getCell(6).setCellStyle(dateCellStyle);
        row.getCell(8).setCellStyle(dateCellStyle);

        row.getCell(1).setCellStyle(decimalStyle);
        row.getCell(7).setCellStyle(decimalStyle);

        row.getCell(2).setCellStyle(currencyStyle);
        if (!isSpyder) row.getCell(9).setCellStyle(percentStyle);
    }


    public String toXML() {
        StringWriter sw = new StringWriter();
        try {
            if (isSpyder) setPowerFactor(null);
            JAXBContext jaxbContext = JAXBContext.newInstance(HistoryExportRow.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            jaxbMarshaller.marshal(this, sw);
        } catch (Exception ex) {
            //throw new SerializationException("Error saving " + userDir);
            LoggerFactory.getLogger(HistoryExportRow.class).error("Exception creating xml", ex);
        }
        return sw.toString();
    }

    public String toJSON() {
        try {
            if (isSpyder) setPowerFactor(null);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception ex) {
            LoggerFactory.getLogger(HistoryExportRow.class).error("Exception creating json", ex);
            return "";
        }
    }

    public void setWeather(WeatherHistory weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "ExportRow{" +
                "name='" + name + '\'' +
                ", timeZoneId='" + timeZoneId + '\'' +
                ", date=" + date +
                ", watts=" + watts +
                ", cost=" + cost +
                '}';
    }

    public String format(DataExportFileType dataExportFileType, TimeZone timeZone, boolean isWeather, boolean isHourlyWeather){
        switch (dataExportFileType){
            case JSON: return toJSON();
            case XML: return toXML();
            default: return toCSV(timeZone, isWeather, isHourlyWeather);
        }
    }
}
