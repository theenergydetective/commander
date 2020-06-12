/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;

import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.UserDAO;
import com.ted.commander.server.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/api")
@RestController
public class VersionController {


    @Autowired
    ServerService serverService;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    UserDAO userDAO;

    public static final String revision = "$Pete Rev: 74$";
    final static Logger LOGGER = LoggerFactory.getLogger(VersionController.class);
    String demoIp = "";

    /**
     * Prints the current version of the applications
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public
    @ResponseBody
    String version(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.info("VERSION REQUEST: {}", revision);
            accountDAO.clearCache();
            userDAO.clearCache();
            return revision;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    @RequestMapping(value = "/version/haltServer", method = RequestMethod.GET)
    public
    @ResponseBody
    String haltServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        serverService.stop();
        return "Halting";
    }





    /**
     * Prints the current version of the applications
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/monte", method = RequestMethod.GET)
    public
    @ResponseBody
    String demoIP(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.info("demoIp: {}", demoIp);
            return demoIp;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }




}