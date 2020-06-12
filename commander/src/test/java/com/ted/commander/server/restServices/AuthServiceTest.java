/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class AuthServiceTest {

    private MockMvc mockMvc;

    @Autowired
    private AuthRESTService authServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setUp() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        //Mockito.reset(authServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.account_member where account_id in (select id from account where accountName='UNITTESTACCOUNTNAME')");
        //jdbcTemplate.update("delete from commander.user where username='unittest@theenergydetective.com'");
        jdbcTemplate.update("delete from commander.account where accountName='UNITTESTACCOUNTNAME'");
    }


//    @Test
//    public void testAuth() throws Exception {
//        mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"utadmin\", \"password\":\"utadmin\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.sessionUser.username", is("utadmin")))
//        ;
//    }
//
//    @Test
//    public void testFailedAuth() throws Exception {
//        mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"admin\", \"password\":\"yoloyoloyolo\"}"))
//                .andExpect(status().isUnauthorized());
//
//
//    }
//
//    @Test
//    public void testLogout() throws Exception {
//        mockMvc.perform(delete("/api/auth"))
//                .andExpect(status().isOk())
//        ;
//
//        mockMvc.perform(delete("/api/auth").header(RESTHeaders.AG_USERID.name(), "123456"))
//                .andExpect(status().isOk())
//        ;
//
//    }

}
