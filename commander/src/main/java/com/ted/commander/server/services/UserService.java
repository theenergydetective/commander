/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.common.model.User;
import com.ted.commander.server.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by pete on 10/28/2014.
 */
@Service
public class UserService {
    static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDAO userDAO;

    public User findByUsername(String username) {
        //TODO: Add some sort of cache here to minimize DB Hits
        return userDAO.findByUsername(username);
    }

    public User create(User user, String key) {
        return userDAO.create(user, key);
    }

    public User getByActivationKey(String activationKey) {
        return userDAO.getByActivationKey(activationKey);
    }

    public User getByActivationKey(String username, String invitationKey) {
        return userDAO.getByActivationKey(username, invitationKey);
    }

    public void updatePassword(User user, String password) {
        userDAO.updatePassword(user, password);
    }

    public User update(User user) {
        return userDAO.update(user);
    }

    public void updateActivationKey(User user, String emailKey) {
        userDAO.updateActivationKey(user, emailKey);
    }

    public void setUpdateUsername(User sessionUser, String username) {
        userDAO.setUpdateUsername(sessionUser, username);
    }

    public User getAuthorizedUserByUserName(String username, String oldPassword) {
        return userDAO.getAuthorizedUserByUserName(username, oldPassword);
    }

    public List<User> findByUsername(String keyword, int limit) {
        return userDAO.findByUsername(keyword, limit);
    }


}
