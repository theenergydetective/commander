/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.google.common.io.CharStreams;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class PVOutoutRestService {

    public static final String AUTH_URL = "http://pvoutput.org/service/r3/tedauth.jsp";
    public static final String POST_URL = "http://pvoutput.org/service/r3/tedpost.jsp";
    public static final Integer RETRY_COUNT = 3;
    final static Logger LOGGER = LoggerFactory.getLogger(PVOutoutRestService.class);

    @RequestMapping(value = "/pvactivate",
            consumes = "application/xml",
            method = RequestMethod.POST)
    public
    @ResponseBody
    String activate(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            String activationBody = CharStreams.toString(request.getReader());
            LOGGER.debug("PVOutput Activation Request {}", activationBody);
            int retryCount = RETRY_COUNT;
            while (retryCount > 0) {
                try {
                    retryCount--;
                    LOGGER.debug("Activation: {} attempts left", retryCount);

                    String pvoutputResponse = Request.Post(AUTH_URL)
                            .connectTimeout(30000)
                            .socketTimeout(30000)
                            .bodyString(activationBody, ContentType.APPLICATION_XML)
                            .execute()
                            .returnContent()
                            .asString();

                    LOGGER.debug("Response: {}", pvoutputResponse);

                    pvoutputResponse = pvoutputResponse.replace("api.pvoutput.org", "commander.theenergydetective.com");
                    pvoutputResponse = pvoutputResponse.replace("/service/r3/tedpost.jsp", "/api/pvpostData");

                    response.setContentType("text/xml");
                    response.setStatus(200);
                    return pvoutputResponse;
                } catch (HttpResponseException ex) {
                    LOGGER.warn("Error posting", ex);
                }
            }
            LOGGER.error("Retry attempts failed");
            response.sendError(500);
            return "";
        } catch (Exception ex) {
            LOGGER.error("Exception with activation post", ex);
            response.sendError(500);
            return "";
        }
    }


    @RequestMapping(value = "/pvpostData",
            consumes = "application/xml",
            method = RequestMethod.POST)

    public
    @ResponseBody
    String postData(HttpServletRequest request, HttpServletResponse response) throws Exception {


        try {
            String activationBody = CharStreams.toString(request.getReader());
            LOGGER.debug("PVOutput Post Request {}", activationBody);
            int retryCount = RETRY_COUNT;
            while (retryCount > 0) {
                try {
                    retryCount--;
                    LOGGER.debug("Posting: {} attempts left", retryCount);

                    String pvoutputResponse = Request.Post(POST_URL)
                            .connectTimeout(30000)
                            .socketTimeout(30000)
                            .bodyString(activationBody, ContentType.APPLICATION_XML)
                            .execute()
                            .returnContent()
                            .asString();

                    LOGGER.debug("Response: {}", pvoutputResponse);
                    response.setContentType("text/plain");
                    response.setStatus(200);
                    return pvoutputResponse;
                } catch (HttpResponseException ex) {
                    LOGGER.warn("Error posting", ex);
                }
            }
            LOGGER.error("Retry attempts failed");
            response.sendError(500);
            return "";
        } catch (Exception ex) {
            LOGGER.error("Exception with activation post", ex);
            response.sendError(500);
            return "";
        }
    }

}
