/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.model.EnergyPlan;
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
public class EnergyPlanDAOTest {

    @Autowired
    protected EnergyPlanDAO energyPlanDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.energyPlan where account_id=50");
    }



    @Test
    public void testDAO() {

        EnergyPlan energyPlan = new EnergyPlan();
        energyPlan.setAccountId(50l);
        energyPlan.setMeterReadDate(15);
        energyPlan.setMeterReadCycle(MeterReadCycle.MONTHLY);
        energyPlan.setPlanType(PlanType.TIER);
        energyPlan.setTouApplicableSaturday(true);
        energyPlan.setTouApplicableSunday(false);
        energyPlan.setTouApplicableHoliday(true);
        energyPlan.setHolidayScheduleId(1000l);
        energyPlan.setDemandPlanType(DemandPlanType.TIERED);
        energyPlan.setDemandUseActivePower(true);
        energyPlan.setDemandApplicableSaturday(true);
        energyPlan.setDemandApplicableSunday(false);
        energyPlan.setDemandApplicableHoliday(true);
        energyPlan.setDemandApplicableOffPeak(false);
        energyPlan.setDemandAverageTime(1000);
        energyPlan.setDescription("UNITTEST");
        energyPlan = energyPlanDAO.update(energyPlan);
        assertNotNull(energyPlan.getId());

        EnergyPlan loaded = energyPlanDAO.findById(energyPlan.getId());
        assertEquals(energyPlan, loaded);



        loaded.setMeterReadDate(25);
        loaded.setMeterReadCycle(MeterReadCycle.MONTHLY);
        loaded.setPlanType(PlanType.TOU);
        loaded.setTouApplicableSaturday(false);
        loaded.setTouApplicableSunday(true);
        loaded.setTouApplicableHoliday(false);
        loaded.setHolidayScheduleId(2000l);
        loaded.setDemandPlanType(DemandPlanType.TOU);
        loaded.setDemandUseActivePower(false);
        loaded.setDemandApplicableSaturday(false);
        loaded.setDemandApplicableSunday(true);
        loaded.setDemandApplicableHoliday(false);
        loaded.setDemandApplicableOffPeak(true);
        loaded.setDemandAverageTime(1000);
        loaded.setDescription("AAUNITTEST");


        energyPlanDAO.update(loaded);

        EnergyPlan updated = energyPlanDAO.findById(energyPlan.getId());
        assertEquals(loaded, updated);


        assertEquals(1, energyPlanDAO.findByAccount(50l).size());
        assertEquals(0, energyPlanDAO.findByAccount(-1l).size());


        energyPlanDAO.deleteById(energyPlan.getId());
        assertNull(energyPlanDAO.findById(energyPlan.getId()));



    }


}
