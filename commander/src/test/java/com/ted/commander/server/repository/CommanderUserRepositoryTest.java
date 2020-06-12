/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.repository;


import com.ted.commander.server.Application;
import com.ted.commander.server.model.CommanderUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import static junit.framework.Assert.*;


/**
 * Created by pete on 12/16/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CommanderUserRepositoryTest {

    @Autowired
    DataSource commanderDataSource;

    @Autowired
    CommanderUserRepository commanderUserRepository;


    @Test
    public void testByUsername() {
        //Set up test data. Clean up old data.
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.execute("delete from user where username='testByUsername'");
        jdbcTemplate.execute("insert into user (username, password, state, firstName, lastName, joinDate) values ('testByUsername', 'testByUsernamePassword', 1, 'test','test', 123456789)");

        CommanderUser user = commanderUserRepository.findByEmailAndPassword("testByUsername", "testByUsernamePassword");
        assertEquals("testByUsername", user.getUsername());
//        assertEquals(UserRole.USER, user.getUserRole());
//        assertEquals(UserState.DISABLED, user.getUserState());
//        assertEquals(123456789l, user.getJoinDate());
        assertTrue(user.getId() != 0);

        jdbcTemplate.execute("delete from user where username='testByUsername'");
        assertNull(commanderUserRepository.findByEmailAndPassword("testByUsername", "testByUsernamePassword"));

    }
}
