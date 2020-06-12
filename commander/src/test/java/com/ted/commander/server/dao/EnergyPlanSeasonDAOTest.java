/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.model.EnergyPlanSeason;
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

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class EnergyPlanSeasonDAOTest {

    @Autowired
    protected EnergyPlanSeasonDAO energyPlanSeasonDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.energyPlanSeason where energyPlan_id=0");
    }



    @Test
    public void testDAO() {

        EnergyPlanSeason energyPlanSeason = new EnergyPlanSeason();
        energyPlanSeason.setId(1);

        energyPlanSeason.setSeasonName("Unit Test");
        energyPlanSeason.setSeasonMonth(11);
        energyPlanSeason.setSeasonDayOfMonth(22);

        energyPlanSeason = energyPlanSeasonDAO.update(0l, energyPlanSeason);
        assertNotNull(energyPlanSeason.getId());

        List<EnergyPlanSeason> loadedList = energyPlanSeasonDAO.findByPlan(0l);
        EnergyPlanSeason loaded = loadedList.get(0);
        assertEquals(energyPlanSeason, loaded);


        loaded.setSeasonName("AAAUnit Test");
        loaded.setSeasonMonth(21);
        loaded.setSeasonDayOfMonth(32);
        energyPlanSeasonDAO.update(0l, loaded);


        List<EnergyPlanSeason> updatedList = energyPlanSeasonDAO.findByPlan(0l);
        EnergyPlanSeason updated = updatedList.get(0);
        assertEquals(loaded, updated);


        assertEquals(1, energyPlanSeasonDAO.findByPlan(0l).size());
        assertEquals(0, energyPlanSeasonDAO.findByPlan(-1l).size());


        energyPlanSeasonDAO.deleteByPlan(0l);
        assertEquals(0, energyPlanSeasonDAO.findByPlan(0l).size());



    }


}
