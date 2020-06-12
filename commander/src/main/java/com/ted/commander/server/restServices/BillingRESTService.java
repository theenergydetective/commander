/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.model.export.BillingRequest;
import com.ted.commander.common.model.export.ExportResponse;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.services.BillingService;
import com.ted.commander.server.services.HazelcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api/billing")
public class BillingRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(BillingRESTService.class);

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    BillingService billingService;

    @Autowired
    ResourceDAO resourceDAO;

    @Autowired
    HazelcastService hazelcastService;


    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");


    @RequestMapping(value = "/{formatType}",
            method = RequestMethod.POST)
    public @ResponseBody
    ExportResponse queueExport(@RequestBody BillingRequest billingRequest, @PathVariable("formatType") String formatTypeString, HttpServletResponse response ) {
        ga.postAsync(new PageViewHit("/api/billing", "BillingRESTService", "Get Billing"));
        ExportResponse exportResponse = new ExportResponse();
        DataExportFileType dataExportFileType = DataExportFileType.valueOf(formatTypeString);
        String fileId = UUID.randomUUID().toString();
        LOGGER.info("Requesting Billing:d:{} er:{}", dataExportFileType, billingRequest);
        exportResponse.setUrl("/api/download/" + fileId);
        hazelcastService.getFilePropertyMap().put(fileId, dataExportFileType.getExtension());
        billingService.generateFile(billingRequest, fileId);
        return exportResponse;
    }


}
