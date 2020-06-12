/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.CostDifference;
import com.ted.commander.common.model.history.EnergyDifference;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.server.dao.HistoryBillingCycleDAO;
import com.ted.commander.server.model.ProjectedCost;
import com.ted.commander.server.model.TOUDifference;
import com.ted.commander.server.util.CalendarUtils;
import com.ted.commander.server.util.HolidayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


/**
 * Service that retrieves the costed history items
 */
@Service
public class CostService {
    static final Logger LOGGER = LoggerFactory.getLogger(CostService.class);

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;



    public double getTaxRate(EnergyPlan energyPlan, int season){
        double taxRate = 0.0;
        if (energyPlan.isTaxes()){
            if (energyPlan.getSeasonList().size() > season){
                for (AdditionalCharge additionalCharge: energyPlan.getSeasonList().get(season).getAdditionalChargeList()){
                    if (additionalCharge.getAdditionalChargeType().equals(AdditionalChargeType.TAX)){
                        taxRate = additionalCharge.getRate();
                        break;
                    }
                }
            }
        }
        return taxRate;
    }



    public double applyTax(double amount, double taxRate){
        return amount + (amount * (taxRate / 100.0));
    }



    public CostDifference calcCost(HistoryBillingCycle historyBillingCycle, EnergyDifference energyDifference, TOUDifference touDifference, EnergyPlan energyPlan, int season) {

        CostDifference costDifference = new CostDifference();

        double taxRate = 0.0;
        double surcharge = 0.0;

        try {
            if (energyPlan.getSeasonList().size() > season) {
                for (AdditionalCharge additionalCharge : energyPlan.getSeasonList().get(season).getAdditionalChargeList()) {
                    if (energyPlan.isTaxes() && additionalCharge.getAdditionalChargeType().equals(AdditionalChargeType.TAX)) {
                        taxRate = additionalCharge.getRate();
                    }

                    if (energyPlan.isSurcharge() && additionalCharge.getAdditionalChargeType().equals(AdditionalChargeType.SURCHARGE)) {
                        surcharge = additionalCharge.getRate();
                    }
                }
            }
        } catch (Exception ex){
            LOGGER.error("Error looking up additional charges", ex);
        }

        if (energyPlan.isBuyback() && energyDifference.getNet() < 0.0){
            double rate = energyPlan.getBuybackRate();
            rate = rate + (rate * (taxRate / 100.0));
            costDifference.setRate(rate);
            costDifference.setLoad((energyDifference.getLoad() / 1000.0) * rate);
            costDifference.setGeneration((energyDifference.getGeneration() / 1000.0) * rate);
            costDifference.setNet((energyDifference.getNet() / 1000.0) * rate);
        } else {
            switch (energyPlan.getPlanType()) {
                case FLAT: {
                    double rate = 0.10;
                    if (energyPlan.getSeasonList().size() > season) {
                        rate = energyPlan.getSeasonList().get(season).getEnergyRateList().get(0).getRate();
                    }
                    costDifference.setRate(rate);

                    rate = rate + (rate * (taxRate / 100.0)) + surcharge;


                    costDifference.setLoad((energyDifference.getLoad() / 1000.0) * rate);
                    costDifference.setGeneration((energyDifference.getGeneration() / 1000.0) * rate);
                    costDifference.setNet((energyDifference.getNet() / 1000.0) * rate);
                    break;
                }

                case TIER: {
                    double oldNetCost = findTierCost(energyPlan, season, TOUPeakType.OFF_PEAK, historyBillingCycle.getNet(), taxRate, surcharge);
                    double oldLoadCost = findTierCost(energyPlan, season, TOUPeakType.OFF_PEAK, historyBillingCycle.getLoad(), taxRate, surcharge);
                    double newNetCost = findTierCost(energyPlan, season, TOUPeakType.OFF_PEAK, historyBillingCycle.getNet() + energyDifference.getNet(), taxRate, surcharge);
                    double newLoadCost = findTierCost(energyPlan, season, TOUPeakType.OFF_PEAK, historyBillingCycle.getLoad() + energyDifference.getLoad(), taxRate, surcharge);

                    int tier = findTier(energyPlan, season, historyBillingCycle.getNet() + energyDifference.getNet());
                    costDifference.setRate(getRate(energyPlan, season, tier, TOUPeakType.OFF_PEAK));
                    costDifference.setNet(newNetCost - oldNetCost);
                    costDifference.setLoad(newLoadCost - oldLoadCost);
                    costDifference.setGeneration(costDifference.getNet() - costDifference.getLoad());
                    break;
                }

                case TOU: {
                    double rate = getRate(energyPlan, season, 0, touDifference.getTouPeakType());
                    costDifference.setRate(rate);
                    rate = rate + (rate * (taxRate / 100.0)) + surcharge;
                    costDifference.setLoad((energyDifference.getLoad() / 1000.0) * rate);
                    costDifference.setGeneration((energyDifference.getGeneration() / 1000.0) * rate);
                    costDifference.setNet((energyDifference.getNet() / 1000.0) * rate);
                    break;
                }

                case TIERTOU: {
                    double oldNetCost = findTierCost(energyPlan, season, touDifference.getTouPeakType(), historyBillingCycle.getNet(), taxRate, surcharge);
                    double oldLoadCost = findTierCost(energyPlan, season, touDifference.getTouPeakType(), historyBillingCycle.getLoad(), taxRate, surcharge);

                    double newNetCost = findTierCost(energyPlan, season, touDifference.getTouPeakType(), historyBillingCycle.getNet() + energyDifference.getNet(), taxRate, surcharge);
                    double newLoadCost = findTierCost(energyPlan, season, touDifference.getTouPeakType(), historyBillingCycle.getNet() + energyDifference.getLoad(), taxRate, surcharge);

                    int tier = findTier(energyPlan, season, historyBillingCycle.getNet() + energyDifference.getNet());
                    costDifference.setRate(getRate(energyPlan, season, tier, touDifference.getTouPeakType()));
                    costDifference.setNet(newNetCost - oldNetCost);
                    costDifference.setLoad(newLoadCost - oldLoadCost);
                    costDifference.setGeneration(costDifference.getNet() - costDifference.getLoad());
                    break;
                }

            }
        }
        return costDifference;
    }


    /**
     * Calculates the season index
     *
     * @param energyPlan
     * @param timeZone
     * @param epochTime
     * @return
     */
    public int findSeason(EnergyPlan energyPlan, TimeZone timeZone, long epochTime) {
        if (energyPlan.getNumberSeasons() <= 1) return 0;

        Calendar epochTimeCalendar = Calendar.getInstance(timeZone);
        epochTimeCalendar.setTimeInMillis(epochTime * 1000);
        CalendarUtils.zeroTime(epochTimeCalendar);

        for (EnergyPlanSeason energyPlanSeason : energyPlan.getSeasonList()) {
            Calendar c = Calendar.getInstance(timeZone);
            CalendarUtils.zeroTime(c);
            c.set(Calendar.YEAR, epochTimeCalendar.get(Calendar.YEAR));
            c.set(Calendar.MONTH, energyPlanSeason.getSeasonMonth());
            c.set(Calendar.DATE, energyPlanSeason.getSeasonDayOfMonth());
            c.add(Calendar.DATE, 1);
            if (epochTimeCalendar.before(c)) return energyPlanSeason.getId();
        }
        return 0;
    }

    /**
     * Determines the TOU level for the given date.
     *
     * @param energyPlan
     * @param season
     * @param timeZone
     * @param epochTime
     * @return
     */
    public TOUPeakType findTOU(EnergyPlan energyPlan, int season, TimeZone timeZone, long epochTime) {

        Calendar testDate = Calendar.getInstance(timeZone);
        testDate.setTimeInMillis(epochTime * 1000);

        if (energyPlan.getPlanType().equals(PlanType.FLAT) || energyPlan.getPlanType().equals(PlanType.TIER))
            return TOUPeakType.OFF_PEAK;
        if (HolidayUtil.isHoliday(testDate, energyPlan.getHolidayScheduleId() == 1) && !energyPlan.getTouApplicableHoliday())
            return TOUPeakType.OFF_PEAK;
        if ((testDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) && !energyPlan.getTouApplicableSaturday())
            return TOUPeakType.OFF_PEAK;
        if ((testDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) && !energyPlan.getTouApplicableSunday())
            return TOUPeakType.OFF_PEAK;

        if (energyPlan.getSeasonList().size() > season) {
            //Check each time
            EnergyPlanSeason energyPlanSeason = energyPlan.getSeasonList().get(season);
            for (EnergyPlanTOU energyPlanTOU : energyPlanSeason.getTouList()) {
                Calendar startTOU = Calendar.getInstance(timeZone);
                Calendar endTOU = Calendar.getInstance(timeZone);
                startTOU.setTimeInMillis(testDate.getTimeInMillis());
                endTOU.setTimeInMillis(testDate.getTimeInMillis());
                CalendarUtils.zeroTime(startTOU);
                CalendarUtils.zeroTime(endTOU);
                startTOU.set(Calendar.HOUR_OF_DAY, energyPlanTOU.getTouStartHour());
                startTOU.set(Calendar.MINUTE, energyPlanTOU.getTouStartMinute());
                endTOU.set(Calendar.HOUR_OF_DAY, energyPlanTOU.getTouEndHour());
                endTOU.set(Calendar.MINUTE, energyPlanTOU.getTouEndMinute());
                if (testDate.getTimeInMillis() >= startTOU.getTimeInMillis() && testDate.getTimeInMillis() < endTOU.getTimeInMillis()) {
                    return energyPlanTOU.getPeakType();
                }
            }
        }
        return TOUPeakType.OFF_PEAK;
    }

    public int findTier(EnergyPlan energyPlan, int season, double cumulativeEnergy){

        if (energyPlan.getPlanType().equals(PlanType.TIER)|| energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            List<EnergyPlanTier> tierList = energyPlan.getSeasonList().get(season).getTierList();
            for (int t = energyPlan.getNumberTier()-2; t >= 0; t--) {
                long tierMax = ((tierList.get(t).getKwh()) + 1);
                if (cumulativeEnergy >= tierMax) {

                    return t+1;
                }
            }
        }
        return 0;
    }

    public double findTierCost(EnergyPlan energyPlan, int season, TOUPeakType tou, double totalWatts, double taxRate, double surcharge) {
        LOGGER.trace("LOOKING UP: S:{} tou:{}  w:{}", new Object[]{season, tou, totalWatts});
        double remainingTotal = totalWatts;
        double tierCost = 0;

        List<EnergyPlanTier> tierList = energyPlan.getSeasonList().get(season).getTierList();
        for (int t = energyPlan.getNumberTier(); t > 1; t--) {
            long tierMin = ((tierList.get(t - 2).getKwh() ) + 1);
            if (remainingTotal >= tierMin) {
                double rate = getRate(energyPlan, season, t - 1, tou);
                rate = rate + (rate * (taxRate/100.0)) + surcharge; //Pay tax on the surcharge
                double tierTotal = remainingTotal - tierMin;
                remainingTotal = (tierMin - 1);
                tierCost += (tierTotal / 1000.0) * rate;
            }
        }

        double rate = getRate(energyPlan, season, 0, tou);
        rate = rate + (rate * (taxRate/100.0)) + surcharge; //Pay tax on the surcharge
        tierCost += (remainingTotal / 1000.0) * rate;
        return tierCost;

    }

    public double getRate(EnergyPlan energyPlan, int season, int tier, TOUPeakType touPeakType){
        if (energyPlan.getId().equals(0l)) return 0.10;
        try {
            for (EnergyRate energyRate: energyPlan.getSeasonList().get(season).getEnergyRateList()){
                //TODO: Cache?
                if (energyRate.getTierId() == tier && energyRate.getPeakType().equals(touPeakType)){
                    return (energyRate.getRate()==null)?0.0:energyRate.getRate();
                }
            }
        } catch (Exception ex){
            LOGGER.error("Error looking up rate: s:{} tier:{} tou:{} ep:{}", season, tier, touPeakType, energyPlan);
            return 0.10;
        }
        return 0.10; //No rates found.
    }


    public ProjectedCost getProjectedCost(EnergyPlan energyPlan, TimeZone timeZone, HistoryBillingCycle currentBillingCycle) {
        int minutesUsed = historyBillingCycleDAO.getMinutesSoFar(currentBillingCycle);
        int month = currentBillingCycle.getKey().getBillingCycleMonth();
        int year = currentBillingCycle.getKey().getBillingCycleYear();
        Calendar startCalendar = Calendar.getInstance(timeZone);
        startCalendar.set(Calendar.YEAR, year);
        startCalendar.set(Calendar.MONTH, month);
        startCalendar.set(Calendar.DATE, energyPlan.getMeterReadDate());
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        if (energyPlan.getMeterReadCycle().equals(MeterReadCycle.MONTHLY)){
            month++;
        } else {
            month += 2;
        }

        if (month > 11){
            month -= 12;
            year++;
        }

        Calendar endCalendar = Calendar.getInstance(timeZone);
        endCalendar.set(Calendar.YEAR, year);
        endCalendar.set(Calendar.MONTH, month);
        endCalendar.set(Calendar.DATE, energyPlan.getMeterReadDate());
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        long minutesInMonth = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        minutesInMonth /= 1000;
        minutesInMonth /= 60;
        double energy = currentBillingCycle.getNet();
        double cost = currentBillingCycle.getNetCost();

        energy /= minutesUsed;
        cost /= minutesUsed;
        energy *= minutesInMonth;
        cost *= minutesInMonth;

        if (!energyPlan.getDemandPlanType().equals(DemandPlanType.NONE)) cost += currentBillingCycle.getDemandCost();
        if (energyPlan.isFixed()) cost += currentBillingCycle.getFixedCharge();
        if (energyPlan.isMinimum() && cost < currentBillingCycle.getMinimumCharge()) {
            cost = currentBillingCycle.getMinimumCharge();
        }

        return new ProjectedCost(energy, cost);

    }

    public String getTouName(EnergyPlan energyPlan, TOUPeakType touPeakType) {
        for (EnergyPlanTOULevel touLevel : energyPlan.getTouLevels()){
            if (touLevel.getPeakType().equals(touPeakType)){
                return touLevel.getTouLevelName();
            }
        }
        return touPeakType.name();
    }
}
