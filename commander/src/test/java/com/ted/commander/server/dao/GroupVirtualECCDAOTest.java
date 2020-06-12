/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.model.GroupVirtualECC;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class GroupVirtualECCDAOTest {

    @Autowired
    protected GroupVirtualECCDAO groupVirtualECCDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.group_virtualecc where group_id=1");
    }


    @Test
    public void testDAO() {

        GroupVirtualECC groupMember = new GroupVirtualECC();
        groupMember.setGroupId(1l);
        groupMember.setVirtualECCId(2l);
        groupMember = groupVirtualECCDAO.update(groupMember);


        GroupVirtualECC loaded = groupVirtualECCDAO.findById(groupMember.getId());
        assertEquals(groupMember, loaded);


        groupVirtualECCDAO.update(loaded);
        assertEquals(loaded, groupVirtualECCDAO.findById(groupMember.getId()));

        assertEquals(1, groupVirtualECCDAO.findByGroup(1l).size());
        assertEquals(0, groupVirtualECCDAO.findByGroup(0l).size());

        groupVirtualECCDAO.deleteById(groupMember.getId());
        assertNull(groupVirtualECCDAO.findById(groupMember.getId()));


    }

}
