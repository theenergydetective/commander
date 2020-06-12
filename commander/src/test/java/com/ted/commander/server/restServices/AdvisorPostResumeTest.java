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
import com.ted.commander.server.services.AdvisorService;
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
public class AdvisorPostResumeTest {

    @Autowired
    AdvisorService advisorService;

    @Test
    public void postDataTest(){
        advisorService.checkNoPost();
    }



    private static String template = "<ted5000 GWID=\"FFFF01\" auth=\"1234567890\" ver=\"625\">\n" +
            "    <COST mrd=\"1\" fixed=\"0.00\" min=\"0.00\"/>\n" +
            "    <MTU ID=\"000001\" type=\"0\" ver=\"600\">\n" +
            "        <cumulative timestamp=\"TIMESTAMP\" watts=\"WATTS\" rate=\"0.10000\" pf=\"100.0\" voltage=\"240.0\"/>\n" +
            "    </MTU>\n" +
            "</ted5000>";

}

