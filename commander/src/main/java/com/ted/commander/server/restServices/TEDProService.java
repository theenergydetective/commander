/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.AccountState;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.AccountMemberDAO;
import com.ted.commander.server.model.ActivationConfigPost;
import com.ted.commander.server.model.TEDProRESTJoinRequest;
import com.ted.commander.server.model.TEDProRESTPostResponse;
import com.ted.commander.server.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@RestController
@RequestMapping("/api/tedPro")
public class TEDProService {

    final static Logger LOGGER = LoggerFactory.getLogger(TEDProService.class);


    @Autowired
    UserService userService;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    AccountMemberDAO accountMemberDAO;

    @Autowired
    EmailService emailService;

    @Autowired
    KeyService keyService;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    VirtualECCService virtualECCService;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");
    /**
     * retrieves the activation key for the user if the credentials are correct
     */
    @RequestMapping(value = "/checkUser", method = RequestMethod.POST)
    public
    @ResponseBody
    TEDProRESTPostResponse getActivationKey(@RequestBody TEDProRESTJoinRequest tedProRESTJoinRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/api/tedPro/checkUser", "FootprintsActivation", "Get Activation Key"));


        try {
            //Validate the user
            User user = userService.getAuthorizedUserByUserName(tedProRESTJoinRequest.getUsername(), tedProRESTJoinRequest.getPassword());

            if (user == null) {
                LOGGER.debug("User not found: {}", tedProRESTJoinRequest.getUsername());
                if (userService.findByUsername(tedProRESTJoinRequest.getUsername()) == null)
                    return new TEDProRESTPostResponse(0l, "NOT FOUND");
                else return new TEDProRESTPostResponse(0l, "UNAUTHORIZED");
            }

            TEDProRESTPostResponse tedProRESTPostResponse = new TEDProRESTPostResponse(user.getId(), "SUCCESS");
            //Get accounts
            List<AccountMembership> accountMemberships = accountDAO.findMemberships(user.getId());
            for (AccountMembership accountMembership : accountMemberships) {
                if (accountMembership.isEditor()) {
                    tedProRESTPostResponse.getAccountList().add(accountMembership.getAccount());
                }

            }
            LOGGER.debug("CHECK USER: {}", tedProRESTPostResponse);
            return tedProRESTPostResponse;

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }

    /**
     * creates a new user and returns the activation key associated with it.
     */
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public
    @ResponseBody
    TEDProRESTPostResponse registerUser(@RequestBody TEDProRESTJoinRequest joinRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/api/tedPro/registerUser", "FootprintsActivation", "Create User"));

        try {

            LOGGER.info("registerUser {}", joinRequest);
            User user = userService.findByUsername(joinRequest.getUsername());

            if (user != null) {
                LOGGER.debug("USER ALREADY EXISTS: {}", user);
                return getActivationKey(joinRequest, request, response);
            } else {
                LOGGER.debug("CREATING NEW USER");
                user = new User();
                user.setUsername(joinRequest.getUsername());
                user.setFirstName(joinRequest.getFirstName());
                user.setMiddleName(joinRequest.getMiddleName());
                user.setLastName(joinRequest.getLastName());
                user.setJoinDate(System.currentTimeMillis());
                user.setUsername(joinRequest.getUsername());

                String emailKey = keyService.generateEmailKey();
                user.setUserState(UserState.ENABLED);
                user = userService.create(user, emailKey);
                userService.updatePassword(user, joinRequest.getPassword());

                //Create a default account.
                Account account = new Account();
                account.setActivationKey(keyService.generateKey());
                account.setName(user.getFormattedName());
                account.setAccountState(AccountState.ENABLED);
                account = accountDAO.update(account);

                AccountMember accountMember = new AccountMember();
                accountMember.setAccountId(account.getId());
                accountMember.setUserId(user.getId());
                accountMember.setAccountRole(AccountRole.OWNER);
                accountMember = accountMemberDAO.update(accountMember);
                LOGGER.debug("Added member to account: {}", accountMember);

                TEDProRESTPostResponse restPostResponse = new TEDProRESTPostResponse();
                restPostResponse.setId(user.getId());
                restPostResponse.setMsg(UserState.ENABLED.name());
                restPostResponse.getAccountList().add(account);
                return restPostResponse;
            }


        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }

    /**
     * creates the system settings
     */
    @RequestMapping(value = "/registerSystem", method = RequestMethod.POST)
    public
    @ResponseBody
    TEDProRESTPostResponse registerSystem(@RequestBody ActivationConfigPost configPost, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ga.postAsync(new PageViewHit("/api/tedPro/registerSystem", "FootprintsActivation", "Update System Settings"));

        try {
            LOGGER.debug("RECEIVED: {}", configPost);

            //Check auth credentials
            User user = userService.getAuthorizedUserByUserName(configPost.getUsername(), configPost.getPassword());
            if (user == null) {
                LOGGER.warn("Invalid credentials: {}", configPost.getUsername());
                return new TEDProRESTPostResponse(0l, "UNAUTHORIZED");
            }

            //Check account access
            boolean hasAccess = false;
            List<AccountMembership> accountMembershipsList = accountDAO.findMemberships(user.getId());
            for (AccountMembership accountMembership : accountMembershipsList) {
                if (accountMembership.getAccount().getId().equals(configPost.getAccount())) {
                    hasAccess = true;
                    break;
                }
            }
            if (!hasAccess) {
                LOGGER.warn("Unauthorized account access: {} {}", user, configPost.getAccount());
                return new TEDProRESTPostResponse(0l, "UNAUTHORIZED");
            }


            VirtualECC virtualECC = virtualECCService.parseFromECCXML(configPost.getDescription(), configPost.getAccount(), configPost.getEnergyPlanId(), configPost.getXmlPayload());
            LOGGER.debug("Created virtualECCn: {}", virtualECC);
            if (virtualECC == null || virtualECC.getId() == null || virtualECC.getId() == 0) {
                return new TEDProRESTPostResponse(null, "FALSE");
            } else {
                return new TEDProRESTPostResponse(virtualECC.getId(), "TRUE");
            }


        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * creates the utility settings
     *
     * @param configPost
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/registerUtility", method = RequestMethod.POST)
    public
    @ResponseBody
    TEDProRESTPostResponse registerUtility(@RequestBody ActivationConfigPost configPost, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/api/tedPro/registerUtility", "FootprintsActivation", "Update Utility Settings"));
        try {

            LOGGER.debug("RECEIVED: {}", configPost);

            //Check auth credentials
            User user = userService.getAuthorizedUserByUserName(configPost.getUsername(), configPost.getPassword());
            if (user == null) {
                LOGGER.warn("Invalid credentials: {}", configPost.getUsername());
                return new TEDProRESTPostResponse(0l, "UNAUTHORIZED");
            }

            //Check account access
            boolean hasAccess = false;
            List<AccountMembership> accountMembershipsList = accountDAO.findMemberships(user.getId());
            for (AccountMembership accountMembership : accountMembershipsList) {
                if (accountMembership.getAccount().getId().equals(configPost.getAccount())) {
                    hasAccess = true;
                    break;
                }
            }
            if (!hasAccess) {
                LOGGER.warn("Unauthorized account access: {} {}", user, configPost.getAccount());
                return new TEDProRESTPostResponse(0l, "UNAUTHORIZED");
            }


            EnergyPlan energyPlan = energyPlanService.parseFromECCXML(configPost.getDescription(), configPost.getAccount(), configPost.getXmlPayload());
            LOGGER.debug("Created Energy Plan: {}", energyPlan);
            if (energyPlan == null || energyPlan.getId() == null || energyPlan.getId() == 0) {
                return new TEDProRESTPostResponse(null, "FALSE");
            } else {
                return new TEDProRESTPostResponse(energyPlan.getId(), "TRUE");
            }

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


}
