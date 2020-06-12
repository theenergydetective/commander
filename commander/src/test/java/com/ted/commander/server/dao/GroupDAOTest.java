/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.model.Group;
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
public class GroupDAOTest {

    @Autowired
    protected GroupDAO groupDAO;

    @Autowired
    private DataSource commanderDataSource;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.update("delete from commander.group where account_id=1");
    }


    @Test
    public void testDAO() {

        Group group = new Group();
        group.setAccountId(1l);
        group.setDescription("UNITTEST");
        group = groupDAO.update(group);
        groupDAO.update(group);

        Group loaded = groupDAO.findById(group.getId());
        assertEquals(group, loaded);

        loaded.setDescription("AUNITTEST");
        groupDAO.update(loaded);

        assertEquals(loaded, groupDAO.findById(group.getId()));

        assertEquals(1l, groupDAO.findByOwner(1l).size());
        assertEquals(0l, groupDAO.findByOwner(0l).size());

        groupDAO.deleteById(group.getId());
        assertNull(groupDAO.findById(group.getId()));


    }

}
