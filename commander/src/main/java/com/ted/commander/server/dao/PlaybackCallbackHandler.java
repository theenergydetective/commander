/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.*;
import com.ted.commander.server.services.EnergyPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pete on 12/18/2014.
 */
public class PlaybackCallbackHandler implements RowCallbackHandler {

    final static Logger LOGGER = LoggerFactory.getLogger(PlaybackCallbackHandler.class);

    final EnergyPostService energyPostService;
    final VirtualECC virtualECC;
    final VirtualECCMTU virtualECCMTU;
    final EnergyPlan energyPlan;
    final int totalMTU;
    final MTU mtu;


    public PlaybackCallbackHandler(EnergyPostService energyPostService, VirtualECC virtualECC, VirtualECCMTU virtualECCMTU, MTU mtu, EnergyPlan energyPlan, int totalMTU) {
        this.energyPostService = energyPostService;
        this.virtualECC = virtualECC;
        this.virtualECCMTU = virtualECCMTU;
        this.energyPlan = energyPlan;
        this.totalMTU = totalMTU;
        this.mtu = mtu;
    }


    @Override
    public void processRow(ResultSet rs) throws SQLException {
        try {
            EnergyData energyData = EnergyDataDAO.rowMapper.mapRow(rs, 0);
            LOGGER.debug("Process ROW: {} {}", virtualECCMTU.getHexId(), energyData);

            if (mtu.getValidation().equals(0l) || Math.abs(energyData.getEnergyDifference()) < ((double) mtu.getValidation() / 60.0)) {
                energyPostService.processEnergyPost(virtualECC, totalMTU, virtualECCMTU, energyPlan, rs.getLong("timestamp"), energyData, true);
            } else {
                //Skip the big number record
                LOGGER.warn("Energy Validation Failed: {}", energyData);
            }


        } catch (Exception ex) {
            LOGGER.error("PLAYBACK EXCEPTION:", ex);
        }
    }

}
