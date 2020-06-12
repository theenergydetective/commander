/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.model.DemandChargeTier;
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
public class DemandChargeTierDAOTest {

    @Autowired
    protected DemandChargeTierDAO demandChargeTierDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.demandChargeTier where energyPlan_id=1");
    }



    @Test
    public void testDAO() {

        DemandChargeTier demandChargeTier = new DemandChargeTier();
        demandChargeTier.setId(2);
        demandChargeTier.setSeasonId(0);
        demandChargeTier.setEnergyPlanId(1l);
        demandChargeTier.setPeak(150.0);
        demandChargeTier.setRate(50.0);
        demandChargeTier = demandChargeTierDAO.update(demandChargeTier);


        DemandChargeTier loadedDTO = demandChargeTierDAO.findById(2,0,1l);

        assertEquals(demandChargeTier, loadedDTO);


        assertEquals((Integer) 2, loadedDTO.getId());
        assertEquals((Integer) 0, loadedDTO.getSeasonId());
        assertEquals((Long)1l, loadedDTO.getEnergyPlanId());
        assertEquals((Double) 150.0, loadedDTO.getPeak());
        assertEquals((Double)50.0, loadedDTO.getRate());

        loadedDTO.setPeak(100.0);
        loadedDTO.setRate(200.0);
        demandChargeTierDAO.update(loadedDTO);

        DemandChargeTier updated = demandChargeTierDAO.findById(2,0,1l);
        assertEquals((Double) 100.0, updated.getPeak());
        assertEquals((Double) 200.0, updated.getRate());

        assertEquals(1, demandChargeTierDAO.findByEnergyPlan(1l).size());
        assertEquals(0, demandChargeTierDAO.findByEnergyPlan(0l).size());


        demandChargeTierDAO.deleteById(2,0,1l);
        assertNull(demandChargeTierDAO.findById(2,0,1l));



    }


}
