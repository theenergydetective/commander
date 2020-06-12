/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.PlayBackRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * This is a simple in memory queue that is used to prevent the same ECC's energy post from being posted at the same time.
 */

@Service
public class PlaybackService {
    static final Logger LOGGER = LoggerFactory.getLogger(PlaybackService.class);


    @Autowired
    PollingService pollingService;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    EnergyDataDAO energyDataDAO;

    @Autowired
    HistoryService historyService;

    @Autowired
    EnergyPostService energyPostService;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    PlaybackDAO playbackDAO;

    @Autowired
    ServerService serverService;

    @Autowired
    HazelcastService hazelcastService;

    @Autowired
    MTUDAO mtudao;

    @PostConstruct
    public void init(){
        //This next step is done to get around the spring Async requirement that a method call happen in another service.
        for (int i = 0; i < pollingService.NUMBER_PLAYBACK_THREADS; i++){
            pollingService.startPlaybackPoller(hazelcastService, this, i);
        }
    }

    public void sleep(long ms){
        try {
            Thread.sleep(ms);
        }catch (Exception ex){
            LOGGER.error("Thread Sleep Exception: ", ex);
        }
    }
    public void processPlaybackRequest(PlayBackRow request) {
        LOGGER.info(">>>PROCESS PLAYBACK REQUEST STARTED: {}", request);
        long s = System.currentTimeMillis();
        VirtualECC virtualECC = virtualECCDAO.findById(request.getVirtualECCId());
        List<VirtualECCMTU> virtualECCMTUList = virtualECCMTUDAO.findByVirtualECC(virtualECC.getId(), virtualECC.getAccountId());

        HashMap<Long, MTU> mtuMap = new HashMap<>(virtualECCMTUList.size() * 2);
        HashMap<Long, Long> firstEnergyDataMap = new HashMap<>(virtualECCMTUList.size() * 2);


        int totalMTU = 0;
        for (VirtualECCMTU virtualECCMTU: virtualECCMTUList){
            MTU mtu = mtudao.findById(virtualECCMTU.getMtuId(), virtualECCMTU.getAccountId());
            if (virtualECCMTU.getMtuType() != MTUType.STAND_ALONE) totalMTU++;
            mtuMap.put(mtu.getId(), mtu);

            EnergyData energyData = energyDataDAO.findFirst(mtu.getAccountId(), mtu.getId());
            if (energyData == null){
                firstEnergyDataMap.put(mtu.getId(), request.getEndDate());
            } else {
                firstEnergyDataMap.put(mtu.getId(), energyData.getTimeStamp());
            }

        }

        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());

        long epochTime = request.getStartDate();
        while (epochTime < request.getEndDate()){
            for (VirtualECCMTU virtualECCMTU: virtualECCMTUList){
                long firstEpoch = firstEnergyDataMap.get(virtualECCMTU.getMtuId());
                if (firstEpoch >= epochTime) {
                    sleep(1); //So we don't grind the server to 100%
                    continue;
                }; //No data recorded yet.

                try {
                    EnergyData energyData = energyDataDAO.findById(epochTime, virtualECC.getAccountId(), virtualECCMTU.getMtuId());
                    if (energyData != null){

                        try {
                            MTU mtu = mtuMap.get(virtualECCMTU.getMtuId());
                            if (mtu.getValidation().equals(0l) || Math.abs(energyData.getEnergyDifference()) < ((double) mtu.getValidation() / 60.0)) {
                                energyPostService.processEnergyPost(virtualECC, totalMTU, virtualECCMTU, energyPlan, epochTime, energyData, true);
                            } else {
                                //Skip the big number record
                                LOGGER.warn("Energy Validation Failed: {}", energyData);
                            }
                        } catch (Exception ex){
                            LOGGER.error("Error processing minute: ts:{} ep:{}", new Object[]{epochTime, energyData}, ex);
                        }
                    } else {
                        LOGGER.debug("No Energy Data for M:{} A:{} T:{}", virtualECCMTU.getMtuId(), virtualECC.getAccountId(), epochTime);
                    }
                } catch (Exception ex){
                    LOGGER.error("Exception caught during playback", ex);
                }

            }
            epochTime += 60;
        }



        LOGGER.info("Completed: {} {}", System.currentTimeMillis() - s, request);
    }


    private boolean stringMatch(String s1, String s2){
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        return (s1.trim().toLowerCase().equals(s2.trim().toLowerCase()));
    }


    private boolean compareDouble(Double d1, Double d2, double difference){
        if (d1 > 0 && d2 < 0) return false;
        if (d2 > 0 && d1 < 0) return false;
        return Math.abs(d2 - d1) < difference;
    }

    private boolean mtuMatch (VirtualECC base, VirtualECC test){
        List<VirtualECCMTU> baseMTUs = virtualECCMTUDAO.findByVirtualECC(base.getId(), base.getAccountId());
        List<VirtualECCMTU> testMTUs = virtualECCMTUDAO.findByVirtualECC(test.getId(), test.getAccountId());
        if (baseMTUs.size() != testMTUs.size()) {
            return false;
        }

        for (VirtualECCMTU baseMTU: baseMTUs){
            boolean foundMatch = false;
            for (VirtualECCMTU testMTU: testMTUs){
                if (!baseMTU.getMtuId().equals(testMTU.getMtuId())) continue;
                if (!baseMTU.getMtuType().equals(testMTU.getMtuType())) continue;
                if (!compareDouble(baseMTU.getPowerMultiplier(), testMTU.getPowerMultiplier(), .001)) continue;;
                if (!compareDouble(baseMTU.getVoltageMultiplier(), testMTU.getVoltageMultiplier(), .001)) continue;;
                foundMatch = true;
                break;
            }
            if (!foundMatch) {
                return false;
            }

        }
        return true;
    }

    private List<VirtualECC> locationMatch(VirtualECC base){
        List<VirtualECC> matchList = new ArrayList<>();

        List <VirtualECC> virtualECCList = virtualECCDAO.findByAccount(base.getAccountId());
        for (VirtualECC test: virtualECCList){

            List<VirtualECCMTU> baseMTUs = virtualECCMTUDAO.findByVirtualECC(base.getId(), base.getAccountId());
            if (test.getId().equals(base.getId())) continue;
            if (!stringMatch(base.getName(), test.getName())) continue;
            if (!base.getSystemType().equals(test.getSystemType())) continue;
            if (!stringMatch(base.getTimezone(), test.getTimezone())) continue;
            if (!stringMatch(base.getStreet1(), test.getStreet1())) continue;
            if (!stringMatch(base.getStreet2(), test.getStreet2())) continue;
            if (!stringMatch(base.getCity(), test.getCity())) continue;
            if (!stringMatch(base.getState(), test.getState())) continue;
            if (!stringMatch(base.getPostal(), test.getPostal())) continue;
            if (!mtuMatch(base, test)) continue;;
            if (!matchEnergyPlan(base, test)) continue;;
            matchList.add(test);
        }
        return  matchList;
    }

    public boolean exists(long virtualEccId){
        return (virtualECCDAO.findById(virtualEccId) != null);
    }

    public boolean matchEnergyPlan(VirtualECC base, VirtualECC test){
        EnergyPlan basePlan = energyPlanService.loadEnergyPlanByVirtualECC(base.getId());
        EnergyPlan testPlan = energyPlanService.loadEnergyPlanByVirtualECC(test.getId());
        if (basePlan == null) basePlan = new EnergyPlan();
        if (testPlan == null) testPlan = new EnergyPlan();


        if (basePlan.getNumberSeasons() != testPlan.getNumberSeasons()) return false;
        if (!basePlan.getPlanType().equals(testPlan.getPlanType())) return false;
        if (basePlan.getNumberTier() != testPlan.getNumberTier()) return false;
        if (basePlan.getNumberTOU() != testPlan.getNumberTOU()) return false;
        if (!basePlan.getDemandPlanType().equals(testPlan.getDemandPlanType())) return false;
        if (!basePlan.getMeterReadDate().equals(testPlan.getMeterReadDate())) return false;
        if (!basePlan.getMeterReadCycle().equals(testPlan.getMeterReadCycle())) return false;
        if (!basePlan.getDemandApplicableHoliday().equals(testPlan.getDemandApplicableHoliday())) return  false;
        if (!basePlan.getDemandApplicableSaturday().equals(testPlan.getDemandApplicableSaturday())) return  false;
        if (!basePlan.getDemandApplicableSunday().equals(testPlan.getDemandApplicableSunday())) return  false;
        if (!basePlan.getRateType().equals(testPlan.getRateType())) return false;
        if (!stringMatch(basePlan.getDescription(), testPlan.getDescription())) return false;


        LOGGER.error("MATCHING ENERGY PLAN!!! {} {} {} {}", base.getId(), basePlan.getId(), test.getId(), testPlan.getId());





        return false;
    }

    public void findDuplicates(){
        List<VirtualECC> virtualECCList = virtualECCDAO.findAll();

        List<VirtualECC> deleteList = new ArrayList<>();

        int totalMatches = 0;


        for (VirtualECC virtualECC: virtualECCList){
            if (!exists(virtualECC.getId())) {
                LOGGER.error("No longer exists: {}", virtualECC.getId());
                continue;
            };

            List<VirtualECC> locationMatches = locationMatch(virtualECC);
            if (locationMatches.size() > 0) {
                LOGGER.warn("Possible Location Matches: {}", locationMatches.size());
                totalMatches += locationMatches.size();
            }

        }
        LOGGER.info("-----------------LOCATION MATCHES: {}------------------", totalMatches);
    }

    @Async
    public void queuePlayback(int server) {
        LOGGER.info("Queing request for: {}", server);
        List<PlayBackRow> historyPlaybackRequestList = playbackDAO.getPlaybackRequest(server);
        LOGGER.info("RESULT SIZE:{}", historyPlaybackRequestList.size());
        for (PlayBackRow request: historyPlaybackRequestList){
            LOGGER.info("Adding Playback Record for: {} {}", hazelcastService.addPlaybackRecord(request), request);
            LOGGER.info(">>>>>>>PBR:{}", request);
            try {
                Thread.sleep(15000);
            } catch (Exception ex){
                LOGGER.error("Thread Error:", ex);
            }
        }
    }

    public void markComplete(PlayBackRow historyPlaybackRequest) {
        playbackDAO.markComplete(historyPlaybackRequest);
    }

    public void markRunning(PlayBackRow historyPlaybackRequest) {
        playbackDAO.markRunning(historyPlaybackRequest);
    }


    public long getEpoch(TimeZone timeZone, int year, int month, int date){
        Calendar mycal = new GregorianCalendar(year, month, 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28

        Calendar epochCal = Calendar.getInstance(timeZone);
        epochCal.set(Calendar.MILLISECOND, 0);
        epochCal.set(Calendar.SECOND,0);
        epochCal.set(Calendar.MINUTE,0);
        epochCal.set(Calendar.HOUR_OF_DAY,0);
        epochCal.set(Calendar.DATE,1);
        epochCal.set(Calendar.YEAR, year);
        epochCal.set(Calendar.MONTH,month);

        if (date > daysInMonth){
            epochCal.set(Calendar.DATE, daysInMonth);
        } else {
            epochCal.set(Calendar.DATE, date);
        }
        return epochCal.getTimeInMillis()/1000;
    }

    public void addLocationToPlaybackQueue(long virtualECCId){
        EnergyPlan energyPlan = energyPlanService.loadEnergyPlanByVirtualECC(virtualECCId);
        VirtualECC virtualECC = virtualECCDAO.findById(virtualECCId);
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());


        List<Long> dates = new ArrayList<>();
        dates.add(getEpoch(timeZone, 2015, 11, energyPlan.getMeterReadDate()));
        dates.add(getEpoch(timeZone, 2016, 0, energyPlan.getMeterReadDate()));
        dates.add(getEpoch(timeZone, 2016, 1, energyPlan.getMeterReadDate()));
        dates.add(getEpoch(timeZone, 2016, 2, energyPlan.getMeterReadDate()));

        HistoryMinute nextHistoryMinute = historyMinuteDAO.findFirstAfterDate(virtualECCId, 1457586000l);

        long lastPostDate = 1457586000l;
        if (nextHistoryMinute != null){
            lastPostDate = nextHistoryMinute.getStartEpoch();
        }

        List<Long> finalDates = new ArrayList<>();
        for (long date: dates){
            if (date < lastPostDate){
                finalDates.add(date);
            }
        }
        finalDates.add(lastPostDate);

//        LOGGER.info("RESULTS!!!!!!!!!");
//        LOGGER.info("RESULTS!!!!!!!!! {}", virtualECCId);
//        LOGGER.info("RESULTS!!!!!!!!! {}", finalDates.size());


        for (int i=0; i < (finalDates.size()-1); i++){
            long startDate = finalDates.get(i);
            long endDate = finalDates.get(i+1);
            int server = new Date(startDate * 1000).getMonth();
//            LOGGER.info("ADDING RECOD: v:{} s:{} e:{} svr:{}", virtualECCId, new Date(startDate * 1000), new Date(endDate * 1000), server);
            playbackDAO.addToQueue(virtualECCId, startDate, endDate, server);
        }









    }
}
