/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.server.Application;

import com.ted.commander.server.services.HazelcastService;
import com.ted.commander.server.services.PlaybackService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class AccessControlDAOTest {

    @Autowired
    AccessControlDAO accessControlDAO;


    @Test
    public void testQueue(){
        //playbackService.submitPlayback(1429l, 0, System.currentTimeMillis()/1000);
        Assert.assertTrue(accessControlDAO.canViewLocation(911l, "pete@petecode.com"));
        Assert.assertFalse(accessControlDAO.canViewLocation(911l, "pete2@petecode.com"));
    }
}

