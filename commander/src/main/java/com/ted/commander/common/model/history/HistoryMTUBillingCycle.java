package com.ted.commander.common.model.history;

import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMTUBillingCycle implements History, HistoryMTU, HistoryVoltage, HistoryPowerFactor, HistoryMTUCost, Serializable {

    @EmbeddedId
    HistoryMTUBillingCycleKey key;

    Long startEpoch;
    Long endEpoch;

    @Column(precision = 21, scale = 6)
    double energy = 0.0;

    @Column(precision = 21, scale = 6)
    double demandPeak = 0.0;
    Long demandPeakTime = 0l;
    @Column(precision = 21, scale = 6)
    double touOffPeak = 0.0;
    @Column(precision = 21, scale = 6)
    double touPeak = 0.0;
    @Column(precision = 21, scale = 6)
    double touMidPeak = 0.0;
    @Column(precision = 21, scale = 6)
    double touSuperPeak = 0.0;
    double pfTotal = 0;
    long pfSampleCount = 0;
    private double cost = 0.0;
    private CalendarKey calendarKey;
    private CalendarKey demandPeakCalendarKey;
    private double peakVoltage = 0;
    private Long peakVoltageTime = 0l;
    private CalendarKey peakVoltageCalendarKey;
    private double minVoltage = 0;
    private Long minVoltageTime = 0l;
    private CalendarKey minVoltageCalendarKey;

    public HistoryMTUBillingCycleKey getKey() {
        return key;
    }

    public void setKey(HistoryMTUBillingCycleKey key) {
        this.key = key;
    }

    public Long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public Long getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(Long endEpoch) {
        this.endEpoch = endEpoch;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getDemandPeak() {
        return demandPeak;
    }

    public void setDemandPeak(double demandPeak) {
        this.demandPeak = demandPeak;
    }

    public Long getDemandPeakTime() {
        return demandPeakTime;
    }

    public void setDemandPeakTime(Long demandPeakTime) {
        this.demandPeakTime = demandPeakTime;
    }

    public double getTouOffPeak() {
        return touOffPeak;
    }

    public void setTouOffPeak(double touOffPeak) {
        this.touOffPeak = touOffPeak;
    }

    public double getTouPeak() {
        return touPeak;
    }

    public void setTouPeak(double touPeak) {
        this.touPeak = touPeak;
    }

    public double getTouMidPeak() {
        return touMidPeak;
    }

    public void setTouMidPeak(double touMidPeak) {
        this.touMidPeak = touMidPeak;
    }

    public double getTouSuperPeak() {
        return touSuperPeak;
    }

    public void setTouSuperPeak(double touSuperPeak) {
        this.touSuperPeak = touSuperPeak;
    }


    @Override
    public double getPfTotal() {
        return pfTotal;
    }

    @Override
    public void setPfTotal(double pfTotal) {
        this.pfTotal = pfTotal;
    }

    @Override
    public long getPfSampleCount() {
        return pfSampleCount;
    }

    @Override
    public void setPfSampleCount(long pfSampleCount) {
        this.pfSampleCount = pfSampleCount;
    }

    @Override
    public double getPeakVoltage() {
        return peakVoltage;
    }

    @Override
    public void setPeakVoltage(double peakVoltage) {
        this.peakVoltage = peakVoltage;
    }

    @Override
    public Long getPeakVoltageTime() {
        return peakVoltageTime;
    }

    public void setPeakVoltageTime(Long peakVoltageTime) {
        this.peakVoltageTime = peakVoltageTime;
    }

    public CalendarKey getPeakVoltageCalendarKey() {
        return peakVoltageCalendarKey;
    }

    public void setPeakVoltageCalendarKey(CalendarKey peakVoltageCalendarKey) {
        this.peakVoltageCalendarKey = peakVoltageCalendarKey;
    }

    @Override
    public double getMinVoltage() {
        return minVoltage;
    }

    @Override
    public void setMinVoltage(double minVoltage) {
        this.minVoltage = minVoltage;
    }

    @Override
    public Long getMinVoltageTime() {
        return minVoltageTime;
    }

    public void setMinVoltageTime(Long minVoltageTime) {
        this.minVoltageTime = minVoltageTime;
    }

    public CalendarKey getMinVoltageCalendarKey() {
        return minVoltageCalendarKey;
    }

    public void setMinVoltageCalendarKey(CalendarKey minVoltageCalendarKey) {
        this.minVoltageCalendarKey = minVoltageCalendarKey;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryMTUBillingCycle that = (HistoryMTUBillingCycle) o;

        if (Double.compare(that.energy, energy) != 0) return false;
        if (Double.compare(that.demandPeak, demandPeak) != 0) return false;
        if (Double.compare(that.touOffPeak, touOffPeak) != 0) return false;
        if (Double.compare(that.touPeak, touPeak) != 0) return false;
        if (Double.compare(that.touMidPeak, touMidPeak) != 0) return false;
        if (Double.compare(that.touSuperPeak, touSuperPeak) != 0) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (startEpoch != null ? !startEpoch.equals(that.startEpoch) : that.startEpoch != null) return false;
        return demandPeakTime != null ? demandPeakTime.equals(that.demandPeakTime) : that.demandPeakTime == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = key != null ? key.hashCode() : 0;
        result = 31 * result + (startEpoch != null ? startEpoch.hashCode() : 0);
        temp = Double.doubleToLongBits(energy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(demandPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (demandPeakTime != null ? demandPeakTime.hashCode() : 0);
        temp = Double.doubleToLongBits(touOffPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touMidPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touSuperPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "HistoryMTUBillingCycle{" +
                "key=" + key +
                ", startEpoch=" + startEpoch +
                ", energy=" + energy +
                ", demandPeak=" + demandPeak +
                ", demandPeakTime=" + demandPeakTime +
                ", touOffPeak=" + touOffPeak +
                ", touPeak=" + touPeak +
                ", touMidPeak=" + touMidPeak +
                ", touSuperPeak=" + touSuperPeak +
                '}';
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
    }

    @Override
    public double getLoadPeak() {
        return 0;
    }

    @Override
    public void setLoadPeak(double LoadPeak) {

    }

    @Override
    public Long getLoadPeakTime() {
        return null;
    }

    @Override
    public void setLoadPeakTime(Long LoadPeakTime) {

    }

    @Override
    public CalendarKey getLoadPeakCalendarKey() {
        return null;
    }

    @Override
    public void setLoadPeakCalendarKey(CalendarKey key) {

    }

    @Override
    public double getGenerationPeak() {
        return 0;
    }

    @Override
    public void setGenerationPeak(double GenerationPeak) {

    }

    @Override
    public Long getGenerationPeakTime() {
        return null;
    }

    @Override
    public void setGenerationPeakTime(Long GenerationPeakTime) {

    }

    @Override
    public CalendarKey getGenerationPeakCalendarKey() {
        return null;
    }

    @Override
    public void setGenerationPeakCalendarKey(CalendarKey key) {

    }

    public CalendarKey getDemandPeakCalendarKey() {
        return demandPeakCalendarKey;
    }

    public void setDemandPeakCalendarKey(CalendarKey demandPeakCalendarKey) {
        this.demandPeakCalendarKey = demandPeakCalendarKey;
    }
}



