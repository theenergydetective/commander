/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.AdditionalChargeType;
import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.util.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pete on 10/29/2014.
 */
@Service
public class EnergyPlanService {

    final static Logger LOGGER = LoggerFactory.getLogger(EnergyPlanService.class);

    @Autowired
    EnergyPlanDAO energyPlanDAO;

    @Autowired
    EnergyPlanSeasonDAO energyPlanSeasonDAO;

    @Autowired
    EnergyPlanTOULevelDAO energyPlanTOULevelDAO;

    @Autowired
    EnergyPlanTierDAO energyPlanTierDAO;

    @Autowired
    AdditionalChargeDAO additionalChargeDAO;

    @Autowired
    DemandChargeTierDAO demandChargeTierDAO;

    @Autowired
    DemandChargeTOUDAO demandChargeTOUDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;


    @Autowired
    EnergyPlanTOUDAO energyPlanTOUDAO;

    @Autowired
    EnergyRateDAO energyRateDAO;



    //TODO: Make this a cluster aware (Hazelcast).
    //TODO: Add timer to check and purge map (or make this a fixed size).
//    HashMap<Long, EnergyPlan> energyPlanHashMap = new HashMap<Long, EnergyPlan>(1024);
    Cache<Long, EnergyPlan> energyPlanCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(6, TimeUnit.HOURS).maximumSize(10000).build();

    public void saveEnergyPlan(EnergyPlan energyPlan) {
        energyPlanCache.invalidate(energyPlan.getId());

        LOGGER.debug("Saving Seasons: {}", energyPlan.getSeasonList().size());
        energyPlanSeasonDAO.deleteByPlan(energyPlan.getId());
        for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
            energyPlanSeasonDAO.update(energyPlan.getId(), season);
        }

        LOGGER.debug("Saving Tiers");
        energyPlanTierDAO.deleteByPlan(energyPlan.getId());
        if (energyPlan.getPlanType().equals(PlanType.TIER) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
                for (EnergyPlanTier energyPlanTier : season.getTierList()) {
                    energyPlanTier.setSeasonId(season.getId());
                    energyPlanTier.setEnergyPlanId(energyPlan.getId());
                    energyPlanTierDAO.update(energyPlanTier);
                }
            }
        }

        LOGGER.debug("Saving TOU");
        energyPlanTOULevelDAO.deleteByPlan(energyPlan.getId());
        energyPlanTOUDAO.deleteByPlan(energyPlan.getId());

        if (energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            for (EnergyPlanTOULevel energyPlanTOULevel : energyPlan.getTouLevels()) {
                energyPlanTOULevel.setEnergyPlanId(energyPlan.getId());
                energyPlanTOULevelDAO.update(energyPlanTOULevel);
            }

            for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
                for (EnergyPlanTOU energyPlanTOU : season.getTouList()) {
                    energyPlanTOU.setSeasonId(season.getId());
                    energyPlanTOU.setEnergyPlanId(energyPlan.getId());
                    LOGGER.debug("Saving TOU: {}", energyPlanTOU);
                    energyPlanTOUDAO.update(energyPlanTOU);
                }
            }
        }

        LOGGER.error("Saving Rates");
        energyRateDAO.deleteByPlan(energyPlan.getId());
        for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
            for (EnergyRate energyRate : season.getEnergyRateList()) {
                LOGGER.error("SAVING ENERGY RATE!!!! {} {}", season.getId(), energyRate);
                energyRate.setSeasonId(season.getId());
                energyRate.setEnergyPlanId(energyPlan.getId());
                energyRateDAO.update(energyRate);
            }
        }

        LOGGER.debug("Saving Demand Charge Tier");
        demandChargeTierDAO.deleteByPlan(energyPlan.getId());
        if (energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED) || energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED_PEAK)) {
            for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
                for (DemandChargeTier d : season.getDemandChargeTierList()) {
                    d.setSeasonId(season.getId());
                    d.setEnergyPlanId(energyPlan.getId());
                    demandChargeTierDAO.update(d);
                }
            }
        }

        LOGGER.debug("Saving Demand Charge TOU");
        demandChargeTOUDAO.deleteByPlan(energyPlan.getId());
        if (energyPlan.getDemandPlanType().equals(DemandPlanType.TOU)) {
            for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
                for (DemandChargeTOU d : season.getDemandChargeTOUList()) {
                    d.setSeasonId(season.getId());
                    d.setEnergyPlanId(energyPlan.getId());
                    demandChargeTOUDAO.update(d);
                }
            }
        }


        LOGGER.debug("Saving Additional Charges");
        additionalChargeDAO.deleteByPlan(energyPlan.getId());
        for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
            for (AdditionalCharge charge : season.getAdditionalChargeList()) {
                charge.setSeasonId(season.getId());
                charge.setEnergyPlanId(energyPlan.getId());
                additionalChargeDAO.update(charge);
            }
        }

        energyPlanCache.invalidate(energyPlan.getId());
    }


    public EnergyPlan loadEnergyPlan(long energyPlanId) {
        if (energyPlanId == 0L) {
            LOGGER.debug("Returning default energy plan");
            return new EnergyPlan();
        }


        //Check cache
        EnergyPlan energyPlan = energyPlanCache.getIfPresent(energyPlanId);
        if (energyPlan != null) {
            LOGGER.debug("RETURNING CACHED ENERGY PLAN: {}", energyPlan);
            return energyPlan;
        }

        energyPlan = energyPlanDAO.findById(energyPlanId);
        if (energyPlan == null) {
            LOGGER.debug("INVALID ENERGY PLAN ID: {}", energyPlanId);
            energyPlanCache.put(energyPlanId, new EnergyPlan());
            return new EnergyPlan();
        }
        //Populate the sub fields
        LOGGER.debug("Loading Seasons for energy plan: {} ", energyPlan);
        energyPlan.setSeasonList(energyPlanSeasonDAO.findByPlan(energyPlanId));

        if (energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            LOGGER.debug("Loading TOULevels for energy plan: {} ", energyPlan);
            energyPlan.setTouLevels(energyPlanTOULevelDAO.findByEnergyPlan(energyPlanId));
        }


        LOGGER.debug("Load additional charges");

        List<AdditionalCharge> additionalChargeList = additionalChargeDAO.findByEnergyPlan(energyPlan.getId());
        for (AdditionalCharge additionalCharge : additionalChargeList) {
            try {
                energyPlan.getSeasonList().get(additionalCharge.getSeasonId()).getAdditionalChargeList().add(additionalCharge);

            } catch (Exception ex) {
                LOGGER.warn("Additional Charge found for a missing season: {}", additionalCharge);
            }
        }

        if (energyPlan.getPlanType().equals(PlanType.TIER) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            LOGGER.debug("Loading Tiers");
            List<EnergyPlanTier> energyPlanTierList = energyPlanTierDAO.findByEnergyPlan(energyPlan.getId());
            for (EnergyPlanTier energyPlanTier : energyPlanTierList) {
                try {
                    energyPlan.getSeasonList().get(energyPlanTier.getSeasonId()).getTierList().add(energyPlanTier);
                } catch (Exception ex) {
                    LOGGER.warn("EnergyPlanTier found for a missing season: {}", energyPlanTier);
                }
            }
        }

        if (energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            LOGGER.debug("Loading TOUs");
            List<EnergyPlanTOU> energyPlanTOUList = energyPlanTOUDAO.findByEnergyPlan(energyPlan.getId());
            for (EnergyPlanTOU energyPlanTOU : energyPlanTOUList) {
                try {
                    energyPlan.getSeasonList().get(energyPlanTOU.getSeasonId()).getTouList().add(energyPlanTOU);
                } catch (Exception ex) {
                    LOGGER.warn("EnergyPlanTOU found for a missing season: {}", energyPlanTOU);
                }
            }
        }

        LOGGER.debug("Adding energy rates");
        List<EnergyRate> energyRateList = energyRateDAO.findByEnergyPlan(energyPlan.getId());


        for (EnergyRate energyRate : energyRateList) {
            try {
                energyPlan.getSeasonList().get(energyRate.getSeasonId()).getEnergyRateList().add(energyRate);
            } catch (Exception ex) {
                LOGGER.warn("EnergyRate found for a missing season: {}", energyRate);

            }
        }


        if (energyPlan.getDemandPlanType() != DemandPlanType.NONE) {
            if (energyPlan.getDemandPlanType().equals(DemandPlanType.TOU)) {
                LOGGER.debug("LOADING TOU DEMAND SETTINGS");
                List<DemandChargeTOU> demandChargeTOUList = demandChargeTOUDAO.findByEnergyPlan(energyPlanId);
                for (DemandChargeTOU demandChargeTOU : demandChargeTOUList) {
                    try {
                        energyPlan.getSeasonList().get(demandChargeTOU.getSeasonId()).getDemandChargeTOUList().add(demandChargeTOU);
                    } catch (Exception ex) {
                        LOGGER.warn("DemandChargeTOU found for a missing season: {}", demandChargeTOU);

                    }
                }
            } else {
                LOGGER.debug("LOADING TIER DEMAND SETTINGS");
                List<DemandChargeTier> demandChargeTierList = demandChargeTierDAO.findByEnergyPlan(energyPlanId);
                for (DemandChargeTier demandChargeTier : demandChargeTierList) {
                    try {
                        energyPlan.getSeasonList().get(demandChargeTier.getSeasonId()).getDemandChargeTierList().add(demandChargeTier);
                    } catch (Exception ex) {
                        LOGGER.warn("DemandChargeTier found for a missing season: {}", demandChargeTier);
                    }
                }
            }
        }


        energyPlanCache.put(energyPlan.getId(), energyPlan);
        LOGGER.debug("RETURNING ENERGY PLAN: {}", energyPlan);

        return energyPlan;
    }

    public void deleteEnergyPlan(Long energyPlanId) {
        LOGGER.info("Deleting Energy Plan: {}", energyPlanId);
        energyPlanDAO.deleteById(energyPlanId);
        energyPlanSeasonDAO.deleteByPlan(energyPlanId);
        energyPlanTierDAO.deleteByPlan(energyPlanId);
        energyPlanTOUDAO.deleteByPlan(energyPlanId);
        energyRateDAO.deleteByPlan(energyPlanId);
        additionalChargeDAO.deleteByPlan(energyPlanId);
        demandChargeTierDAO.deleteByPlan(energyPlanId);
        demandChargeTOUDAO.deleteByPlan(energyPlanId);
        energyPlanTOULevelDAO.deleteByPlan(energyPlanId);
    }


    public EnergyPlan parseFromECCXML(String description, Long accountId, String xmlData) {
        LOGGER.debug("Parsing EnergyPlan from XML");
        EnergyPlan energyPlan = new EnergyPlan();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlData)));
            Element doc = document.getDocumentElement();


            energyPlan.setNumberSeasons(XMLUtil.getInteger(doc, "NumberOfSeasons"));
            energyPlan.setPlanType(PlanType.values()[XMLUtil.getInteger(doc, "PlanType")]);
            energyPlan.setAccountId(accountId);
            energyPlan.setDescription(description);
            energyPlan.setMeterReadDate(XMLUtil.getInteger(doc, "MeterReadDate"));
            energyPlan.setHolidayScheduleId(XMLUtil.getBoolean(doc, "CANADA") ? 1l : 0l);
            energyPlan.setSurcharge(XMLUtil.getBoolean(doc, "UseEnergySurcharge"));
            energyPlan.setFixed(XMLUtil.getBoolean(doc, "FixedMonthlyCharge"));
            energyPlan.setMinimum(XMLUtil.getBoolean(doc, "MinMonthlyCharge"));
            energyPlan.setTaxes(XMLUtil.getBoolean(doc, "TaxCharge"));
            energyPlan.setUtilityName("");

            if (energyPlan.getPlanType() == PlanType.TOU || energyPlan.getPlanType() == PlanType.TIERTOU) {
                energyPlan.setNumberTOU(XMLUtil.getInteger(doc, "NumberTOU"));
                energyPlan.setTouApplicableSaturday(XMLUtil.getBoolean(doc, "Saturday"));
                energyPlan.setTouApplicableSunday(XMLUtil.getBoolean(doc, "Sunday"));
                energyPlan.setTouApplicableHoliday(XMLUtil.getBoolean(doc, "Holliday"));
                energyPlan.setNumberTOU(XMLUtil.getInteger(doc, "NumberTOU"));
            }

            if (energyPlan.getPlanType() == PlanType.TIERTOU || energyPlan.getPlanType() == PlanType.TIER) {
                energyPlan.setNumberTier(XMLUtil.getInteger(doc, "NumberTiers"));
            }


            boolean useDemand = XMLUtil.getBoolean(doc, "UseDemandLevels");
            if (useDemand) {
                LOGGER.debug("UseDemand: {}", useDemand);
                boolean isDemandTOU = XMLUtil.getBoolean(doc, "DemandType");
                energyPlan.setDemandPlanType(isDemandTOU ? DemandPlanType.TOU : DemandPlanType.TIERED);
                if (!isDemandTOU) {
                    energyPlan.setNumberDemandSteps(XMLUtil.getInteger(doc, "DemandLevels"));
                }
                energyPlan.setDemandAverageTime(XMLUtil.getInteger(doc, "DemandAvgTime"));
                energyPlan.setDemandApplicableSaturday(XMLUtil.getBoolean(doc, "DemandApplicableSaturday"));
                energyPlan.setDemandApplicableSunday(XMLUtil.getBoolean(doc, "DemandApplicableSunday"));
                energyPlan.setDemandApplicableHoliday(XMLUtil.getBoolean(doc, "DemandApplicableHolliday"));
                energyPlan.setDemandApplicableOffPeak(XMLUtil.getBoolean(doc, "DemandApplicableOffPeak"));
                energyPlan.setDemandUseActivePower(!XMLUtil.getBoolean(doc, "DemandBase"));
            }
            energyPlan = energyPlanDAO.update(energyPlan);

            //Set up seasons.
            LOGGER.debug("Saving seasons");
            for (int seasonId = 0; seasonId < energyPlan.getNumberSeasons(); seasonId++) {
                Element season = (Element) doc.getElementsByTagName("Season").item(seasonId);
                EnergyPlanSeason energyPlanSeason = new EnergyPlanSeason(
                        seasonId,
                        XMLUtil.getString(season, "SeasonName"),
                        XMLUtil.getInteger(season, "SeasonEndMonth"),
                        XMLUtil.getInteger(season, "SeasonEndDay")
                );
                energyPlanSeasonDAO.update(energyPlan.getId(), energyPlanSeason);
                energyPlan.getSeasonList().add(energyPlanSeason);
            }

            //Set up TOU level names
            LOGGER.debug("Saving TOU level names");
            if (energyPlan.getPlanType() == PlanType.TOU || energyPlan.getPlanType() == PlanType.TIERTOU) {
                for (int i = 0; i < energyPlan.getNumberTOU(); i++) {
                    //The TOU listing order is reverse on Commander.
                    int fpTOUIndex = (energyPlan.getNumberTOU() - 1) - i;
                    String name = XMLUtil.getString(doc, "Level" + (i + 1) + "Name");
                    EnergyPlanTOULevel energyPlanTOULevel = new EnergyPlanTOULevel(energyPlan.getId(), TOUPeakType.values()[fpTOUIndex], name);
                    energyPlan.getTouLevels().add(energyPlanTOULevelDAO.update(energyPlanTOULevel));
                }


                //Set up TOU times.
                LOGGER.debug("Saving TOU levels");
                Element touTimes = (Element) doc.getElementsByTagName("TOUTimes").item(0);
                for (int season = 0; season < energyPlan.getNumberSeasons(); season++) {
                    Element seasonElement = (Element) touTimes.getElementsByTagName("Season" + (season + 1)).item(0);
                    for (int i = 0; i < energyPlan.getNumberTOU(); i++) {
                        int fpTOUIndex = (energyPlan.getNumberTOU() - 1) - i;
                        if (fpTOUIndex == 0) continue; //Skip Off-Peak

                        Element touLevelElement = (Element) seasonElement.getElementsByTagName("TOULevel" + (i + 1)).item(0);

                        addTOUTimes(TOUPeakType.values()[fpTOUIndex], energyPlan, season,
                                XMLUtil.getInteger(touLevelElement, "StartHour1"), XMLUtil.getInteger(touLevelElement, "StartMin1"),
                                XMLUtil.getInteger(touLevelElement, "EndHour1"), XMLUtil.getInteger(touLevelElement, "EndMin1"), true);

                        addTOUTimes(TOUPeakType.values()[fpTOUIndex], energyPlan, season,
                                XMLUtil.getInteger(touLevelElement, "StartHour2"), XMLUtil.getInteger(touLevelElement, "StartMin2"),
                                XMLUtil.getInteger(touLevelElement, "EndHour2"), XMLUtil.getInteger(touLevelElement, "EndMin2"), false);
                    }
                }

            }

            //Set up tier levels
            LOGGER.debug("Saving tier levels");
            if (energyPlan.getPlanType().equals(PlanType.TIER) || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
                Element tierElement = (Element) doc.getElementsByTagName("Tier").item(0);
                for (int season = 0; season < energyPlan.getNumberSeasons(); season++) {
                    Element seasonElement = (Element) tierElement.getElementsByTagName("Season" + (season + 1)).item(0);
                    for (int step = 0; step < energyPlan.getNumberTier(); step++) {
                        long kwh = XMLUtil.getLong(seasonElement, "Step" + (step + 1));
                        energyPlanTierDAO.update(new EnergyPlanTier(step, season, energyPlan.getId(), kwh));
                    }
                }
            }

            LOGGER.debug("Saving energy rates");
            //Set up energy rates
            Element erElement = (Element) doc.getElementsByTagName("EnergyRates").item(0);
            for (int seasonId = 0; seasonId < energyPlan.getNumberSeasons(); seasonId++) {
                Element seasonElement = (Element) erElement.getElementsByTagName("Season" + (seasonId + 1)).item(0);
                for (int step = 0; step < energyPlan.getNumberTier(); step++) {
                    Element stepElement = (Element) seasonElement.getElementsByTagName("Step" + (step + 1)).item(0);
                    for (int tou = 0; tou < energyPlan.getNumberTOU(); tou++) {
                        int fpTOUIndex = (energyPlan.getNumberTOU() - 1) - tou;
                        double energyRateValue = ((float) XMLUtil.getInteger(stepElement, "TOU" + (tou + 1))) / 100000f;
                        TOUPeakType peakType = TOUPeakType.values()[fpTOUIndex];
                        energyRateDAO.update(new EnergyRate(energyPlan.getId(), seasonId, step, peakType, energyRateValue));
                    }
                }
            }

            LOGGER.debug("Parsing demand charges");
            Element dElement = (Element) doc.getElementsByTagName("Demand").item(0);
            for (int season = 0; season < energyPlan.getNumberSeasons(); season++) {
                Element seasonElement = (Element) dElement.getElementsByTagName("Season" + (season + 1)).item(0);

                if (energyPlan.getDemandPlanType().equals(DemandPlanType.TOU)) {
                    //Load up the TOU types
                    for (int tou = 0; tou < energyPlan.getNumberTOU(); tou++) {
                        int fpTOUIndex = (energyPlan.getNumberTOU() - 1) - tou;
                        TOUPeakType peakType = TOUPeakType.values()[fpTOUIndex];
                        Element dlElement = (Element) seasonElement.getElementsByTagName("DemandLevel" + (tou + 1)).item(0);
                        double rate = (float) (((float) XMLUtil.getInteger(dlElement, "Charge")) / 1000.0);
                        demandChargeTOUDAO.update(new DemandChargeTOU(peakType, energyPlan.getId(), season, rate));
                    }
                } else {
                    //Load up the TIER types
                    for (int level = 0; level < energyPlan.getNumberDemandSteps(); level++) {
                        Element dlElement = (Element) seasonElement.getElementsByTagName("DemandLevel" + (level + 1)).item(0);
                        double peak = (float) ((float) XMLUtil.getInteger(dlElement, "OverKW") / 100.0);
                        double rate = (float) (((float) XMLUtil.getInteger(dlElement, "Charge")) / 1000.0);
                        demandChargeTierDAO.update(new DemandChargeTier(level, energyPlan.getId(), season, peak, rate));
                    }
                }
            }

            //Additional charges

            Element acElement = (Element) doc.getElementsByTagName("AdditionalCharges").item(0);
            for (int season = 0; season < energyPlan.getNumberSeasons(); season++) {
                Element seasonElement = (Element) acElement.getElementsByTagName("Season" + (season + 1)).item(0);

                if (energyPlan.isSurcharge()) {
                    double value = ((float) XMLUtil.getInteger(seasonElement, "EnergySurcharge")) / 100f;
                    ;
                    additionalChargeDAO.update(new AdditionalCharge(AdditionalChargeType.SURCHARGE, energyPlan.getId(), season, value));
                }

                if (energyPlan.isFixed()) {
                    double value = ((float) XMLUtil.getInteger(seasonElement, "FixedCharge")) / 100f;
                    additionalChargeDAO.update(new AdditionalCharge(AdditionalChargeType.FIXED, energyPlan.getId(), season, value));
                }

                if (energyPlan.isMinimum()) {
                    double value = ((float) XMLUtil.getInteger(seasonElement, "MinCharge")) / 100f;
                    additionalChargeDAO.update(new AdditionalCharge(AdditionalChargeType.MINIMUM, energyPlan.getId(), season, value));
                }

                if (energyPlan.isTaxes()) {
                    double value = ((float) XMLUtil.getInteger(seasonElement, "Tax")) / 100f;
                    additionalChargeDAO.update(new AdditionalCharge(AdditionalChargeType.TAX, energyPlan.getId(), season, value));
                }
            }

        } catch (Exception ex) {
            LOGGER.error("Error parsing energy plan xml", ex);
        }

        //Go ahead and reload it.
        if (energyPlan.getId() != null) {
            LOGGER.debug("Generated energy plan: {}", energyPlan);
            return loadEnergyPlan(energyPlan.getId());
        } else return null;
    }


    private void addTOUTimes(TOUPeakType touPeakType, EnergyPlan energyPlan, int season, int sHour, int sMin, int eHour, int eMin, boolean isAm) {
        if (sHour == eHour && sMin == eMin) return;
        energyPlanTOUDAO.update(new EnergyPlanTOU(touPeakType, isAm, season, energyPlan.getId(), sHour, sMin, eHour, eMin));
    }


    public EnergyPlan loadEnergyPlanByVirtualECC(Long virtualECCId) {
        VirtualECC virtualECC = virtualECCDAO.findOneFromCache(virtualECCId);
        return loadEnergyPlan(virtualECC.getEnergyPlanId());
    }
}
