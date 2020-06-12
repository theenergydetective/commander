/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.ProjectedCost;
import com.ted.commander.server.model.alexa.AlexaResponseAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


/**
 * Service for Alexa Calls
 */
@Service
public class AlexaService {

    static final Logger LOGGER = LoggerFactory.getLogger(AlexaService.class);

    @Autowired
    AlexaDAO alexaDAO;
    
    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    HistoryDayDAO historyDayDAO;

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;


    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    CostService costService;
    private String introduction;
    private String helpQuestions;


    public List<AlexaResponseAccount> findPossibleLocations(long userId){
        LOGGER.debug("Looking up locations for {}", userId);
        return alexaDAO.findLocationsByUserId(userId);
    }


    public String generateToken(long userID) {
        String token = UUID.randomUUID().toString().replace("-", "");
        alexaDAO.updateToken(userID, token);
        return token;
    }

    public void clearLocations(long userId){
        alexaDAO.clearLocations(userId);
    }

    public void addLocation(long userId, long locationId, String alexaName) {
        alexaDAO.setAddLocation(userId, locationId, alexaName);
    }

    public VirtualECC getAlexaLocationForUser(Long userId) {
        return virtualECCDAO.findAlexaLocationForUser(userId);
    }



    static DecimalFormat kwFormat = new DecimalFormat("0.0");
    static DecimalFormat wattFormat = new DecimalFormat("0");




    public String getSummary(VirtualECC virtualECC) {
        Integer totalMTU = virtualECCMTUDAO.countNetMTU(virtualECC.getId(), virtualECC.getAccountId());
        HistoryMinute historyMinute =  historyMinuteDAO.findLastCompletedPost(virtualECC.getId(), System.currentTimeMillis()/1000, totalMTU);
        HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECC.getId(), System.currentTimeMillis()/1000);

        StringBuilder response = new StringBuilder("<speak>");
        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);

        response.append("Right now you are using ");

        double netUsage = historyMinute.getNet() * 60.0;
        response.append(powerFormat(netUsage));

        response.append(" costing ");
        response.append(format.format(historyMinute.getNetCost() * 60.0));
        response.append(" per hour <break time='500ms'/>");

        long daysLeft = historyBillingCycle.getEndEpoch() - ((new Date()).getTime() / 1000);
        daysLeft = (long)(Math.floor(daysLeft / 86400));
        response.append("You have ").append(daysLeft).append(" days remaining in this billing cycle");
        response.append("<break time='500ms'/>");

        response.append("Expect your electric bill to be approximately ");

        ProjectedCost projectedCost = costService.getProjectedCost(energyPlan, TimeZone.getTimeZone(virtualECC.getTimezone()), historyBillingCycle);
        response.append(format.format(projectedCost.getCost()));

        response.append("</speak>");
        return response.toString();

    }

    public String getUsageNow(VirtualECC virtualECC) {
        Integer totalMTU = virtualECCMTUDAO.countNetMTU(virtualECC.getId(), virtualECC.getAccountId());
        HistoryMinute historyMinute =  historyMinuteDAO.findLastCompletedPost(virtualECC.getId(), System.currentTimeMillis()/1000, totalMTU);


        StringBuilder response = new StringBuilder("<speak>");
        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);
        response.append("Right now you are using ");

        double netUsage = historyMinute.getNet() * 60.0;
        response.append(powerFormat(netUsage));
        response.append(" costing ");
        response.append(format.format(historyMinute.getNetCost() * 60.0));
        response.append(" per hour <break time='500ms'/>");

        response.append("</speak>");
        return response.toString();

    }

    public String getUsage(VirtualECC virtualECC, boolean today) {
        StringBuilder response = new StringBuilder("<speak>");
        double netUsage = 0.0;
        if (today){
            HistoryDay historyDay = historyDayDAO.findMostRecent(virtualECC.getId());
            netUsage = historyDay.getNet();
            response.append("Your energy usage today is ");
        } else {
            HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECC.getId(), System.currentTimeMillis()/1000);
            netUsage = historyBillingCycle.getNet();
            response.append("Your energy usage this billing cycle is ");
        }
        response.append(energyFormat(netUsage));

        response.append("</speak>");
        return response.toString();
    }

    public String getProjected(VirtualECC virtualECC) {
        HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECC.getId(), System.currentTimeMillis()/1000);
        StringBuilder response = new StringBuilder("<speak>");
        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);
        response.append("Expect your electric bill to be approximately ");
        ProjectedCost projectedCost = costService.getProjectedCost(energyPlan, TimeZone.getTimeZone(virtualECC.getTimezone()), historyBillingCycle);
        response.append(format.format(projectedCost.getCost()));
        response.append("</speak>");
        return response.toString();
    }

    public String getSpent(VirtualECC virtualECC, boolean today) {
        StringBuilder response = new StringBuilder("<speak>");
        double netCost = 0.0;
        EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getEnergyPlanId());
        Currency currentCurrency = Currency.getInstance(energyPlan.getRateType());
        NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        format.setCurrency(currentCurrency);

        if (today){
            HistoryDay historyDay = historyDayDAO.findMostRecent(virtualECC.getId());
            netCost = historyDay.getNetCost();
            response.append("You have spent ").append(format.format(netCost)).append(" today");
        } else {
            HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECC.getId(), System.currentTimeMillis()/1000);
            netCost = historyBillingCycle.getNetCost();
            response.append("You have spent ").append(format.format(netCost)).append(" this billing cycle");
        }

        response.append("</speak>");
        return response.toString();

    }
    public String getDaysLeft(VirtualECC virtualECC) {
        HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECC.getId(), System.currentTimeMillis()/1000);
        StringBuilder response = new StringBuilder("<speak>");
        long daysLeft = historyBillingCycle.getEndEpoch() - ((new Date()).getTime() / 1000);
        daysLeft = (long)(Math.floor(daysLeft / 86400));
        response.append("You have ").append(daysLeft).append(" days remaining in this billing cycle");
        response.append("</speak>");
        return response.toString();
    }

    public String activeLoads(VirtualECC virtualECC) {
        StringBuilder response = new StringBuilder("<speak>");
        List<String> activeLoads = alexaDAO.findActiveLoads(virtualECC.getId());
        if (activeLoads.size() == 0){
            response.append("No spyder loads were detected at this time");
        } else {
            response.append("Here is your Spider summary. The following loads are active");
            for (String s: activeLoads){
                response.append("<break time='500ms'/>").append(s);
            }
        }
        response.append("</speak>");
        return response.toString();
    }

    public String getGeneration(VirtualECC virtualECC, boolean today) {
        StringBuilder response = new StringBuilder("<speak>");
        double netUsage = 0.0;
        if (today){
            HistoryDay historyDay = historyDayDAO.findMostRecent(virtualECC.getId());
            netUsage = historyDay.getGeneration();
            response.append("Your generation today is ");
        } else {
            HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(virtualECC.getId(), System.currentTimeMillis()/1000);
            netUsage = historyBillingCycle.getNet();
            response.append("Your generation this billing cycle is ");
        }
        response.append(energyFormat(netUsage));
        response.append("</speak>");
        return response.toString();
    }

    static String energyFormat(double power){
        StringBuilder response =new StringBuilder();
        if (power < 1000) {
            response.append(wattFormat.format(power)).append(" watt hours ");
        } else if (power < 2000) {
            response.append(kwFormat.format(power/1000.0)).append(" kilowatt hour");
        } else {
            response.append(kwFormat.format(power/1000.0)).append(" kilowatt hours");
        }
        return response.toString();
    }


    static String powerFormat(double power){
        StringBuilder response =new StringBuilder();
        if (power < 1000) {
            response.append(wattFormat.format(power)).append(" watts ");
        } else if (power < 2000) {
            response.append(kwFormat.format(power/1000.0)).append(" kilowatt");
        } else {
            response.append(kwFormat.format(power/1000.0)).append(" kilowatts");
        }
        return response.toString();
    }

    public String generationNow(VirtualECC virtualECC) {
        Integer totalMTU = virtualECCMTUDAO.countNetMTU(virtualECC.getId(), virtualECC.getAccountId());
        HistoryMinute historyMinute =  historyMinuteDAO.findLastCompletedPost(virtualECC.getId(), System.currentTimeMillis()/1000, totalMTU);
        StringBuilder response = new StringBuilder("<speak>");
        response.append("Right now you are generating ");
        double netUsage = historyMinute.getGeneration() * 60.0;
        response.append(powerFormat(netUsage));
        response.append("</speak>");
        return response.toString();
    }

    public String getOpen() {
        StringBuilder response = new StringBuilder("<speak>");
        response.append("Welcome to the TED Commander skill. What would you like to know?");
        response.append("</speak>");
        return response.toString();
    }

    public String getHelp() {
        StringBuilder response = new StringBuilder("<speak>");
        response.append("You can ask for detailed energy use information, such as your current usage, projected energy bill, or active household loads. For example, you can ask Ted what your power bill is going to be. A full list of questions is now being displayed in the Alexa app on your mobile device. What would you like to know?");
        response.append("</speak>");
        return response.toString();
    }

    public String getHelpQuestions() {
        StringBuilder response = new StringBuilder();
        response.append("What is my energy summary?\\n");
        response.append("What electrical loads are running now?\\n");
        response.append("What is my power bill going to be?\\n");
        response.append("How much electricity am I using right now?\\n");
        response.append("What is my cost right now?\\n");
        response.append("What is my usage today?\\n");
        response.append("What is my usage this month?\\n");
        response.append("How much have I spent today?\\n");
        response.append("How much have I spent this month?\\n");
        response.append("How many days are left in my billing cycle?\\n");
        response.append("How much am I generating right now?\\n");
        response.append("How much have I generated today?\\n");
        response.append("How much have I generated this month?\\n");
        return response.toString();
    }
}
