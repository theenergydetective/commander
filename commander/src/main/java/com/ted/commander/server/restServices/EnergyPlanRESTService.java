/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.EnergyPlanDAO;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.services.EnergyPlanService;
import com.ted.commander.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class EnergyPlanRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(EnergyPlanRESTService.class);

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    UserService userService;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    EnergyPlanDAO energyPlanDAO;


    @Autowired
    VirtualECCDAO virtualECCDAO;


    @Autowired
    ACLService aclService;


    /**
     * Permission check to see if the supplied token has access to edit the energy plan
     *
     * @param user
     * @param energyPlanId
     * @return
     */
    private AccountMembership getMembership(User user, Long energyPlanId) {
        LOGGER.debug("Getting calculators {} for {}", energyPlanId, user);
        AccountMembership accountMembership = accountDAO.findByEnergyPlan(user.getId(), energyPlanId);
        if (accountMembership == null) {
            LOGGER.error("User {} not member of account for calculators {}", user, energyPlanId);
            return null;
        }
        return accountMembership;
    }

    /**
     * Adds a new EnergyPlan to the system
     *
     * @param energyPlan
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/energyPlan", method = RequestMethod.POST)
    public
    @ResponseBody
    RESTPostResponse create(@RequestBody EnergyPlan energyPlan, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());

            LOGGER.debug("Adding energy plan {} for {}", energyPlan, user.getUsername());

            //check membership
            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), energyPlan.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, energyPlan.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            if (accountMembership.getAccountRole().equals(AccountRole.READ_ONLY)) {
                LOGGER.error("Insufficient privilege on account {}:{}", accountMembership, user);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            energyPlan = energyPlanDAO.update(energyPlan);
            energyPlanService.saveEnergyPlan(energyPlan);

            //Create the virtual ECC
            RESTPostResponse restPostResponse = new RESTPostResponse();
            restPostResponse.setId(energyPlan.getId());
            restPostResponse.setMsg("SUCCESS");
            return restPostResponse;

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * updates an energy plan in the system
     *
     * @param energyPlan
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/energyPlan", method = RequestMethod.PUT)
    public void update(@RequestBody EnergyPlan energyPlan, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());
            LOGGER.debug("Updating Energy Plan {} for {}", energyPlan, user);


            AccountMembership accountMembership = getMembership(user, energyPlan.getId());
            if (accountMembership != null && !accountMembership.getAccountRole().equals(AccountRole.READ_ONLY)) {
                //Update the energy plan
                energyPlanDAO.update(energyPlan);
                energyPlanService.saveEnergyPlan(energyPlan);



            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


    /**
     * Returns a calculators for the given id.
     *
     * @param energyPlanId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/energyPlan/{energyPlanId}", method = RequestMethod.GET)
    public
    @ResponseBody
    EnergyPlan get(@PathVariable Long energyPlanId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());
            LOGGER.debug("Getting energyPlanId {} for {}", energyPlanId, user);

            AccountMembership accountMembership = getMembership(user, energyPlanId);
            if (accountMembership != null) {
                return energyPlanService.loadEnergyPlan(energyPlanId);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }


        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Returns the energy plan for the specified location
     *
     * @param virtualECCId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/energyPlan", method = RequestMethod.GET)
    public
    @ResponseBody
    EnergyPlan getForLocation(@QueryParam("virtualECCId") Long virtualECCId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (aclService.canViewLocation(request.getUserPrincipal().getName(), virtualECCId)){
                return energyPlanService.loadEnergyPlanByVirtualECC(virtualECCId);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }

    /**
     * Returns a virtual ECC for the given account.
     *
     * @param accountId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}/energyPlanKeys", method = RequestMethod.GET)
    public
    @ResponseBody
    List<EnergyPlanKey> getKeysForAccount(@PathVariable Long accountId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            User user = userService.findByUsername(request.getUserPrincipal().getName());
            LOGGER.debug("Getting virtual ecc for account {}", accountId, user);


            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), accountId);
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            return energyPlanDAO.findKeysByAccount(accountId);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Deletes an EnergyPlan
     *
     * @param energyPlanId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/energyPlan/{energyPlanId}", method = RequestMethod.DELETE)
    public void deleteByPlan(@PathVariable Long energyPlanId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());
            AccountMembership accountMembership = getMembership(user, energyPlanId);
            if (accountMembership != null && !accountMembership.getAccountRole().equals(AccountRole.READ_ONLY)) {
                energyPlanService.deleteEnergyPlan(energyPlanId);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }





}
