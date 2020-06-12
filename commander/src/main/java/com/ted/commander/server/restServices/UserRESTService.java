/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.AccountState;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.AccountMemberDAO;
import com.ted.commander.server.dao.LastPostDAO;
import com.ted.commander.server.services.EmailService;
import com.ted.commander.server.services.KeyService;
import com.ted.commander.server.services.PushService;
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
public class UserRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(UserRESTService.class);

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
    LastPostDAO lastPostDAO;

    @Autowired
    PushService pushService;


    /**
     * Creates a new user in the system
     *
     * @param joinRequest
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.POST)

    public
    @ResponseBody
    RESTPostResponse createUser(@RequestBody JoinRequest joinRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.info("Join Request {}", joinRequest);

            //Check for invite key
            if (joinRequest.getInvitationKey() != null && !joinRequest.getInvitationKey().isEmpty()) {
                User user = userService.getByActivationKey(joinRequest.getInvitationKey());
                if (user != null) {
                    LOGGER.debug("INVITED USER FOUND: {}", user);

                    user.setFirstName(joinRequest.getFirstName());
                    user.setMiddleName(joinRequest.getMiddleName());
                    user.setLastName(joinRequest.getLastName());
                    user.setJoinDate(System.currentTimeMillis());
                    user.setUsername(joinRequest.getUsername());
                    user.setUserState(UserState.ENABLED);

                    userService.update(user);
                    userService.updatePassword(user, joinRequest.getPassword());
                    userService.updateActivationKey(user, null);

                    RESTPostResponse restPostResponse = new RESTPostResponse();
                    restPostResponse.setId(user.getId());
                    restPostResponse.setMsg(UserState.ENABLED.name());
                    return restPostResponse;
                } else {
                    LOGGER.debug("INVALID JOIN KEY. Creating new user");
                    joinRequest.setInvitationKey(null);
                    return createUser(joinRequest, request, response);
                }

            } else {

                User user = userService.findByUsername(joinRequest.getUsername());
                if (user != null) {
                    LOGGER.debug("USER ALREADY EXISTS: {}", user);

                    //Check to see if this is an activation key request
                    if (user.getUserState() == UserState.WAITING_ACTIVATION || user.getUserState() == UserState.INVITED) {
//                        LOGGER.debug("REFRESHING ACTIVATION KEY: {}", user);
//                        String emailKey = keyService.generateEmailKey();
//                        user.setUserState(UserState.WAITING_ACTIVATION);
//                        user.setFirstName(joinRequest.getFirstName());
//                        user.setMiddleName(joinRequest.getMiddleName());
//                        user.setLastName(joinRequest.getLastName());
//
//                        userService.update(user);
//                        LOGGER.debug("CREATED USER: " + user);
//                        userService.updatePassword(user, joinRequest.getPassword());
//                        userService.updateActivationKey(user, emailKey);
//                        emailService.sendActivationEmail(user, emailKey);

                        //TED-161- Go ahead and enable them.
                        user.setFirstName(joinRequest.getFirstName());
                        user.setMiddleName(joinRequest.getMiddleName());
                        user.setLastName(joinRequest.getLastName());
                        user.setJoinDate(System.currentTimeMillis());
                        user.setUsername(joinRequest.getUsername());
                        user.setUserState(UserState.ENABLED);

                        userService.update(user);
                        userService.updatePassword(user, joinRequest.getPassword());
                        userService.updateActivationKey(user, null);

                        RESTPostResponse restPostResponse = new RESTPostResponse();
                        restPostResponse.setId(user.getId());
                        restPostResponse.setMsg(UserState.ENABLED.name());
                        return restPostResponse;
                    }

                    RESTPostResponse restPostResponse = new RESTPostResponse();
                    restPostResponse.setId(null);
                    restPostResponse.setMsg("EXISTS");
                    return restPostResponse;

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
                    user.setUserState(UserState.WAITING_ACTIVATION);
                    user = userService.create(user, emailKey);
                    userService.updatePassword(user, joinRequest.getPassword());
                    emailService.sendActivationEmail(user, emailKey);

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

                    RESTPostResponse restPostResponse = new RESTPostResponse();
                    restPostResponse.setId(user.getId());
                    restPostResponse.setMsg(UserState.WAITING_ACTIVATION.name());
                    return restPostResponse;
                }


            }

        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }




    /**
     * updates a  user in the system
     *
     * @param user
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public void updateUser(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User authorizedUser = userService.findByUsername(request.getUserPrincipal().getName());

            //Make sure its for the user we are getting the keys for
            if (!user.getId().equals(authorizedUser.getId())) {
                LOGGER.warn("Token {} does not match userId {}", authorizedUser, user.getId());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //Make sure the user isn't trying to change the state
            User sessionUser = authorizedUser;
            sessionUser.setFirstName(user.getFirstName());
            sessionUser.setLastName(user.getLastName());
            sessionUser.setMiddleName(user.getMiddleName());
            userService.update(sessionUser);
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }

    /**
     * Gets the current user from session.
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public
    @ResponseBody
    User updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User authorizedUser = userService.findByUsername(request.getUserPrincipal().getName());
            return authorizedUser;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    /**
     * updates a  user's email in the system
     *
     * @param userEmail
     * @param userId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{userId}/email", method = RequestMethod.PUT)
    public void changeEmail(@RequestBody UserEmail userEmail, @PathVariable Long userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User authorizedUser = userService.findByUsername(request.getUserPrincipal().getName());

            //Make sure its for the user we are getting the keys for (permission check)
            if (!userId.equals(authorizedUser.getId())) {
                LOGGER.warn("Token {} does not match userId {}", authorizedUser, userId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //Make sure the user isn't trying to change the state
            User sessionUser = authorizedUser;

            if (sessionUser.getUsername().equals(userEmail.getOldEmail())) {
                sessionUser.setUsername(userEmail.getNewEmail());
                userService.setUpdateUsername(sessionUser, sessionUser.getUsername());
                LOGGER.info("Email changed for {}", sessionUser);
            } else {
                LOGGER.warn("Old email does not match for {}", authorizedUser);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }

    /**
     * updates a  user's email in the system
     *
     * @param userPassword
     * @param userId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{userId}/password", method = RequestMethod.PUT)
    public void changePassword(@RequestBody UserPassword userPassword, @PathVariable Long userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            User authorizedUser = userService.findByUsername(request.getUserPrincipal().getName());


            //Make sure its for the user we are getting the keys for (permission check)
            if (!userId.equals(authorizedUser.getId())) {
                LOGGER.warn("Token {} does not match userId {}", authorizedUser, userId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //Make sure the user isn't trying to change the state
            User sessionUser = authorizedUser;

            User authUser = userService.getAuthorizedUserByUserName(sessionUser.getUsername(), userPassword.getOldPassword());
            if (authUser != null && authUser.getId().equals(sessionUser.getId())) {
                LOGGER.info("Password changed for {}", sessionUser);
                userService.updatePassword(sessionUser, userPassword.getNewPassword());
            } else {
                LOGGER.warn("Old password does not match for {}", authorizedUser);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }
    }

    /**
     * Activates a user.
     *
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/activate/{activationKey}", method = RequestMethod.GET)
    public void activate(@PathVariable String activationKey, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("ACTIVATE CALLED: {}", activationKey);
            //Validate password
            User user = userService.getByActivationKey(activationKey);

            if (user == null) {
                LOGGER.error("User not found for {}", activationKey);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!user.getUserState().equals(UserState.WAITING_ACTIVATION)) {
                LOGGER.warn("User already activated. {}", user);
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            user.setUserState(UserState.ENABLED);
            userService.update(user);
            LOGGER.info("Activated User User Login: {}", user);
            return;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
        }

    }


    @RequestMapping(value = "/user/{userId}/accountMemberships",
            method = RequestMethod.GET)

    public
    @ResponseBody
    AccountMemberships getAccountMemberships(@PathVariable("userId") long userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("getActivationKeys: {}", userId);
            AccountMemberships accountMemberships = new AccountMemberships();

            //Verify Session Token

            User authorizedUser = userService.findByUsername(request.getUserPrincipal().getName());

            //Make sure its for the user we are getting the keys for
            if (userId != authorizedUser.getId()) {
                LOGGER.warn("Token {} does not match user{}", authorizedUser.getId(), userId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }

            //Look up and return the activation keys
            List<AccountMembership> accountMembershipList = accountDAO.findMemberships(userId);

            if (accountMembershipList == null || accountMembershipList.size() == 0) {
                //Create a default account.
                Account account = new Account();
                account.setActivationKey(keyService.generateKey());
                account.setName(authorizedUser.getFormattedName());
                account.setAccountState(AccountState.ENABLED);
                account = accountDAO.update(account);

                AccountMember accountMember = new AccountMember();
                accountMember.setAccountId(account.getId());
                accountMember.setUserId(authorizedUser.getId());
                accountMember.setAccountRole(AccountRole.OWNER);
                accountMember = accountMemberDAO.update(accountMember);
                LOGGER.debug("Added member to account: {}", accountMember);

                accountMembershipList = accountDAO.findMemberships(userId);
            }


            accountMemberships.setAccountMemberships(accountMembershipList);
            return accountMemberships;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    @RequestMapping(value = "/users",
            params = {"keyword", "limit"},
            method = RequestMethod.GET)

    public
    @ResponseBody
    Users getUsers(@QueryParam("keyword") String keyword, @QueryParam("limit") int limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            LOGGER.debug("getUsers: {}", keyword);
            Users users = new Users();
            users.setUsers(userService.findByUsername(keyword, limit));
            return users;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }

    }


}
