/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.AdviceState;
import com.ted.commander.common.enums.AdviceTriggerState;
import com.ted.commander.common.enums.TriggerType;
import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.history.HistoryBillingCycleKey;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.LastPostDAO;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.dao.VirtualECCMTUDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class NoPostTest {

    @Autowired
    AdvisorService advisorService;


    @Test
    public void testNoPost(){
//        advisorService.checkNoPost();

  //      advisorService.checkNoPost();

        Advice advice = new Advice();
        advice.setAccountId(52L);
        advice.setVirtualECCId(1134L);
        advice.setState(AdviceState.NORMAL);
        advice.setId(0L);
        advice.setAdviceName("Test Advice");
        advice.setLocationName("Test Location");

        AdviceTrigger  trigger = new AdviceTrigger();
        trigger.setTriggerState(AdviceTriggerState.NORMAL);
        trigger.setLastSent(0L);
        trigger.setDelayMinutes(60);
        trigger.setTriggerType(TriggerType.COMMANDER_NO_POST);
        trigger.setAllMTUs(false);

        advisorService.checkLastPost(advice, trigger);
    }



}

