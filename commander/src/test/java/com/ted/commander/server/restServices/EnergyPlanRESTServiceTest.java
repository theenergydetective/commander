/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import com.ted.commander.server.dao.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

/**
 * Created by pete on 6/17/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class EnergyPlanRESTServiceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRESTService userRESTServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource commanderDataSource;


    @Autowired
    UserDAO userDAO;

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    AccountMemberDAO accountMemberDAO;




    @Autowired
    EnergyPlanSeasonDAO energyPlanSeasonDAO;

    @Autowired
    EnergyPlanTOULevelDAO energyPlanTOULevelDAO;

    @Autowired
    EnergyPlanTierDAO energyPlanTierDAO;

    @Autowired
    AdditionalChargeDAO additionalChargeDAO;

    @Autowired
    DemandChargeTierDAO demandChargeTierDAO;

    @Autowired
    DemandChargeTOUDAO demandChargeTOUDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;


    @Autowired
    EnergyPlanTOUDAO energyPlanTOUDAO;

    @Autowired
    EnergyRateDAO energyRateDAO;


    @Before
    public void setUp() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.energyPlan where account_id = 50");
        jdbcTemplate.update("delete from commander.energyPlan where id = 1");
        jdbcTemplate.update("delete from commander.energyPlanSeason where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.energyPlanTier where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.energyPlanTOU where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.energyRate where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.additionalCharge where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.touLevel where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.demandChargeTier where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.demandChargeTOU where energyPlan_id = 1");
        jdbcTemplate.update("delete from commander.virtualECC where energyPlan_id = 1");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
//
//    @Test
//    public void testGet() throws Exception {
//        AccountMember accountMember = new AccountMember();
//        accountMember.setAccountId(50l);
//        accountMember.setAccountRole(AccountRole.ADMIN);
//        accountMember.setUserId(200l);
//        accountMemberDAO.update(accountMember);
//
//        EnergyPlan energyPlan = new EnergyPlan();
//        energyPlan.setAccountId(50l);
//        long id = energyPlanDAO.update(energyPlan).getId();
//
//
//        MvcResult authResult = mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"utadmin\", \"password\":\"utadmin\"}")).andExpect(status().isOk()).andReturn();
//
//        ObjectMapper mapper = new ObjectMapper();
//        SessionToken sessionToken = mapper.readValue(authResult.getResponse().getContentAsString(), SessionToken.class);
//
//        MvcResult mvcResult = mockMvc.perform(get("/api/energyPlan/" + id)
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//        ).andExpect(status().isOk()).andReturn();
//
//        System.out.println(mvcResult.getResponse().getContentAsString());
//    }
//
//
//    @Test
//    public void testDelete() throws Exception {
//        AccountMember accountMember = new AccountMember();
//        accountMember.setAccountId(50l);
//        accountMember.setAccountRole(AccountRole.ADMIN);
//        accountMember.setUserId(200l);
//        accountMemberDAO.update(accountMember);
//
//        EnergyPlan energyPlan = new EnergyPlan();
//        energyPlan.setDescription("Unit Test");
//        energyPlan.setAccountId(50l);
//        energyPlan = energyPlanDAO.update(energyPlan);
//
//
//        MvcResult authResult = mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"utadmin\", \"password\":\"utadmin\"}")).andExpect(status().isOk()).andReturn();
//
//        ObjectMapper mapper = new ObjectMapper();
//        SessionToken sessionToken = mapper.readValue(authResult.getResponse().getContentAsString(), SessionToken.class);
//
//        MvcResult mvcResult = mockMvc.perform(delete("/api/energyPlan/" + energyPlan.getId() + "")
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//        ).andExpect(status().isOk()).andReturn();
//
//        System.out.println(mvcResult.getResponse().getContentAsString());
//    }
//
//

}
