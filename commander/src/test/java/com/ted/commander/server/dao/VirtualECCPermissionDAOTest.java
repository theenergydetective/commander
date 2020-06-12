/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.enums.VirtualECCRole;
import com.ted.commander.common.model.VirtualECCPermission;
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
public class VirtualECCPermissionDAOTest {

    @Autowired
    protected VirtualECCPermissionDAO virtualECCPermissionDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.virtualecc_permission where virtualecc_id=1");
    }


    @Test
    public void testDAO() {

        VirtualECCPermission groupPermission = new VirtualECCPermission();
        groupPermission.setVirtualECCId(1l);
        groupPermission.setUserId(2l);
        groupPermission.setRole(VirtualECCRole.EDIT);
        virtualECCPermissionDAO.update(groupPermission);

        VirtualECCPermission loaded = virtualECCPermissionDAO.findById(groupPermission.getId());
        assertEquals(groupPermission, loaded);
        assertEquals((Long)1l, groupPermission.getVirtualECCId());
        assertEquals((Long)2l, groupPermission.getUserId());
        assertEquals(VirtualECCRole.EDIT, groupPermission.getRole());

        loaded.setRole(VirtualECCRole.OWNER);
        virtualECCPermissionDAO.update(loaded);

        assertEquals(loaded, virtualECCPermissionDAO.findById(groupPermission.getId()));

        assertEquals(1l, virtualECCPermissionDAO.findByVirtualECC(1l).size());
        assertEquals(0l, virtualECCPermissionDAO.findByVirtualECC(0l).size());

        assertEquals(1l, virtualECCPermissionDAO.findByUser(2l).size());
        assertEquals(0l, virtualECCPermissionDAO.findByUser(0l).size());

        virtualECCPermissionDAO.deleteById(groupPermission.getId());
        assertNull(virtualECCPermissionDAO.findById(groupPermission.getId()));


    }

}
