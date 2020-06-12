/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.enums.ECCState;
import com.ted.commander.common.model.ECC;
import com.ted.commander.common.model.EnergyData;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.ECCDAO;
import com.ted.commander.server.dao.EnergyDataDAO;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.services.PlaybackService;
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

import java.util.List;

import static junit.framework.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class PlaybackServiceTest {


    private MockMvc mockMvc;

    @Autowired
    private PlaybackService playbackService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setUp() {
        //stubbing and verified behavior would "leak" from one test to another.
        //Mockito.reset(authServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
//        jdbcTemplate.update("delete from history_billingCycle where virtualECC_id=1134");
//        jdbcTemplate.update("delete from history_day where virtualECC_id=1134");
//        jdbcTemplate.update("delete from history_hour where virtualECC_id=1134");
//        jdbcTemplate.update("delete from history_minute where virtualECC_id=1134");
//        jdbcTemplate.update("delete from history_mtu_day where virtualECC_id=1134");
//        jdbcTemplate.update("delete from history_mtu_hour where virtualECC_id=1134");
//        jdbcTemplate.update("delete from history_mtu_billingCycle where virtualECC_id=1134");
//        jdbcTemplate.update("update virtualECCMTU set lastPost = 0 where virtualECC_id=1134");
    }

    @Autowired
    VirtualECCDAO virtualECCDAO;




    @Test
    public void testDuplicates() throws Exception {
        playbackService.findDuplicates();

        while(true) {
            Thread.sleep(30000);


        }

    }
}

