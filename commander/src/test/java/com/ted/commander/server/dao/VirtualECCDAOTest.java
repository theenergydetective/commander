/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.User;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class VirtualECCDAOTest {

    @Autowired
    protected VirtualECCDAO virtualECCDAO;

    @Autowired
    protected UserDAO userDAO;


    @Autowired
    private DataSource commanderDataSource;


    User unitTestUser = null;

    @Before
    public void setup(){
        if (unitTestUser==null) unitTestUser = userDAO.findByUsername("utadmin");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.virtualECC where account_id=50");
    }



    @Test
    public void testDAO() {

        VirtualECC virtualECC = new VirtualECC();
        virtualECC.setAccountId(50l);
        virtualECC.setName("UNITTESTECC");
        virtualECC.setTimezone("US/Eastern");
        virtualECC.setSystemType(VirtualECCType.NET_ONLY);
        virtualECC = virtualECCDAO.update(virtualECC);
        assertNotNull(virtualECC.getId());

        VirtualECC loadedVECC = virtualECCDAO.findById(virtualECC.getId());

        assertEquals(virtualECC, loadedVECC);

        assertEquals((Long)50l, loadedVECC.getAccountId());
        assertEquals("UNITTESTECC", loadedVECC.getName());
        assertEquals("US/Eastern", loadedVECC.getTimezone());

        loadedVECC.setTimezone("AUS/Eastern");
        loadedVECC.setName("AUNITTESTECC");

        virtualECCDAO.update(loadedVECC);

        VirtualECC updated = virtualECCDAO.findById(virtualECC.getId());



        assertEquals("AUNITTESTECC", updated.getName());
        assertEquals("AUS/Eastern", updated.getTimezone());


        assertEquals(1, virtualECCDAO.findByAccount(50l).size());
        assertEquals(0, virtualECCDAO.findByAccount(0l).size());




    }


}
