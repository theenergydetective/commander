/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.AccountState;
import com.ted.commander.common.model.Account;
import com.ted.commander.server.Application;
import com.ted.commander.server.config.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccountDAOTest {

    @Autowired
    protected AccountDAO accountDAO;

    @Autowired
    private DataSource commanderDataSource;



    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.account where activationKey like '%UNITTEST%' and id != 50");
    }

    @Test
    public void testDAO() {

        long time = System.currentTimeMillis();

        Account account = new Account();
        account.setName("UNIT TEST ACCOUNT");
        account.setPhoneNumber("5555551212");
        account.setCreateDate(time);
        account.setAccountState(AccountState.ENABLED);
        account.setActivationKey("UNITTEST");


        account = accountDAO.update(account);
        assertNotNull(account.getId());

        Account loadedDTO = accountDAO.findById(account.getId());
        assertEquals(account, loadedDTO);

        assertEquals("UNITTEST", account.getActivationKey());
        assertEquals("UNIT TEST ACCOUNT", account.getName());
        assertEquals(time, account.getCreateDate());
        assertEquals("5555551212", account.getPhoneNumber());

        loadedDTO.setAccountState(AccountState.DISABLED);
        loadedDTO.setPhoneNumber("12345");
        loadedDTO.setActivationKey("UNITTEST2");
        loadedDTO.setName("UNIT TEST");

        accountDAO.update(loadedDTO);

        Account updatedDTO = accountDAO.findById(account.getId());
        assertEquals(loadedDTO,  updatedDTO);

        Account keyDTO = accountDAO.findByActivationKey("UNITTEST2");
        assertEquals(loadedDTO, keyDTO);

        Account nullKeyDTO = accountDAO.findByActivationKey("UNITTEST22222222222");
        assertNull(nullKeyDTO);


        accountDAO.deleteById(account.getId());
        assertNull(accountDAO.findById(account.getId()));

    }


}
