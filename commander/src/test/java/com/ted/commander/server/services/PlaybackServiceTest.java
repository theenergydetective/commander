/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryQuery;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.dao.VirtualECCMTUDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class PlaybackServiceTest {


    @Autowired
    PlaybackService playbackService;



    @Test
    public void testAddToPlaybackQueue(){

        playbackService.addLocationToPlaybackQueue(860);
        playbackService.addLocationToPlaybackQueue(999);
        playbackService.addLocationToPlaybackQueue(1000);
        playbackService.addLocationToPlaybackQueue(1002);
        playbackService.addLocationToPlaybackQueue(1035);
        playbackService.addLocationToPlaybackQueue(1036);
        playbackService.addLocationToPlaybackQueue(1037);
        playbackService.addLocationToPlaybackQueue(1038);
        playbackService.addLocationToPlaybackQueue(1040);
        playbackService.addLocationToPlaybackQueue(1041);
        playbackService.addLocationToPlaybackQueue(1047);
        playbackService.addLocationToPlaybackQueue(1052);
        playbackService.addLocationToPlaybackQueue(1054);
        playbackService.addLocationToPlaybackQueue(1058);
        playbackService.addLocationToPlaybackQueue(1079);
        playbackService.addLocationToPlaybackQueue(1082);
        playbackService.addLocationToPlaybackQueue(1094);
        playbackService.addLocationToPlaybackQueue(1114);
        playbackService.addLocationToPlaybackQueue(1190);
        playbackService.addLocationToPlaybackQueue(1200);
        playbackService.addLocationToPlaybackQueue(1212);
        playbackService.addLocationToPlaybackQueue(1213);
        playbackService.addLocationToPlaybackQueue(1250);
        playbackService.addLocationToPlaybackQueue(1251);
        playbackService.addLocationToPlaybackQueue(1257);
        playbackService.addLocationToPlaybackQueue(1262);
        playbackService.addLocationToPlaybackQueue(1266);
        playbackService.addLocationToPlaybackQueue(1288);
        playbackService.addLocationToPlaybackQueue(1290);
        playbackService.addLocationToPlaybackQueue(1295);
        playbackService.addLocationToPlaybackQueue(1296);
        playbackService.addLocationToPlaybackQueue(1299);
        playbackService.addLocationToPlaybackQueue(1301);
        playbackService.addLocationToPlaybackQueue(1310);
        playbackService.addLocationToPlaybackQueue(1314);
        playbackService.addLocationToPlaybackQueue(1315);
        playbackService.addLocationToPlaybackQueue(1320);
        playbackService.addLocationToPlaybackQueue(1321);
        playbackService.addLocationToPlaybackQueue(1322);
        playbackService.addLocationToPlaybackQueue(1338);
        playbackService.addLocationToPlaybackQueue(1388);
        playbackService.addLocationToPlaybackQueue(1550);



        while(true){
            try{
                Thread.sleep(1000);

            }catch (Exception ex){};

        }
    }



}


