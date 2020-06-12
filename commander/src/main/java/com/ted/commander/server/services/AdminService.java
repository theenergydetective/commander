/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.LastPost;
import com.ted.commander.server.dao.LastPostDAO;
import com.ted.commander.server.model.admin.MTUAlarmEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Interface for getting data from openweather.org
 */
@Service
public class AdminService {

    static Logger LOGGER = LoggerFactory.getLogger(AdminService.class);
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
    @Autowired
    LastPostDAO lastPostDAO;
    @Autowired
    EmailService emailService;
    @Autowired
    ServerService serverService;
    boolean inAlarm = false;

    @Scheduled(cron = "00 00 00 * * * ")
    public void checkLastPost() {
        if (serverService.isDevelopment()) {
            return;
        }

        LOGGER.info("Checking for last post");

        List<LastPost> lastPostList = lastPostDAO.findExpired();

        StringBuilder emailBody = new StringBuilder();

        emailBody.append("The following accounts and locations have not received a post for an MTU or Spyder within the past 24 hours.<br>");
        emailBody.append("Locations marked with an '*' have posted data within the past 24 hours (so only an mtu or spyder is not posting. <br><br>");

        emailBody.append("<table>");
        emailBody.append("<tr><th>Account</th><th>ECC Name</th><th>MTU</th><th>MTU Description</th><th>Last Post Time</th></tr>");

        Long lastId = null;

        for (LastPost lastPost : lastPostList) {
            boolean printAccount = false;
            if (lastId == null || !lastId.equals(lastPost.getId())) {
                printAccount = true;
                lastId = lastPost.getId();
            }
            emailBody.append("<tr>");
            emailBody.append("<td>");
            if (printAccount) emailBody.append(lastPost.getAccountName());

            emailBody.append("</td>");


            emailBody.append("<td>");
            if (printAccount) {
                emailBody.append(lastPost.getEccName());
                if (lastPost.getActiveCount() > 0) emailBody.append("*");
            }
            emailBody.append("</td>");


            emailBody.append("<td>");
            emailBody.append(lastPost.getMtuId());
            emailBody.append("</td>");

            emailBody.append("<td>");
            emailBody.append(lastPost.getMtuDescription());
            emailBody.append("</td>");


            emailBody.append("<td>");
            if (lastPost.getLastPost() == 0) {
                emailBody.append("Never");
            } else {
                emailBody.append(simpleDateFormat.format(new Date(lastPost.getLastPost() * 1000)));
            }
            emailBody.append("</td>");
            emailBody.append("</tr>");

        }
        emailBody.append("<br>");
        emailBody.append("<br>");

        LOGGER.debug("{}", emailBody);

        emailService.sendNoPost(emailBody.toString());

    }

    //@Scheduled(cron = "00 0/5 * * * * ")
    public void checkActivePost() {
        if (serverService.isDevelopment()) {
            return;
        }

        boolean isActive = lastPostDAO.IsActive();

        LOGGER.info("CHECK ACTIVE POST: {}", isActive);

        if (!isActive) {
            if (!inAlarm) {
                inAlarm = true;
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("<h1>ALERT!!!! Commander has not recieved data from any of the test sites for 15 minute!! PLEASE CHECK THE SERVER!!</h1><br><br>");
                emailBody.append("Check with Pete to see if this is a server error or part of normal maintenance. If you can't get a hold of Pete, go ahead and restart the server.<br><br>");

                List<MTUAlarmEntry> mtuAlarmEntryList = lastPostDAO.findAlarmState();
                StringBuilder alarmBuilder = new StringBuilder();
                for (MTUAlarmEntry mtuAlarmEntry : mtuAlarmEntryList) {
                    alarmBuilder.append("     ").append(mtuAlarmEntry.toString()).append("<br>");
                    emailBody.append("     ").append(mtuAlarmEntry.toString()).append("<br>");
                }

                LOGGER.error("MTU ALARM STATE ENTERED:\n {}", mtuAlarmEntryList);
                emailService.sendAlarm(emailBody.toString());
            }
        } else {
            if (inAlarm && lastPostDAO.IsResumed()) {
                inAlarm = false;
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("<h1>POSTING HAS RESUMED</h1><br><br>");
                emailBody.append("It appears tha posting has resumed and the server is no longer in the alarm state");
                emailService.sendAlarm(emailBody.toString());


                List<MTUAlarmEntry> mtuAlarmEntryList = lastPostDAO.findAlarmState();
                StringBuilder alarmBuilder = new StringBuilder();
                for (MTUAlarmEntry mtuAlarmEntry : mtuAlarmEntryList) {
                    alarmBuilder.append("     ").append(mtuAlarmEntry.toString()).append("\n");
                }
                LOGGER.error("MTU ALARM STATE RESUMED:\n {}", mtuAlarmEntryList);
            }
        }

    }

}
