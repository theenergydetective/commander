/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.AdviceRecipient;
import com.ted.commander.server.services.AdviceService;
import com.ted.commander.server.services.UserService;
import com.ted.commander.server.services.VirtualECCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;


/**
 * Service for editing advice recipients
 */

@RestController
@RequestMapping("/api/advice/recipient")
public class AdviceRecipientRestService {

    final static Logger LOGGER = LoggerFactory.getLogger(AdviceRecipientRestService.class);

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
    public void updateAdvice(@RequestBody AdviceRecipient adviceRecipient, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        Advice advice = adviceService.findById(adviceRecipient.getAdviceId());
        if (aclService.canEditAdvice(activeUser.getUsername(), advice.getAccountId(), advice.getId())){
            LOGGER.info("user {} is updating adviceRecipient {}", activeUser.getUsername(), adviceRecipient);
            adviceService.saveRecipient(adviceRecipient);
        }  else {
            LOGGER.warn("{} does not have permission to edit {}", activeUser.getUsername(), adviceRecipient);
        }
    }

    /**
     * Creates a new user in the system
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteAdvice(@RequestBody AdviceRecipient adviceRecipient, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        Advice advice = adviceService.findById(adviceRecipient.getAdviceId());
        if (aclService.canEditAdvice(activeUser.getUsername(), advice.getAccountId(), advice.getId())){
            LOGGER.info("user {} is deleting adviceRecipient {}", activeUser.getUsername(), adviceRecipient);
            adviceService.deleteAdviceRecipient(adviceRecipient);
        }  else {
            LOGGER.warn("{} does not have permission to deleting {}", activeUser.getUsername(), adviceRecipient);
        }
    }


    /**
     * Gets a list of advice for the specified location
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Advice> getForAccount(@QueryParam("accountId") Long accountId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        LOGGER.info("Getting advice list for {} by {}", accountId, activeUser.getUsername());
        if (aclService.canViewAccount(activeUser.getUsername(), accountId)){
            return adviceService.findForAccount(accountId, false);
        }  else {
            LOGGER.warn("{} does not have permission to view {}", activeUser.getUsername(), accountId);
            return new ArrayList<>();
        }
    }




    /**
     * Gets a list of advice for the specified location
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{adviceId}", method = RequestMethod.GET)
    public @ResponseBody Advice get(@PathVariable Long adviceId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        Advice advice = adviceService.findById(adviceId);
        if (aclService.canViewLocation(activeUser.getUsername(), advice.getVirtualECCId())){
            return advice;
        }  else {
            LOGGER.warn("{} does not have permission to view {}", activeUser.getUsername(), advice);
            return null;
        }
    }



}
