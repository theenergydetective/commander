package com.ted.commander.common.model.history;

public interface HistoryVoltage {
    double getMinVoltage();

    void setMinVoltage(double minVoltage);

    Long getMinVoltageTime();

    void setMinVoltageTime(Long minVoltageTime);

    double getPeakVoltage();

    void setPeakVoltage(double peakVoltage);

    Long getPeakVoltageTime();

    void setPeakVoltageTime(Long peakVoltageTime);

}
