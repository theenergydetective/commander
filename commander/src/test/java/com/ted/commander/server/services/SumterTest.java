/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryQuery;
import com.ted.commander.server.Application;
import com.ted.commander.server.dao.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class SumterTest {


    @Autowired
    PlaybackService playbackService;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    AccountMemberDAO accountMemberDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    KeyService keyService;

    @Autowired
    VirtualECCDAO virtualECCDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    EnergyPlanService energyPlanService;

    @Autowired
    EnergyPlanDAO energyPlanDAO;

    @Autowired
    EnergyPlanSeasonDAO energyPlanSeasonDAO;

    @Autowired
    EnergyRateDAO energyRateDAO;

    @Autowired
    MTUDAO mtudao;


    private AccountMember createAccountMember(long userId, Account account, AccountRole accountRole){
        AccountMember accountMember = new AccountMember();
        accountMember.setUserId(userId);
        accountMember.setAccountId(account.getId());
        accountMember.setAccountRole(accountRole);
        accountMemberDAO.update(accountMember);
        return accountMember;
    }

    @Test
    public void createAccounts(){
        List<User> userList = userDAO.findSumter();

        for (User user :userList) {
            Long mtuId = Long.valueOf(user.getFirstName(), 16);


            Account account = new Account();
            account.setName(user.getFirstName() + " Sumter");
            account.setAccountState(AccountState.ENABLED);
            account.setCreateDate(System.currentTimeMillis());
            account.setActivationKey(keyService.generateKey());
            account = accountDAO.update(account);

            createAccountMember(user.getId(), account, AccountRole.OWNER);
            createAccountMember(1546l, account, AccountRole.ADMIN);
            createAccountMember(259l, account, AccountRole.ADMIN);


            EnergyPlan energyPlan = new EnergyPlan();
            energyPlan.setAccountId(account.getId());
            energyPlan = energyPlanDAO.update(energyPlan);

            System.err.print(energyPlan);

            EnergyPlanSeason energyPlanSeason = new EnergyPlanSeason(0, "Season1", 2, 15);
            energyPlanSeason = energyPlanSeasonDAO.update(energyPlan.getId(), energyPlanSeason);

            EnergyRate energyRate = new EnergyRate(energyPlan.getId(), energyPlanSeason.getId(), 0, TOUPeakType.OFF_PEAK, 0.110600000d);
            energyRateDAO.update(energyRate);


            MTU mtu = new MTU(mtuId, account.getId(), "MTU " + user.getFirstName(), MTUType.NET);
            mtudao.update(mtu);

            VirtualECC virtualECC = new VirtualECC(account.getId(), user.getFirstName(), VirtualECCType.NET_ONLY, "US/Eastern");
            virtualECC.setEnergyPlanId(energyPlan.getId());
            virtualECC =  virtualECCDAO.update(virtualECC);

            System.err.print(virtualECC);

            VirtualECCMTU virtualECCMTU = new VirtualECCMTU(virtualECC.getId(), mtu);
            virtualECCMTUDAO.update(virtualECCMTU);

        }




    }



}


