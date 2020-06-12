/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.model.PasswordResetRequest;
import com.ted.commander.common.model.RESTPostResponse;
import com.ted.commander.common.model.User;
import com.ted.commander.server.services.EmailService;
import com.ted.commander.server.services.KeyService;
import com.ted.commander.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class PasswordResetService {

    final static Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;


    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public
    @ResponseBody
    RESTPostResponse requestPasswordReset(@QueryParam("email") String email) {
        try {
            LOGGER.info("Request password reset for : {}", email);
            User user = userService.findByUsername(email);
            if (user == null) {
                LOGGER.warn("Email Address not found: {}", email);
                return new RESTPostResponse(null, "NOTFOUND");
            } else {
                String key = KeyService.generateSecurityKey(20);
                LOGGER.debug("updating activation key for password reset: {} {}", key, user);
                userService.updateActivationKey(user, key);
                emailService.sendResetPassword(user, key);
                return new RESTPostResponse(null, "SUCCESS");
            }
        } catch (Exception ex) {
            LOGGER.error("Error requesting password reset", ex);
            return new RESTPostResponse(null, "NOTFOUND");
        }
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public
    @ResponseBody
    RESTPostResponse requestPasswordReset(@RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            LOGGER.info("Password Reset Request: {}", passwordResetRequest);
            User user = userService.getByActivationKey(passwordResetRequest.getActivationKey());
            if (user == null) {
                LOGGER.warn("Password Reset Failed. Activation Key Not Found");
                return new RESTPostResponse(null, "NOTFOUND");
            } else {
                userService.updatePassword(user, passwordResetRequest.getPassword());
                userService.updateActivationKey(user, KeyService.generateSecurityKey(20));
                LOGGER.info("Password Reset Success");
                return new RESTPostResponse(null, "SUCCESS");
            }
        } catch (Exception ex) {
            LOGGER.error("Error requesting password reset", ex);
            return new RESTPostResponse(null, "NOTFOUND");
        }
    }

}
