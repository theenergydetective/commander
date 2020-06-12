/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.server.Application;
import com.ted.commander.server.model.PushId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class PushServiceTest {

    @Autowired
    protected PushService pushService;


    @Test
    public void testPush() {
        List<PushId> pushIdList = pushService.getIdsForUser(179l);
        assertTrue(pushIdList.size() > 0);



        for (PushId pushId: pushIdList){
            if (pushId.isIos()) {
                pushService.sendAPNS(pushId, "TED Advisor Test", "Test Message #2 " + System.currentTimeMillis());
            }
//
//            if (pushId.isAdm()){
//                pushService.sendADM(pushId, "TED Advisor Test", "Test Message #2 " + System.currentTimeMillis());
//            }
//          else if (pushId.isIos()){
//                pushService.sendAPNS(pushId, "TED Advisor Test", "Test Message #2 " + System.currentTimeMillis());
//            } else {
//                pushService.sendGCM(pushId, "TED Advisor Test", "Test Message #2" + System.currentTimeMillis());
//            }

        }


    }


}
