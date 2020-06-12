/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.EnergyData;
import com.ted.commander.common.model.history.HistoryBillingCycleKey;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class BigNumberTest {
    @Autowired
    EnergyDataDAO energyDataDAO;

    @Autowired
    BigNumberService bigNumberService;


    @Test
    public void testBigNumberFix(){
        //bigNumberService.findBigNumbers(2039L, 1246329L, 1538412240L, 1538412240L, 2000.0);
        EnergyData fix = energyDataDAO.findById(1538628000L, 2039L, 1246329L);
        bigNumberService.fixOneBigNumber(fix);
    }





}

