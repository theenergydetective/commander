/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;


import com.ted.commander.common.model.LastPost;
import com.ted.commander.server.model.NonPostingMTU;
import com.ted.commander.server.model.admin.MTUAlarmEntry;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("lastPostDAO")

public class LastPostDAO extends SimpleAbstractDAO {

    static final String LAST_POST_QUERY = "  select distinct a.accountName, v.eccName, m.lastpost as maxtx, Hex(m.id) as mtuId, v.id as id, m.description as mtuDescription,  " +
            " (       select count(*)   " +
            "       from virtualECCMTU vmtu straight_join mtu on vmtu.mtu_id = mtu.id  " +
            "       where vmtu.virtualECC_id = vm.virtualECC_id  " +
            "       and mtu.lastpost is not null and  " +
            "       mtu.lastpost >= (UNIX_TIMESTAMP() - 86400)) as isActive  " +
            "  " +
            " from mtu m  " +
            "       straight_join account a on m.account_id = a.id  " +
            "    straight_join virtualECCMTU vm on vm.mtu_id = m.id and vm.account_id = a.id  " +
            "    straight_join virtualECC v on v.id = vm.virtualECC_id and v.account_id = a.id  " +
            "    where (m.lastpost < (UNIX_TIMESTAMP() - 86400)  or m.lastpost is null)  " +
            "    order by accountName, eccName";

    static final String LAST_POST_QUERY_BY_ACCOUNT = "  select distinct a.accountName, v.eccName, m.lastpost as maxtx, Hex(m.id) as mtuId, v.id as id, vm.mtuDescription as mtuDescription,  " +
            " (       select count(*)   " +
            "       from virtualECCMTU vmtu straight_join mtu on vmtu.mtu_id = mtu.id  " +
            "       where vmtu.virtualECC_id = vm.virtualECC_id  " +
            "       and mtu.lastpost is not null and  " +
            "       mtu.lastpost >= (UNIX_TIMESTAMP() - 86400)) as isActive  " +
            "  " +
            " from mtu m  " +
            "       straight_join account a on m.account_id = a.id  " +
            "    straight_join virtualECCMTU vm on vm.mtu_id = m.id and vm.account_id = a.id  " +
            "    straight_join virtualECC v on v.id = vm.virtualECC_id and v.account_id = a.id  " +
            "    where (m.lastpost < (UNIX_TIMESTAMP() - 86400)  or m.lastpost is null)  " +
            "    and a.id=:accountId " +
            "    order by accountName, eccName";

    static final String ACTIVE_POSTING_QUERY = " select count(*) " +
            "from mtu USE INDEX (ACCOUNT) " +
            "where  ((mtu.id = 1445249 and account_id = 52) " +
            "or (mtu.id = 1442606 and account_id = 73) " +
            "or (mtu.id = 1445078 and account_id = 242) " +
            "or (mtu.id = 1248700 and account_id = 86)) " +
            "and ((UNIX_TIMESTAMP()-mtu.lastPost)/60)  > 15  ";

    static final String ACTIVE_POSTING_STATE = " select HEX(mtu.id) as mtuId, CONVERT_TZ(FROM_UNIXTIME(mtu.lastPost), 'System', 'US/Eastern') as lastPostTime " +
            "            from mtu USE INDEX (ACCOUNT)  " +
            "            where  ((mtu.id = 1445249 and account_id = 52)  " +
            "            or (mtu.id = 1442606 and account_id = 73)  " +
            "            or (mtu.id = 1445078 and account_id = 242)  " +
            "            or (mtu.id = 1248700 and account_id = 86))  " +
            "            ";

    static final String MTU_LOCATION_LASTPOST = "select m.account_id, vm.mtuDescription, hex(m.id) as mtuHex, m.lastPost from virtualECCMTU vm  straight_join mtu m on m.id = vm.mtu_id and m.account_id = vm.account_id where vm.virtualECC_id = ? and m.lastPost < ?";
    static final String MTU_LOCATION_COUNT = "select count(*) from virtualECCMTU where virtualECC_id = ?";

    private RowMapper<NonPostingMTU> nonPostingMTURowMapper = new RowMapper<NonPostingMTU>() {

        @Override
        public NonPostingMTU mapRow(ResultSet rs, int i) throws SQLException {
            try {
                NonPostingMTU lastPost = new NonPostingMTU();
                lastPost.setLastPost(rs.getLong("lastPost"));
                lastPost.setDescription(rs.getString("mtuDescription"));
                lastPost.setHex(rs.getString("mtuHex"));
                return lastPost;
            } catch (SQLException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    };

    private RowMapper<LastPost> rowMapper = new RowMapper<LastPost>() {

        @Override
        public LastPost mapRow(ResultSet rs, int i) throws SQLException {
            try {
                LastPost lastPost = new LastPost();
                lastPost.setAccountName(rs.getString("accountName"));
                lastPost.setEccName(rs.getString("eccName"));
                lastPost.setLastPost(rs.getLong("maxtx"));
                lastPost.setId(rs.getLong("id"));
                lastPost.setActiveCount(rs.getInt("isActive"));
                lastPost.setMtuId(rs.getString("mtuId"));
                lastPost.setMtuDescription(rs.getString("mtuDescription"));
                return lastPost;
            } catch (SQLException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    };
    private RowMapper<MTUAlarmEntry> alarmEntryRowMapper = new RowMapper<MTUAlarmEntry>() {

        @Override
        public MTUAlarmEntry mapRow(ResultSet rs, int i) throws SQLException {
            try {
                MTUAlarmEntry lastPost = new MTUAlarmEntry();
                lastPost.setMtuId(rs.getString("mtuId"));
                lastPost.setLastPostTime(rs.getString("lastPostTime"));
                return lastPost;
            } catch (SQLException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    };

    public List<LastPost> findExpired() {
        return getJdbcTemplate().query(LAST_POST_QUERY, new Object[]{}, rowMapper);
    }


    public List<NonPostingMTU> findNonPostingMTU(long virtualECCId, long lastPostEpoch) {
        return getJdbcTemplate().query(MTU_LOCATION_LASTPOST, new Object[]{virtualECCId, lastPostEpoch}, nonPostingMTURowMapper);
    }

    public Integer getMTUCount(long virtualECCId) {
        return getJdbcTemplate().queryForObject(MTU_LOCATION_COUNT, new Object[]{virtualECCId}, Integer.class);
    }


    public boolean IsActive() {
        int v = getJdbcTemplate().queryForObject(ACTIVE_POSTING_QUERY, Integer.class);
        if (!(v >= 1)) {
            LOGGER.error("TOO MANY SITES ARE NOT ACTIVE: {}", v);
        }

        return v >= 1;
    }

    public boolean IsResumed() {
        return (getJdbcTemplate().queryForObject(ACTIVE_POSTING_QUERY, Integer.class) < 2);
    }

    public List<LastPost> findExpired(Long id) {
        Map namedParameters = new HashMap();
        namedParameters.put("accountId", id);
        return getNamedParameterJdbcTemplate().query(LAST_POST_QUERY_BY_ACCOUNT, namedParameters, rowMapper);
    }

    public List<MTUAlarmEntry> findAlarmState() {
        return getJdbcTemplate().query(ACTIVE_POSTING_STATE, alarmEntryRowMapper);
    }

}
