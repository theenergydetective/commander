/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import com.ted.commander.server.model.ActivationConfigPost;
import com.ted.commander.server.util.TestUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

import java.util.Date;
import java.util.logging.Level;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class TedProServiceTest {

    private MockMvc mockMvc;


    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() {


        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        //Mockito.reset(authServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void testPostSystem() throws Exception {
        String testData1 = TestUtil.getFileAsString("SystemSettings.xml");

        ActivationConfigPost activationConfigPost = new ActivationConfigPost();
        activationConfigPost.setXmlPayload(testData1);
        activationConfigPost.setAccount(52l);
        activationConfigPost.setUsername("pete@petecode.com");
        activationConfigPost.setPassword("wy0ggkw");
        activationConfigPost.setEnergyPlanId(0l);
        activationConfigPost.setDescription("Location: " + (new Date()));

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/tedPro/registerSystem").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activationConfigPost)))
                .andExpect(status().isOk());

        ;
    }

}
