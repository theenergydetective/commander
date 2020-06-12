/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlanTOULevel;
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
public class EnergyPlanTOULevelDAOTest {

    @Autowired
    protected EnergyPlanTOULevelDAO energyPlanTOULevelDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.touLevel where energyPlan_id=1");
    }



    @Test
    public void testDAO() {

        EnergyPlanTOULevel energyPlanTOULevel = new EnergyPlanTOULevel();
        energyPlanTOULevel.setEnergyPlanId(1l);
        energyPlanTOULevel.setTouLevelName("UNITTEST");
        energyPlanTOULevel.setPeakType(TOUPeakType.OFF_PEAK);
        energyPlanTOULevel = energyPlanTOULevelDAO.update(energyPlanTOULevel);

        EnergyPlanTOULevel loaded = energyPlanTOULevelDAO.findById(1l, TOUPeakType.OFF_PEAK);
        assertEquals(energyPlanTOULevel, loaded);
        energyPlanTOULevel.setTouLevelName("AUNITTEST");
        energyPlanTOULevel.setPeakType(TOUPeakType.SUPER_PEAK);
        energyPlanTOULevelDAO.update(loaded);

        EnergyPlanTOULevel updated = energyPlanTOULevelDAO.findById(1l, TOUPeakType.OFF_PEAK);
        assertEquals(loaded, updated);


        assertEquals(1, energyPlanTOULevelDAO.findByEnergyPlan(1l).size());
        assertEquals(0, energyPlanTOULevelDAO.findByEnergyPlan(-1l).size());

        energyPlanTOULevelDAO.deleteById(1l, TOUPeakType.OFF_PEAK);
        assertNull(energyPlanTOULevelDAO.findById(1l, TOUPeakType.OFF_PEAK));



    }


}
