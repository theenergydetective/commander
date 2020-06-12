package com.ted.commander.common.model.history;

import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMTUMinute implements HistoryMTU, HistoryMTUCost, Serializable {


    Long virtualECCId;
    Long mtuId;

    Long startEpoch;
    Long endEpoch;

    @Column(precision = 21, scale = 6)
    double energy = 0.0;

    @Column(precision = 21, scale = 6)
    double demandPeak = 0.0;
    long demandPeakTime = 0;

    double voltage = 0.0;
    double powerFactor = 0.0;

    @Column(precision = 21, scale = 6)
    double touOffPeak = 0.0;
    @Column(precision = 21, scale = 6)
    double touPeak = 0.0;
    @Column(precision = 21, scale = 6)
    double touMidPeak = 0.0;
    @Column(precision = 21, scale = 6)
    double touSuperPeak = 0.0;
    private CalendarKey calendarKey;
    private CalendarKey demandPeakCalendarKey;
    private double cost;

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

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getDemandPeak() {
        return demandPeak;
    }

    @Override
    public void setDemandPeak(double demandPeak) {
        this.demandPeak = demandPeak;
    }

    @Override
    public double getTouOffPeak() {
        return touOffPeak;
    }

    @Override
    public void setTouOffPeak(double touOffPeak) {
        this.touOffPeak = touOffPeak;
    }

    @Override
    public double getTouPeak() {
        return touPeak;
    }

    @Override
    public void setTouPeak(double touPeak) {
        this.touPeak = touPeak;
    }

    @Override
    public double getTouMidPeak() {
        return touMidPeak;
    }

    @Override
    public void setTouMidPeak(double touMidPeak) {
        this.touMidPeak = touMidPeak;
    }

    @Override
    public double getTouSuperPeak() {
        return touSuperPeak;
    }

    @Override
    public void setTouSuperPeak(double touSuperPeak) {
        this.touSuperPeak = touSuperPeak;
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
    }

    public CalendarKey getDemandPeakCalendarKey() {
        return demandPeakCalendarKey;
    }

    public void setDemandPeakCalendarKey(CalendarKey demandPeakCalendarKey) {
        this.demandPeakCalendarKey = demandPeakCalendarKey;
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

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public Long getDemandPeakTime() {
        return demandPeakTime;
    }

    public void setDemandPeakTime(long demandPeakTime) {
        this.demandPeakTime = demandPeakTime;
    }

    @Override
    public void setDemandPeakTime(Long demandPeakTime) {
        this.demandPeakTime = demandPeakTime;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Long getMtuId() {
        return mtuId;
    }

    public void setMtuId(Long mtuId) {
        this.mtuId = mtuId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryMTUMinute that = (HistoryMTUMinute) o;

        if (Double.compare(that.energy, energy) != 0) return false;
        if (Double.compare(that.demandPeak, demandPeak) != 0) return false;
        if (demandPeakTime != that.demandPeakTime) return false;
        if (Double.compare(that.voltage, voltage) != 0) return false;
        if (Double.compare(that.powerFactor, powerFactor) != 0) return false;
        if (Double.compare(that.touOffPeak, touOffPeak) != 0) return false;
        if (Double.compare(that.touPeak, touPeak) != 0) return false;
        if (Double.compare(that.touMidPeak, touMidPeak) != 0) return false;
        if (Double.compare(that.touSuperPeak, touSuperPeak) != 0) return false;
        if (Double.compare(that.cost, cost) != 0) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (startEpoch != null ? !startEpoch.equals(that.startEpoch) : that.startEpoch != null) return false;
        if (endEpoch != null ? !endEpoch.equals(that.endEpoch) : that.endEpoch != null) return false;
        if (calendarKey != null ? !calendarKey.equals(that.calendarKey) : that.calendarKey != null) return false;
        return demandPeakCalendarKey != null ? demandPeakCalendarKey.equals(that.demandPeakCalendarKey) : that.demandPeakCalendarKey == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (mtuId != null ? mtuId.hashCode() : 0);
        result = 31 * result + (startEpoch != null ? startEpoch.hashCode() : 0);
        result = 31 * result + (endEpoch != null ? endEpoch.hashCode() : 0);
        temp = Double.doubleToLongBits(energy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(demandPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (demandPeakTime ^ (demandPeakTime >>> 32));
        temp = Double.doubleToLongBits(voltage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(powerFactor);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touOffPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touMidPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touSuperPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (calendarKey != null ? calendarKey.hashCode() : 0);
        result = 31 * result + (demandPeakCalendarKey != null ? demandPeakCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "HistoryMTUMinute{" +
                "virtualECCId=" + virtualECCId +
                ", mtuId=" + mtuId +
                ", startEpoch=" + startEpoch +
                ", endEpoch=" + endEpoch +
                ", energy=" + energy +
                ", demandPeak=" + demandPeak +
                ", demandPeakTime=" + demandPeakTime +
                ", voltage=" + voltage +
                ", powerFactor=" + powerFactor +
                ", touOffPeak=" + touOffPeak +
                ", touPeak=" + touPeak +
                ", touMidPeak=" + touMidPeak +
                ", touSuperPeak=" + touSuperPeak +
                ", calendarKey=" + calendarKey +
                ", demandPeakCalendarKey=" + demandPeakCalendarKey +
                ", cost=" + cost +
                '}';
    }
}



