/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.AccountMember;
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
public class AccountMemberDAOTest {

    @Autowired
    protected AccountMemberDAO accountMemberDAO;

    @Autowired
    private DataSource commanderDataSource;


    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.account_member where user_id=200");
    }

    @Test
    public void testDAO() {
        AccountMember accountMember = new AccountMember();
        accountMember.setUserId(200l);
        accountMember.setAccountId(0l);
        accountMember.setAccountRole(AccountRole.READ_ONLY);


        accountMember = accountMemberDAO.update(accountMember);
        assertNotNull(accountMember.getId());

        AccountMember loadedDTO = accountMemberDAO.findById(accountMember.getId());
        assertEquals(accountMember, loadedDTO);

        loadedDTO.setAccountRole(AccountRole.EDIT_ECCS);

        accountMemberDAO.update(loadedDTO);

        AccountMember updatedDTO = accountMemberDAO.findById(accountMember.getId());
        assertEquals(loadedDTO,  updatedDTO);


        accountMemberDAO.deleteById(accountMember.getId());
        assertNull(accountMemberDAO.findById(accountMember.getId()));

    }


}
