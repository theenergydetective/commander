package com.ted.commander.common.model.history;

import com.ted.commander.common.enums.TOUPeakType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

//import org.springframework.cache.annotation.Cacheable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingRecord implements Serializable{
    String eccName;
    Long virtualECCId;
    String timeZone;
    Integer billingCycleMonth = null;
    Integer billingCycleYear = null;
    long startEpoch;
    long endEpoch;
    double net = 0.0;
    double demandPeak = 0.0;
    long demandPeakTime = 0;
    double demandCost = 0.0;
    double netCost = 0.0;
    private TOUPeakType demandPeakTOU;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getEccName() {
        return eccName;
    }

    public void setEccName(String eccName) {
        this.eccName = eccName;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Integer getBillingCycleMonth() {
        return billingCycleMonth;
    }

    public void setBillingCycleMonth(Integer billingCycleMonth) {
        this.billingCycleMonth = billingCycleMonth;
    }

    public Integer getBillingCycleYear() {
        return billingCycleYear;
    }

    public void setBillingCycleYear(Integer billingCycleYear) {
        this.billingCycleYear = billingCycleYear;
    }

    public long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public long getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(long endEpoch) {
        this.endEpoch = endEpoch;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getDemandPeak() {
        return demandPeak;
    }

    public void setDemandPeak(double demandPeak) {
        this.demandPeak = demandPeak;
    }

    public long getDemandPeakTime() {
        return demandPeakTime;
    }

    public void setDemandPeakTime(long demandPeakTime) {
        this.demandPeakTime = demandPeakTime;
    }

    public double getDemandCost() {
        return demandCost;
    }

    public void setDemandCost(double demandCost) {
        this.demandCost = demandCost;
    }

    public double getNetCost() {
        return netCost;
    }

    public void setNetCost(double netCost) {
        this.netCost = netCost;
    }

    @Override
    public String toString() {
        return "BillingRecord{" +
                "eccName='" + eccName + '\'' +
                ", virtualECCId=" + virtualECCId +
                ", billingCycleMonth=" + billingCycleMonth +
                ", billingCycleYear=" + billingCycleYear +
                ", startEpoch=" + startEpoch +
                ", endEpoch=" + endEpoch +
                ", net=" + net +
                ", demandPeak=" + demandPeak +
                ", demandPeakTime=" + demandPeakTime +
                ", demandCost=" + demandCost +
                ", netCost=" + netCost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillingRecord that = (BillingRecord) o;

        if (billingCycleMonth != that.billingCycleMonth) return false;
        if (billingCycleYear != that.billingCycleYear) return false;
        if (startEpoch != that.startEpoch) return false;
        if (endEpoch != that.endEpoch) return false;
        if (Double.compare(that.net, net) != 0) return false;
        if (Double.compare(that.demandPeak, demandPeak) != 0) return false;
        if (demandPeakTime != that.demandPeakTime) return false;
        if (Double.compare(that.demandCost, demandCost) != 0) return false;
        if (Double.compare(that.netCost, netCost) != 0) return false;
        if (eccName != null ? !eccName.equals(that.eccName) : that.eccName != null) return false;
        return virtualECCId != null ? virtualECCId.equals(that.virtualECCId) : that.virtualECCId == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = eccName != null ? eccName.hashCode() : 0;
        result = 31 * result + (virtualECCId != null ? virtualECCId.hashCode() : 0);
        result = 31 * result + billingCycleMonth;
        result = 31 * result + billingCycleYear;
        result = 31 * result + (int) (startEpoch ^ (startEpoch >>> 32));
        result = 31 * result + (int) (endEpoch ^ (endEpoch >>> 32));
        temp = Double.doubleToLongBits(net);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(demandPeak);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (demandPeakTime ^ (demandPeakTime >>> 32));
        temp = Double.doubleToLongBits(demandCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(netCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setDemandPeakTOU(TOUPeakType demandPeakTOU) {
        this.demandPeakTOU = demandPeakTOU;
    }

    public TOUPeakType getDemandPeakTOU() {
        return demandPeakTOU;
    }
}



