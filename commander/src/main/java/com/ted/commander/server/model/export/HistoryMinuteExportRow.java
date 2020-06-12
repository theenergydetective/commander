package com.ted.commander.server.model.export;


import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.history.HistoryMTUMinute;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.common.model.history.WeatherHistory;
import com.ted.commander.server.util.CalendarUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@XmlRootElement(name = "Export")
public class HistoryMinuteExportRow implements Serializable {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final DecimalFormat kwFormat = new DecimalFormat("0.000");
    static final DecimalFormat costFormat = new DecimalFormat("0.00");
    private String name;
    private Date date;
    private double watts;
    private double cost;
    private double voltage;
    private double demand;
    private double powerFactor;
    private WeatherHistory weather;


    public HistoryMinuteExportRow(){

    }

    public HistoryMinuteExportRow(HistoryMTUMinute dto, VirtualECCMTU virtualECCMTU, WeatherHistory weatherHistory){
        name = virtualECCMTU.getMtuDescription();
        watts = dto.getEnergy() * 60.0;
        cost = dto.getCost() * 60.0;
        demand = dto.getDemandPeak();
        voltage = dto.getVoltage();
        powerFactor = dto.getPowerFactor();
        weather = weatherHistory;


    }

    public HistoryMinuteExportRow(MTUType mtuType, HistoryMinute dto, WeatherHistory weatherHistory){
        switch (mtuType) {
            case NET:
                name = "Location Net";
                watts = dto.getNet() * 60.0;
                cost = dto.getNetCost() * 60.0;
                break;
            case LOAD:
                name = "Location Load";
                watts = dto.getLoad() * 60.0;
                cost = dto.getLoadCost() * 60.0;
                break;
            case GENERATION:
                name = "Location Generation";
                watts = dto.getGeneration() * 60.0;
                cost = dto.getGenCost() * 60.0;
                break;
        }


        voltage = dto.getVoltageTotal() / dto.getMtuCount();
        demand = dto.getDemandPeak();
        powerFactor = dto.getPfTotal() / dto.getPfSampleCount();
        powerFactor /= 100.0;
        this.weather = weatherHistory;
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

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
    }


    public String toCSV(boolean exportWeather, TimeZone timeZone) {

        dateFormat.setTimeZone(timeZone);

        StringBuilder output = new StringBuilder();
        String n = name.replace("\"", "");
        output.append("\"").append(n).append("\",");
        output.append(dateFormat.format(date)).append(",");
        output.append(kwFormat.format(watts / 1000.0)).append(",");
        output.append(costFormat.format(cost)).append(",");
        output.append(voltage).append(",");
        output.append(powerFactor).append(",");

        output.append(kwFormat.format(demand / 1000.0));

        if (exportWeather) {
            output.append(",");
            if (weather == null) {
                output.append(",");
                output.append(",");
            } else {
                output.append(weather.getTemp()).append(",");
                output.append(weather.getWindspeed()).append(",");
                output.append(weather.getClouds());
            }
        }
        output.append("\n");
        return output.toString();
    }

    public void toXLS(Workbook wb, Sheet sheet, Row row) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
        XSSFDataFormat decimalFormat = (XSSFDataFormat) wb.createDataFormat();
        CellStyle decimalStyle = wb.createCellStyle();
        decimalStyle.setDataFormat(decimalFormat.getFormat("0.000"));
        CellStyle currencyStyle = wb.createCellStyle();
        currencyStyle.setDataFormat((short) 7);

        CellStyle percentStyle = wb.createCellStyle();
        percentStyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));

        currencyStyle.setDataFormat((short) 7);

        row.createCell(0).setCellValue(date);
        row.createCell(1).setCellValue(watts / 1000.0);
        row.createCell(2).setCellValue(cost);
        row.createCell(3).setCellValue(voltage);
        row.createCell(4).setCellValue(demand / 1000.0);
        row.createCell(5).setCellValue(powerFactor / 100.0);

        row.getCell(0).setCellStyle(dateCellStyle);
        row.getCell(1).setCellStyle(decimalStyle);
        row.getCell(2).setCellStyle(currencyStyle);
        row.getCell(4).setCellStyle(decimalStyle);
        row.getCell(5).setCellStyle(percentStyle);
    }

    public String toXML() {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HistoryMinuteExportRow.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            jaxbMarshaller.marshal(this, sw);
        } catch (Exception ex) {
            //throw new SerializationException("Error saving " + userDir);
            org.slf4j.LoggerFactory.getLogger(HistoryMinuteExportRow.class).error("Exception creating xml", ex);
        }
        String s = sw.toString();
        s = s.substring(s.indexOf("<Ex"));
        return s;
    }

    public String toJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(HistoryMinuteExportRow.class).error("Exception creating json", ex);
            return "";
        }
    }

    public WeatherHistory getWeather() {
        return weather;
    }

    public void setWeather(WeatherHistory weather) {
        this.weather = weather;
    }


    public static String format(MTUType mtuType, DataExportFileType dataExportFileType, HistoryMinute dto, WeatherHistory weatherHistory, TimeZone timeZone){
        HistoryMinuteExportRow row = new HistoryMinuteExportRow(mtuType, dto, weatherHistory);
        row.date = CalendarUtils.fromCalendarKey(dto.getCalendarKey(), timeZone).getTime();
        if (weatherHistory != null) row.setWeather(weatherHistory);
        switch (dataExportFileType){
            case JSON: return row.toJSON();
            case XML: return row.toXML();
            default: return row.toCSV(weatherHistory != null, timeZone);
        }
    }

    public static String format(DataExportFileType dataExportFileType, HistoryMTUMinute dto, VirtualECCMTU mtu, WeatherHistory weatherHistory, TimeZone timeZone){
        HistoryMinuteExportRow row = new HistoryMinuteExportRow(dto, mtu, weatherHistory);
        row.date = CalendarUtils.fromCalendarKey(dto.getCalendarKey(), timeZone).getTime();
        switch (dataExportFileType){
            case JSON: return row.toJSON();
            case XML: return row.toXML();
            default: return row.toCSV(weatherHistory != null, timeZone);
        }
    }

}
