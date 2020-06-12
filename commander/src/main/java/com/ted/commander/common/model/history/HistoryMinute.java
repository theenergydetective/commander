package com.ted.commander.common.model.history;

import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMinute implements History, HistoryNetGenLoad, HistoryTOU, HistoryPowerFactor, HistoryRunningTotal, HistoryCost, HistoryDemandPeak, Serializable {

    Long virtualECCId;

    Long startEpoch;
    @Column(precision = 21, scale = 6)
    double net = 0.0;

    @Column(name = "loadValue", precision = 21, scale = 6)
    double load = 0.0;
    @Column(precision = 21, scale = 6)
    double generation = 0.0;

    @Column(precision = 21, scale = 6)
    double touOffPeakNet = 0.0;
    @Column(precision = 21, scale = 6)
    double touPeakNet = 0.0;
    @Column(precision = 21, scale = 6)
    double touMidPeakNet = 0.0;
    @Column(precision = 21, scale = 6)
    double touSuperPeakNet = 0.0;
    @Column(precision = 21, scale = 6)
    double touOffPeakGen = 0.0;
    @Column(precision = 21, scale = 6)
    double touPeakGen = 0.0;
    @Column(precision = 21, scale = 6)
    double touMidPeakGen = 0.0;
    @Column(precision = 21, scale = 6)
    double touSuperPeakGen = 0.0;
    @Column(precision = 21, scale = 6)
    double touOffPeakLoad = 0.0;
    @Column(precision = 21, scale = 6)
    double touPeakLoad = 0.0;
    @Column(precision = 21, scale = 6)
    double touMidPeakLoad = 0.0;

    @Column(precision = 21, scale = 6)
    double touSuperPeakLoad = 0.0;

    @Column(precision = 21, scale = 6)

    double demandPeak = 0.0;
    Long demandPeakTime = 0l;
    CalendarKey demandPeakCalendarKey;

    double loadPeak = 0.0;
    Long loadPeakTime = 0l;
    CalendarKey loadPeakCalendarKey;

    double generationPeak = 0.0;
    Long generationPeakTime = 0l;
    CalendarKey generationPeakCalendarKey;


    @Column(precision = 21, scale = 6)
    double voltageTotal = 0.0;

    @Column(precision = 10, scale = 2)
    double pfTotal = 0.0;
    long pfSampleCount = 0l;
    int mtuCount = 0;

    @Column(precision = 32, scale = 4)
    double runningNetTotal = 0.0;


    @Column(precision = 21, scale = 6)
    double rateInEffect = 0.0;

    @Column(precision = 21, scale = 6)
    double netCost = 0.0;

    @Column(precision = 21, scale = 6)
    double loadCost = 0.0;

    @Column(precision = 21, scale = 6)
    double genCost = 0.0;
    int dow;
    private CalendarKey calendarKey;

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    @Override
    public double getNet() {
        return net;
    }

    @Override
    public void setNet(double net) {
        this.net = net;
    }

    @Override
    public double getLoad() {
        return load;
    }

    @Override
    public void setLoad(double load) {
        this.load = load;
    }

    @Override
    public double getGeneration() {
        return generation;
    }

    @Override
    public void setGeneration(double generation) {
        this.generation = generation;
    }

    @Override
    public double getTouOffPeakNet() {
        return touOffPeakNet;
    }

    @Override
    public void setTouOffPeakNet(double touOffPeakNet) {
        this.touOffPeakNet = touOffPeakNet;
    }

    @Override
    public double getTouPeakNet() {
        return touPeakNet;
    }

    @Override
    public void setTouPeakNet(double touPeakNet) {
        this.touPeakNet = touPeakNet;
    }

    @Override
    public double getTouMidPeakNet() {
        return touMidPeakNet;
    }

    @Override
    public void setTouMidPeakNet(double touMidPeakNet) {
        this.touMidPeakNet = touMidPeakNet;
    }

    @Override
    public double getTouSuperPeakNet() {
        return touSuperPeakNet;
    }

    @Override
    public void setTouSuperPeakNet(double touSuperPeakNet) {
        this.touSuperPeakNet = touSuperPeakNet;
    }

    @Override
    public double getTouOffPeakGen() {
        return touOffPeakGen;
    }

    @Override
    public void setTouOffPeakGen(double touOffPeakGen) {
        this.touOffPeakGen = touOffPeakGen;
    }

    @Override
    public double getTouPeakGen() {
        return touPeakGen;
    }

    @Override
    public void setTouPeakGen(double touPeakGen) {
        this.touPeakGen = touPeakGen;
    }

    @Override
    public double getTouMidPeakGen() {
        return touMidPeakGen;
    }

    @Override
    public void setTouMidPeakGen(double touMidPeakGen) {
        this.touMidPeakGen = touMidPeakGen;
    }

    @Override
    public double getTouSuperPeakGen() {
        return touSuperPeakGen;
    }

    @Override
    public void setTouSuperPeakGen(double touSuperPeakGen) {
        this.touSuperPeakGen = touSuperPeakGen;
    }

    @Override
    public double getTouOffPeakLoad() {
        return touOffPeakLoad;
    }

    @Override
    public void setTouOffPeakLoad(double touOffPeakLoad) {
        this.touOffPeakLoad = touOffPeakLoad;
    }

    @Override
    public double getTouPeakLoad() {
        return touPeakLoad;
    }

    @Override
    public void setTouPeakLoad(double touPeakLoad) {
        this.touPeakLoad = touPeakLoad;
    }

    @Override
    public double getTouMidPeakLoad() {
        return touMidPeakLoad;
    }

    @Override
    public void setTouMidPeakLoad(double touMidPeakLoad) {
        this.touMidPeakLoad = touMidPeakLoad;
    }

    @Override
    public double getTouSuperPeakLoad() {
        return touSuperPeakLoad;
    }

    @Override
    public void setTouSuperPeakLoad(double touSuperPeakLoad) {
        this.touSuperPeakLoad = touSuperPeakLoad;
    }

    public double getDemandPeak() {
        return demandPeak;
    }

    public void setDemandPeak(double demandPeak) {
        this.demandPeak = demandPeak;
    }

    public double getVoltageTotal() {
        return voltageTotal;
    }

    public void setVoltageTotal(double voltageTotal) {
        this.voltageTotal = voltageTotal;
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

    public int getMtuCount() {
        return mtuCount;
    }

    public void setMtuCount(int mtuCount) {
        this.mtuCount = mtuCount;
    }

    @Override
    public double getRunningNetTotal() {
        return runningNetTotal;
    }

    @Override
    public void setRunningNetTotal(double runningNetTotal) {
        this.runningNetTotal = runningNetTotal;
    }

    @Override
    public double getRateInEffect() {
        return rateInEffect;
    }

    @Override
    public void setRateInEffect(double rateInEffect) {
        this.rateInEffect = rateInEffect;
    }

    @Override
    public double getNetCost() {
        return netCost;
    }

    @Override
    public void setNetCost(double netCost) {
        this.netCost = netCost;
    }

    @Override
    public double getLoadCost() {
        return loadCost;
    }

    @Override
    public void setLoadCost(double loadCost) {
        this.loadCost = loadCost;
    }

    @Override
    public double getGenCost() {
        return genCost;
    }

    @Override
    public void setGenCost(double genCost) {
        this.genCost = genCost;
    }

    public int getDow() {
        return dow;
    }

    public void setDow(int dow) {
        this.dow = dow;
    }

    public Long getDemandPeakTime() {
        return demandPeakTime;
    }

    public void setDemandPeakTime(Long demandPeakTime) {
        this.demandPeakTime = demandPeakTime;
    }

    @Override
    public CalendarKey getDemandPeakCalendarKey() {
        return demandPeakCalendarKey;
    }

    public void setDemandPeakCalendarKey(CalendarKey demandPeakCalendarKey) {
        this.demandPeakCalendarKey = demandPeakCalendarKey;
    }

    @Override
    public double getLoadPeak() {
        return loadPeak;
    }

    @Override
    public void setLoadPeak(double loadPeak) {
        this.loadPeak = loadPeak;
    }

    public Long getLoadPeakTime() {
        return loadPeakTime;
    }

    public void setLoadPeakTime(Long loadPeakTime) {
        this.loadPeakTime = loadPeakTime;
    }

    @Override
    public CalendarKey getLoadPeakCalendarKey() {
        return loadPeakCalendarKey;
    }

    public void setLoadPeakCalendarKey(CalendarKey loadPeakCalendarKey) {
        this.loadPeakCalendarKey = loadPeakCalendarKey;
    }

    @Override
    public double getGenerationPeak() {
        return generationPeak;
    }

    @Override
    public void setGenerationPeak(double generationPeak) {
        this.generationPeak = generationPeak;
    }

    @Override
    public Long getGenerationPeakTime() {
        return generationPeakTime;
    }

    @Override
    public void setGenerationPeakTime(Long generationPeakTime) {
        this.generationPeakTime = generationPeakTime;
    }

    @Override
    public CalendarKey getGenerationPeakCalendarKey() {
        return generationPeakCalendarKey;
    }

    public void setGenerationPeakCalendarKey(CalendarKey generationPeakCalendarKey) {
        this.generationPeakCalendarKey = generationPeakCalendarKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryMinute that = (HistoryMinute) o;

        if (Double.compare(that.net, net) != 0) return false;
        if (Double.compare(that.load, load) != 0) return false;
        if (Double.compare(that.generation, generation) != 0) return false;
        if (Double.compare(that.touOffPeakNet, touOffPeakNet) != 0) return false;
        if (Double.compare(that.touPeakNet, touPeakNet) != 0) return false;
        if (Double.compare(that.touMidPeakNet, touMidPeakNet) != 0) return false;
        if (Double.compare(that.touSuperPeakNet, touSuperPeakNet) != 0) return false;
        if (Double.compare(that.touOffPeakGen, touOffPeakGen) != 0) return false;
        if (Double.compare(that.touPeakGen, touPeakGen) != 0) return false;
        if (Double.compare(that.touMidPeakGen, touMidPeakGen) != 0) return false;
        if (Double.compare(that.touSuperPeakGen, touSuperPeakGen) != 0) return false;
        if (Double.compare(that.touOffPeakLoad, touOffPeakLoad) != 0) return false;
        if (Double.compare(that.touPeakLoad, touPeakLoad) != 0) return false;
        if (Double.compare(that.touMidPeakLoad, touMidPeakLoad) != 0) return false;
        if (Double.compare(that.touSuperPeakLoad, touSuperPeakLoad) != 0) return false;
        if (Double.compare(that.demandPeak, demandPeak) != 0) return false;
        if (Double.compare(that.loadPeak, loadPeak) != 0) return false;
        if (Double.compare(that.generationPeak, generationPeak) != 0) return false;
        if (Double.compare(that.voltageTotal, voltageTotal) != 0) return false;
        if (Double.compare(that.pfTotal, pfTotal) != 0) return false;
        if (pfSampleCount != that.pfSampleCount) return false;
        if (mtuCount != that.mtuCount) return false;
        if (Double.compare(that.runningNetTotal, runningNetTotal) != 0) return false;
        if (Double.compare(that.rateInEffect, rateInEffect) != 0) return false;
        if (Double.compare(that.netCost, netCost) != 0) return false;
        if (Double.compare(that.loadCost, loadCost) != 0) return false;
        if (Double.compare(that.genCost, genCost) != 0) return false;
        if (dow != that.dow) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;
        if (startEpoch != null ? !startEpoch.equals(that.startEpoch) : that.startEpoch != null) return false;
        if (demandPeakTime != null ? !demandPeakTime.equals(that.demandPeakTime) : that.demandPeakTime != null)
            return false;
        if (demandPeakCalendarKey != null ? !demandPeakCalendarKey.equals(that.demandPeakCalendarKey) : that.demandPeakCalendarKey != null)
            return false;
        if (loadPeakTime != null ? !loadPeakTime.equals(that.loadPeakTime) : that.loadPeakTime != null) return false;
        if (loadPeakCalendarKey != null ? !loadPeakCalendarKey.equals(that.loadPeakCalendarKey) : that.loadPeakCalendarKey != null)
            return false;
        if (generationPeakTime != null ? !generationPeakTime.equals(that.generationPeakTime) : that.generationPeakTime != null)
            return false;
        if (generationPeakCalendarKey != null ? !generationPeakCalendarKey.equals(that.generationPeakCalendarKey) : that.generationPeakCalendarKey != null)
            return false;
        return calendarKey != null ? calendarKey.equals(that.calendarKey) : that.calendarKey == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = virtualECCId != null ? virtualECCId.hashCode() : 0;
        result = 31 * result + (startEpoch != null ? startEpoch.hashCode() : 0);
        temp = Double.doubleToLongBits(net);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(load);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(generation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touOffPeakNet);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touPeakNet);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touMidPeakNet);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touSuperPeakNet);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touOffPeakGen);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touPeakGen);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touMidPeakGen);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touSuperPeakGen);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touOffPeakLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touPeakLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touMidPeakLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(touSuperPeakLoad);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(demandPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (demandPeakTime != null ? demandPeakTime.hashCode() : 0);
        result = 31 * result + (demandPeakCalendarKey != null ? demandPeakCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(loadPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (loadPeakTime != null ? loadPeakTime.hashCode() : 0);
        result = 31 * result + (loadPeakCalendarKey != null ? loadPeakCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(generationPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (generationPeakTime != null ? generationPeakTime.hashCode() : 0);
        result = 31 * result + (generationPeakCalendarKey != null ? generationPeakCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(voltageTotal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pfTotal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (pfSampleCount ^ (pfSampleCount >>> 32));
        result = 31 * result + mtuCount;
        temp = Double.doubleToLongBits(runningNetTotal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rateInEffect);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(netCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(loadCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(genCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (calendarKey != null ? calendarKey.hashCode() : 0);
        result = 31 * result + dow;
        return result;
    }

    @Override
    public String toString() {
        return "HistoryMinute{" +
                "virtualECCId=" + virtualECCId +
                ", startEpoch=" + startEpoch +
                ", net=" + net +
                ", load=" + load +
                ", generation=" + generation +
                ", touOffPeakNet=" + touOffPeakNet +
                ", touPeakNet=" + touPeakNet +
                ", touMidPeakNet=" + touMidPeakNet +
                ", touSuperPeakNet=" + touSuperPeakNet +
                ", touOffPeakGen=" + touOffPeakGen +
                ", touPeakGen=" + touPeakGen +
                ", touMidPeakGen=" + touMidPeakGen +
                ", touSuperPeakGen=" + touSuperPeakGen +
                ", touOffPeakLoad=" + touOffPeakLoad +
                ", touPeakLoad=" + touPeakLoad +
                ", touMidPeakLoad=" + touMidPeakLoad +
                ", touSuperPeakLoad=" + touSuperPeakLoad +
                ", demandPeak=" + demandPeak +
                ", demandPeakTime=" + demandPeakTime +
                ", demandPeakCalendarKey=" + demandPeakCalendarKey +
                ", loadPeak=" + loadPeak +
                ", loadPeakTime=" + loadPeakTime +
                ", loadPeakCalendarKey=" + loadPeakCalendarKey +
                ", generationPeak=" + generationPeak +
                ", generationPeakTime=" + generationPeakTime +
                ", generationPeakCalendarKey=" + generationPeakCalendarKey +
                ", voltageTotal=" + voltageTotal +
                ", pfTotal=" + pfTotal +
                ", pfSampleCount=" + pfSampleCount +
                ", mtuCount=" + mtuCount +
                ", runningNetTotal=" + runningNetTotal +
                ", rateInEffect=" + rateInEffect +
                ", netCost=" + netCost +
                ", loadCost=" + loadCost +
                ", genCost=" + genCost +
                ", calendarKey=" + calendarKey +
                ", dow=" + dow +
                '}';
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
    }
}
