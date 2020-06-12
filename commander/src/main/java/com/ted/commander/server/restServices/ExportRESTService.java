/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.model.export.ExportRequest;
import com.ted.commander.common.model.export.ExportResponse;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.services.ExportService;
import com.ted.commander.server.services.HazelcastService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class ExportRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(ExportRESTService.class);

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    ExportService exportService;

    @Autowired
    ResourceDAO resourceDAO;

    @Autowired
    HazelcastService hazelcastService;


    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");

    @PreAuthorize("@aclService.canViewLocation(principal.username,#virtualECCId)")
    @RequestMapping(value = "/export/{virtualECCId}/{formatType}",
            method = RequestMethod.POST)
    public @ResponseBody
    ExportResponse queueExport(@RequestBody ExportRequest exportRequest, @PathVariable("virtualECCId") Long virtualECCId, @PathVariable("formatType") String formatTypeString, HttpServletResponse response ) {
        ga.postAsync(new PageViewHit("/api/export", "ExportRESTService", "Get Export"));
        ExportResponse exportResponse = new ExportResponse();
        DataExportFileType dataExportFileType = DataExportFileType.valueOf(formatTypeString);
        String fileId = UUID.randomUUID().toString();
        LOGGER.info("Requesting Export: v:{} d:{} er:{}", virtualECCId, dataExportFileType, exportRequest);
        exportResponse.setUrl("/api/download/" + fileId);
        hazelcastService.getFilePropertyMap().put(fileId, dataExportFileType.getExtension());
        exportService.generateFile(exportRequest, fileId);
        return exportResponse;
    }



    @RequestMapping(value = "/download/{fileId}", method = RequestMethod.GET)
    void downloadFile(@PathVariable("fileId") String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOGGER.info("Looking up request status for {}", fileId);

        StringBuilder fileName = new StringBuilder("Export-");
        fileName.append(System.currentTimeMillis());
        fileName.append(hazelcastService.getFilePropertyMap().getIfPresent(fileId));




        InputStream is = null;
        File file = new File("/temp/" + fileId);
        try {
            LOGGER.debug("Setting response headers");
            response.setContentType("application/force-download");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setStatus(200);
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName.toString() + "\"");
            LOGGER.debug("writing output stream");
            is = new FileInputStream(file);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception ex) {
            LOGGER.error("Exception caught", ex);
        } finally {
            if (is != null) is.close();
            file.delete();
            hazelcastService.getFilePropertyMap().invalidate(fileId);
        }

    }


}
