/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.model.PieGraphPoint;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing the EnergyData object
 */
@Repository
public class PieGraphPointDAO extends SimpleAbstractDAO {

    private static final String FIND_BY_HISTORY_DAY_QUERY = "select d.mtu_id, energy, cost, vm.mtuDescription from history_mtu_day2 d  straight_join virtualECCMTU vm on vm.mtu_id = d.mtu_id and vm.account_id = ?  and vm.virtualECC_id = ? where  d.virtualECC_id=?  and d.startEpoch=?  and vm.mtuType = 3  order by d.mtu_id";
    private RowMapper<PieGraphPoint> rowMapper = new RowMapper<PieGraphPoint>() {
        public PieGraphPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            PieGraphPoint dto = new PieGraphPoint();
            dto.setEnergy(rs.getDouble("energy"));
            dto.setCost(rs.getDouble("cost"));
            dto.setLabel(rs.getString("mtuDescription"));
            dto.setMtuId(rs.getLong("mtu_id"));
            return dto;
        }
    };

    public List<PieGraphPoint> findByHistoryDay(long account_id, long virtualECCId, long startEpoch) {
        return getJdbcTemplate().query(FIND_BY_HISTORY_DAY_QUERY, new Object[]{account_id, virtualECCId, virtualECCId, startEpoch}, rowMapper);
    }
}
