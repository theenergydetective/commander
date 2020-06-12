/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;



import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.model.PlayBackRow;
import com.ted.commander.server.model.StandAlonePost;
import com.ted.commander.server.model.energyPost.EnergyPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * This is a simple in memory queue that is used to prevent the same ECC's energy post from being posted at the same time.
 */

@Service
public class PollingService {
    static final Logger LOGGER = LoggerFactory.getLogger(PollingService.class);

    //public static int NUMBER_PLAYBACK_THREADS = 20;
    //public static int NUMBER_ENERGYPOST_THREADS = 0;

    public static int NUMBER_PLAYBACK_THREADS = 0;
    public static int NUMBER_ENERGYPOST_THREADS = 40;

    public static int NUMBER_STANDALONE_THREADS = 0 *  NUMBER_ENERGYPOST_THREADS;

    public static boolean skipCache = false;

    @Autowired
    ServerService serverService;

    @Autowired
    AccountDAO accountDAO;

    int standAloneRunning = 0;
    int playbackRunning = 0;
    int playbackSARunning = 0;
    int energyPostRunning = 0;

    int playbacksCompleted = 0;


    boolean energyPostProcessing[] = new boolean[NUMBER_ENERGYPOST_THREADS];
    boolean standAloneProcessing[] = new boolean[NUMBER_STANDALONE_THREADS];
    boolean playbackProcessing[] = new boolean[NUMBER_PLAYBACK_THREADS];

    private int getProcessingCount(boolean list[]){
        int count = 0;
        for (boolean b: list) if(b) count++;
        return count;
    }


    @PostConstruct
    public void init(){
        for (int i=0; i < EPP.length; i++) EPP[i]=100;

        if (serverService.isDevelopment()){



//            NUMBER_PLAYBACK_THREADS = 1;
//            NUMBER_PLAYBACK_SA_THREADS = 1;
//            NUMBER_ENERGYPOST_THREADS = 2;
//            NUMBER_STANDALONE_THREADS = 20;

        }
    }

    @Async
    public void startEnergyPostPoller(HazelcastService hazelcastService, EnergyPostService energyPostService, int index){
        energyPostRunning++;
        LOGGER.info("Starting startEnergyPostPoller: {}", energyPostRunning);
        while(serverService.keepRunning){
            EnergyPost energyPost = null;
            try {
                energyPost = hazelcastService.getEnergyPostRecordsQueue().take();

                energyPostProcessing[index] = true;
                if (energyPost != null){
                    energyPostService.processEnergyPost(energyPost);
                }
                energyPostProcessing[index] = false;
            } catch (Exception ex){
                LOGGER.error("Error getting item from queue", ex);
                energyPostProcessing[index] = false;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        energyPostRunning--;
        LOGGER.error("HALTED THREAD: startEnergyPostPoller: {} left", energyPostRunning);

    }



    @Async
    public void startStandAlonePoller(HazelcastService hazelcastService, EnergyPostService energyPostService, int index) {
        standAloneRunning++;
        LOGGER.info("Starting startStandAlonePoller: {}", standAloneRunning);
        while(serverService.keepRunning){
            StandAlonePost standAlonePost = null;
            try {
                standAlonePost = hazelcastService.getStandAlonePostQueue().take();
                standAloneProcessing[index] = true;
                if (standAlonePost != null){
                    LOGGER.debug("Processing: {}", standAlonePost);
                    energyPostService.postStandAlone(standAlonePost);
                }
                standAloneProcessing[index] = false;
            } catch (Exception ex){
                LOGGER.error("Error getting item from queue", ex);
                standAloneProcessing[index] = false;
            }
        }
        LOGGER.error("HALTED THREAD: startStandAlonePoller {} left", standAloneRunning);
    }



    @Async
    public void startPlaybackPoller(HazelcastService hazelcastService, PlaybackService playbackService, int index){
        playbackRunning++;
        LOGGER.info("Starting startPlaybackPoller: {}", playbackRunning);
        while(serverService.keepRunning){
            PlayBackRow historyPlaybackRequest = null;
            try {
                historyPlaybackRequest = hazelcastService.getPlaybackRequestQueue().take();
                playbackProcessing[index] = true;
                if (historyPlaybackRequest != null){
                    LOGGER.info(">>>>>>>>>Processing: {}", historyPlaybackRequest);
                    playbackService.markRunning(historyPlaybackRequest);
                    playbackService.processPlaybackRequest(historyPlaybackRequest);
                    playbackService.markComplete(historyPlaybackRequest);

                    playbacksCompleted++;
                }
                playbackProcessing[index] = false;
            } catch (Exception ex){
                LOGGER.error("Error getting item from queue", ex);
                playbackProcessing[index] = false;
            }
        }

        LOGGER.error("HALTED THREAD: startPlaybackPoller: {} left", playbackRunning);
    }

//
//    @Async
//    public void startPlaybackSAPoller(HazelcastService hazelcastService, PlaybackService playbackService, int index){
//        playbackSARunning++;
//        LOGGER.info("Starting startPlaybackSAPoller: {}", playbackSARunning);
//        while(serverService.keepRunning){
//            HistoryPlaybackMTURequest mtuRequest = null;
//            try {
//                mtuRequest = hazelcastService.getPlaybackSARequestQueue().take();
//                if (mtuRequest != null){
//                    LOGGER.debug("Processing: {}", mtuRequest);
//                    playbackService.processPlaybackMTURequest(1, mtuRequest);
//                }
//            } catch (Exception ex){
//                LOGGER.error("Error getting item from queue", ex);
//            }
//            if (mtuRequest == null) {
//                //Don't grind if we don't have data
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception ex) {
//                    LOGGER.error("Thread Sleep Exception", ex);
//                }
//            }
//        }
//
//        LOGGER.error("HALTED THREAD: startPlaybackPoller: {} left", playbackRunning);
//    }

    int EPP[] =new int[6];
    int EPP_INDEX = 0;


    int MAX_ACCOUNT = Integer.MAX_VALUE;



    @Scheduled(cron = "0/15 * * * * * ")
    public void dumpStats() {
        if (!serverService.isDevelopment()) {

          //  if (MAX_ACCOUNT == Integer.MAX_VALUE) MAX_ACCOUNT = accountDAO.findMaxAccount();;

            int eppCount = getProcessingCount(energyPostProcessing);

                    LOGGER.info(">>>>>THREADS RUNNING: EP: {} SA:{} PB:{} EPP:{} SAP:{} PBP:{} playbackCompleted:{}", energyPostRunning, standAloneRunning,
                    playbackRunning,
                    eppCount,
                    //getProcessingCount(energyPostProcessing),
                    getProcessingCount(standAloneProcessing),
                    getProcessingCount(playbackProcessing),
                    playbacksCompleted
            );

                    /*
            if (serverService.keepRunning) {
                EPP[EPP_INDEX] = eppCount;
                EPP_INDEX++;
                if (EPP_INDEX >= EPP.length) EPP_INDEX = 0;

                boolean ready4More = true;
                int goodCount = 0;
                for (int i = 0; i < EPP.length; i++) {
                    if (EPP[i] > 20) ready4More = false;
                    else goodCount++;
                }

                if (ready4More) {
                    if (MAX_ACCOUNT < 1900) MAX_ACCOUNT += 50;
                    else MAX_ACCOUNT += 20;

                    for (int i = 0; i < EPP.length; i++) EPP[i] = 100;
                    if (MAX_ACCOUNT < 2300) {
                        LOGGER.warn("EPP ACTIVATING MORE ACCOUNTS ********* {} **********", MAX_ACCOUNT);
                        //accountDAO.activateMore(MAX_ACCOUNT);
                    }
                } else {
                    LOGGER.warn("EPP STILL PROCESSING. GOOD COUNT:{}", goodCount);
                }
            }
            */
        }
    }

}
