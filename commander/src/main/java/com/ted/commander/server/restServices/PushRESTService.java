/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.model.User;
import com.ted.commander.server.model.PushId;
import com.ted.commander.server.services.PushService;
import com.ted.commander.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Service for handling push notification registrations
 */

@RestController
@RequestMapping("/api/notification")
public class PushRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(PushRESTService.class);

    @Autowired
    UserService userService;

    @Autowired
    PushService pushService;


    /**
     * Adds a Push notification registration id for a specific user
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void addPush(@RequestBody PushId pushId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        LOGGER.info("Adding Push Key: {} {}", activeUser.getUsername(), pushId.getRegistrationId());
        User user = userService.findByUsername(activeUser.getUsername());
        pushId.setUserId(user.getId());
        pushService.addId(pushId);
    }

    /**
     * Adds a Push notification registration id for a specific user
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void removePush(@RequestBody PushId pushId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        LOGGER.info("Removing Push Key (user logout): {} {}", activeUser.getUsername(), pushId);
        User user = userService.findByUsername(activeUser.getUsername());
        pushId.setUserId(user.getId());
        pushService.delete(pushId);
    }

}
