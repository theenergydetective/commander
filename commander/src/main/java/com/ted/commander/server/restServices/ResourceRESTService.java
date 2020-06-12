/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.server.dao.ResourceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class ResourceRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(ResourceRESTService.class);


    @Autowired
    ResourceDAO resourceDAO;

    @RequestMapping(value = "/resource/utilities",
            params = {"query", "limit"},
            method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getUtilities(@RequestParam(value = "query", required = true) String query, @RequestParam(value = "limit", required = true) int limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("looking up {} utilities with the substring {}", limit, query);
            return resourceDAO.findUtilityName(query, limit);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


}
