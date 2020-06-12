/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.EnergyData;
import com.ted.commander.common.model.EnergyDataLastCur;
import com.ted.commander.server.model.TestValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class TestDAO extends SimpleAbstractDAO{

    static final String INSERT_QUERY = "INSERT IGNORE INTO test(id, value) values (?,?)";
    static final String DELETE_QUERY = "delete from test";


    public void deleteAll(){
        getJdbcTemplate().update(DELETE_QUERY);
    }



    public void insert(List<TestValue> batchList) {

        int[] retval = new int[0];
        try {
            long start = System.currentTimeMillis();

            retval = getJdbcTemplate().batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    TestValue dto = batchList.get(i);
                    ps.setLong(1, dto.getId());
                    ps.setLong(2, dto.getValue());
                }
                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });

            long end = System.currentTimeMillis();
            //LOGGER.info(">>>>>>>> SPRING JDBC TIME:{}", (end - start));


            //return true;

        } catch(DuplicateKeyException dex){
            LOGGER.error("Duplicate Key on insert: {}", dex.getMessage());
            //return false;

        }

        LOGGER.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");;
        for (int i=0; i  < retval.length; i++){
            LOGGER.error("! {} {}",batchList.get(i), retval[i]);
        }


    }

}
