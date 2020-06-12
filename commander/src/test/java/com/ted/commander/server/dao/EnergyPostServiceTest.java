/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyRate;
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
public class EnergyPostServiceTest {

    @Autowired
    protected EnergyRateDAO energyRateDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.energyRate where energyPlan_id=1");
    }



    @Test
    public void testDAO() {

        EnergyRate energyPlan = new EnergyRate();
        energyPlan.setSeasonId(0);
        energyPlan.setEnergyPlanId(1l);
        energyPlan.setPeakType(TOUPeakType.SUPER_PEAK);
        energyPlan.setTierId(4);
        energyPlan.setRate(60.123456);
        energyPlan = energyRateDAO.update(energyPlan);
        EnergyRate loaded = energyRateDAO.findById(0, 1, TOUPeakType.SUPER_PEAK, 4);
        assertEquals(energyPlan, loaded);



        loaded.setRate(100.7654321);
        energyRateDAO.update(loaded);

        EnergyRate updated = energyRateDAO.findById(0, 1, TOUPeakType.SUPER_PEAK, 4);
        assertEquals(loaded, updated);


        assertEquals(1, energyRateDAO.findByEnergyPlan(1l).size());
        assertEquals(0, energyRateDAO.findByEnergyPlan(0l).size());



        energyRateDAO.deleteById(0, 1, TOUPeakType.SUPER_PEAK, 4);
        assertNull(energyRateDAO.findById(0, 1, TOUPeakType.SUPER_PEAK, 4));



    }


}
