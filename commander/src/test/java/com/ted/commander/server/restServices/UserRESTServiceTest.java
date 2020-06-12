/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;



import com.ted.commander.common.model.JoinRequest;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import com.ted.commander.server.dao.AccountDAO;
import com.ted.commander.server.dao.UserDAO;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserRESTServiceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRESTService userRESTServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource commanderDataSource;


    @Autowired
    UserDAO userDAO;

    @Autowired
    AccountDAO accountDAO;

    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        //Mockito.reset(authServiceMock);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.account_member where account_id in (select id from account where accountName='UNITTESTACCOUNTNAME')");
        jdbcTemplate.update("delete from commander.user where username='unittestuserrest@theenergydetective.com'");
        jdbcTemplate.update("delete from commander.account where accountName='UNITTESTACCOUNTNAME'");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUsername("unittestuserrest@theenergydetective.com");
        joinRequest.setPassword("wy0ggkw");
        joinRequest.setFirstName("MyFirstName");
        joinRequest.setLastName("UNITTESTLAST");
        joinRequest.setMiddleName("MyMiddleName");

        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        //TODO: Check content message
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        assertNotNull(userDAO.findByUsername("unittestuserrest@theenergydetective.com"));

    }


    @Test

    public void testActivateUser() throws Exception {
//
//        JoinRequest joinRequest = new JoinRequest();
//        joinRequest.setUsername("unittestuserrest@theenergydetective.com");
//        joinRequest.setPassword("wy0ggkw");
//        joinRequest.setFirstName("MyFirstName");
//        joinRequest.setLastName("UNITTESTLAST");
//        joinRequest.setMiddleName("MyMiddleName");
//
//
//        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(joinRequest)))
//                .andExpect(status().isOk());
//
//
//        AuthRequest authRequest = new AuthRequest();
//        authRequest.setUsername("unittestuserrest@theenergydetective.com");
//
//        authRequest.setPassword("badactivationKey");
//
//
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
//        jdbcTemplate.update("update commander.user set emailActivationKey='unittestactivationkey' where username='unittestuserrest@theenergydetective.com'");
//
//
//        mockMvc.perform(post("/api/user/activate").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest)))
//                .andExpect(status().isUnauthorized());
//
//        authRequest.setPassword("unittestactivationkey");
//
//
//        mockMvc.perform(post("/api/user/activate").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest)))
//                .andExpect(status().isOk());
//
//        //Make sure the second attempt isn't allowed
//        mockMvc.perform(post("/api/user/activate").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest)))
//                .andExpect(status().isUnauthorized());
//
//        assertNotNull(userDAO.findByUsername("unittestuserrest@theenergydetective.com"));


    }


}
