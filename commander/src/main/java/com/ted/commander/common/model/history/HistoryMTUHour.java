package com.ted.commander.common.model.history;

import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMTUHour implements History, HistoryMTU, HistoryMTUCost, HistoryVoltage, HistoryPowerFactor, Serializable {


    Long virtualECCId;
    Long mtuId;
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
    double pfTotal = 0.0;
    long pfSampleCount = 0l;
    private CalendarKey calendarKey;
    private CalendarKey demandPeakCalendarKey;
    private double cost = 0.0;
    private double peakVoltage = 0.0;
    private Long peakVoltageTime = 0l;
    private CalendarKey peakVoltageCalendarKey;
    private double minVoltage = 0.0;
    private Long minVoltageTime = 0l;
    private CalendarKey minVoltageCalendarKey;

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

    @Override
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

    @Override
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryMTUHour that = (HistoryMTUHour) o;

        if (Double.compare(that.energy, energy) != 0) return false;
        if (Double.compare(that.demandPeak, demandPeak) != 0) return false;
        if (Double.compare(that.touOffPeak, touOffPeak) != 0) return false;
        if (Double.compare(that.touPeak, touPeak) != 0) return false;
        if (Double.compare(that.touMidPeak, touMidPeak) != 0) return false;
        if (Double.compare(that.touSuperPeak, touSuperPeak) != 0) return false;
        if (Double.compare(that.cost, cost) != 0) return false;
        if (Double.compare(that.peakVoltage, peakVoltage) != 0) return false;
        if (Double.compare(that.minVoltage, minVoltage) != 0) return false;
        if (Double.compare(that.pfTotal, pfTotal) != 0) return false;
        if (pfSampleCount != that.pfSampleCount) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (mtuId != null ? !mtuId.equals(that.mtuId) : that.mtuId != null) return false;
        if (startEpoch != null ? !startEpoch.equals(that.startEpoch) : that.startEpoch != null) return false;
        if (endEpoch != null ? !endEpoch.equals(that.endEpoch) : that.endEpoch != null) return false;
        if (demandPeakTime != null ? !demandPeakTime.equals(that.demandPeakTime) : that.demandPeakTime != null)
            return false;
        if (calendarKey != null ? !calendarKey.equals(that.calendarKey) : that.calendarKey != null) return false;
        if (demandPeakCalendarKey != null ? !demandPeakCalendarKey.equals(that.demandPeakCalendarKey) : that.demandPeakCalendarKey != null)
            return false;
        if (peakVoltageTime != null ? !peakVoltageTime.equals(that.peakVoltageTime) : that.peakVoltageTime != null)
            return false;
        if (peakVoltageCalendarKey != null ? !peakVoltageCalendarKey.equals(that.peakVoltageCalendarKey) : that.peakVoltageCalendarKey != null)
            return false;
        if (minVoltageTime != null ? !minVoltageTime.equals(that.minVoltageTime) : that.minVoltageTime != null)
            return false;
        return minVoltageCalendarKey != null ? minVoltageCalendarKey.equals(that.minVoltageCalendarKey) : that.minVoltageCalendarKey == null;

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
        result = 31 * result + (demandPeakTime != null ? demandPeakTime.hashCode() : 0);
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
        temp = Double.doubleToLongBits(peakVoltage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (peakVoltageTime != null ? peakVoltageTime.hashCode() : 0);
        result = 31 * result + (peakVoltageCalendarKey != null ? peakVoltageCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(minVoltage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (minVoltageTime != null ? minVoltageTime.hashCode() : 0);
        result = 31 * result + (minVoltageCalendarKey != null ? minVoltageCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(pfTotal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (pfSampleCount ^ (pfSampleCount >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "HistoryMTUHour{" +
                "virtualECCId=" + virtualECCId +
                ", mtuId=" + mtuId +
                ", startEpoch=" + startEpoch +
                ", endEpoch=" + endEpoch +
                ", energy=" + energy +
                ", demandPeak=" + demandPeak +
                ", demandPeakTime=" + demandPeakTime +
                ", touOffPeak=" + touOffPeak +
                ", touPeak=" + touPeak +
                ", touMidPeak=" + touMidPeak +
                ", touSuperPeak=" + touSuperPeak +
                ", calendarKey=" + calendarKey +
                ", demandPeakCalendarKey=" + demandPeakCalendarKey +
                ", cost=" + cost +
                ", peakVoltage=" + peakVoltage +
                ", peakVoltageTime=" + peakVoltageTime +
                ", peakVoltageCalendarKey=" + peakVoltageCalendarKey +
                ", minVoltage=" + minVoltage +
                ", minVoltageTime=" + minVoltageTime +
                ", minVoltageCalendarKey=" + minVoltageCalendarKey +
                ", pfTotal=" + pfTotal +
                ", pfSampleCount=" + pfSampleCount +
                '}';
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}



