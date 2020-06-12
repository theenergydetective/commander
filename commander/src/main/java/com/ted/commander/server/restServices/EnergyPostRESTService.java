/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.AccountState;
import com.ted.commander.common.enums.ECCState;
import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.ECC;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.ECCDAO;
import com.ted.commander.server.dao.MTUDAO;
import com.ted.commander.server.model.ServerInfo;
import com.ted.commander.server.model.energyPost.EnergyPost;
import com.ted.commander.server.model.energyPost.Ted5000Activation;
import com.ted.commander.server.model.energyPost.Ted5000ActivationResponse;
import com.ted.commander.server.services.EnergyPostQueue;
import com.ted.commander.server.services.HazelcastService;
import com.ted.commander.server.services.KeyService;
import com.ted.commander.server.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class EnergyPostRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(EnergyPostRESTService.class);
    final static Logger XML_LOGGER = LoggerFactory.getLogger("com.ted.commander.server.restServices.xml");

    @Autowired
    ServerInfo serverInfo;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    ECCDAO eccDAO;
    @Autowired
    MTUDAO mtuDAO;
    @Autowired
    KeyService keyService;

    @Autowired
    HazelcastService hazelcastService;

    @Autowired
    VersionController versionController;

    @Autowired
    ServerService serverService;
    Stat postStat = new Stat();

    @Autowired
    EnergyPostQueue energyPostQueue;

    @PostConstruct
    void init() {
        XML_LOGGER.info("EnergyPostRESTService started");
    }

    /**
     * Creates a new user in the system
     *
     * @param ted5000Activation
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/activate",
            consumes = "application/xml",
            method = RequestMethod.POST)
    public
    @ResponseBody
    Ted5000ActivationResponse activate(@RequestBody Ted5000Activation ted5000Activation, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.info("activationRequest {}", ted5000Activation);

            //Load the account from the activation key.
            Account account = accountDAO.findByActivationKey(ted5000Activation.unique);
            if (account == null) {
                LOGGER.warn("Invalid activation key specified {}", ted5000Activation);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return null;
            }

            //Check if the ECC is already part of the account. If not, add it.
            ECC ecc = eccDAO.findById(account.getId(), ted5000Activation.getECCId());
            if (ecc != null) {
                LOGGER.info("ECC {} already activated for account {}", ecc, account);
            } else {
                LOGGER.info("Activating ECC {} for account {}", ecc, account);
                ecc = new ECC();
                ecc.setId(ted5000Activation.getECCId());
                ecc.setUserAccountId(account.getId());
                ecc.setState(ECCState.ACTIVATED);
                ecc.setSecurityKey(keyService.generateSecurityKey(20));
                ecc = eccDAO.update(ecc);
            }


            Ted5000ActivationResponse activationResponse = new Ted5000ActivationResponse();

            if (!serverService.isDevelopment()) {
                activationResponse.PostServer = serverInfo.getServerName();
                activationResponse.UseSSL = "F";
                activationResponse.PostPort = 80;
                activationResponse.PostURL = "/api/postData";
                activationResponse.AuthToken = ecc.getSecurityKey();
                activationResponse.PostRate = 1;
                activationResponse.Spyder = "T";
            } else {
                activationResponse.PostServer = "192.168.1.121";
                activationResponse.UseSSL = "F";
                activationResponse.PostPort = 8080;
                activationResponse.PostURL = "/api/postData";
                activationResponse.AuthToken = ecc.getSecurityKey();
                activationResponse.PostRate = 1;
                activationResponse.Spyder = "T";
            }


            LOGGER.info("Returning Activation Response: {}", activationResponse);
            return activationResponse;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }

    private void sendFinalResponse(HttpServletResponse response, int code, String message, PrintWriter out) {
        //response.setContentType("text/xml");
        response.setStatus(code);
        out.write(message);
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/postData",
            consumes = "application/xml",
            method = RequestMethod.POST)
    public void postData(@RequestBody EnergyPost energyPost, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            PrintWriter out = response.getWriter();

            if (!serverService.isKeepRunning()){
                sendFinalResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "MAINTENANCE", out);
                return;
            }

            if (energyPostQueue.size(energyPost.getGateway()) > 10){
                LOGGER.warn("Backlog for {}. Denying packet.", energyPost.getGateway());
                sendFinalResponse(response, 555, "BACKLOG", out);
                return;
            }

            if (hazelcastService.getEnergyPostRecordsQueue().size() > 3000){
                LOGGER.warn("Max energy post records in queue. Denying packet: {}", energyPost.getGateway());
                sendFinalResponse(response, 555, "BACKLOG", out);
                return;
            }

            if (hazelcastService.getStandAlonePostQueue().size() > 10000){
                LOGGER.warn("Max stand along post records in queue. Denying packet.", energyPost.getGateway());
                sendFinalResponse(response, 555, "BACKLOG", out);
                return;
            }



            if (energyPost == null || energyPost.getGateway() == null) {
                sendFinalResponse(response, 554, "NO DATA", out);
                return;
            }

            //This will prevent a post from being received if a long running post is being processed (long term smoothing).
//            if (energyPostService.isProcessing(energyPost.getGateway())){
//                LOGGER.warn("ECC {} id still posting. Discarding the post", energyPost.getGateway());
//                sendFinalResponse(response, 555, "STILL PROCESSING", out);
//                return;
//            }


            LOGGER.debug("energyPost {}", energyPost);
            long s = System.currentTimeMillis();


            //Check the ECC
            ECC ecc = eccDAO.findByKey(energyPost.getSecurityKey());
            if (ecc == null) {
                LOGGER.warn("ECC NOT ACTIVATED: {} {}", energyPost.getECCId(), energyPost.getSecurityKey());
                sendFinalResponse(response, HttpServletResponse.SC_FORBIDDEN, "NOT ACTIVATED", out);
                return;
            }

            if (energyPost.getVersion() != null && ecc.getVersion() != null && !ecc.getVersion().equals(energyPost.getVersion())) {
                LOGGER.info("Updating {} version to: {}", ecc.getVersion(), energyPost.getVersion());
                ecc.setVersion(energyPost.getVersion());
                eccDAO.update(ecc);
            }

            //Check to make sure the ecc matches the record for the key and that its enabled.
            if (!ecc.getId().equals(energyPost.getECCId()) || ecc.getState().equals(ECCState.DISABLED)) {
                LOGGER.warn("Invalid Gateway ID specified in record or ECC disabled: ECC:{} Record:{}", ecc, energyPost);
                sendFinalResponse(response, HttpServletResponse.SC_FORBIDDEN, "INVALID ECC ID", out);
                return;
            }

            //Check the account
            Account account = accountDAO.findById(ecc.getUserAccountId());
            if (account.getId().equals(242l)) {

                String ipAddress = request.getHeader("X-FORWARDED-FOR");
                if (ipAddress == null) {
                    ipAddress = request.getRemoteAddr();
                }
                if (!versionController.demoIp.equals(ipAddress)) {
                    LOGGER.debug("Setting new demo ip address: {}", ipAddress);
                    versionController.demoIp = ipAddress;
                }
            }

            if (account.getAccountState().equals(AccountState.DISABLED)) {
                sendFinalResponse(response, HttpServletResponse.SC_FORBIDDEN, "ACCOUNT LOCKED", out);
                return;
            }

            energyPost.setAccount(account);

            LOGGER.debug("Adding record to queue");


            if (!hazelcastService.addEnergyPostRecord(energyPost)) {
                LOGGER.error("Could not add record to the queue: {}", energyPost);
                response.sendError(500);
                return;
            }

            //Write the success message

            sendFinalResponse(response, 200, "SUCCESS", out);

            postStat.add(System.currentTimeMillis() - s);
            if (account != null && account.getId().equals(52l)) {
                LOGGER.info("Average POST TIME: {}", postStat);
            }


        } catch (Exception ex) {
            LOGGER.error("Critical Exception: {}", new Object[]{energyPost}, ex);
            response.sendError(500);
            return;
        }
    }

    class Stat {
        long sum = 0;
        long count = 0;
        long avg = 0;

        public void add(long val) {
            sum = sum + val;
            count++;

            if (count >= 1000000) {
                count = 0;
                sum = 0;
                avg = 0;
            }

            avg = sum / count;
        }

        @Override
        public String toString() {
            return "avg: " + avg;
        }
    }


}
