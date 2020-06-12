/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.*;
import com.ted.commander.server.model.PlayBackRow;
import com.ted.commander.server.services.EnergyPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class PlaybackDAO extends SimpleAbstractDAO {

    static String PLAYBACK_QUERY = "select * from playback_queue where server=? and running=0 and completed=0 order by virtualECC_id";
    static String ADD_TO_QUEUE = "insert into playback_queue (virtualECC_id, startDate, endDate, server) " +
            "values (:virtualECCId,:startDate,:endDate, :server)";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;
    private RowMapper<PlayBackRow> playBackRowRowMapper = new RowMapper<PlayBackRow>() {
        public PlayBackRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayBackRow dto = new PlayBackRow();
            dto.setVirtualECCId(rs.getLong("virtualECC_id"));
            dto.setStartDate(rs.getLong("startDate"));
            dto.setEndDate(rs.getLong("endDate"));
            return dto;
        }
    };

    public void playBackCumulativeEnergyPost(EnergyPostService energyPostService, VirtualECC virtualECC, int totalMTU, VirtualECCMTU virtualECCMTU, MTU mtu, EnergyPlan energyPlan, long startEpoch, long endEpoch) {
        LOGGER.info(">>>>>> RUNNING playBackCumulativeEnergyPost: mtu:{} s:{} e:{}", virtualECCMTU.getHexId(), simpleDateFormat.format(new Date(startEpoch * 1000)), simpleDateFormat.format(new Date(endEpoch * 1000)));
        long currentEpoch = startEpoch;

        while (currentEpoch < endEpoch) {
            EnergyData energyData = null;
            try {
                String query = "select * from commander.energydata where account_id= ? and mtu_id=? and timestamp=?";
                energyData = getJdbcTemplate().queryForObject(query, new Object[]{mtu.getAccountId(), mtu.getId(), currentEpoch}, EnergyDataDAO.rowMapper);

            } catch (EmptyResultDataAccessException ex) {
            }

            if (energyData == null) {
                LOGGER.warn("Could not find energy data record: mtu:{} account:{} ts:{}", mtu, virtualECC.getAccountId(), currentEpoch);
            } else {
                try {
                    if (mtu.getValidation().equals(0l) || Math.abs(energyData.getEnergyDifference()) < ((double) mtu.getValidation() / 60.0)) {
                        energyPostService.processEnergyPost(virtualECC, totalMTU, virtualECCMTU, energyPlan, currentEpoch, energyData, true);
                    } else {
                        //Skip the big number record
                        LOGGER.warn("Energy Validation Failed: {}", energyData);
                    }
                } catch (Exception ex) {
                    LOGGER.error("Error processing minute: ts:{} ep:{}", new Object[]{currentEpoch, energyData}, ex);
                }
            }
            currentEpoch += 60;
        }
    }

    public void playBackCumulativeEnergyPostOld(EnergyPostService energyPostService, VirtualECC virtualECC, int totalMTU, VirtualECCMTU virtualECCMTU, MTU mtu, EnergyPlan energyPlan, long startEpoch, long endEpoch) {
        LOGGER.debug("RUNNING playBackCumulativeEnergyPost: mtu:{} s:{} e:{}", virtualECCMTU.getHexId(), simpleDateFormat.format(new Date(startEpoch * 1000)), simpleDateFormat.format(new Date(endEpoch * 1000)));
        String query = "Select * from energydata where mtu_id = :mtuId and account_id= :accountId and timestamp >= :startEpoch and timestamp < :endEpoch order by timestamp, mtu_id"; //TODO: processed flag?

        PlaybackCallbackHandler playbackCallbackHandler = new PlaybackCallbackHandler(energyPostService, virtualECC, virtualECCMTU, mtu, energyPlan, totalMTU);
        Map namedParameters = new HashMap(24);
        namedParameters.put("accountId", virtualECC.getAccountId());
        namedParameters.put("mtuId", virtualECCMTU.getMtuId());
        namedParameters.put("startEpoch", startEpoch);
        namedParameters.put("endEpoch", endEpoch);
        getNamedParameterJdbcTemplate().query(query, namedParameters, playbackCallbackHandler);
    }

    public List<PlayBackRow> getPlaybackRequest(int server) {
        LOGGER.info("QUERY:{}", PLAYBACK_QUERY.replace("?", server + ""));
        return getJdbcTemplate().query(PLAYBACK_QUERY, new Object[]{server}, playBackRowRowMapper);
    }

    public void addToQueue(long virtualECCId, long startDate, long endDate, int server) {

        Map namedParameters = new HashMap(64);
        namedParameters.put("virtualECCId", virtualECCId);
        namedParameters.put("startDate", startDate);
        namedParameters.put("endDate", endDate);
        namedParameters.put("server", server);


//        getNamedParameterJdbcTemplate().update(ADD_TO_QUEUE,
//                namedParameters);
//        LOGGER.info("addToQueueStop:");

        System.err.println(ADD_TO_QUEUE
                .replace(":virtualECCId", virtualECCId + "")
                .replace(":startDate", startDate + "")
                .replace(":endDate", endDate + "")
                .replace(":server", server + "")
        );

    }

    public void markRunning(PlayBackRow row) {
        String q = "update playback_queue set running = 1 where virtualECC_id =? and startDate=? and endDate=?";
        getJdbcTemplate().update(q, new Object[]{row.getVirtualECCId(), row.getStartDate(), row.getEndDate()});
    }

    public void markComplete(PlayBackRow row) {
        String q = "update playback_queue set completed = 1 where virtualECC_id =? and startDate=? and endDate=?";
        getJdbcTemplate().update(q, new Object[]{row.getVirtualECCId(), row.getStartDate(), row.getEndDate()});
    }
}
