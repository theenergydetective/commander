/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.PageViewHit;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.admin.AdminStringValue;
import com.ted.commander.server.model.admin.AdminUser;
import com.ted.commander.server.model.admin.AdminUser_;
import com.ted.commander.server.model.admin.MTULastPost;
import com.ted.commander.server.repository.AdminUserRepository;
import com.ted.commander.server.services.PlaybackService;
import com.ted.commander.server.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@RestController
@RequestMapping("/api/admin")
public class AdminRestService {

    final static Logger LOGGER = LoggerFactory.getLogger(AdminRestService.class);

    @Autowired
    AdminUserRepository adminUserRepository;

    @Autowired
    UserDAO userDAO;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    AccountMemberDAO accountMemberDAO;

    @Autowired
    MTUDAO mtuDAO;

    @Autowired
    ServerService serverService;

    GoogleAnalytics ga = new GoogleAnalytics("UA-87346279-1");

    static Specification<AdminUser> containsText(String searchText) {
        return (root, query, builder) -> {
            String padSearchText = "%" + searchText + "%";
            Predicate p = builder.like(root.get(AdminUser_.username), padSearchText);
            p = builder.or(p, builder.like(root.get(AdminUser_.lastName), padSearchText));
            p = builder.or(p, builder.like(root.get(AdminUser_.firstName), padSearchText));
            p = builder.or(p, builder.like(root.get(AdminUser_.middleName), padSearchText));
            return p;
        };
    }


    /**
     * Returns a list of users in the system.
     *
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/userSearch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<AdminUser> updateAccount(@QueryParam("index") Integer index, @QueryParam("rows") Integer rows, @QueryParam("sortBy") Integer sortBy, @QueryParam("filter") String filter) {
        ga.postAsync(new PageViewHit("/apt/admin", "AdminRESTService", "UpdateAccount"));
        LOGGER.debug("Searching for admin users: filter:{} sortBy:{} index:{}", filter, sortBy, index);
        Page page = null;

        if (sortBy == null) sortBy = 0;
        if (filter == null) filter = "";
        if (index == null) index = 0;
        if (rows == null) rows = 1000;

        switch (sortBy) {
            case 1:
                page = adminUserRepository.findAll(containsText(filter), new PageRequest(index / rows, rows, new Sort(Sort.Direction.ASC, "username")));
                break;
            case 2:
                page = adminUserRepository.findAll(containsText(filter), new PageRequest(index / rows, rows, new Sort(Sort.Direction.ASC, "lastName")));
                break;
            case 3:
                page = adminUserRepository.findAll(containsText(filter), new PageRequest(index / rows, rows, new Sort(Sort.Direction.DESC, "userState")));
                break;
            default:
                page = adminUserRepository.findAll(containsText(filter), new PageRequest(index / rows, rows, new Sort(Sort.Direction.DESC, "id")));
        }
        return page.getContent();
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public
    @ResponseBody
    AdminUser getUser(@PathVariable Long userId) {
        LOGGER.debug("Logging up user {}", userId);
        return adminUserRepository.findOne(userId);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/{userId}/userState", method = RequestMethod.PUT)
    public void setUserState(@RequestBody AdminStringValue adminStringValue, @PathVariable Long userId) {
        AdminUser adminUser = adminUserRepository.findOne(userId);
        UserState userState = UserState.valueOf(adminStringValue.getStringValue());
        LOGGER.info("Changing user state from {} to {}", adminUser.getUserState(), userState);
        adminUser.setUserState(userState);
        adminUserRepository.saveAndFlush(adminUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/{userId}/password", method = RequestMethod.PUT)
    public void setPassword(@RequestBody UserPassword userPassword, @PathVariable Long userId) {
        ga.postAsync(new PageViewHit("/apt/admin", "AdminRESTService", "setPassword"));
        User user = userDAO.findById(userId);
        userDAO.updatePassword(user, userPassword.getNewPassword());
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/user/{userId}/accounts", method = RequestMethod.GET)
    public List<AccountMembership> getAccounts(@PathVariable Long userId) {
        List<AccountMembership> accountMembershipList = accountDAO.findMemberships(userId);
        List<AccountMembership> result = new ArrayList<>();
        for (AccountMembership accountMembership : accountMembershipList) {
            if (accountMembership.isAdmin()) {
                result.add(accountMembership);
            }
        }
        return result;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable Long accountId) {
        return accountDAO.findById(accountId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/account/{accountId}/members", method = RequestMethod.GET)
    public List<AccountMember> getAccountMembers(@PathVariable Long accountId) {
        return accountMemberDAO.findByAccount(accountId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/account/{accountId}/mtu", method = RequestMethod.GET)
    public List<MTULastPost> getMTULastPost(@PathVariable Long accountId) {
        return mtuDAO.findLastPost(accountId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/account/{accountId}/members", method = RequestMethod.PUT)
    public void addMember(@RequestBody AdminStringValue adminStringValue, @PathVariable Long accountId) {
        LOGGER.info("Adding {} to account id: {}", adminStringValue, accountId);
        User user = userDAO.findByUsername(adminStringValue.getStringValue());
        if (user != null) {

            AccountMember existingAccountMember = accountMemberDAO.findByUserName(accountId, user.getUsername());
            if (existingAccountMember != null) {
                LOGGER.info("Updating Existing user: {}", existingAccountMember);
                existingAccountMember.setAccountRole(AccountRole.ADMIN);
                accountMemberDAO.update(existingAccountMember);
            } else {
                LOGGER.info("Adding user {} to {}", user, accountId);
                AccountMember newAccountMember = new AccountMember();
                newAccountMember.setAccountId(accountId);
                newAccountMember.setUserId(user.getId());
                newAccountMember.setAccountRole(AccountRole.ADMIN);
                accountMemberDAO.update(newAccountMember);
            }
        }
        ;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/account/{accountId}/members/{accountMemberId}", method = RequestMethod.DELETE)
    public void deleteAccountMember(@PathVariable Long accountId, @PathVariable Long accountMemberId) {
        accountMemberDAO.deleteById(accountMemberId);
    }


    @Autowired
    PlaybackService playbackService;

    @Autowired
    VirtualECCDAO virtualECCDAO;


    //TODO: Require Auth from admin
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/playback/{server}", method = RequestMethod.GET)
    public String playbackAccount(@PathVariable int server){
        LOGGER.info("QUEUE PLAYBACK!!!");
        playbackService.queuePlayback(server);
        LOGGER.info("DONE QUEUE PLAYBACK!!!");
        return "RUNNING";
    }

    //TODO: Require Auth from admin
    @RequestMapping(value = "/st0pS3rv3r", method = RequestMethod.GET)
    public String stopTheServer(){
        LOGGER.warn("MANAULLY STOPPING THE SERVER. PLEASE WAIT FOR ALL THREADS TO EXIT");
        LOGGER.warn("MANAULLY STOPPING THE SERVER. PLEASE WAIT FOR ALL THREADS TO EXIT");
        LOGGER.warn("MANAULLY STOPPING THE SERVER. PLEASE WAIT FOR ALL THREADS TO EXIT");
        LOGGER.warn("MANAULLY STOPPING THE SERVER. PLEASE WAIT FOR ALL THREADS TO EXIT");
        LOGGER.warn("MANAULLY STOPPING THE SERVER. PLEASE WAIT FOR ALL THREADS TO EXIT");
        LOGGER.warn("MANAULLY STOPPING THE SERVER. PLEASE WAIT FOR ALL THREADS TO EXIT");
        serverService.stop();
        return "STOPPING...Check the logs";
    }
}
