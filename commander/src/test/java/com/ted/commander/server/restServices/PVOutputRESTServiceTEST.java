/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.ECCState;
import com.ted.commander.common.model.ECC;
import com.ted.commander.common.model.EnergyData;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.ECCDAO;
import com.ted.commander.server.dao.EnergyDataDAO;
import com.ted.commander.server.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static junit.framework.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class PVOutputRESTServiceTEST {

  private MockMvc mockMvc;


    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void testActivate() throws Exception {
        String testData = "<ted5000Activation><Gateway>22FFFA</Gateway><Unique>35238-543986fc-b</Unique></ted5000Activation>";

        MvcResult mvcResult=  mockMvc.perform(post("/api/pvactivate").contentType(MediaType.APPLICATION_XML).content(testData))
                .andExpect(status().isOk()).andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        System.out.println("--------------");
        System.out.println(result);

        assertTrue(result.contains("<ted500ActivationResponse>"));
        assertTrue(result.contains("</ted500ActivationResponse>"));
        assertTrue(result.contains("<PostServer>"));
        assertTrue(result.contains("<UseSSL>"));
        assertTrue(result.contains("<PostPort>"));
        assertTrue(result.contains("<PostURL>"));
        assertTrue(result.contains("<AuthToken>"));


    }



    @Test
    public void testSuccessfulPost() throws Exception {
        //207a0be249de63273c1b
        String testData = "<ted5000 GWID=\"22FFFA\" auth=\"207a0be249de63273c1b\" ver=\"649\"><COST mrd=\"1\" fixed=\"2.00\" min=\"3.00\"/><MTU ID=\"16FFFA\" type=\"0\" ver=\"641\"><cumulative timestamp=\"1424877840\" watts=\"18330261.0064\" rate=\"0.10000\" pf=\"95.2\" voltage=\"240.0\"/><cumulative timestamp=\"1424877900\" watts=\"18330161.0104\" rate=\"0.10000\" pf=\"95.2\" voltage=\"240.0\"/><cumulative timestamp=\"1424877960\" watts=\"18330061.0144\" rate=\"0.10000\" pf=\"95.2\" voltage=\"240.0\"/><cumulative timestamp=\"1424878020\" watts=\"18329961.0184\" rate=\"0.10000\" pf=\"95.2\" voltage=\"240.0\"/></MTU></ted5000>";


        MvcResult mvcResult=  mockMvc.perform(post("/api/pvpostData").contentType(MediaType.APPLICATION_XML).content(testData))
                .andExpect(status().isOk()).andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("OK 200: Added Status", result.trim());

    }

}

