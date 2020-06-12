/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.DemandChargeTOU;
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
public class DemandChargeTOUDAOTest {

    @Autowired
    protected DemandChargeTOUDAO demandChargeTOUDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.demandChargeTOU where energyPlan_Id=1");
    }



    @Test
    public void testDAO() {

        DemandChargeTOU demandChargeTOU = new DemandChargeTOU();
        demandChargeTOU.setPeakType(TOUPeakType.SUPER_PEAK);
        demandChargeTOU.setSeasonId(0);
        demandChargeTOU.setEnergyPlanId(1l);
        demandChargeTOU.setRate(50.0);
        demandChargeTOU = demandChargeTOUDAO.update(demandChargeTOU);


        DemandChargeTOU loadedDTO = demandChargeTOUDAO.findById(TOUPeakType.SUPER_PEAK, 0, 1l);

        assertEquals(demandChargeTOU, loadedDTO);


        assertEquals(TOUPeakType.SUPER_PEAK, loadedDTO.getPeakType());
        assertEquals((Integer)0, loadedDTO.getSeasonId());
        assertEquals((Long) 1l, loadedDTO.getEnergyPlanId());

        assertEquals((Double)50.0, loadedDTO.getRate());

        loadedDTO.setRate(200.0);
        demandChargeTOUDAO.update(loadedDTO);

        DemandChargeTOU updated = demandChargeTOUDAO.findById(TOUPeakType.SUPER_PEAK, 0, 1l);
        assertEquals((Double) 200.0, updated.getRate());

        assertEquals(1, demandChargeTOUDAO.findByEnergyPlan(1l).size());
        assertEquals(0, demandChargeTOUDAO.findByEnergyPlan(0l).size());

        demandChargeTOUDAO.deleteById(TOUPeakType.SUPER_PEAK, 0, 1l);
        assertNull(demandChargeTOUDAO.findById(TOUPeakType.SUPER_PEAK, 0, 1l));



    }


}
