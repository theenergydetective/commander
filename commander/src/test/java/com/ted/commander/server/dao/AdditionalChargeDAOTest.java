/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.AdditionalChargeType;
import com.ted.commander.common.model.AdditionalCharge;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AdditionalChargeDAOTest {

    @Autowired
    protected AdditionalChargeDAO additionalChargeDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.additionalCharge where energyPlan_id=1");
    }



    @Test
    public void testDAO() {

        AdditionalCharge additionalCharge = new AdditionalCharge();
        additionalCharge.setAdditionalChargeType(AdditionalChargeType.MINIMUM);
        additionalCharge.setSeasonId(0);
        additionalCharge.setEnergyPlanId(1l);
        additionalCharge.setRate(50.0);
        additionalCharge = additionalChargeDAO.update(additionalCharge);
        AdditionalCharge loaded = additionalChargeDAO.findById(additionalCharge.getAdditionalChargeType(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId());

        assertEquals(additionalCharge, loaded);

        assertEquals(AdditionalChargeType.MINIMUM, loaded.getAdditionalChargeType());
        assertEquals((Integer)0, loaded.getSeasonId());
        assertEquals((Long) 1l, loaded.getEnergyPlanId());
        assertEquals((Double)50.0, loaded.getRate());

        loaded.setRate(100.0);
        additionalChargeDAO.update(loaded);

        AdditionalCharge updated = additionalChargeDAO.findById(additionalCharge.getAdditionalChargeType(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId());
        assertEquals((Double)100.0, updated.getRate());

        assertEquals(1, additionalChargeDAO.findByEnergyPlan(1l).size());
        assertEquals(1, additionalChargeDAO.findBySeasonAndEnergyPlan(0, 1l).size());
        assertEquals(0, additionalChargeDAO.findByEnergyPlan(2l).size());
        assertEquals(0, additionalChargeDAO.findBySeasonAndEnergyPlan(1, 1l).size());

        additionalChargeDAO.deleteById(additionalCharge.getAdditionalChargeType(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId());
        assertNull(additionalChargeDAO.findById(additionalCharge.getAdditionalChargeType(), additionalCharge.getSeasonId(), additionalCharge.getEnergyPlanId()));



    }


}
