/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.server.services.AdviceService;
import com.ted.commander.server.services.UserService;
import com.ted.commander.server.services.VirtualECCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Service for editing advice triggers
 */

@RestController
@RequestMapping("/api/advice/trigger")
public class AdviceTriggerRestService {

    final static Logger LOGGER = LoggerFactory.getLogger(AdviceTriggerRestService.class);

    @Autowired
    UserService userService;

    @Autowired
    ACLService aclService;

    @Autowired
    AdviceService adviceService;


    @Autowired
    VirtualECCService virtualECCService;

    /**
     * Creates a new user in the system
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateTrigger(@RequestBody AdviceTrigger adviceTrigger, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        Advice advice = adviceService.findById(adviceTrigger.getAdviceId());
        if (aclService.canEditAdvice(activeUser.getUsername(), advice.getAccountId(), advice.getId())){
            LOGGER.info("user {} is updating adviceTrigger {}", activeUser.getUsername(), adviceTrigger);
            adviceService.saveAdviceTrigger(adviceTrigger);
        }  else {
            LOGGER.warn("{} does not have permission to edit {}", activeUser.getUsername(), adviceTrigger);
        }
    }

    /**
     * Creates a new user in the system
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteAdvice(@RequestBody AdviceTrigger adviceTrigger, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        Advice advice = adviceService.findById(adviceTrigger.getAdviceId());
        if (aclService.canEditAdvice(activeUser.getUsername(), advice.getAccountId(), advice.getId())){
            LOGGER.info("user {} is deleting adviceTrigger {}", activeUser.getUsername(), adviceTrigger);
            adviceService.deleteAdviceTrigger(adviceTrigger);
        }  else {
            LOGGER.warn("{} does not have permission to deleting {}", activeUser.getUsername(), adviceTrigger);
        }
    }



}
