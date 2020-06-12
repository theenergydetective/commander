/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.server.model.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Rest service for handing login/logout.
 */

@RestController
@RequestMapping("/api")
public class AuthRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(AuthRESTService.class);

    @Autowired
    ServerInfo serverInfo;

    @RequestMapping(value = "/auth", method = RequestMethod.DELETE)
    public void logoff(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("LOGOFF CALLED: {}", request.getUserPrincipal().getName());
    }

}
