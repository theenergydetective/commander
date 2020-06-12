/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.EnergyData;
import com.ted.commander.common.model.EnergyDataLastCur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class EnergyDataDAO {

    static final String SINGLE_INSERT_QUERY = "INSERT INTO energydata(timestamp, mtu_id, account_id, energy, energyDifference,  pf, voltage) values (?, ?, ?,?,?, ?, ?)";
    static final String BATCH_INSERT_QUERY = "INSERT INTO energydata(timestamp, mtu_id, account_id, energy, energyDifference, pf, voltage, avg5, avg10, avg15, avg20, avg30,smoothing) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static RowMapper<EnergyData> rowMapper = new RowMapper<EnergyData>() {
        public EnergyData mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyData dto = new EnergyData();
            dto.setMtuId(rs.getLong("mtu_id"));
            dto.setTimeStamp(rs.getLong("timestamp"));
            dto.setEnergy(rs.getDouble("energy"));
            dto.setAccountId(rs.getLong("account_id"));
            dto.setEnergyDifference(rs.getDouble("energyDifference"));
            dto.setVoltage(rs.getDouble("voltage"));
            dto.setPowerFactor(rs.getDouble("pf"));
            dto.setAvg5(rs.getDouble("avg5"));
            dto.setAvg10(rs.getDouble("avg10"));
            dto.setAvg15(rs.getDouble("avg15"));
            dto.setAvg20(rs.getDouble("avg20"));
            dto.setAvg30(rs.getDouble("avg30"));

            //TODO: dto.setProcessed(rs.getBoolean("processed"));
            return dto;
        }
    };
    public static RowMapper<EnergyDataLastCur> lastCurrowMapper = new RowMapper<EnergyDataLastCur>() {
        public EnergyDataLastCur mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyDataLastCur dto = new EnergyDataLastCur();
            dto.setMtuId(rs.getLong("mtu_id"));
            dto.setTimeStamp(rs.getLong("timestamp"));
            dto.setAccountId(rs.getLong("account_id"));
            dto.setEnergyDifference(rs.getDouble("energyDifference"));
            dto.setEnergy(rs.getDouble("energy"));
            return dto;
        }
    };
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private DataSource commanderDataSource;
    private JdbcTemplate jdbcTemplate = null;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

    public RowMapper<EnergyData> getRowMapper() {
        return rowMapper;
    }

    public void delete(EnergyData energyData) {
        String query = "delete from energydata where account_id=? and mtu_id=? and timestamp=?";
        getJdbcTemplate().update(query, new Object[]{energyData.getAccountId(), energyData.getMtuId(), energyData.getTimeStamp()});
    }

    public void deleteByMTUID(Long mtuId, Long accountId) {
        String query = "delete from commander.energydata where account_id=? and mtu_id=?";
        getJdbcTemplate().update(query, new Object[]{accountId,mtuId});
    }

    public EnergyData findById(Long timestamp, Long accountId, Long mtuId) {
        try {
            String query = "select * from commander.energydata where account_id=? and mtu_id=? and timestamp= ?";
            return getJdbcTemplate().queryForObject(query, new Object[]{accountId, mtuId, timestamp}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }
    }

    protected JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(commanderDataSource);
        }
        return jdbcTemplate;
    }

    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(commanderDataSource);
        }
        return namedParameterJdbcTemplate;
    }

    public EnergyData findFirst(Long accountId, Long mtuId) {
        try {
            String query = "select *  from commander.energydata where account_id=? and mtu_id=? order by timestamp limit 1";
            return getJdbcTemplate().queryForObject(query, new Object[]{accountId, mtuId}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.debug("No Results returned");
            return null;
        }

    }

    public EnergyDataLastCur findLast(long accountId, long mtuId) {
//        String query = "select e.account_id, e.mtu_id, e.timestamp, e.energy, e.energyDifference " +
//                "from mtu m " +
//                "straight_join energydata e on e.timestamp = m.lastPost " +
//                "and e.account_id = m.account_id " +
//                "and e.mtu_id = m.id " +
//                "where m.account_id = ? " +
//                "and m.id = ?";


        String query = "select e.account_id, e.mtu_id, e.timestamp, e.energy, e.energyDifference " +
                "from energydata e " +
                "where e.account_id = ? " +
                "and e.mtu_id = ? " +
                "and e.timestamp= (select lastPost from mtu where id = ? and account_id = ?) " +
                "order by e.timestamp desc limit 1";

        try {
            return getJdbcTemplate().queryForObject(query, new Object[]{accountId, mtuId, mtuId, accountId}, lastCurrowMapper);
        } catch (EmptyResultDataAccessException ex){
            return null;
        }
    }



    public void insert(EnergyData energyData) {

        getJdbcTemplate().update(SINGLE_INSERT_QUERY, new Object[]{
            energyData.getTimeStamp(),
            energyData.getMtuId(),
            energyData.getAccountId(),
            energyData.getEnergy(),
            energyData.getEnergyDifference(),
            energyData.getPowerFactor(),
            energyData.getVoltage()
        });
    }



    public boolean insert(List<EnergyData> batchList) {

        try {
            long start = System.currentTimeMillis();

            getJdbcTemplate().batchUpdate(BATCH_INSERT_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    EnergyData energyData = batchList.get(i);
                    ps.setLong(1, energyData.getTimeStamp());
                    ps.setLong(2, energyData.getMtuId());
                    ps.setLong(3, energyData.getAccountId());
                    ps.setDouble(4, energyData.getEnergy());
                    ps.setDouble(5, energyData.getEnergyDifference());
                    ps.setDouble(6, energyData.getPowerFactor());
                    ps.setDouble(7, energyData.getVoltage());
                    ps.setDouble(8, energyData.getAvg5());
                    ps.setDouble(9, energyData.getAvg10());
                    ps.setDouble(10, energyData.getAvg15());
                    ps.setDouble(11, energyData.getAvg20());
                    ps.setDouble(12, energyData.getAvg30());
                    ps.setInt(13, energyData.isSmoothing() ? 1 : 0);
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });

            long end = System.currentTimeMillis();
            //LOGGER.info(">>>>>>>> SPRING JDBC TIME:[{}] {}",batchList.size(), (end - start));
            return true;

        } catch(DuplicateKeyException dex){
            LOGGER.debug("Duplicate Key on insert: {}", dex.getMessage());
            return false;
        }

    }

}
