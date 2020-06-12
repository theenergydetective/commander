package com.ted.commander.common.model.history;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import java.io.Serializable;

//import org.springframework.cache.annotation.Cacheable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryBillingCycle implements History, HistoryNetGenLoad, HistoryTOU, HistoryVoltage, HistoryPowerFactor, HistoryDemandPeak, HistoryCost, Serializable {

    @EmbeddedId
    HistoryBillingCycleKey key;

    Long startEpoch;
    Long endEpoch;

    @Column(precision = 21, scale = 6)
    double net = 0.0;

    @Column(name = "loadValue", precision = 21, scale = 6)
    double load = 0.0;

    @Column(precision = 21, scale = 6)
    double generation = 0.0;

    double demandPeak = 0.0;
    Long demandPeakTime = 0l;


    double loadPeak = 0.0;
    Long loadPeakTime = 0l;
    CalendarKey loadPeakCalendarKey;

    double generationPeak = 0.0;
    Long generationPeakTime = 0l;
    CalendarKey generationPeakCalendarKey;

    @Column(precision = 21, scale = 6)
    double demandCost = 0.0;

    @Column(columnDefinition = "TINYINT")
    TOUPeakType demandCostPeakTOU = TOUPeakType.OFF_PEAK;

    @Column(precision = 21, scale = 6)
    double demandCostPeak = 0.0;
    Long demandCostPeakTime = 0l;
    String demandCostPeakTOUName = "Off Peak";

    @Column(precision = 21, scale = 6)
    double minimumCharge = 0.0;

    @Column(precision = 21, scale = 6)
    double fixedCharge = 0.0;

    @Column(precision = 6, scale = 2)
    double minVoltage = 0.0;
    Long minVoltageTime = 0l;

    @Column(precision = 6, scale = 2)
    double peakVoltage = 0.0;
    Long peakVoltageTime = 0l;

    @Column(precision = 10, scale = 2)
    double pfTotal = 0.0;
    long pfSampleCount = 0l;

    int mtuCount = 0;

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
    double rateInEffect = 0.0;

    @Column(precision = 21, scale = 6)
    double netCost = 0.0;

    @Column(precision = 21, scale = 6)
    double loadCost = 0.0;

    @Column(precision = 21, scale = 6)
    double genCost = 0.0;

    //Added TED-253 to not lose days when the meter read date on the EP is changed.
    int meterReadStartDate = 1;
    CalendarKey calendarKey;
    CalendarKey demandPeakCalendarKey;
    CalendarKey minVoltageCalendarKey;
    CalendarKey peakVoltageCalendarKey;
    CalendarKey demandCostPeakCalendarKey;
    private boolean meterReadDateChanged = false;

    public HistoryBillingCycleKey getKey() {
        return key;
    }

    public void setKey(HistoryBillingCycleKey key) {
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
    public double getDemandPeak() {
        return demandPeak;
    }

    @Override
    public void setDemandPeak(double demandPeak) {
        this.demandPeak = demandPeak;
    }

    @Override
    public Long getDemandPeakTime() {
        return demandPeakTime;
    }

    @Override
    public void setDemandPeakTime(Long demandPeakTime) {
        this.demandPeakTime = demandPeakTime;
    }

    public double getDemandCost() {
        return demandCost;
    }

    public void setDemandCost(double demandCost) {
        this.demandCost = demandCost;
    }

    public TOUPeakType getDemandCostPeakTOU() {
        return demandCostPeakTOU;
    }

    public void setDemandCostPeakTOU(TOUPeakType demandCostPeakTOU) {
        this.demandCostPeakTOU = demandCostPeakTOU;
    }

    public double getDemandCostPeak() {
        return demandCostPeak;
    }

    public void setDemandCostPeak(double demandCostPeak) {
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

    public double getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(double minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public double getFixedCharge() {
        return fixedCharge;
    }

    public void setFixedCharge(double fixedCharge) {
        this.fixedCharge = fixedCharge;
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

    public double getRateInEffect() {
        return rateInEffect;
    }

    public void setRateInEffect(double rateInEffect) {
        this.rateInEffect = rateInEffect;
    }

    public double getNetCost() {
        return netCost;
    }

    public void setNetCost(double netCost) {
        this.netCost = netCost;
    }

    public double getLoadCost() {
        return loadCost;
    }

    public void setLoadCost(double loadCost) {
        this.loadCost = loadCost;
    }

    public double getGenCost() {
        return genCost;
    }

    public void setGenCost(double genCost) {
        this.genCost = genCost;
    }

    public int getMeterReadStartDate() {
        return meterReadStartDate;
    }

    public void setMeterReadStartDate(int meterReadStartDate) {
        this.meterReadStartDate = meterReadStartDate;
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

    public CalendarKey getMinVoltageCalendarKey() {
        return minVoltageCalendarKey;
    }

    public void setMinVoltageCalendarKey(CalendarKey minVoltageCalendarKey) {
        this.minVoltageCalendarKey = minVoltageCalendarKey;
    }

    public CalendarKey getPeakVoltageCalendarKey() {
        return peakVoltageCalendarKey;
    }

    public void setPeakVoltageCalendarKey(CalendarKey peakVoltageCalendarKey) {
        this.peakVoltageCalendarKey = peakVoltageCalendarKey;
    }

    public CalendarKey getDemandCostPeakCalendarKey() {
        return demandCostPeakCalendarKey;
    }

    public void setDemandCostPeakCalendarKey(CalendarKey demandCostPeakCalendarKey) {
        this.demandCostPeakCalendarKey = demandCostPeakCalendarKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryBillingCycle that = (HistoryBillingCycle) o;

        if (Double.compare(that.net, net) != 0) return false;
        if (Double.compare(that.load, load) != 0) return false;
        if (Double.compare(that.generation, generation) != 0) return false;
        if (Double.compare(that.demandPeak, demandPeak) != 0) return false;
        if (Double.compare(that.loadPeak, loadPeak) != 0) return false;
        if (Double.compare(that.generationPeak, generationPeak) != 0) return false;
        if (Double.compare(that.demandCost, demandCost) != 0) return false;
        if (Double.compare(that.demandCostPeak, demandCostPeak) != 0) return false;
        if (Double.compare(that.minimumCharge, minimumCharge) != 0) return false;
        if (Double.compare(that.fixedCharge, fixedCharge) != 0) return false;
        if (Double.compare(that.minVoltage, minVoltage) != 0) return false;
        if (Double.compare(that.peakVoltage, peakVoltage) != 0) return false;
        if (Double.compare(that.pfTotal, pfTotal) != 0) return false;
        if (pfSampleCount != that.pfSampleCount) return false;
        if (mtuCount != that.mtuCount) return false;
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
        if (Double.compare(that.rateInEffect, rateInEffect) != 0) return false;
        if (Double.compare(that.netCost, netCost) != 0) return false;
        if (Double.compare(that.loadCost, loadCost) != 0) return false;
        if (Double.compare(that.genCost, genCost) != 0) return false;
        if (meterReadStartDate != that.meterReadStartDate) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (startEpoch != null ? !startEpoch.equals(that.startEpoch) : that.startEpoch != null) return false;
        if (endEpoch != null ? !endEpoch.equals(that.endEpoch) : that.endEpoch != null) return false;
        if (demandPeakTime != null ? !demandPeakTime.equals(that.demandPeakTime) : that.demandPeakTime != null)
            return false;
        if (loadPeakTime != null ? !loadPeakTime.equals(that.loadPeakTime) : that.loadPeakTime != null) return false;
        if (loadPeakCalendarKey != null ? !loadPeakCalendarKey.equals(that.loadPeakCalendarKey) : that.loadPeakCalendarKey != null)
            return false;
        if (generationPeakTime != null ? !generationPeakTime.equals(that.generationPeakTime) : that.generationPeakTime != null)
            return false;
        if (generationPeakCalendarKey != null ? !generationPeakCalendarKey.equals(that.generationPeakCalendarKey) : that.generationPeakCalendarKey != null)
            return false;
        if (demandCostPeakTOU != that.demandCostPeakTOU) return false;
        if (demandCostPeakTime != null ? !demandCostPeakTime.equals(that.demandCostPeakTime) : that.demandCostPeakTime != null)
            return false;
        if (demandCostPeakTOUName != null ? !demandCostPeakTOUName.equals(that.demandCostPeakTOUName) : that.demandCostPeakTOUName != null)
            return false;
        if (minVoltageTime != null ? !minVoltageTime.equals(that.minVoltageTime) : that.minVoltageTime != null)
            return false;
        if (peakVoltageTime != null ? !peakVoltageTime.equals(that.peakVoltageTime) : that.peakVoltageTime != null)
            return false;
        if (calendarKey != null ? !calendarKey.equals(that.calendarKey) : that.calendarKey != null) return false;
        if (demandPeakCalendarKey != null ? !demandPeakCalendarKey.equals(that.demandPeakCalendarKey) : that.demandPeakCalendarKey != null)
            return false;
        if (minVoltageCalendarKey != null ? !minVoltageCalendarKey.equals(that.minVoltageCalendarKey) : that.minVoltageCalendarKey != null)
            return false;
        if (peakVoltageCalendarKey != null ? !peakVoltageCalendarKey.equals(that.peakVoltageCalendarKey) : that.peakVoltageCalendarKey != null)
            return false;
        return demandCostPeakCalendarKey != null ? demandCostPeakCalendarKey.equals(that.demandCostPeakCalendarKey) : that.demandCostPeakCalendarKey == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = key != null ? key.hashCode() : 0;
        result = 31 * result + (startEpoch != null ? startEpoch.hashCode() : 0);
        result = 31 * result + (endEpoch != null ? endEpoch.hashCode() : 0);
        temp = Double.doubleToLongBits(net);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(load);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(generation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(demandPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (demandPeakTime != null ? demandPeakTime.hashCode() : 0);
        temp = Double.doubleToLongBits(loadPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (loadPeakTime != null ? loadPeakTime.hashCode() : 0);
        result = 31 * result + (loadPeakCalendarKey != null ? loadPeakCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(generationPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (generationPeakTime != null ? generationPeakTime.hashCode() : 0);
        result = 31 * result + (generationPeakCalendarKey != null ? generationPeakCalendarKey.hashCode() : 0);
        temp = Double.doubleToLongBits(demandCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (demandCostPeakTOU != null ? demandCostPeakTOU.hashCode() : 0);
        temp = Double.doubleToLongBits(demandCostPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (demandCostPeakTime != null ? demandCostPeakTime.hashCode() : 0);
        result = 31 * result + (demandCostPeakTOUName != null ? demandCostPeakTOUName.hashCode() : 0);
        temp = Double.doubleToLongBits(minimumCharge);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fixedCharge);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minVoltage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (minVoltageTime != null ? minVoltageTime.hashCode() : 0);
        temp = Double.doubleToLongBits(peakVoltage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (peakVoltageTime != null ? peakVoltageTime.hashCode() : 0);
        temp = Double.doubleToLongBits(pfTotal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (pfSampleCount ^ (pfSampleCount >>> 32));
        result = 31 * result + mtuCount;
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
        temp = Double.doubleToLongBits(rateInEffect);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(netCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(loadCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(genCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + meterReadStartDate;
        result = 31 * result + (calendarKey != null ? calendarKey.hashCode() : 0);
        result = 31 * result + (demandPeakCalendarKey != null ? demandPeakCalendarKey.hashCode() : 0);
        result = 31 * result + (minVoltageCalendarKey != null ? minVoltageCalendarKey.hashCode() : 0);
        result = 31 * result + (peakVoltageCalendarKey != null ? peakVoltageCalendarKey.hashCode() : 0);
        result = 31 * result + (demandCostPeakCalendarKey != null ? demandCostPeakCalendarKey.hashCode() : 0);
        return result;
    }

    @Override
    public double getLoadPeak() {
        return loadPeak;
    }

    @Override
    public void setLoadPeak(double loadPeak) {
        this.loadPeak = loadPeak;
    }

    @Override
    public Long getLoadPeakTime() {
        return loadPeakTime;
    }

    @Override
    public void setLoadPeakTime(Long loadPeakTime) {
        this.loadPeakTime = loadPeakTime;
    }

    @Override
    public CalendarKey getLoadPeakCalendarKey() {
        return loadPeakCalendarKey;
    }

    @Override
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

    @Override
    public void setGenerationPeakCalendarKey(CalendarKey generationPeakCalendarKey) {
        this.generationPeakCalendarKey = generationPeakCalendarKey;
    }

    @Override
    public String toString() {
        return "HistoryBillingCycle{" +
                "key=" + key +
                ", startEpoch=" + startEpoch +
                ", endEpoch=" + endEpoch +
                ", net=" + net +
                ", load=" + load +
                ", generation=" + generation +
                ", demandPeak=" + demandPeak +
                ", demandPeakTime=" + demandPeakTime +
                ", loadPeak=" + loadPeak +
                ", loadPeakTime=" + loadPeakTime +
                ", loadPeakCalendarKey=" + loadPeakCalendarKey +
                ", generationPeak=" + generationPeak +
                ", generationPeakTime=" + generationPeakTime +
                ", generationPeakCalendarKey=" + generationPeakCalendarKey +
                ", demandCost=" + demandCost +
                ", demandCostPeakTOU=" + demandCostPeakTOU +
                ", demandCostPeak=" + demandCostPeak +
                ", demandCostPeakTime=" + demandCostPeakTime +
                ", demandCostPeakTOUName='" + demandCostPeakTOUName + '\'' +
                ", minimumCharge=" + minimumCharge +
                ", fixedCharge=" + fixedCharge +
                ", minVoltage=" + minVoltage +
                ", minVoltageTime=" + minVoltageTime +
                ", peakVoltage=" + peakVoltage +
                ", peakVoltageTime=" + peakVoltageTime +
                ", pfTotal=" + pfTotal +
                ", pfSampleCount=" + pfSampleCount +
                ", mtuCount=" + mtuCount +
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
                ", rateInEffect=" + rateInEffect +
                ", netCost=" + netCost +
                ", loadCost=" + loadCost +
                ", genCost=" + genCost +
                ", meterReadStartDate=" + meterReadStartDate +
                ", calendarKey=" + calendarKey +
                ", demandPeakCalendarKey=" + demandPeakCalendarKey +
                ", minVoltageCalendarKey=" + minVoltageCalendarKey +
                ", peakVoltageCalendarKey=" + peakVoltageCalendarKey +
                ", demandCostPeakCalendarKey=" + demandCostPeakCalendarKey +
                '}';
    }

    public boolean isMeterReadDateChanged() {
        return meterReadDateChanged;
    }

    public void setMeterReadDateChanged(boolean meterReadDateChanged) {
        this.meterReadDateChanged = meterReadDateChanged;
    }
}



