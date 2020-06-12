/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.*;
import com.ted.commander.server.model.PlayBackRow;
import com.ted.commander.server.model.StandAlonePost;
import com.ted.commander.server.model.energyPost.EnergyPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@Service
public class HazelcastService {

    static final Logger LOGGER = LoggerFactory.getLogger(HazelcastService.class);

    BlockingQueue<PlayBackRow> playbackRequestQueue;
    BlockingQueue<EnergyPost> energyPostRecordsQueue;
    BlockingQueue<StandAlonePost> standAlonePostQueue;


    HistoryBillingCycleKeyCache<HistoryBillingCycle> historyBillingCycleMap = new HistoryBillingCycleKeyCache<>(2, 60, TimeUnit.DAYS);
    HistoryKeyCache<HistoryDay> historyDayMap = new HistoryKeyCache<>(2, 2, TimeUnit.DAYS);
    HistoryKeyCache<HistoryHour> historyHourMap = new HistoryKeyCache<>(2, 2, TimeUnit.HOURS);
    HistoryKeyCache<HistoryMinute> historyMinuteMap = new HistoryKeyCache<>(45, 45, TimeUnit.MINUTES);

    HistoryMTUBillingCycleKeyCache<HistoryMTUBillingCycle> historyMTUBillingCycleMap = new HistoryMTUBillingCycleKeyCache<>(2, 60, TimeUnit.DAYS);
    HistoryMTUKeyCache<HistoryMTUDay> historyMTUDayMap = new HistoryMTUKeyCache<>(2, 2, TimeUnit.DAYS);
    HistoryMTUKeyCache<HistoryMTUHour> historyMTUHourMap = new HistoryMTUKeyCache<>(2, 2, TimeUnit.HOURS);



    Cache<Long, VirtualECC> virtualECCMap;

    Cache<String, String> filePropertyMap;


//    HazelcastInstance hazelcastInstance;

    long postCount = 0;
    long postSACount = 0;
    long playbackCount = 0;
    long playbackSACount = 0;

    @Autowired
    ServerService serverService;


    @PostConstruct
    public void init() {

        energyPostRecordsQueue = new LinkedBlockingQueue<>();
        playbackRequestQueue = new LinkedBlockingQueue<>();
        standAlonePostQueue = new LinkedBlockingQueue<>();
        virtualECCMap  = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(60, TimeUnit.DAYS).maximumSize(10000).initialCapacity(5000).build();
        filePropertyMap = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(1000).build();
        LOGGER.info("Hazelcast Initiated");
    }

    public Cache<String, String> getFilePropertyMap() {
        return filePropertyMap;
    }

    public HistoryBillingCycleKeyCache<HistoryBillingCycle> getHistoryBillingCycleMap() {
        return historyBillingCycleMap;
    }

    public HistoryKeyCache<HistoryDay> getHistoryDayMap() {
        return historyDayMap;
    }

    public HistoryKeyCache<HistoryHour> getHistoryHourMap() {
        return historyHourMap;
    }

    public HistoryKeyCache<HistoryMinute> getHistoryMinuteMap() {
        return historyMinuteMap;
    }

    public HistoryMTUBillingCycleKeyCache<HistoryMTUBillingCycle> getHistoryMTUBillingCycleMap() {
        return historyMTUBillingCycleMap;
    }

    public HistoryMTUKeyCache<HistoryMTUDay> getHistoryMTUDayMap() {
        return historyMTUDayMap;
    }

    public HistoryMTUKeyCache<HistoryMTUHour> getHistoryMTUHourMap() {
        return historyMTUHourMap;
    }

    public BlockingQueue<PlayBackRow> getPlaybackRequestQueue() {
        return playbackRequestQueue;
    }

    public Cache<Long, VirtualECC> getVirtualECCMap() {
        return virtualECCMap;
    }

    public boolean addPlaybackRecord(PlayBackRow historyPlaybackRequest) {
        playbackCount++;
        try {
            LOGGER.info("Adding playback record: {}", historyPlaybackRequest);
            playbackRequestQueue.add(historyPlaybackRequest);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Exception adding energy post record", ex);
            init();
            try {
                playbackRequestQueue.add(historyPlaybackRequest);
            } catch (Exception ex2) {
                LOGGER.error("Exception on second attempt to add to queue", ex);
                return false;
            }
        }
        return false;
    }



    public boolean addEnergyPostRecord(EnergyPost energyPost) {
        postCount++;
        try {
            energyPostRecordsQueue.add(energyPost);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Exception adding energy post record", ex);
            init();
            try {
                energyPostRecordsQueue.add(energyPost);
            } catch (Exception ex2) {
                LOGGER.error("Exception on second attempt to add to queue", ex);
                return false;
            }
        }
        return false;
    }

    public boolean addStandAloneRecord(StandAlonePost standAlonePost) {
        postSACount++;
        try {
            standAlonePostQueue.add(standAlonePost);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Exception adding standAlonePost", ex);
            init();
            try {
                standAlonePostQueue.add(standAlonePost);
            } catch (Exception ex2) {
                LOGGER.error("Exception on second attempt to add to queue", ex);
                return false;
            }
        }
        return false;
    }

    public BlockingQueue<StandAlonePost> getStandAlonePostQueue() {
        return standAlonePostQueue;
    }

    public BlockingQueue<EnergyPost> getEnergyPostRecordsQueue() {
        return energyPostRecordsQueue;
    }


    @Scheduled(cron = "0/30 * * * * * ")
    public void dumpStats() {
        if (!serverService.isDevelopment()) {
            LOGGER.info(">>>>>Queue Stats: energyPostRecordsQueue Left:{} Total:{}", energyPostRecordsQueue.size(), postCount);
            LOGGER.info(">>>>>Queue Stats: standAlonePostQueue Left:{} Total:{}", standAlonePostQueue.size(), postSACount);
            LOGGER.info(">>>>>Queue Stats: playbackRequestQueue Left:{} Total:{}", playbackRequestQueue.size(), playbackCount);
       }
    }



}
