/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;

import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.MTU;
import com.ted.commander.common.model.User;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.EnergyDataDAO;
import com.ted.commander.server.dao.MTUDAO;
import com.ted.commander.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class MTURESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(MTURESTService.class);

    @Autowired
    MTUDAO mtuDAO;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    EnergyDataDAO energyDataDAO;


    @Autowired
    UserService userService;

    /**
     * Returns a list of MTU's for the given account.
     *
     * @param accountId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}/mtus", method = RequestMethod.GET)
    public
    @ResponseBody
    List<MTU> getMTUList(@PathVariable("accountId") long accountId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());
            LOGGER.debug("Adding looking up mtu list for {}", accountId, user);


            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), accountId);
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            return mtuDAO.findByAccount(accountId);

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    @RequestMapping(value = "/mtu", method = RequestMethod.PUT)
    public void updateMTU(@RequestBody MTU mtu, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());
            LOGGER.debug("Updating MTU {}", mtu);

            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), mtu.getAccountId());
            if (accountMembership == null || !accountMembership.isEditor()) {
                LOGGER.error("User {} does not have permission to edit account {}", user, mtu.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            mtuDAO.update(mtu);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }

    }


    @RequestMapping(value = "/mtu", method = RequestMethod.DELETE)
    public void deleteMTU(@RequestBody MTU mtu, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User user = userService.findByUsername(request.getUserPrincipal().getName());
            LOGGER.info("Deleting MTU {}", mtu);


            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), mtu.getAccountId());
            if (accountMembership == null || !accountMembership.isEditor()) {
                LOGGER.error("User {} does not have permission to edit account {}", user, mtu.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            mtuDAO.deleteById(mtu.getId(), mtu.getAccountId());
            LOGGER.debug("Deleting Energy Data for {}", mtu);
            energyDataDAO.deleteByMTUID(mtu.getId(), mtu.getAccountId());
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }

    }

}
