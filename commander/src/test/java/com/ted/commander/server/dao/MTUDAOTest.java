/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;



import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.MTU;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class MTUDAOTest {

    @Autowired
    protected MTUDAO mtuDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.mtu where account_id=50");
    }



    @Test
    public void testDAO() {

        MTU mtu = new MTU();
        mtu.setId(1l);
        mtu.setAccountId(50l);
        mtu.setMtuType(MTUType.GENERATION);
        mtu.setDescription("PJAUNITTEST");
        mtu = mtuDAO.update(mtu);

        assertNotNull(mtu.getId());
        MTU loaded = mtuDAO.findById(1l, 50l);
        assertEquals(mtu, loaded);


        loaded.setDescription("SPJAUNITTEST");
        loaded.setMtuType(MTUType.LOAD);
        mtuDAO.update(loaded);

        MTU updated = mtuDAO.findById(1l,  50l);
        assertEquals(loaded, updated);



        mtuDAO.deleteById(1l, 50l);
        assertNull(mtuDAO.findById(1l,  50l));



    }


}
