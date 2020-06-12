/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.enums.PlanType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyPlan implements Serializable {

    Long id = 0l;

    Long accountId;

    Integer meterReadDate = 1;

    MeterReadCycle meterReadCycle = MeterReadCycle.MONTHLY; //0-default monthly

    PlanType planType = PlanType.FLAT;

    boolean touApplicableSaturday = false;
    boolean touApplicableSunday = false;
    boolean touApplicableHoliday = false;

    Long holidayScheduleId = 0l; //US Schedule Default

    DemandPlanType demandPlanType = DemandPlanType.NONE;

    boolean demandUseActivePower = true;
    boolean demandApplicableSaturday = false;
    boolean demandApplicableSunday = false;
    boolean demandApplicableHoliday = false;
    boolean demandApplicableOffPeak = false;
    Integer demandAverageTime = 15;
    String description;
    String rateType = "USD";
    String utilityName;

    Double buybackRate = 0.9;

    boolean buyback = false;
    int numberSeasons = 1;
    int numberTOU = 1;

    int numberTier = 1;

    int numberDemandSteps = 1;

    boolean surcharge = false;
    boolean fixed = false;
    boolean minimum = false;
    boolean taxes = false;

    List<EnergyPlanSeason> seasonList = new ArrayList<EnergyPlanSeason>();

    List<EnergyPlanTOULevel> touLevels = new ArrayList<EnergyPlanTOULevel>();

    public EnergyPlan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getMeterReadDate() {
        return meterReadDate;
    }

    public void setMeterReadDate(Integer meterReadDate) {
        this.meterReadDate = meterReadDate;
    }

    public MeterReadCycle getMeterReadCycle() {
        return meterReadCycle;
    }

    public void setMeterReadCycle(MeterReadCycle meterReadCycle) {
        this.meterReadCycle = meterReadCycle;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public Boolean getTouApplicableSaturday() {
        return touApplicableSaturday;
    }

    public void setTouApplicableSaturday(Boolean touApplicableSaturday) {
        this.touApplicableSaturday = touApplicableSaturday;
    }

    public Boolean getTouApplicableSunday() {
        return touApplicableSunday;
    }

    public void setTouApplicableSunday(Boolean touApplicableSunday) {
        this.touApplicableSunday = touApplicableSunday;
    }

    public Boolean getTouApplicableHoliday() {
        return touApplicableHoliday;
    }

    public void setTouApplicableHoliday(Boolean touApplicableHoliday) {
        this.touApplicableHoliday = touApplicableHoliday;
    }

    public Long getHolidayScheduleId() {
        if (holidayScheduleId == null) holidayScheduleId = 0l;
        return holidayScheduleId;
    }

    public void setHolidayScheduleId(Long holidayScheduleId) {
        this.holidayScheduleId = holidayScheduleId;
    }

    public DemandPlanType getDemandPlanType() {
        return demandPlanType;
    }

    public void setDemandPlanType(DemandPlanType demandPlanType) {
        this.demandPlanType = demandPlanType;
    }

    public Boolean getDemandUseActivePower() {
        return demandUseActivePower;
    }

    public void setDemandUseActivePower(Boolean demandUseActivePower) {
        this.demandUseActivePower = demandUseActivePower;
    }

    public Boolean getDemandApplicableSaturday() {
        return demandApplicableSaturday;
    }

    public void setDemandApplicableSaturday(Boolean demandApplicableSaturday) {
        this.demandApplicableSaturday = demandApplicableSaturday;
    }

    public Boolean getDemandApplicableSunday() {
        return demandApplicableSunday;
    }

    public void setDemandApplicableSunday(Boolean demandApplicableSunday) {
        this.demandApplicableSunday = demandApplicableSunday;
    }

    public Boolean getDemandApplicableHoliday() {
        return demandApplicableHoliday;
    }

    public void setDemandApplicableHoliday(Boolean demandApplicableHoliday) {
        this.demandApplicableHoliday = demandApplicableHoliday;
    }

    public Boolean getDemandApplicableOffPeak() {
        return demandApplicableOffPeak;
    }

    public void setDemandApplicableOffPeak(Boolean demandApplicableOffPeak) {
        this.demandApplicableOffPeak = demandApplicableOffPeak;
    }

    public Integer getDemandAverageTime() {
        if (demandAverageTime == null) {
            demandAverageTime = 15;
        }
        return demandAverageTime;
    }

    public void setDemandAverageTime(Integer demandAverageTime) {
        this.demandAverageTime = demandAverageTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public List<EnergyPlanSeason> getSeasonList() {
        return seasonList;
    }

    public void setSeasonList(List<EnergyPlanSeason> seasonList) {
        this.seasonList = seasonList;
    }

    public List<EnergyPlanTOULevel> getTouLevels() {
        return touLevels;
    }

    public void setTouLevels(List<EnergyPlanTOULevel> touLevels) {
        this.touLevels = touLevels;
    }

    public Double getBuybackRate() {
        return buybackRate;
    }

    public void setBuybackRate(Double buybackRate) {
        this.buybackRate = buybackRate;
    }

    public int getNumberSeasons() {
        return numberSeasons;
    }

    public void setNumberSeasons(int numberSeasons) {
        this.numberSeasons = numberSeasons;
    }

    public int getNumberTOU() {
        //  if (planType == PlanType.FLAT || planType == PlanType.TIER) return 1;
        return numberTOU;
    }

    public void setNumberTOU(int numberTOU) {

        this.numberTOU = numberTOU;
    }

    public int getNumberTier() {
        //     if (planType == PlanType.FLAT || planType == PlanType.TOU) return 1;
        return numberTier;
    }

    public void setNumberTier(int numberTier) {
        this.numberTier = numberTier;
    }

    public boolean isBuyback() {
        return buyback;
    }

    public void setBuyback(boolean buyback) {
        this.buyback = buyback;
    }

    public int getNumberDemandSteps() {
        return numberDemandSteps;
    }

    public void setNumberDemandSteps(int numberDemandSteps) {
        this.numberDemandSteps = numberDemandSteps;
    }

    public boolean isSurcharge() {
        return surcharge;
    }

    public void setSurcharge(boolean surcharge) {
        this.surcharge = surcharge;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean isMinimum() {
        return minimum;
    }

    public void setMinimum(boolean minimum) {
        this.minimum = minimum;
    }

    public boolean isTaxes() {
        return taxes;
    }

    public void setTaxes(boolean taxes) {
        this.taxes = taxes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPlan that = (EnergyPlan) o;

        if (touApplicableSaturday != that.touApplicableSaturday) return false;
        if (touApplicableSunday != that.touApplicableSunday) return false;
        if (touApplicableHoliday != that.touApplicableHoliday) return false;
        if (demandUseActivePower != that.demandUseActivePower) return false;
        if (demandApplicableSaturday != that.demandApplicableSaturday) return false;
        if (demandApplicableSunday != that.demandApplicableSunday) return false;
        if (demandApplicableHoliday != that.demandApplicableHoliday) return false;
        if (demandApplicableOffPeak != that.demandApplicableOffPeak) return false;
        if (buyback != that.buyback) return false;
        if (numberSeasons != that.numberSeasons) return false;
        if (numberTOU != that.numberTOU) return false;
        if (numberTier != that.numberTier) return false;
        if (numberDemandSteps != that.numberDemandSteps) return false;
        if (surcharge != that.surcharge) return false;
        if (fixed != that.fixed) return false;
        if (minimum != that.minimum) return false;
        if (taxes != that.taxes) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (meterReadDate != null ? !meterReadDate.equals(that.meterReadDate) : that.meterReadDate != null)
            return false;
        if (meterReadCycle != that.meterReadCycle) return false;
        if (planType != that.planType) return false;
        if (holidayScheduleId != null ? !holidayScheduleId.equals(that.holidayScheduleId) : that.holidayScheduleId != null)
            return false;
        if (demandPlanType != that.demandPlanType) return false;
        if (demandAverageTime != null ? !demandAverageTime.equals(that.demandAverageTime) : that.demandAverageTime != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (rateType != null ? !rateType.equals(that.rateType) : that.rateType != null) return false;
        if (utilityName != null ? !utilityName.equals(that.utilityName) : that.utilityName != null) return false;
        if (buybackRate != null ? !buybackRate.equals(that.buybackRate) : that.buybackRate != null) return false;
        if (seasonList != null ? !seasonList.equals(that.seasonList) : that.seasonList != null) return false;
        return !(touLevels != null ? !touLevels.equals(that.touLevels) : that.touLevels != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (meterReadDate != null ? meterReadDate.hashCode() : 0);
        result = 31 * result + (meterReadCycle != null ? meterReadCycle.hashCode() : 0);
        result = 31 * result + (planType != null ? planType.hashCode() : 0);
        result = 31 * result + (touApplicableSaturday ? 1 : 0);
        result = 31 * result + (touApplicableSunday ? 1 : 0);
        result = 31 * result + (touApplicableHoliday ? 1 : 0);
        result = 31 * result + (holidayScheduleId != null ? holidayScheduleId.hashCode() : 0);
        result = 31 * result + (demandPlanType != null ? demandPlanType.hashCode() : 0);
        result = 31 * result + (demandUseActivePower ? 1 : 0);
        result = 31 * result + (demandApplicableSaturday ? 1 : 0);
        result = 31 * result + (demandApplicableSunday ? 1 : 0);
        result = 31 * result + (demandApplicableHoliday ? 1 : 0);
        result = 31 * result + (demandApplicableOffPeak ? 1 : 0);
        result = 31 * result + (demandAverageTime != null ? demandAverageTime.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (rateType != null ? rateType.hashCode() : 0);
        result = 31 * result + (utilityName != null ? utilityName.hashCode() : 0);
        result = 31 * result + (buybackRate != null ? buybackRate.hashCode() : 0);
        result = 31 * result + (buyback ? 1 : 0);
        result = 31 * result + numberSeasons;
        result = 31 * result + numberTOU;
        result = 31 * result + numberTier;
        result = 31 * result + numberDemandSteps;
        result = 31 * result + (surcharge ? 1 : 0);
        result = 31 * result + (fixed ? 1 : 0);
        result = 31 * result + (minimum ? 1 : 0);
        result = 31 * result + (taxes ? 1 : 0);
        result = 31 * result + (seasonList != null ? seasonList.hashCode() : 0);
        result = 31 * result + (touLevels != null ? touLevels.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnergyPlan{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", meterReadDate=" + meterReadDate +
                ", meterReadCycle=" + meterReadCycle +
                ", planType=" + planType +
                ", touApplicableSaturday=" + touApplicableSaturday +
                ", touApplicableSunday=" + touApplicableSunday +
                ", touApplicableHoliday=" + touApplicableHoliday +
                ", holidayScheduleId=" + holidayScheduleId +
                ", demandPlanType=" + demandPlanType +
                ", demandUseActivePower=" + demandUseActivePower +
                ", demandApplicableSaturday=" + demandApplicableSaturday +
                ", demandApplicableSunday=" + demandApplicableSunday +
                ", demandApplicableHoliday=" + demandApplicableHoliday +
                ", demandApplicableOffPeak=" + demandApplicableOffPeak +
                ", demandAverageTime=" + demandAverageTime +
                ", description='" + description + '\'' +
                ", rateType='" + rateType + '\'' +
                ", utilityName='" + utilityName + '\'' +
                ", buybackRate=" + buybackRate +
                ", buyback=" + buyback +
                ", numberSeasons=" + numberSeasons +
                ", numberTOU=" + numberTOU +
                ", numberTier=" + numberTier +
                ", numberDemandSteps=" + numberDemandSteps +
                ", surcharge=" + surcharge +
                ", fixed=" + fixed +
                ", minimum=" + minimum +
                ", taxes=" + taxes +
                ", seasonList=" + seasonList +
                ", touLevels=" + touLevels +
                '}';
    }


}
