/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.User;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import com.ted.commander.server.util.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserDAOTest {

        @Autowired
        protected UserDAO userDAO;

    @Autowired
    private DataSource commanderDataSource;


    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.user where lastName='UNITTESTLAST'");
    }


    @Test
    public void testDAO() {

        String username = TestUtil.getUniqueKey();
        String password = TestUtil.getUniqueKey();
        String activationKey = TestUtil.getUniqueKey();

        User user = new User();
        user.setLastName("UNITTESTLAST");
        user.setFirstName("UNITTESTFIRST");
        user.setMiddleName("MIDDLE");
        user.setUsername(username);
        user.setUserState(UserState.ENABLED);

        //Test create
        User createdUser = userDAO.create(user, "1234567890");
        Assert.assertNotNull(createdUser.getId());

        Assert.assertNotNull(userDAO.getByActivationKey(user.getUsername(), "1234567890"));

        userDAO.updateActivationKey(user, "987654321");
        Assert.assertNotNull(userDAO.getByActivationKey(user.getUsername(), "987654321"));


        //Test Save
        String password2 = TestUtil.getUniqueKey();
        String activationKey2 = TestUtil.getUniqueKey();

        userDAO.update(createdUser);

        //Save test and findByUsername test
        User savedUser = userDAO.findByUsername(username);
        Assert.assertNotNull(savedUser);


        Assert.assertEquals("UNITTESTLAST", savedUser.getLastName());
        Assert.assertEquals("UNITTESTFIRST", savedUser.getFirstName());
        Assert.assertEquals("MIDDLE", savedUser.getMiddleName());
        Assert.assertEquals(UserState.ENABLED, user.getUserState());


        Assert.assertEquals(savedUser.getUsername(), username);
        userDAO.updatePassword(savedUser, password);

        User authUser = userDAO.getAuthorizedUserByUserName(savedUser.getUsername(), password);
        Assert.assertEquals(savedUser, authUser);


        userDAO.updatePassword(savedUser, password2);
        authUser = userDAO.getAuthorizedUserByUserName(savedUser.getUsername(), password);
        Assert.assertNull(authUser);

        authUser = userDAO.getAuthorizedUserByUserName(savedUser.getUsername(), password2);
        Assert.assertEquals(savedUser, authUser);

        userDAO.deleteById(savedUser.getId());
        Assert.assertNull(userDAO.findById(savedUser.getId()));


        Assert.assertNull(userDAO.findByUsername("NOTANAME@NOTADOMAIN.COM"));




    }


}
