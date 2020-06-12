/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlanTOU;
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
public class EnergyPlanTOUDAOTest {

    @Autowired
    protected EnergyPlanTOUDAO energyPlanTOUDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.energyPlanTOU where energyPlan_id=1");
    }



    @Test
    public void testDAO() {

        EnergyPlanTOU energyPlanTOU = new EnergyPlanTOU();
        energyPlanTOU.setSeasonId(0);
        energyPlanTOU.setEnergyPlanId(1l);
        energyPlanTOU.setPeakType(TOUPeakType.OFF_PEAK);
        energyPlanTOU.setTouStartHour(1);
        energyPlanTOU.setTouStartMinute(2);
        energyPlanTOU.setTouEndHour(3);
        energyPlanTOU.setTouEndMinute(4);
        energyPlanTOU.setMorningTou(true);


        energyPlanTOUDAO.update(energyPlanTOU);


        EnergyPlanTOU loadedDTO = energyPlanTOUDAO.findById(0, 1l, TOUPeakType.OFF_PEAK, true);

        assertEquals(energyPlanTOU, loadedDTO);

        loadedDTO.setTouEndHour(100);
        energyPlanTOUDAO.update(loadedDTO);


        EnergyPlanTOU updated = energyPlanTOUDAO.findById(0, 1l, TOUPeakType.OFF_PEAK, true);
        assertEquals(loadedDTO, updated);

        assertEquals(1, energyPlanTOUDAO.findByEnergyPlan(1l).size());
        assertEquals(0, energyPlanTOUDAO.findByEnergyPlan(2l).size());

        energyPlanTOUDAO.deleteById(0, 1l, TOUPeakType.OFF_PEAK, true);
        assertNull(energyPlanTOUDAO.findById(0, 1l, TOUPeakType.OFF_PEAK, true));



    }


}

