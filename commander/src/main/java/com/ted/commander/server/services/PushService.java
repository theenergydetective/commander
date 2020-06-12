/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.PushDAO;
import com.ted.commander.server.model.NonPostingMTU;
import com.ted.commander.server.model.PushId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


/**
 * Interface for the Email subsystem
 */
@Service
public class PushService {

    static Logger LOGGER = LoggerFactory.getLogger(PushService.class);
    @Autowired
    ServerService serverService;

    @Autowired
    PushDAO pushDAO;

    static final String GCM_API_KEY = "<INSERT KEY>>";
    DecimalFormat decimalFormat = new DecimalFormat("0.0#");

    public void sendTOURateNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, String touName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The TOU ")
                .append(touName)
                .append(" for location ")
                .append(virtualECC.getName())
                .append(" will be starting in ")
                .append(adviceTrigger.getMinutesBefore())
                .append(" minutes.");
        sendPushMessages(adviceRecipient, "New TOU Alert", stringBuilder.toString());

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
        sendPushMessages(adviceRecipient, "Rate Change Alert", stringBuilder.toString());
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
        sendPushMessages(adviceRecipient, "Demand Cost Alert", stringBuilder.toString());
    }

    public void sendMoneySpentNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, EnergyPlan energyPlan, double expectedAmount, double actualAmount) {

        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);

        StringBuilder text = new StringBuilder();
        text.append("The money spent since ");

        if (adviceTrigger.getSinceStart().equals(HistoryType.DAILY)) text.append("Midnight ");
        else text.append("the Billing Cycle Start ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append(format.format(expectedAmount))
                .append(". It is now ")
                .append(format.format(actualAmount))
                .append(".");
        sendPushMessages(adviceRecipient, "Money Spent Alert", text.toString());
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
        sendPushMessages(adviceRecipient, "Energy Consumed Alert", text.toString());
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
        sendPushMessages(adviceRecipient, "Average Power Alert", text.toString());
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
        sendPushMessages(adviceRecipient, "Generated Power Alert", text.toString());
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
        sendPushMessages(adviceRecipient, "Consumed Power Alert", text.toString());
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
                    .append(" hours. ");
        } else {
            text.append("No posts from the following MTUs been received from location ")
                    .append(virtualECC.getName())
                    .append(" for at least ")
                    .append(decimalFormat.format((double)adviceTrigger.getDelayMinutes()/60.0))
                    .append(" hours: ");

            boolean firstMTU = true;
            for (NonPostingMTU nonPostingMTU: nonPostingMTUList){
                if (firstMTU){
                    firstMTU = false;
                    text.append(", ");
                }
                calendar.setTimeInMillis(nonPostingMTU.getLastPost() * 1000);
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
            }
        }
        sendPushMessages(adviceRecipient, "Not Posting Alert", text.toString());
    }


    public void sendPostingNotification(AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC) {
        TimeZone timeZone = TimeZone.getTimeZone(virtualECC.getTimezone());
        Calendar calendar = Calendar.getInstance(timeZone);
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        formatter.setTimeZone(timeZone);

        StringBuilder text = new StringBuilder();
        if (adviceTrigger.isAllMTUs()){
            text.append("Posts have resumed for location ")
                    .append(virtualECC.getName())
                    .append(".");
        } else {
            text.append("Posts have resumed for all MTUs from location ")
                    .append(virtualECC.getName())
                    .append(".");
        }
        sendPushMessages(adviceRecipient, "Not Posting Alert", text.toString());
    }

    public void addId(PushId pushId) {
        PushId existingId = pushDAO.findByKey(pushId);
        if (existingId != null){

            if (!existingId.getUserId().equals(pushId.getUserId())){
                LOGGER.info("Different user using same device: {} {}", existingId, pushId);
                pushDAO.deleteByKey(pushId);
                pushDAO.insert(pushId);
            } else {
                LOGGER.info("User Already registered for device");
            }
        } else {
            LOGGER.info("Registering new user on device: {}", pushId);
            pushDAO.insert(pushId);;
        }
    }

    public void delete(PushId pushId) {
        LOGGER.info("Deleting {}", pushId);
        pushDAO.deleteByKey(pushId);
    }

    public List<PushId> getIdsForUser(long userId) {
        return pushDAO.findByUser(userId);
    }

    public void sendPushMessages(AdviceRecipient adviceRecipient, String title, String messageText){
        List<PushId> pushIdList = getIdsForUser(adviceRecipient.getUserId());
        for (PushId pushId: pushIdList){
            if (pushId.isIos()) {
                sendAPNS(pushId, title, messageText);
            } else if (pushId.isAdm()) {
                sendADM(pushId, title, messageText);
            }else
            {
                sendGCM(pushId, title, messageText);
            }
        }
    }

    public void sendAPNS(PushId pushId, String title, String messageText) {
        try {
            ApnsServiceBuilder serviceBuilder = APNS.newService();

//            serviceBuilder.withCert("/home/ec2-user/dev_cert.p12", "1234").withSandboxDestination();
           serviceBuilder.withCert("/home/ec2-user/prod_cert.p12", "pushm3n0w!").withProductionDestination();

            ApnsService service = serviceBuilder.build();
            String payload = APNS.newPayload()
                    .alertBody(messageText)
                    .alertTitle(title)
                    .sound("default")
                    .build();
            LOGGER.info(">>>>>>>>>>>>> SENDING MESSAGE!: {}", pushId);
            service.push(pushId.getRegistrationId(), payload);
        } catch (Exception ex){
            LOGGER.error("Error sending APNs message: {}", pushId, ex);
        }

    }

    public void sendGCM(PushId pushId, String title, String messageText) {
        Sender sender = new Sender(GCM_API_KEY);
        Message message = new Message.Builder()
                .addData("title", title)
                .addData("body", messageText)
                .addData("icon", "notlogo")
                .build();
        LOGGER.info("Sending Message:{}", message);
        final int retries = 3;
        try {
            Result result = sender.send(message, pushId.getRegistrationId(), retries);
            if (StringUtils.isEmpty(result.getErrorCodeName())) {
                LOGGER.info(message.toString() + "GCM Notification is sent successfully" + result.toString());
                return;
            }
            LOGGER.error("Error occurred while sending push notification :" + result.getErrorCodeName());

        } catch (InvalidRequestException e) {
            LOGGER.error("Invalid Request", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }
    }


    public void sendADM(PushId pushId, String title, String messageText) {
        LOGGER.info("Sending message to ADM: t:{} m:{} p:{}", title, messageText, pushId);

        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //con.setRequestProperty("Authorization", "Basic NGEwMGZmMjItY2NkNy0xMWUzLTk5ZDUtMDAwYzI5NDBlNjJj");
            con.setRequestProperty("Authorization", "Basic NDc5NDg0N2ItZTI1YS00ODlkLTgwYTQtOWEyNjRlMmE5N2Vi");
            con.setRequestMethod("POST");

            StringBuilder stringBuilder = new StringBuilder("{");
            stringBuilder.append("\"include_amazon_reg_ids\": [\"").append(pushId.getRegistrationId()).append("\"],");
            stringBuilder.append("\"app_id\": \"5f18651a-e860-47c9-9630-8a1f5b726e0a\",");
            stringBuilder.append("\"adm_sound\":\"notification\",");
            stringBuilder.append("\"adm_small_icon\":\"notlogo\",");
            stringBuilder.append("\"android_accent_color\":\"#FF0000\",");
            stringBuilder.append("\"contents\": {\"en\": \"").append(messageText).append("\"},");
            stringBuilder.append("\"headings\": {\"en\": \"").append(title).append("\"}");
            stringBuilder.append("}");

            String strJsonBody = stringBuilder.toString();

            LOGGER.info("strJsonBody:\n" + strJsonBody); //TODO: Change to Debug

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            LOGGER.info("httpResponse: " + httpResponse); //TODO: Change to debug

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            LOGGER.info("jsonResponse:\n" + jsonResponse); //TODO: Change to Trace

        } catch(Throwable t) {
            t.printStackTrace();
        }

    }


    public void sendRealTimePowerNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {
        DecimalFormat format = new DecimalFormat("0.0##");
        StringBuilder text = new StringBuilder();
        text.append("The real time power  ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append(format.format(expectedAmount))
                .append(" kW. ")
                .append("It is now ")
                .append(format.format(actualAmount))
                .append(" kW. ");
        sendPushMessages(adviceRecipient, "Real Time Power Alert", text.toString());
    }

    public void sendVoltageExceedsNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {
        DecimalFormat format = new DecimalFormat("0.0##");
        StringBuilder text = new StringBuilder();
        text.append("The voltage  ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" has exceeded ")
                .append((int)expectedAmount)
                .append(" V. ")
                .append("It is now ")
                .append((int)actualAmount)
                .append(" V. ");
        sendPushMessages(adviceRecipient, "Peak Voltage Alert", text.toString());
    }


    public void sendVoltageDoesNotExceedNotification(Advice advice, AdviceRecipient adviceRecipient, AdviceTrigger adviceTrigger, VirtualECC virtualECC, double expectedAmount, double actualAmount) {
        DecimalFormat format = new DecimalFormat("0.0##");
        StringBuilder text = new StringBuilder();
        text.append("The voltage  ");
        text.append("for location ")
                .append(virtualECC.getName())
                .append(" is below ")
                .append((int)expectedAmount)
                .append(" V. ")
                .append("It is now ")
                .append((int)actualAmount)
                .append(" V. ");
        sendPushMessages(adviceRecipient, "Low Voltage Alert", text.toString());
    }

}
