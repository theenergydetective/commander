/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.GroupRole;
import com.ted.commander.common.model.GroupPermission;
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
public class GroupPermissionDAOTest {

    @Autowired
    protected GroupPermissionDAO groupPermissionDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.group_permission where group_id=1");
    }


    @Test
    public void testDAO() {

        GroupPermission groupPermission = new GroupPermission();
        groupPermission.setGroupId(1l);
        groupPermission.setUserId(2l);
        groupPermission.setRole(GroupRole.EDIT);
        groupPermissionDAO.update(groupPermission);

        GroupPermission loaded = groupPermissionDAO.findById(groupPermission.getId());
        assertEquals(groupPermission, loaded);
        assertEquals((Long)1l, groupPermission.getGroupId());
        assertEquals((Long)2l, groupPermission.getUserId());
        assertEquals(GroupRole.EDIT, groupPermission.getRole());

        loaded.setRole(GroupRole.OWNER);
        groupPermissionDAO.update(loaded);

        assertEquals(loaded, groupPermissionDAO.findById(groupPermission.getId()));

        assertEquals(1l, groupPermissionDAO.findByGroup(1l).size());
        assertEquals(0l, groupPermissionDAO.findByGroup(0l).size());

        assertEquals(1l, groupPermissionDAO.findByUser(2l).size());
        assertEquals(0l, groupPermissionDAO.findByUser(0l).size());

        groupPermissionDAO.deleteById(groupPermission.getId());
        assertNull(groupPermissionDAO.findById(groupPermission.getId()));


    }

}
