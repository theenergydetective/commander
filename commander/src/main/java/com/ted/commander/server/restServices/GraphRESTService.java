/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.export.GraphRequest;
import com.ted.commander.common.model.export.GraphResponse;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.services.GraphService;
import com.ted.commander.server.services.HazelcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class GraphRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(GraphRESTService.class);

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    GraphService graphService;

    @Autowired
    ResourceDAO resourceDAO;

    @Autowired
    HazelcastService hazelcastService;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");


    @PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/graph/{virtualECCId}/{historyType}",
            method = RequestMethod.POST)
    public
    @ResponseBody
    GraphResponse queueExport(@RequestBody GraphRequest graphRequest, @PathVariable("virtualECCId") Long virtualECCId, @PathVariable("historyType") String historyTypeString ) {
        ga.postAsync(new PageViewHit("/api/graph", "GraphRESTservice", "Get Graph"));
        HistoryType historyType = HistoryType.valueOf(historyTypeString);
        LOGGER.info("Requesting Graph: v:{} h:{} gr:{}", virtualECCId, historyType, graphRequest);
        return graphService.generateResponse(graphRequest);
    }


}
