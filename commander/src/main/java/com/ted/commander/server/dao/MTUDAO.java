/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.EnergyData;
import com.ted.commander.common.model.MTU;
import com.ted.commander.server.model.admin.MTULastPost;
import com.ted.commander.server.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the Gateway object
 */
@Repository("mtuDAO")
public class MTUDAO extends SimpleAbstractDAO {

    @Autowired
    ServerService serverService;

    Cache<Long, Cache<Long, MTU>> accountCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(5000).build();

    public static final String CREATE_QUERY = "insert into commander.mtu (id, account_id, mtuType, description, spyder, validation, no_negative) values (?,?,?,?,?, ?,?)";
    public static final String UPDATE_QUERY = "update commander.mtu set mtuType=?, description=?, validation=?, no_negative=? where id = ? and account_id=?";
    public static final String SELECT_ACCOUNT = "select id,  account_id, mtuType, description, spyder, lastpost, validation, no_negative from commander.mtu where account_id = ?";
    public static final String FIND_BY_ID = "select id,  account_id, mtuType, description, spyder, lastpost, validation, no_negative from commander.mtu where id=? and account_id=?";
    public static final String DELETE_BY_ID = "delete from commander.mtu where id=? and account_id=?";
    public static final String FIND_LAST_POST = "select m.id as id, lastpost as lastPostTime from mtu m where m.account_id = :accountId";
    public static final String UPDATE_LAST_POST = "update mtu set lastpost=? where id=? and account_id=?";
    public static final String SELECT_ALL = "select * from mtu";

    public Cache<Long, MTU> getMTUCache(Long accountId){
        Cache<Long, MTU> mtuCache = accountCache.getIfPresent(accountId);
        if (mtuCache == null){
            mtuCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(1000).build();
            accountCache.put(accountId, mtuCache);
        }
        return mtuCache;
    }



    private RowMapper<MTU> rowMapper = new RowMapper<MTU>() {
        public MTU mapRow(ResultSet rs, int rowNum) throws SQLException {
            MTU mtu = new MTU();
            mtu.setId(rs.getLong("id"));
            mtu.setAccountId(rs.getLong("account_id"));
            mtu.setMtuType(MTUType.values()[rs.getInt("mtuType")]);
            mtu.setSpyder(rs.getBoolean("spyder"));
            mtu.setDescription(rs.getString("description"));
            mtu.setLastPost(rs.getLong("lastpost"));
            mtu.setValidation(rs.getLong("validation"));
            mtu.setNoNegative(rs.getBoolean("no_negative"));
            return mtu;
        }
    };

    private RowMapper<MTULastPost> mtuLastPostMapper = new RowMapper<MTULastPost>() {
        public MTULastPost mapRow(ResultSet rs, int rowNum) throws SQLException {
            MTULastPost mtu = new MTULastPost();
            mtu.setId(rs.getLong("id"));
            mtu.setLastPostTime(rs.getLong("lastPostTime"));
            return mtu;
        }
    };

    public MTU update(final MTU mtu) {
        //Check for an existing MTU.
        MTU existingMTU = findById(mtu.getId(), mtu.getAccountId());

        if (existingMTU == null) {
            LOGGER.debug("CREATING NEW MTU: {}", mtu);

            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.NO_GENERATED_KEYS);
                    ps.setLong(1, mtu.getId());
                    ps.setLong(2, mtu.getAccountId());
                    ps.setInt(3, mtu.getMtuType().ordinal());
                    ps.setString(4, mtu.getDescription());
                    ps.setBoolean(5, mtu.isSpyder());
                    ps.setLong(6, mtu.getValidation());
                    ps.setBoolean(7, mtu.isNoNegative());
                    return ps;
                }
            });
        } else {
            LOGGER.debug("UPDATING MTU: {}", mtu);
            getJdbcTemplate().update(UPDATE_QUERY,
                    mtu.getMtuType().ordinal(),
                    mtu.getDescription(),
                    mtu.getValidation(),
                    mtu.isNoNegative(),
                    mtu.getId(), mtu.getAccountId());
        }

        getMTUCache(mtu.getAccountId()).put(mtu.getId(), mtu);

        return mtu;
    }


    class LastPostUpdate{
        final long mtuId;
        final long accountId;
        final long lastPost;

        LastPostUpdate(long mtuId, long accountId, long lastPost) {
            this.mtuId = mtuId;
            this.accountId = accountId;
            this.lastPost = lastPost;
        }

        public long getMtuId() {
            return mtuId;
        }

        public long getAccountId() {
            return accountId;
        }

        public long getLastPost() {
            return lastPost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LastPostUpdate that = (LastPostUpdate) o;
            return mtuId == that.mtuId &&
                    accountId == that.accountId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mtuId, accountId);
        }
    }

    static final int POST_QUEUE_SIZE = 100000;
    Cache<LastPostUpdate,LastPostUpdate> lastPostCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(POST_QUEUE_SIZE).build();
    long batchCount = 0L;
    long lastBatchPost = System.currentTimeMillis()/1000L;


    public synchronized void processLastUpdateBatch(){
        batchCount++;
        long secondsSinceLastPost = (System.currentTimeMillis()/1000L) - lastBatchPost;

        if (secondsSinceLastPost < (60*15) && serverService.isKeepRunning()) return; //Hold off

        lastBatchPost = System.currentTimeMillis()/1000L;
        long start = System.currentTimeMillis();

        List<LastPostUpdate> batchList = new ArrayList<>();
        for (LastPostUpdate u: lastPostCache.asMap().keySet()){
            batchList.add(lastPostCache.getIfPresent(u));
        }

        getJdbcTemplate().batchUpdate(UPDATE_LAST_POST, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LastPostUpdate lastPostUpdate = batchList.get(i);
                ps.setLong(1, lastPostUpdate.getLastPost());
                ps.setLong(2, lastPostUpdate.getMtuId());
                ps.setLong(3, lastPostUpdate.getAccountId());
            }
            @Override
            public int getBatchSize() {
                return batchList.size();
            }
        });
        long end = System.currentTimeMillis() - start;
        LOGGER.error("MTU UPDATE TIME:{}", end);
        lastPostCache.invalidateAll();
        batchCount  = 0;
    }



    public void updateLastPost(final long mtuId, final long accountId, long lastPost) {
        //Check for an existing MTU.
        LOGGER.debug("UPDATING MTU LAST POST: {} {}", mtuId, accountId, lastPost);

        MTU mtu = getMTUCache(accountId).getIfPresent(mtuId);
        if (mtu != null){
            mtu.setLastPost(lastPost);
            getMTUCache(accountId).put(mtuId, mtu);
        }

        LastPostUpdate lastPostUpdate = new LastPostUpdate( mtuId, accountId, lastPost);
        lastPostCache.put(lastPostUpdate,lastPostUpdate);
        processLastUpdateBatch();

        //        getJdbcTemplate().update(UPDATE_LAST_POST,
//                lastPost, mtuId, accountId);
    }


    public MTU findById(Long mtuId, Long accountId) {
        MTU mtu = getMTUCache(accountId).getIfPresent(mtuId);
        if (mtu == null) {
            try {
                mtu = getJdbcTemplate().queryForObject(FIND_BY_ID, new Object[]{mtuId, accountId}, rowMapper);
                getMTUCache(accountId).put(mtuId, mtu);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return mtu;
    }

    public List<MTU> findByAccount(long accountId) {
        return getJdbcTemplate().query(SELECT_ACCOUNT, new Object[]{accountId}, rowMapper);
    }


    public List<MTU> findAll() {
        return getJdbcTemplate().query(SELECT_ALL,  rowMapper);
    }

    public List<MTULastPost> findLastPost(long accountId) {
        Map namedParameters = new HashMap();
        namedParameters.put("accountId", accountId);
        return getNamedParameterJdbcTemplate().query(FIND_LAST_POST, namedParameters, mtuLastPostMapper);
    }

    public void deleteById(long mtuId, long accountId) {
        getMTUCache(accountId).invalidate(mtuId);
        getJdbcTemplate().update(DELETE_BY_ID, new Object[]{mtuId, accountId});
    }


}
