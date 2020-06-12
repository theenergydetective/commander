/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.server.Application;
import com.ted.commander.server.dao.*;
import com.ted.commander.common.model.history.HistoryBillingCycleKey;
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
public class AdminServiceTest {

    @Autowired
    LastPostDAO lastPostDAO;

    @Autowired
    AdminService adminService;

    @Autowired
    EmailService emailService;

    @Autowired
    EnergyPostService energyPostService;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Test
    public void testMailPost(){
        Assert.assertTrue(lastPostDAO.IsActive());
        Assert.assertTrue(lastPostDAO.IsResumed());

    }


    @Test
    public void testPlayback(){

        //VirtualECC virtualECC = virtualECCDAO.findById(1134l);
        //EnergyPlan energyPlan = energyPlanService.loadEnergyPlan(virtualECC.getId());
        //List<VirtualECCMTU> virtualECCMTUList = virtualECCMTUDAO.findByVirtualECC(virtualECC.getId(), virtualECC.getAccountId());
        //energyPostService.playback(virtualECC, virtualECCMTUList, energyPlan, 0, 1454183725);

        HistoryBillingCycleKey historyBillingCycleKey = new HistoryBillingCycleKey();
        historyBillingCycleKey.setBillingCycleYear(2016);
        historyBillingCycleKey.setBillingCycleMonth(0);
        historyBillingCycleKey.setVirtualECCId(1l);

        //energyPostService.resetOldData(historyBillingCycleKey);
    }


    @Autowired
    AccountDAO accountDAO;



}

