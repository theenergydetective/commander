/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.LastPost;
import com.ted.commander.common.model.User;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.AccountMemberDAO;
import com.ted.commander.server.dao.LastPostDAO;
import com.ted.commander.server.services.EmailService;
import com.ted.commander.server.services.KeyService;
import com.ted.commander.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@Controller
@RequestMapping("/api")
public class NoPostRESTService {

    final static Logger LOGGER = LoggerFactory.getLogger(NoPostRESTService.class);

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


    @RequestMapping(value = "/nopost", method = RequestMethod.GET)

    public
    @ResponseBody
    List<LastPost> getNoPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            List<LastPost> lastPostList = new ArrayList<>();
            User authorizedUser = userService.findByUsername(request.getUserPrincipal().getName());
            List<AccountMembership> accountMembershipList = accountDAO.findMemberships(authorizedUser.getId());
            for (AccountMembership accountMembership : accountMembershipList) {
                if (accountMembership.getAccountRole().equals(AccountRole.OWNER)) {
                    lastPostList = lastPostDAO.findExpired(accountMembership.getAccount().getId());
                    break;
                }
            }
            if (lastPostList == null) return new ArrayList<>();
            else return lastPostList;
        } catch (Exception ex) {
            LOGGER.error("Critical Exception", ex);
            response.sendError(500);
            return null;
        }

    }


}
