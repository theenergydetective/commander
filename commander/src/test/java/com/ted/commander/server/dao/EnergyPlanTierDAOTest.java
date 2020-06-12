/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.model.EnergyPlanTier;
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
public class EnergyPlanTierDAOTest {

    @Autowired
    protected EnergyPlanTierDAO energyPlanTierDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.energyPlanTier where energyPlan_id=1");
    }



    @Test
    public void testDAO() {

        EnergyPlanTier energyPlanTier = new EnergyPlanTier();
        energyPlanTier.setId(0);
        energyPlanTier.setSeasonId(0);
        energyPlanTier.setEnergyPlanId(1l);
        energyPlanTier.setKwh(150l);
        energyPlanTier = energyPlanTierDAO.update(energyPlanTier);
        assertNotNull(energyPlanTier.getId());

        EnergyPlanTier loadedDTO = energyPlanTierDAO.findById(0,0, 1l);

        assertEquals(energyPlanTier, loadedDTO);
        loadedDTO.setKwh(100l);
        energyPlanTierDAO.update(loadedDTO);

        EnergyPlanTier updated = energyPlanTierDAO.findById(0,0, 1l);
        assertEquals(loadedDTO, updated);

        assertEquals(1, energyPlanTierDAO.findByEnergyPlan(1l).size());
        assertEquals(0, energyPlanTierDAO.findByEnergyPlan(2l).size());

        energyPlanTierDAO.deleteById(0,0,1l);
        assertNull(energyPlanTierDAO.findById(0, 0, 1l));



    }


}
