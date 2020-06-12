/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.ECCState;
import com.ted.commander.common.model.ECC;
import com.ted.commander.common.model.EnergyData;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static junit.framework.Assert.*;

/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class EnergyPostRESTServiceTEST {

    @Autowired
    ECCDAO eccDAO;

    @Autowired
    EnergyDataDAO energyDataDAO;


    private MockMvc mockMvc;

    @Autowired
    private AuthRESTService authServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setUp() {
        //stubbing and verified behavior would "leak" from one test to another.
        //Mockito.reset(authServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.ecc where account_id=50");
        jdbcTemplate.update("delete from commander.mtu where account_id=50");
        //jdbcTemplate.update("delete from commander.energydata where account_id=50");
//        //jdbcTemplate.update("delete from commander.user where username='unittest@theenergydetective.com'");
//        jdbcTemplate.update("delete from commander.account where accountName='UNITTESTACCOUNTNAME'");
    }  //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,


    @Test
    public void testActivate() throws Exception {
        String testData = "<ted5000Activation><Gateway>210000</Gateway><Unique>qiA1Ki1yXF</Unique></ted5000Activation>";

        MvcResult mvcResult=  mockMvc.perform(post("/api/activate").contentType(MediaType.APPLICATION_XML).content(testData))
                .andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();

        assertTrue(result.contains("<ted5000ActivationResponse>"));
        assertTrue(result.contains("</ted5000ActivationResponse>"));
        assertTrue(result.contains("<PostServer>"));
        assertTrue(result.contains("<UseSSL>"));
        assertTrue(result.contains("<PostPort>"));
        assertTrue(result.contains("<PostURL>"));
        assertTrue(result.contains("<AuthToken>"));

        assertNotNull(eccDAO.findById(50l, 2162688l));

        //Check the reactivation
        mockMvc.perform(post("/api/activate").contentType(MediaType.APPLICATION_XML).content(testData))
                .andExpect(status().isOk()).andReturn();

        testData =testData.replace("210000", "210001").replace("qiA1Ki1yXF", "ABCDE");
        mockMvc.perform(post("/api/activate").contentType(MediaType.APPLICATION_XML).content(testData))
                .andExpect(status().isForbidden());
    }



    @Test
    public void testSuccessfulPost() throws Exception {
        //Make sure we have an ecc here.
        ECC ecc = new ECC();
        ecc.setUserAccountId(50l);
        ecc.setId(16776961l);
        ecc.setSecurityKey("1234567890");
        ecc.setState(ECCState.ACTIVATED);
        eccDAO.update(ecc);

        String testData1 = TestUtil.getFileAsString("ValidPost1.xml");
        String testData2 = TestUtil.getFileAsString("ValidPost2.xml");

        MvcResult mvcResult=  mockMvc.perform(post("/api/postData").contentType(MediaType.APPLICATION_XML).content(testData1))
                .andExpect(status().isOk()).andReturn();

        mvcResult=  mockMvc.perform(post("/api/postData").contentType(MediaType.APPLICATION_XML).content(testData2))
                .andExpect(status().isOk()).andReturn();


        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("SUCCESS", result);

        Thread.sleep(8000);

        //spot check values
        EnergyData energyData;

        energyData = energyDataDAO.findById(1401111060l, 50l, 296l);
        assertNotNull(energyData);
        assertEquals(1000000.0000, energyData.getEnergy());
        assertEquals(0.0, energyData.getEnergyDifference());

        energyData = energyDataDAO.findById(1401111120l, 50l, 296l);
        assertEquals(2000000.0000, energyData.getEnergy());
        assertEquals(1000000.0000, energyData.getEnergyDifference());

    }

}

