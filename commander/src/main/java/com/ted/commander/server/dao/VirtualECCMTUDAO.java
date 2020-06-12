/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.server.services.ServerService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the Virtual ECC MTU Object
 */
@Repository
public class VirtualECCMTUDAO extends SimpleAbstractDAO {

    Cache<Long, List<VirtualECCMTU>> virtualECCListCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
    Cache<Long, Cache<Long, VirtualECCMTU>> virtualECCCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).removalListener(new CacheRemovalListener()).build();
    Cache<Long, Integer> countCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();

    @Autowired
    VirtualECCDAO virtualECCDAO;


    public Cache<Long, VirtualECCMTU> getVirtualECCCache(long virtualECCId){
        Cache<Long, VirtualECCMTU> cache = virtualECCCache.getIfPresent(virtualECCId);
        if (cache == null){
            cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
            virtualECCCache.put(virtualECCId, cache);
        }
        return cache;
    }


    void updateCaches(VirtualECCMTU virtualECCMTU){
        getVirtualECCCache(virtualECCMTU.getVirtualECCId()).put(virtualECCMTU.getMtuId(), virtualECCMTU);

        //Update the list cache for the find by mtu
        List<VirtualECCMTU> virtualECCMTUList = virtualECCListCache.getIfPresent(virtualECCMTU.getVirtualECCId());
        if (virtualECCMTUList != null){
            for (VirtualECCMTU vmtu: virtualECCMTUList){
                if (vmtu.getMtuId().equals(virtualECCMTU.getMtuId())){
                    vmtu.setLastPost(virtualECCMTU.getLastPost());
                    vmtu.setMtuType(virtualECCMTU.getMtuType());
                    vmtu.setMtuDescription(virtualECCMTU.getMtuDescription());
                    vmtu.setPowerMultiplier(virtualECCMTU.getPowerMultiplier());
                    vmtu.setVoltageMultiplier(virtualECCMTU.getVoltageMultiplier());
                    vmtu.setSpyder(virtualECCMTU.isSpyder());
                    break;
                }
            }
            virtualECCListCache.put(virtualECCMTU.getVirtualECCId(), virtualECCMTUList);
        }

        //countCache.invalidate(virtualECCMTU.getVirtualECCId());
    }

    void clearCache(){
        virtualECCListCache.invalidateAll();
        countCache.invalidateAll();
        virtualECCDAO.clearCache();
    }


    static final String COUNT_QUERY = "select count(*) from virtualECCMTU where virtualECC_id = ? and account_id = ? and mtuType != 3 and mtuDescription is not null and lastPost > 0";
    public static String DELETE = "delete from commander.virtualECCMTU where virtualECC_id=? and mtu_id=? and account_id=?";
    public static String INSERT = "insert into commander.virtualECCMTU (virtualECC_id, mtu_id, account_id, mtuType, mtuDescription, powerMultiplier, voltageMultiplier,lastPost) values (?,?,?,?,?,?,?,?)";
    public static String UPDATE = "update commander.virtualECCMTU set mtuType=?, mtuDescription=?,powerMultiplier=?, voltageMultiplier=?, lastPost=? where virtualECC_id=? and mtu_id=? and account_id=?";
    public static String GET = "SELECT m.spyder as spyder, m.id as mtu_id, m.account_id as account_id, m.mtuType as defaultMtuType, m.description as defualtMtuDescription, virtualECC_id, v.mtuType, v.mtuDescription, v.lastPost, v.powerMultiplier, v.voltageMultiplier FROM commander.mtu m, commander.virtualECCMTU v where  m.id =  v.mtu_id and m.account_id = v.account_id and m.id=? and m.account_id=? and v.virtualECC_id=?";

    public static String GET_LIST_VIRTUALECC = "SELECT m.spyder as spyder, m.id as mtu_id, m.account_id as account_id, m.mtuType as defaultMtuType, m.description as defaultMTUDescription, v.lastPost, virtualECC_id, v.mtuType, v.mtuDescription, v.powerMultiplier, v.voltageMultiplier  FROM commander.mtu m , commander.virtualECCMTU v where m.id =  v.mtu_id and m.account_id = v.account_id  and m.account_id=? and v.virtualECC_id=? order by v.mtuType";


    //Bulk Set Get
    public static String DELETE_BY_VIRTUAL_ECC = "delete from commander.virtualECCMTU where virtualECC_Id=?";
    private RowMapper<VirtualECCMTU> virtualECCMTURowMapper = new RowMapper<VirtualECCMTU>() {
        public VirtualECCMTU mapRow(ResultSet rs, int rowNum) throws SQLException {
            VirtualECCMTU virtualECCMTU = new VirtualECCMTU();
            virtualECCMTU.setMtuId(rs.getLong("mtu_id"));
            virtualECCMTU.setAccountId(rs.getLong("account_id"));
            virtualECCMTU.setVirtualECCId(rs.getLong("virtualECC_id"));
            virtualECCMTU.setSpyder(rs.getBoolean("spyder"));
            virtualECCMTU.setLastPost(rs.getLong("lastPost"));
            if (rs.wasNull()) {
                virtualECCMTU.setVirtualECCId(null);
                virtualECCMTU.setMtuType(MTUType.values()[rs.getInt("defaultMtuType")]);
                virtualECCMTU.setMtuDescription(rs.getString("defaultMTUDescription"));
                virtualECCMTU.setPowerMultiplier(1.0);
                virtualECCMTU.setVoltageMultiplier(1.0);
            } else {
                virtualECCMTU.setMtuType(MTUType.values()[rs.getInt("mtuType")]);
                virtualECCMTU.setMtuDescription(rs.getString("mtuDescription"));
                virtualECCMTU.setPowerMultiplier(rs.getDouble("powerMultiplier"));
                virtualECCMTU.setVoltageMultiplier(rs.getDouble("voltageMultiplier"));
            }
            return virtualECCMTU;
        }
    };

    public VirtualECCMTU update(final VirtualECCMTU virtualECCMTU) {
        if (null == findByMTUId(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId(), virtualECCMTU.getAccountId())) {
            LOGGER.info("Adding new virtual ECC MTU: {}", virtualECCMTU);
            getJdbcTemplate().update(INSERT,
                    virtualECCMTU.getVirtualECCId(),
                    virtualECCMTU.getMtuId(),
                    virtualECCMTU.getAccountId(),
                    virtualECCMTU.getMtuType().ordinal(),
                    virtualECCMTU.getMtuDescription(),
                    virtualECCMTU.getPowerMultiplier(),
                    virtualECCMTU.getVoltageMultiplier(),
                    virtualECCMTU.getLastPost());
            clearCache();
            getVirtualECCCache(virtualECCMTU.getVirtualECCId()).invalidateAll();

        } else {
            LOGGER.debug("Updating virtual ECC MTU: {}", virtualECCMTU);
            getJdbcTemplate().update(UPDATE,
                    virtualECCMTU.getMtuType().ordinal(),
                    virtualECCMTU.getMtuDescription(),
                    virtualECCMTU.getPowerMultiplier(),
                    virtualECCMTU.getVoltageMultiplier(),
                    virtualECCMTU.getLastPost(),
                    virtualECCMTU.getVirtualECCId(),
                    virtualECCMTU.getMtuId(),
                    virtualECCMTU.getAccountId());
            updateCaches(virtualECCMTU);
        }
        getVirtualECCCache(virtualECCMTU.getVirtualECCId()).put(virtualECCMTU.getMtuId(), virtualECCMTU);
        return virtualECCMTU;
    }

    public void deleteByVirtualECCId(Long virtualECCId) {
        clearCache();
        getVirtualECCCache(virtualECCId).invalidateAll();
        getJdbcTemplate().update(DELETE_BY_VIRTUAL_ECC, virtualECCId);
    }

    public void deleteById(Long virtualECCId, Long mtuId, Long accountId) {
        clearCache();
        getVirtualECCCache(virtualECCId).invalidateAll();
        getJdbcTemplate().update(DELETE, new Object[]{virtualECCId, mtuId, accountId});
    }

    public VirtualECCMTU findByMTUId(Long virtualECCId, Long mtuId, Long accountId) {
        VirtualECCMTU dto = getVirtualECCCache(virtualECCId).getIfPresent(mtuId);
        if (dto == null) {
            try {
                dto = getJdbcTemplate().queryForObject(GET, new Object[]{mtuId, accountId, virtualECCId}, virtualECCMTURowMapper);
                getVirtualECCCache(virtualECCId).put(dto.getMtuId(), dto);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.debug("No Results returned");
                return null;
            }
        }
        return dto;
    }

    public List<VirtualECCMTU> findByVirtualECC(Long virtualECCId, Long accountId) {
        List<VirtualECCMTU> list = virtualECCListCache.getIfPresent(virtualECCId);
        if (list == null){
            try{
                list =getJdbcTemplate().query(GET_LIST_VIRTUALECC, new Object[]{accountId, virtualECCId}, virtualECCMTURowMapper);
            } catch(EmptyResultDataAccessException ex){
                list = new ArrayList<>();
            }
            virtualECCListCache.put(virtualECCId, list);
        }
        return list;
    }

    public Integer countNetMTU(long virutalECCId, Long accountId) {
        Integer count = countCache.getIfPresent(virutalECCId);
        if (count == null){
            count = getJdbcTemplate().queryForObject(COUNT_QUERY, new Object[]{virutalECCId, accountId}, Integer.class);
            countCache.put(virutalECCId, count);
        }
        return count;
    }



    class VMTULastPostUpdate{
        final long virtualECCId;
        final long mtuId;
        final long accountId;
        final long lastPost;


        public VMTULastPostUpdate(long virtualECCId, long mtuId, long accountId, long lastPost) {
            this.virtualECCId = virtualECCId;
            this.mtuId = mtuId;
            this.accountId = accountId;
            this.lastPost = lastPost;
        }

        public long getVirtualECCId() {
            return virtualECCId;
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
            VMTULastPostUpdate that = (VMTULastPostUpdate) o;
            return virtualECCId == that.virtualECCId &&
                    mtuId == that.mtuId &&
                    accountId == that.accountId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(virtualECCId, mtuId, accountId);
        }
    }

    public static final String UPDATE_LAST_POST = "update virtualECCMTU set lastPost=? where virtualECC_id=? and mtu_id=? and account_id=?";
    static final int POST_QUEUE_SIZE = 100000;
    Cache<VMTULastPostUpdate,VMTULastPostUpdate> lastPostCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(POST_QUEUE_SIZE).build();
    long batchCount = 0L;
    long lastBatchPost = System.currentTimeMillis()/1000L;

    @Autowired
    ServerService serverService;

    public synchronized void processLastUpdateBatch(){
        batchCount++;
        long secondsSinceLastPost = (System.currentTimeMillis()/1000L) - lastBatchPost;

        if (secondsSinceLastPost < (60*15) && serverService.isKeepRunning()) return; //Hold off

        lastBatchPost = System.currentTimeMillis()/1000L;
        long start = System.currentTimeMillis();

        List<VMTULastPostUpdate> batchList = new ArrayList<>();
        for (VMTULastPostUpdate u: lastPostCache.asMap().keySet()){
            batchList.add(lastPostCache.getIfPresent(u));
        }
        getJdbcTemplate().batchUpdate(UPDATE_LAST_POST, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                VMTULastPostUpdate lastPostUpdate = batchList.get(i);
                ps.setLong(1, lastPostUpdate.getLastPost());
                ps.setLong(2, lastPostUpdate.getVirtualECCId());
                ps.setLong(3, lastPostUpdate.getMtuId());
                ps.setLong(4, lastPostUpdate.getAccountId());
            }
            @Override
            public int getBatchSize() {
                return batchList.size();
            }
        });
        long end = System.currentTimeMillis() - start;
        LOGGER.info("VMTU Last post UPDATE TIME:{}", end);
        lastPostCache.invalidateAll();
        batchCount  = 0;
    }

    public void updateLastPost(VirtualECCMTU virtualECCMTU, long timestamp) {

        virtualECCMTU.setLastPost(timestamp);

        //Update Cache
        updateCaches(virtualECCMTU);

        //Queue it
        VMTULastPostUpdate lastPostUpdate = new VMTULastPostUpdate(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId(), virtualECCMTU.getAccountId(), timestamp);
        lastPostCache.put(lastPostUpdate,lastPostUpdate);
        processLastUpdateBatch();
    }
}
