/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.TOUPeakType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyPlanTOU implements Serializable {

    TOUPeakType peakType;
    boolean morningTou = false;
    Integer seasonId;
    Long energyPlanId;
    Integer touStartHour = 12;
    Integer touStartMinute = 0;
    Integer touEndHour = 12;
    Integer touEndMinute = 0;

    public EnergyPlanTOU() {
    }

    public EnergyPlanTOU(TOUPeakType peakType, boolean AM, Integer seasonId, Long energyPlanId, Integer touStartHour, Integer touStartMinute, Integer touEndHour, Integer touEndMinute) {
        this.peakType = peakType;
        this.morningTou = morningTou;
        this.seasonId = seasonId;
        this.energyPlanId = energyPlanId;
        this.touStartHour = touStartHour;
        this.touStartMinute = touStartMinute;
        this.touEndHour = touEndHour;
        this.touEndMinute = touEndMinute;
    }

    public TOUPeakType getPeakType() {
        return peakType;
    }

    public void setPeakType(TOUPeakType peakType) {
        this.peakType = peakType;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public Long getEnergyPlanId() {
        return energyPlanId;
    }

    public void setEnergyPlanId(Long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    public Integer getTouStartHour() {
        return touStartHour;
    }

    public void setTouStartHour(Integer touStartHour) {
        this.touStartHour = touStartHour;
    }

    public Integer getTouStartMinute() {
        return touStartMinute;
    }

    public void setTouStartMinute(Integer touStartMinute) {
        this.touStartMinute = touStartMinute;
    }

    public Integer getTouEndHour() {
        return touEndHour;
    }

    public void setTouEndHour(Integer touEndHour) {
        this.touEndHour = touEndHour;
    }

    public Integer getTouEndMinute() {
        return touEndMinute;
    }

    public void setTouEndMinute(Integer touEndMinute) {
        this.touEndMinute = touEndMinute;
    }

    public boolean isMorningTou() {
        return morningTou;
    }

    public void setMorningTou(boolean morningTou) {
        this.morningTou = morningTou;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPlanTOU that = (EnergyPlanTOU) o;

        if (morningTou != that.morningTou) return false;
        if (energyPlanId != null ? !energyPlanId.equals(that.energyPlanId) : that.energyPlanId != null) return false;
        if (peakType != that.peakType) return false;
        if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null) return false;
        if (touEndHour != null ? !touEndHour.equals(that.touEndHour) : that.touEndHour != null) return false;
        if (touEndMinute != null ? !touEndMinute.equals(that.touEndMinute) : that.touEndMinute != null) return false;
        if (touStartHour != null ? !touStartHour.equals(that.touStartHour) : that.touStartHour != null) return false;
        if (touStartMinute != null ? !touStartMinute.equals(that.touStartMinute) : that.touStartMinute != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = peakType != null ? peakType.hashCode() : 0;
        result = 31 * result + (morningTou ? 1 : 0);
        result = 31 * result + (seasonId != null ? seasonId.hashCode() : 0);
        result = 31 * result + (energyPlanId != null ? energyPlanId.hashCode() : 0);
        result = 31 * result + (touStartHour != null ? touStartHour.hashCode() : 0);
        result = 31 * result + (touStartMinute != null ? touStartMinute.hashCode() : 0);
        result = 31 * result + (touEndHour != null ? touEndHour.hashCode() : 0);
        result = 31 * result + (touEndMinute != null ? touEndMinute.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyPlanTOU{" +
                "tou=" + peakType +
                ", morningTou=" + morningTou +
                ", seasonId=" + seasonId +
                ", energyPlanId=" + energyPlanId +
                ", touStartHour=" + touStartHour +
                ", touStartMinute=" + touStartMinute +
                ", touEndHour=" + touEndHour +
                ", touEndMinute=" + touEndMinute +
                '}';
    }

    @JsonIgnore
    public boolean before(EnergyPlanTOU that) {
        if (that.getPeakType().equals(TOUPeakType.OFF_PEAK)) return false;
        if (this.touStartHour < that.touEndHour) {
            return true;
        }
        if (that.touEndHour == this.touStartHour && this.touStartMinute < that.touEndMinute) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public void forceAfter(EnergyPlanTOU energyPlanTOU) {
        this.touStartHour = energyPlanTOU.touEndHour;
        this.touStartMinute = energyPlanTOU.touEndMinute;
        balance();
    }

    @JsonIgnore
    public void balance() {
        if (touEndHour < touStartHour) {
            touEndHour = touStartHour;
            touEndMinute = touStartMinute;
        } else if (touEndHour == touStartHour) {
            if (touEndMinute < touStartMinute) touEndMinute = touStartMinute;
        }
    }
}
