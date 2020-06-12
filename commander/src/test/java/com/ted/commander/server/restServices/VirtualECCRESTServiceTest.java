/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import com.ted.commander.server.dao.*;

import org.codehaus.jackson.map.ObjectMapper;
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
public class VirtualECCRESTServiceTest {

    @Autowired
    AccountMemberDAO accountMemberDAO;
    @Autowired
    VirtualECCDAO virtualECCDAO;
    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;
    private MockMvc mockMvc;
    @Autowired
    private UserRESTService userRESTServiceMock;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DataSource commanderDataSource;

    ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void setUp() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        //Mockito.reset(authServiceMock);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        //Clean up test data
        jdbcTemplate.update("delete from commander.virtualECCMTU where virtualECC_id in (select id from commander.virtualECC where account_id=50)");
        jdbcTemplate.update("delete from commander.virtualECC where account_id=50");

        //Clean up existing account data (from other tests)
        jdbcTemplate.update("delete from commander.account_member where account_id=50");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
//
//    @Test
//    public void testCreateVirtualECC() throws Exception {
//
//        MvcResult authResult = mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"utadmin\", \"password\":\"utadmin\"}")).andExpect(status().isOk()).andReturn();
//
//        ObjectMapper mapper = new ObjectMapper();
//        SessionToken sessionToken = mapper.readValue(authResult.getResponse().getContentAsString(), SessionToken.class);
//
//        AccountMember accountMember = new AccountMember();
//        accountMember.setAccountId(50l);
//        accountMember.setUserId(200l);
//        accountMember.setAccountRole(AccountRole.ADMIN);
//        accountMemberDAO.update(accountMember);
//
//        VirtualECC virtualECC = new VirtualECC();
//        virtualECC.setAccountId(50l);
//        virtualECC.setEnergyPlanId(0l);
//        virtualECC.setName("UNIT TEST VIRTUAL ECC");
//
//
//
//        MvcResult mvcResult = mockMvc.perform(post("/api/virtualECC")
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(virtualECC))
//        ).andExpect(status().isOk()).andReturn();
//
//
//        RESTPostResponse restPostResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), RESTPostResponse.class);
//
//        //Get the virtualECC Back
//
//        mvcResult = mockMvc.perform(get("/api/virtualECC/" + restPostResponse.getId())
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//        ).andExpect(status().isOk()).andReturn();
//
//        VirtualECC getObject = mapper.readValue(mvcResult.getResponse().getContentAsString(), VirtualECC.class);
//
//        assertEquals(virtualECCDAO.findById(restPostResponse.getId()), getObject);
//        assertEquals("UNIT TEST VIRTUAL ECC", getObject.getName());
//
//        //Test the update
//        getObject.setCity("TESTVILLE");
//        mockMvc.perform(put("/api/virtualECC")
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(getObject))
//        ).andExpect(status().isOk()).andReturn();
//
//        mvcResult = mockMvc.perform(get("/api/virtualECC/" + restPostResponse.getId())
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//        ).andExpect(status().isOk()).andReturn();
//
//        VirtualECC updated = mapper.readValue(mvcResult.getResponse().getContentAsString(), VirtualECC.class);
//        assertEquals(getObject, updated);
//
//
//        //test the delete
//        mvcResult = mockMvc.perform(delete("/api/virtualECC/" + restPostResponse.getId())
//                        .header(RESTHeaders.AG_USERID.name(), sessionToken.getTokenId())
//        ).andExpect(status().isOk()).andReturn();
//
//        assertNull(virtualECCDAO.findById(restPostResponse.getId()));
//    }
//
//
//    @Test
//    public void testNoSession() throws Exception {
//
//        VirtualECC virtualECC = new VirtualECC();
//        virtualECC.setAccountId(50l);
//        virtualECC.setEnergyPlanId(0l);
//        virtualECC.setName("UNIT TEST VIRTUAL ECC");
//
//
//        mockMvc.perform(post("/api/virtualECC")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(virtualECC))
//        ).andExpect(status().isUnauthorized()).andReturn();
//
//
//        mockMvc.perform(put("/api/virtualECC")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(virtualECC))
//        ).andExpect(status().isUnauthorized()).andReturn();
//
//        mockMvc.perform(delete("/api/virtualECC/123")).andExpect(status().isUnauthorized());
//        mockMvc.perform(get("/api/virtualECC/123")).andExpect(status().isUnauthorized());
//        mockMvc.perform(get("/api/account/50/virtualECCs")).andExpect(status().isUnauthorized());
//
//    }
}
