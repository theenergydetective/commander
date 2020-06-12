/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.WeatherKey;
import com.ted.commander.server.services.AdviceService;
import com.ted.commander.server.services.VirtualECCService;
import com.ted.commander.server.services.WeatherService;
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
public class VirtualECCRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(VirtualECCRESTService.class);

    @Autowired
    AccountDAO accountDAO;


    @Autowired
    VirtualECCService virtualECCService;

    @Autowired
    UserDAO userDAO;
    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    WeatherService weatherService;

    @Autowired
    AdviceService adviceService;





    /**
     * Adds a new virtual ECC to the system
     *
     * @param virtualECC
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/virtualECC", method = RequestMethod.POST)
    public
    @ResponseBody
    RESTPostResponse create(@RequestBody VirtualECC virtualECC, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("Adding virtual ecc {} for {}", virtualECC, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());

            //check membership
            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECC.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            //Create the virtual ECC
            RESTPostResponse restPostResponse = new RESTPostResponse();

            WeatherKey weatherKey = weatherService.findWeatherKey(virtualECC);
            if (weatherKey != null) {
                LOGGER.debug("Using weather key {}", weatherKey);
                virtualECC.setWeatherId(weatherKey.getId());
            }

            restPostResponse.setId(virtualECCService.update(virtualECC).getId());
            restPostResponse.setMsg("SUCCESS");
            return restPostResponse;

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Gets virtual ECC's that the current user has read access to.
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/virtualECC", method = RequestMethod.GET)
    public
    @ResponseBody
    List<AccountLocation> getForAllAccounts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("Getting ALL virtual ECC's associated with this account: {}", request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());
            LOGGER.debug("findByUser: {}", user);
            return virtualECCService.findByUser(user.getId());
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Adds a new virtual ECC to the system
     *
     * @param virtualECC
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/virtualECC", method = RequestMethod.PUT)
    public void update(@RequestBody VirtualECC virtualECC, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("Updating virtual ecc {} for {}", virtualECC, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());

            //check membership
            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECC.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


            WeatherKey weatherKey = weatherService.findWeatherKey(virtualECC);
            if (weatherKey != null) {
                LOGGER.debug("Using weather key {}", weatherKey);
                virtualECC.setWeatherId(weatherKey.getId());
            }

            //Create the virtual ECC
            virtualECCService.update(virtualECC);

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }

    /**
     * Returns a virtual ECC for the given id.
     *
     * @param virtualECCId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/virtualECC/{virtualECCId}", method = RequestMethod.GET)
    public
    @ResponseBody
    VirtualECC get(@PathVariable Long virtualECCId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            LOGGER.debug("Getting virtual ecc {} for {}", virtualECCId, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());


            VirtualECC virtualECC = virtualECCService.findById(virtualECCId);
            if (virtualECC == null) {
                LOGGER.warn("Virtual ECC Not Found: {}", virtualECCId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return new VirtualECC();
            }

            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECC.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            return virtualECC;
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
    @RequestMapping(value = "/account/{accountId}/virtualECCs", method = RequestMethod.GET)
    public
    @ResponseBody
    List<VirtualECC> getForAccount(@PathVariable Long accountId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            LOGGER.debug("Getting virtual ecc for account {}", accountId, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());


            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), accountId);
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            return virtualECCService.findByAccount(accountId);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Deletes a virtual ECC and its corresponding Virtual MTUs
     *
     * @param virtualECCId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/virtualECC/{virtualECCId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long virtualECCId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            LOGGER.info("Getting virtual ecc {} for {}", virtualECCId, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());


            VirtualECC virtualECC = virtualECCService.findById(virtualECCId);
            if (virtualECC == null) {
                LOGGER.warn("Virtual ECC Not Found: {}", virtualECCId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECC.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            //Delete virtual MTU's

            LOGGER.info("Deleting advice MTU's associated with {}", virtualECCId);
            adviceService.deleteByVirtualECCId(virtualECCId); //TED-299
            LOGGER.info("Deleting virtual MTU's associated with {}", virtualECCId);
            List<VirtualECCMTU> virtualECCMTUS = virtualECCMTUDAO.findByVirtualECC(virtualECCId, virtualECC.getAccountId());
            virtualECCMTUDAO.deleteByVirtualECCId(virtualECCId);
            LOGGER.info("Deleting virtualECC's associated with {}", virtualECCId);
            virtualECCService.deleteById(virtualECCId);
            virtualECCService.deleteHistory(virtualECCId, virtualECCMTUS);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


    @RequestMapping(value = "/account/{accountId}/virtualECC/{virtualECCId}/mtu", method = RequestMethod.GET)
    public
    @ResponseBody
    List<VirtualECCMTU> getVirtualECCMTUList(@PathVariable Long accountId, @PathVariable Long virtualECCId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            LOGGER.debug("Getting virtual ecc {} for {}", virtualECCId, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());


            if (virtualECCId != 0) {
                VirtualECC virtualECC = virtualECCService.findById(virtualECCId);
                if (virtualECC == null) {
                    LOGGER.warn("Virtual ECC Not Found: {}", virtualECCId);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return null;
                }
            }

            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), accountId);
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            return virtualECCMTUDAO.findByVirtualECC(virtualECCId, accountId);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Assigns an MTU to the virtual ECC
     *
     * @param virtualECCMTU
     * @param virtualECCId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/virtualECC/{virtualECCId}/mtu", method = RequestMethod.PUT)
    public void addVirtualECCMTU(@RequestBody VirtualECCMTU virtualECCMTU, @PathVariable Long virtualECCId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("Updating {} for {}", virtualECCMTU, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());

            //Check for a valid Virutal ECC
            VirtualECC virtualECC = virtualECCService.findById(virtualECCId);
            if (virtualECC == null) {
                LOGGER.warn("Virtual ECC not found: {}", virtualECC);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //check membership
            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECCId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            virtualECCMTU.setVirtualECCId(virtualECCId);
            virtualECCMTUDAO.update(virtualECCMTU);

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


    /**
     * Assigns an MTU to the virtual ECC
     *
     * @param virtualECCId
     * @param virtualECCMTU
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "virtualECC/{virtualECCId}/mtu", method = RequestMethod.DELETE)
    public void deleteVirtualECCMTU(@PathVariable Long virtualECCId, @RequestBody VirtualECCMTU virtualECCMTU, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("Deleting {} for {}", virtualECCMTU, request.getUserPrincipal().getName());
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());

            //Check for a valid Virutal ECC
            VirtualECC virtualECC = virtualECCService.findById(virtualECCId);
            if (virtualECC == null) {
                LOGGER.warn("Virtual ECC not found: {}", virtualECC);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


            //check membership
            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECC.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            virtualECCMTUDAO.deleteById(virtualECCId, virtualECCMTU.getMtuId(), virtualECCMTU.getAccountId());

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }

    @RequestMapping(value = "virtualECC/{virtualECCId}/mtus", method = RequestMethod.DELETE)
    public void deleteVirtualECCMTUS(@PathVariable Long virtualECCId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            LOGGER.debug("Deleting all MTUs  {}", virtualECCId);
            User user = userDAO.findByUsername(request.getUserPrincipal().getName());

            //Check for a valid Virutal ECC
            VirtualECC virtualECC = virtualECCService.findById(virtualECCId);
            if (virtualECC == null) {
                LOGGER.warn("Virtual ECC not found: {}", virtualECC);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


            //check membership
            //Check to make sure the user is a member of the account.
            AccountMembership accountMembership = accountDAO.findByMember(user.getId(), virtualECC.getAccountId());
            if (accountMembership == null) {
                LOGGER.error("User {} not member of account {}", user, virtualECC.getAccountId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            virtualECCMTUDAO.deleteByVirtualECCId(virtualECCId);

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


}
