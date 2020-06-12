/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.RESTPostResponse;
import com.ted.commander.common.model.User;
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
 * Service for editing ted advisor entries
 */

@RestController
@RequestMapping("/api/advice")
public class AdviceRestService {

    final static Logger LOGGER = LoggerFactory.getLogger(AdviceRestService.class);

    @Autowired
    UserService userService;

    @Autowired
    ACLService aclService;

    @Autowired
    AdviceService adviceService;


    @Autowired
    VirtualECCService virtualECCService;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");

    /**
     * Creates a new user in the system
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody RESTPostResponse createAdvice(@RequestBody Advice advice, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        ga.postAsync(new PageViewHit("/api/advice", "AdviceRestService", "Create Advice"));
        if (aclService.canEditAccount(activeUser.getUsername(), advice.getAccountId())){
            try {
                User commanderUser = userService.findByUsername(activeUser.getUsername());

                adviceService.createAdvice(commanderUser, advice);

                return new RESTPostResponse(advice.getId(), "SUCCESS");
            } catch (Exception ex){
                return new RESTPostResponse(null, "No Locations");
            }
        } else {
            LOGGER.warn("{} does not have permission to create {}", activeUser.getUsername(), advice);
            return new RESTPostResponse(null, "Invalid Auth");
        }
    }

    /**
     * Creates a new user in the system
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateAdvice(@RequestBody Advice advice, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        ga.postAsync(new PageViewHit("/api/advice", "AdviceRestService", "Update Advice"));
        if (aclService.canEditAdvice(activeUser.getUsername(), advice.getAccountId(), advice.getId())){
            LOGGER.info("user {} is updating advice {}", activeUser.getUsername(), advice);
            adviceService.updateAdvice(advice);
        }  else {
            LOGGER.warn("{} does not have permission to edit {}", activeUser.getUsername(), advice);
        }
    }

    /**
     * Creates a new user in the system
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteAdvice(@RequestBody Advice advice, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        ga.postAsync(new PageViewHit("/api/advice", "AdviceRestService", "Delete Advice"));
        if (aclService.canEditAdvice(activeUser.getUsername(), advice.getAccountId(), advice.getId())){
            LOGGER.info("user {} is deleting advice {}", activeUser.getUsername(), advice);
            adviceService.deleteAdvice(advice);
        }  else {
            LOGGER.warn("{} does not have permission to deleting {}", activeUser.getUsername(), advice);
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
