/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.*;
import com.ted.commander.server.model.NonPostingMTU;
import com.ted.commander.server.model.ServerInfo;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


/**
 * Interface for the Email subsystem
 */
@Service
public class EmailService {

    static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    ServerInfo serverInfo;

    @Autowired
    VelocityEngine velocityEngine;

    @Autowired
    private JavaMailSender mailSender;
    DecimalFormat decimalFormat = new DecimalFormat("0.0#");

    public void sendInviteEmail(final User originator, final String inviteeEmail, String key) {
        if (LOGGER.isInfoEnabled()) LOGGER.info("Send invite to {} from {} ", inviteeEmail, originator);
        //Load up the template
        final Map model = new HashMap();
        model.put("originator", originator);
        model.put("inviteeEmail", inviteeEmail);
        model.put("serverInfo", serverInfo);
        model.put("securityKey", key);

        final String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/ted/commander/emailTemplates/inviteEmail.vm", model);

        //Create the mime message
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(inviteeEmail);
                message.setFrom("donotreply@commander.theenergydetective.com");
                message.setReplyTo("donotreply@commander.theenergydetective.com");
                message.setSubject("[TED Commander] Invitation!");
                message.setText(text, true);
            }
        };

        this.mailSender.send(mimeMessagePreparator);
    }


    /**
     * Requests that the reset password email be sent to the specified user
     *
     * @param user
     */
    public void sendResetPassword(final User user, String securityKey) {
        if (LOGGER.isInfoEnabled()) LOGGER.info("Reset Password Email being set");
        //Load up the template
        final Map model = new HashMap();
        model.put("securityKey", securityKey);
        model.put("user", user);
        model.put("serverInfo", serverInfo);

        final String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/ted/commander/emailTemplates/resetPasswordEmail.vm", model);

        //Create the mime message
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(user.getUsername());
                message.setFrom("donotreply@commander.theenergydetective.com");
                message.setReplyTo("donotreply@commander.theenergydetective.com");
                message.setSubject("[TED Commander] Password Reset Request");
                message.setText(text, true);
            }
        };

        this.mailSender.send(mimeMessagePreparator);


    }


    /**
     * Sends an activation email to the user.
     *
     * @param user
     */
    public void sendActivationEmail(final User user, final String emailKey) {
        if (LOGGER.isInfoEnabled()) LOGGER.info("Send Activation email requested for " + user);
        //Load up the template

        final Map model = new HashMap();
        model.put("user", user);
        model.put("serverInfo", serverInfo);
        model.put("securityKey", emailKey);
        model.put("username", user.getUsername());
        final String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/ted/commander/emailTemplates/activationEmail.vm", model);

        //Create the mime message
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(user.getUsername());
                message.setFrom("donotreply@commander.theenergydetective.com");
                message.setReplyTo("donotreply@commander.theenergydetective.com");
                message.setSubject("[TED Commander] Account Activation");
                message.setText(text, true);
            }
        };


        this.mailSender.send(mimeMessagePreparator);

    }

    public void sendNoPost(String text) {
        //Create the mime message
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo("support@theenergydetective.com");
                message.setCc("parvanitis@theenergydetective.com");
                message.setFrom("donotreply@commander.theenergydetective.com");
                message.setReplyTo("donotreply@commander.theenergydetective.com");
                message.setSubject("[TED Commander] Non Posting ECC Alert");
                message.setText(text, true);
            }
        };
        LOGGER.debug("Sending email: {}", text);
        this.mailSender.send(mimeMessagePreparator);
    }

    public void sendAlarm(String text) {
        //Create the mime message
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setCc("support@theenergydetective.com");
                message.setTo("parvanitis@theenergydetective.com");
                message.setFrom("donotreply@commander.theenergydetective.com");
                message.setReplyTo("donotreply@commander.theenergydetective.com");
                message.setSubject("[TED Commander] SERVER ALERT!!!");
                message.setText(text, true);
                message.setPriority(1);
            }
        };
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>Sending email: {}", text);
        this.mailSender.send(mimeMessagePreparator);
    }

    public void sendAdvisorAlert(AdviceRecipient adviceRecipient, String location, String subject, String text){
        LOGGER.info("[sendAdvisorAlert] Sending Advisor Alert to: {} S:{} L:{}", adviceRecipient.getEmail(), subject, location);
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(adviceRecipient.getEmail());
                message.setFrom("donotreply@commander.theenergydetective.com");
                message.setReplyTo("donotreply@commander.theenergydetective.com");
                message.setSubject("[TED Commander Advisor: " + location + "]  " + subject);
                message.setText(text, true);
                message.setPriority(1);
            }
        };
        this.mailSender.send(mimeMessagePreparator);
    }

    public void sendTOURateNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, String touName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The TOU ")
                    .append(touName)
                    .append(" for location ")
                    .append(virtualECC.getName())
                    .append(" will be starting in ")
                    .append(adviceTrigger.getMinutesBefore())
                    .append(" minutes.");
            sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "New TOU Alert", stringBuilder.toString());
    }

    public void sendRateChangeNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, double oldRate, double newRate) {
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The rate for location ")
                .append(virtualECC.getName())
                .append(" changed from ")
                .append(format.format(oldRate))
                .append(" to ")
                .append(format.format(newRate))
                .append(".");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Rate Change Alert", stringBuilder.toString());

    }


    public void sendDemandCostChangeNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, double oldCost, double newCost) {
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The demand cost for location ")
                .append(virtualECC.getName())
                .append(" changed from ")
                .append(format.format(oldCost))
                .append(" to ")
                .append(format.format(newCost))
                .append(".");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Demand Cost Alert", stringBuilder.toString());

    }

    public void sendMoneySpentNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, double expectedAmount, double actualAmount) {
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);

        StringBuilder text = new StringBuilder();
        text.append("The money spent since ");

        if (adviceTrigger.getSinceStart().equals(HistoryType.DAILY)) text.append("Midnight" );
        else text.append("the Billing Cycle Start ");
                text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append(format.format(expectedAmount))
                .append(". It is now ")
                .append(format.format(actualAmount))
                .append(".");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Money Spent Alert", text.toString());
    }

    public void sendEnergyConsumedNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, double expectedAmount, double actualAmount) {

        DecimalFormat format = new DecimalFormat("0.0##");

        StringBuilder text = new StringBuilder();
        text.append("The NET energy consumed since ");

        if (adviceTrigger.getSinceStart().equals(HistoryType.DAILY)) text.append("Midnight ");
        else text.append("the Billing Cycle Start ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append(format.format(expectedAmount))
                .append(" kWH. ")
                .append("It is now ")
                .append(format.format(actualAmount))
                .append(" kWH. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Energy Consumed Alert", text.toString());
    }

    public void sendKWAverageNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {

        DecimalFormat format = new DecimalFormat("0.0##");

        StringBuilder text = new StringBuilder();
        text.append("The 10-minute power average  ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append(format.format(expectedAmount))
                .append(" kW. ")
                .append("It is now ")
                .append(format.format(actualAmount))
                .append(" kW. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Average Power Alert", text.toString());

    }

    public void sendAverageGenerationNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {
        DecimalFormat format = new DecimalFormat("0.0##");
        StringBuilder text = new StringBuilder();
        double adjActualAmount = 0;
        if (actualAmount < 0) adjActualAmount = Math.abs(actualAmount);

        text.append("The real time power generated ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has not exceeded ")
                .append(format.format(Math.abs(expectedAmount)))
                .append(" kW for")
                .append(decimalFormat.format((double)adviceTrigger.getDelayMinutes()/60.0))
                .append(" hours. ")
                .append("It is now ")
                .append(format.format(adjActualAmount))
                .append(" kW. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Generated Power Alert", text.toString());

    }



    public void sendAverageConsumedNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {

        DecimalFormat format = new DecimalFormat("0.0##");
        StringBuilder text = new StringBuilder();
        text.append("The real time power ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" did not exceeded ")
                .append(format.format((expectedAmount)))
                .append(" kW for")
                .append(decimalFormat.format((double)adviceTrigger.getDelayMinutes()/60.0))
                .append(" hours. ")
                .append("It is now ")
                .append(format.format((actualAmount)))
                .append(" kW. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Consumed Power Alert", text.toString());
    }


    public void sendNotPostingNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, List<NonPostingMTU> nonPostingMTUList) {

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        Calendar calendar = Calendar.getInstance(timeZone);
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        formatter.setTimeZone(timeZone);

        StringBuilder text = new StringBuilder();
        if (adviceTrigger.isAllMTUs()){
            text.append("No posts have been received from location ")
                    .append(virtualECC.getName())
                    .append(" for at least ")
                    .append(decimalFormat.format((double)adviceTrigger.getDelayMinutes()/60.0))
                    .append(" hours.");
        } else {
            text.append("No posts from the following MTUs been received from location ")
                    .append(virtualECC.getName())
                    .append(" for at least ")
                    .append(decimalFormat.format((double)adviceTrigger.getDelayMinutes()/60.0))
                    .append(" hours: <br><br>");

            text.append("<table border=0><tr><th>MTU Name</th><th></th><th>Last Post</th>");

            for (NonPostingMTU nonPostingMTU: nonPostingMTUList){
                calendar.setTimeInMillis(nonPostingMTU.getLastPost() * 1000);
                text.append("<tr><td>");
                if (nonPostingMTU.getDescription().trim().length() == 0){
                    text.append(nonPostingMTU.getHex());
                } else {
                    if (nonPostingMTU.getHex().equals(nonPostingMTU.getDescription())){
                        text.append(nonPostingMTU.getHex());
                    } else {
                        text.append(nonPostingMTU.getDescription())
                                .append("(")
                                .append(nonPostingMTU.getHex())
                                .append(")");
                    }
                }
                text.append("</td><td width='16px'>&nbsp;</td><td>");
                text.append(formatter.format(calendar.getTime()));
                text.append("</td></tr>");
            }
        }
        text.append("</table>");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Not Posting Alert", text.toString());
    }


    public void sendPostingNotification(AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC) {

        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        Calendar calendar = Calendar.getInstance(timeZone);
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        formatter.setTimeZone(timeZone);

        StringBuilder text = new StringBuilder();
        if (adviceTrigger.isAllMTUs()) {
            text.append("Posts have resumed for all MTUs from location ")
                    .append(virtualECC.getName())
                    .append(".");
        } else {
            text.append("Posts have resumed for all MTUs from location ")
                    .append(virtualECC.getName())
                    .append(".");
            sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Not Posting Alert", text.toString());
        }
    }

    public void sendRealTimePowerNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {

        DecimalFormat format = new DecimalFormat("0.0##");

        StringBuilder text = new StringBuilder();
        text.append("The real time power average  ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append(format.format(expectedAmount))
                .append(" kW. ")
                .append("It is now ")
                .append(format.format(actualAmount))
                .append(" kW. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Real Time Power Alert", text.toString());
    }

    public void sendVoltageExceedsNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {
        StringBuilder text = new StringBuilder();
        text.append("The voltage ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append((int)expectedAmount)
                .append(" V. ")
                .append("It is now ")
                .append((int)actualAmount)
                .append(" V. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Peak Voltage Alert", text.toString());
    }

    public void sendVoltageDoesNotExceedNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {
        StringBuilder text = new StringBuilder();
        text.append("The voltage ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" is below ")
                .append((int)expectedAmount)
                .append(" V. ")
                .append("It is now ")
                .append((int)actualAmount)
                .append(" V. ");
        sendAdvisorAlert(adviceRecipient, virtualECC.getName(), "Low Voltage Alert", text.toString());
    }
}
