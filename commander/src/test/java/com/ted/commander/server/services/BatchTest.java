/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.common.enums.AccountState;
import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import com.ted.commander.common.model.history.HistoryMinute;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.*;
import com.ted.commander.server.model.TestValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class BatchTest {

    @Autowired
    protected EnergyPostService energyPostService;

    @Autowired
    HistoryMinuteDAO historyMinuteDAO;

    @Autowired
    MTUDAO mtudao;

    @Test
    public void testHistoryMinute(){
        long startEpoch = 1543726800L;

        for (int i=0; i < 200; i++){
            HistoryMinute historyMinute = new HistoryMinute();
            historyMinute.setVirtualECCId(1L);
            historyMinute.setStartEpoch(startEpoch);
            historyMinute.setMtuCount(1);
            historyMinute.setDemandPeak(0.0);
            historyMinute.setDow(1);



            historyMinute.setNet(300.0);

            historyMinuteDAO.findOne(historyMinute.getVirtualECCId(), historyMinute.getStartEpoch(), TimeZone.getTimeZone("US/Eastern"));
            historyMinute.setNet(400.0);
            historyMinuteDAO.save(historyMinute);

            startEpoch += 60L;
        }

        startEpoch = 1543726800L;

        for (int i=0; i < 200; i++){
            HistoryMinute historyMinute = new HistoryMinute();
            historyMinute.setVirtualECCId(1L);
            historyMinute.setStartEpoch(startEpoch);
            historyMinute.setMtuCount(1);
            historyMinute.setDemandPeak(0.0);
            historyMinute.setDow(1);



            historyMinute.setNet(300.0);

            historyMinuteDAO.findOne(historyMinute.getVirtualECCId(), historyMinute.getStartEpoch(), TimeZone.getTimeZone("US/Eastern"));
            historyMinute.setNet(600.0);
            historyMinuteDAO.save(historyMinute);

            startEpoch += 60L;
        }




    }

    @Test
    public void testBatchPost(){
        long startTime = System.currentTimeMillis();
        energyPostService.insertBatch(1543726800L, 10132122L, 1L, 0.0, 100, 120, 0, false);
        energyPostService.insertBatch(1543734000L, 10132122L, 1L, 10000.0, 100, 120, 0, false);
        energyPostService.insertBatch(1543741200L, 10132122L, 1L, 20000.0, 100, 120, 0, false);
    }

    @Autowired
    MaintenanceService maintenanceService;
    @Test
    public void testMtuLastPost(){
//        for (int i=0; i < 5001L; i++ ) {
//            mtudao.updateLastPost(1, 52, i * 1000);
//        }

        maintenanceService.deleteOldData();
    }


    @Autowired
    TestDAO testDAO;

    @Test
    public void testBatchRecovery(){
        testDAO.deleteAll();

        List<TestValue> breaklist = new ArrayList<>();
        breaklist.add(new TestValue(5, 0));
        testDAO.insert(breaklist);

        List<TestValue> list = new ArrayList<>();
        for (int i=0; i < 10; i++){
            list.add(new TestValue(i,i * 10));
        }

        testDAO.insert(list);
    }


    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Test
    public void testBatchLastUpdate(){
        VirtualECCMTU virtualECCMTU = new VirtualECCMTU();
        virtualECCMTU.setVirtualECCId(1L);
        virtualECCMTU.setMtuId(1L);
        virtualECCMTU.setAccountId(1L);
        for (int i=0; i < 6000; i++){
            virtualECCMTUDAO.updateLastPost(virtualECCMTU, i);
        }
    }

    @Autowired
    HistoryBillingCycleDAO historyBillingCycleDAO;

    @Autowired
    AccountDAO accountDAO;

    @Test
    public void testDisabled(){
        Account account = accountDAO.findById(164L);
        Assert.assertEquals(AccountState.DISABLED,account.getAccountState());
    }

    @Test
    public void testActive(){
        HistoryBillingCycle historyBillingCycle = historyBillingCycleDAO.findActiveBillingCycle(1134L, 1544138812L);
        System.err.println(historyBillingCycle);
        historyBillingCycleDAO.findActiveBillingCycle(1134L, 1544138812L);
        historyBillingCycleDAO.findActiveBillingCycle(1134L, 1544138812L);
        historyBillingCycleDAO.findActiveBillingCycle(1134L, 544138812L);
        historyBillingCycleDAO.findActiveBillingCycle(1134L, 544138812L);
        historyBillingCycleDAO.findActiveBillingCycle(1134L, 544138812L);


    }
}
