/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.AccountMemberDAO;
import com.ted.commander.server.services.EmailService;
import com.ted.commander.server.services.KeyService;
import com.ted.commander.server.services.UserService;
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
@RequestMapping("/api")
public class AccountRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(AccountRESTService.class);


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

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");

    /**
     * Creates a new user in the system
     *
     * @param account
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.PUT)
    public void updateAccount(@RequestBody Account account, @PathVariable Long accountId, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ga.postAsync(new PageViewHit("/account/accountId", "AccountRESTService"));

        try {
            LOGGER.info("updateAccount {}", account);

            User user = userService.findByUsername(request.getUserPrincipal().getName());

            //Make sure the user has access to the account id to edit it.
            AccountMember accountMember = accountMemberDAO.findByUser(accountId, user.getId());
            if (accountMember == null || accountMember.getAccountRole().equals(AccountRole.READ_ONLY)) {
                if (accountMember != null) LOGGER.warn("Account not found or invalid role: {}", accountMember);
                else LOGGER.warn("Account not found or invalid role: {}", accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //Load the account
            Account oldAccount = accountDAO.findById(accountId);
            //Update the accountName & phone
            oldAccount.setPhoneNumber(account.getPhoneNumber());
            oldAccount.setName(account.getName());
            //Save the account
            accountDAO.update(oldAccount);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


    /**
     * Returns a list of members for a given account
     *
     * @param accountId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}/members", method = RequestMethod.GET)
    public
    @ResponseBody
    AccountMembers getAccountMembers(@PathVariable Long accountId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/account/accountId/members", "AccountRESTService"));
        try {
            LOGGER.info("getAccountMembers {}", accountId);
            User user = userService.findByUsername(request.getUserPrincipal().getName());


            //Make sure the user has access to the account id to edit it.
            AccountMember accountMember = accountMemberDAO.findByUser(accountId, user.getId());
            if (accountMember == null) {
                LOGGER.warn("Account not found or invalid role: {}", accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            LOGGER.debug("VALID USER FOUND");


            List<AccountMember> accountMemberList = accountMemberDAO.findByAccount(accountId);

            LOGGER.debug("RETURNING ACCOUNT MEMBERS");
            AccountMembers accountMembers = new AccountMembers();
            accountMembers.setAccountMembers(accountMemberList);
            return accountMembers;

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }

    /**
     * Updates the role of the existing account member
     *
     * @param accountMember
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}/member/{accountMemberId}", method = RequestMethod.PUT)
    public void updateAccountMember(@RequestBody AccountMember accountMember, @PathVariable Long accountId, @PathVariable Long accountMemberId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/account/accountId/member/accountMemberId", "AccountRESTService"));
        try {
            LOGGER.info("updateAccountMember: {}", accountMember);
            User user = userService.findByUsername(request.getUserPrincipal().getName());


            //Make sure the user has access to the account id to edit it.
            AccountMember userAccountMember = accountMemberDAO.findByUser(accountId, user.getId());

            if (userAccountMember == null ||
                    userAccountMember.getAccountRole().equals(AccountRole.READ_ONLY) ||
                    userAccountMember.getAccountRole().equals(AccountRole.EDIT_ECCS)) {
                if (accountMember != null) LOGGER.warn("Account not found or invalid role: {}", accountMember);
                else LOGGER.warn("Account not found or invalid role: {}", accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            accountMemberDAO.update(accountMember);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


    /**
     * Updates the role of the existing account member
     *
     * @param invitedAccountMember
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}/member", method = RequestMethod.POST)
    public
    @ResponseBody
    RESTPostResponse addAccountMember(@RequestBody AccountMember invitedAccountMember, @PathVariable Long accountId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/account/accountId/member", "AccountRESTService"));
        try {
            LOGGER.info("addAccountMember: {}", invitedAccountMember);
            User sessionUser = userService.findByUsername(request.getUserPrincipal().getName());

            //Make sure the user has access to the account id to edit it.
            AccountMember userAccountMember = accountMemberDAO.findByUser(accountId, sessionUser.getId());
            if (userAccountMember == null || !userAccountMember.canEditUsers()) {
                LOGGER.warn("Account not found or invalid role: {}", accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }


            String userName = invitedAccountMember.getUser().getUsername();

            //Check to see if the user exists. If the user exists, use the account. Otherwise, create a new account and
            //do the invite.
            User user = userService.findByUsername(userName);
            if (user == null) {
                LOGGER.info("User not found. Inviting " + userName);
                user = new User();
                user.setUsername(userName);
                user.setUserState(UserState.INVITED);
                user.setJoinDate(System.currentTimeMillis());
                String key = KeyService.generateSecurityKey(20);
                user = userService.create(user, key);
                emailService.sendInviteEmail(user, userName, key);
            }

            //Duplicate check
            AccountMember existingAccountMember = accountMemberDAO.findByUserName(accountId, user.getUsername());
            RESTPostResponse restPostResponse = new RESTPostResponse();
            restPostResponse.setMsg("SUCCESS");


            if (existingAccountMember != null) {
                LOGGER.info("Adding user {} to {}", userName, accountId);

                //Don't allow for an owner change.

                if (!existingAccountMember.getAccountRole().equals(AccountRole.OWNER)) {
                    existingAccountMember.setAccountRole(invitedAccountMember.getAccountRole());
                }
                existingAccountMember = accountMemberDAO.update(existingAccountMember);
                restPostResponse.setId(existingAccountMember.getId());
            } else {
                LOGGER.info("Adding user {} to {}", userName, accountId);
                AccountMember newAccountMember = new AccountMember();
                newAccountMember.setAccountId(accountId);
                newAccountMember.setUserId(user.getId());
                newAccountMember.setAccountRole(invitedAccountMember.getAccountRole());
                newAccountMember = accountMemberDAO.update(newAccountMember);
                restPostResponse.setId(newAccountMember.getId());
            }
            return restPostResponse;


        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * Updates the role of the existing account member
     *
     * @param accountId
     * @param accountMemberId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/account/{accountId}/member/{accountMemberId}", method = RequestMethod.DELETE)
    public void deleteAccountMember(@PathVariable Long accountId, @PathVariable Long accountMemberId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ga.postAsync(new PageViewHit("/account/accountId/member/accountMemberId", "AccountRESTService", "DELETE"));
        try {
            LOGGER.info("deleting account member: {}", accountMemberId);
            User user = userService.findByUsername(request.getUserPrincipal().getName());


            //Make sure the user has access to the account id to edit it.
            AccountMember userAccountMember = accountMemberDAO.findByUser(accountId, user.getId());
            if (userAccountMember == null || !userAccountMember.canEditUsers()) {
                LOGGER.warn("Account not found or invalid role: {}", accountId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            accountMemberDAO.deleteById(accountMemberId);

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }


}
