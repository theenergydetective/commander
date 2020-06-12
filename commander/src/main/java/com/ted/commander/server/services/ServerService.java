/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Server related services
 */
@Service
public class ServerService {

    static Logger LOGGER = LoggerFactory.getLogger(ServerService.class);
    boolean development = false;
    boolean keepRunning = true;
    Long startTime = System.currentTimeMillis();


    String host = null;

    @PostConstruct
    public void init() {
        LOGGER.info("Starting Server service");
        startTime = System.currentTimeMillis();

        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            host = ip.getHostAddress();

            LOGGER.debug("Current IP address : " + ip.getHostAddress());
            if (ip.getHostAddress().contains("192.168")) {
                LOGGER.warn("LOCAL SERVER. SCHEDULED TASKS WILL BE DISABLED: {}", ip.getHostAddress());
                development = true;
            }
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown Host Exception", e);
        }
    }

    public long getUptimeInSeconds(){
        Long uptime = System.currentTimeMillis() - startTime;
        uptime /= 1000;
        return uptime;
    }

    public String getHost() {
        return host;
    }

    public boolean isDevelopment() {
        return development;
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void stop() {
        keepRunning = false;
    }


}
