/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.MTU;
import com.ted.commander.common.model.VirtualECCMTU;
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
public class VirtualECCMTUDAOTest {

    @Autowired
    protected VirtualECCMTUDAO virtualECCMTUDAO;


    @Autowired
    private DataSource commanderDataSource;

    @Autowired
    MTUDAO mtuDAO;


    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.mtu where account_id=50");
        jdbcTemplate.update("delete from commander.virtualECCMTU where account_id=50");
    }



    @Test
    public void testDAO() {
        for (int i=0; i < 5; i++)
        {
            MTU mtu = new MTU();
            mtu.setId((long) (i + 1));
            mtu.setAccountId(50l);
            mtu.setMtuType(MTUType.STAND_ALONE);
            mtu.setDescription("UNIT TEST MTU" + i);
            mtuDAO.update(mtu);
        }


        VirtualECCMTU virtualECCMTU = new VirtualECCMTU();
        virtualECCMTU.setVirtualECCId(1l);
        virtualECCMTU.setMtuId(2l);
        virtualECCMTU.setAccountId(50l);
        virtualECCMTU.setMtuType(MTUType.GENERATION);
        virtualECCMTU.setMtuDescription("UNIT TEST");
        virtualECCMTU.setPowerMultiplier(5.0);
        virtualECCMTU.setVoltageMultiplier(10.0);
        virtualECCMTU = virtualECCMTUDAO.update(virtualECCMTU);


        VirtualECCMTU loadedVECC = virtualECCMTUDAO.findByMTUId(1l, 2l, 50l);
        assertEquals(virtualECCMTU, loadedVECC);

        assertEquals((Long)1l, loadedVECC.getVirtualECCId());
        assertEquals((Long)2l, loadedVECC.getMtuId());
        assertEquals(MTUType.GENERATION, loadedVECC.getMtuType());
        assertEquals("UNIT TEST", loadedVECC.getMtuDescription());
        assertEquals((Double)5.0, loadedVECC.getPowerMultiplier());
        assertEquals((Double)10.0, loadedVECC.getVoltageMultiplier());

        loadedVECC.setMtuType(MTUType.STAND_ALONE);

        virtualECCMTUDAO.update(loadedVECC);
        VirtualECCMTU updated = virtualECCMTUDAO.findByMTUId(1l, 2l, 50l);
        assertEquals(loadedVECC, updated);


        assertEquals(1, virtualECCMTUDAO.findByVirtualECC(1l, 50l).size());

        virtualECCMTUDAO.deleteById(1l, 2l, 50l);
        assertNull(virtualECCMTUDAO.findByMTUId(1l,2l, 50l));

        assertEquals(0, virtualECCMTUDAO.findByVirtualECC(1l, 50l).size());



    }


}
