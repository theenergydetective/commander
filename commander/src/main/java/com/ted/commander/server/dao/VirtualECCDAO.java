/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.server.services.HazelcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DAO for accessing the Virtual ECC Object
 */
@Repository
public class VirtualECCDAO extends AbstractDAO<VirtualECC> {

    Cache<Long, Cache<Long, List<VirtualECC>>> accountCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();

    public void clearCache(){
        accountCache.invalidateAll();
    }


    public static String INSERT_VIRTUAL_ECC = "insert into commander.virtualECC (account_id, eccName,  timezone, systemType, street1, street2, city, state, postal, country, weatherId, energyPlan_id) values (?,?,?,?,?,?,?,?,?,?,?,?)";
    public static String UPDATE_VIRTUAL_ECC = "update commander.virtualECC set  eccName=?,  timezone=?, systemType=?, street1=?, street2=?, city=?, state=?, postal=?, country=?, weatherId=?, energyPlan_id=? where  id=?";
    public static String SELECT_VIRTUAL_ECC_BY_ACCOUNT = "select id, account_id, eccName,  timezone,  systemType, street1, street2, city, state, postal, country,weatherId, energyPlan_id from commander.virtualECC where account_id =? order by eccName";

    public static String SELECT_VIRTUAL_ECC_BY_MTU = "select id, account_id, eccName,  timezone,  systemType, street1, street2, city, state, postal, country,weatherId, energyPlan_id from commander.virtualECC where id in (select distinct virtualECC_id from virtualECCMTU vm where vm.account_id=? and vm.mtu_id=?) order by eccName";

    public static String SELECT_BY_USERID = "select a.accountName, am.role, v.id, v.account_id, v.eccName, v.timezone, v.systemType, street1, street2, city, state, postal, country, weatherId, energyPlan_id  from commander.virtualECC v, commander.account_member am, account a  where a.id = v.account_id and v.account_id = am.account_id and user_id=? order by am.role, a.accountName";


    public static String SELECT_ALEXA_LOCATION = "select v.* from virtualECC v  " +
            "straight_join alexa_location a on a.virtualECC_id = v.id  " +
            "where a.user_id = ?";


    @Autowired
    HazelcastService hazelcastService;

    private RowMapper<VirtualECC> virtualECCRowMapper = new RowMapper<VirtualECC>() {
        public VirtualECC mapRow(ResultSet rs, int rowNum) throws SQLException {
            VirtualECC virtualECC = new VirtualECC();
            virtualECC.setId(rs.getLong("id"));
            virtualECC.setAccountId(rs.getLong("account_id"));
            virtualECC.setName(rs.getString("eccName"));
            virtualECC.setTimezone(rs.getString("timezone"));
            virtualECC.setStreet1(rs.getString("street1"));
            virtualECC.setStreet2(rs.getString("street2"));
            virtualECC.setCity(rs.getString("city"));
            virtualECC.setState(rs.getString("state"));
            virtualECC.setPostal(rs.getString("postal"));
            virtualECC.setCountry(rs.getString("country"));
            virtualECC.setWeatherId(rs.getLong("weatherId"));
            virtualECC.setSystemType(VirtualECCType.values()[rs.getInt("systemType")]);
            virtualECC.setEnergyPlanId(rs.getLong("energyPlan_id"));
            return virtualECC;
        }
    };


    private RowMapper<AccountLocation> accountLocationsRowMapper = new RowMapper<AccountLocation>() {
        public AccountLocation mapRow(ResultSet rs, int rowNum) throws SQLException {
            AccountLocation accountLocation = new AccountLocation();
            accountLocation.setAccountName(rs.getString("accountName"));
            accountLocation.setAccountRole(AccountRole.values()[rs.getInt("role")]);
            accountLocation.setVirtualECC(new VirtualECC());
            accountLocation.getVirtualECC().setId(rs.getLong("id"));
            accountLocation.getVirtualECC().setAccountId(rs.getLong("account_id"));
            accountLocation.getVirtualECC().setName(rs.getString("eccName"));
            accountLocation.getVirtualECC().setTimezone(rs.getString("timezone"));
            accountLocation.getVirtualECC().setStreet1(rs.getString("street1"));
            accountLocation.getVirtualECC().setStreet2(rs.getString("street2"));
            accountLocation.getVirtualECC().setCity(rs.getString("city"));
            accountLocation.getVirtualECC().setState(rs.getString("state"));
            accountLocation.getVirtualECC().setPostal(rs.getString("postal"));
            accountLocation.getVirtualECC().setCountry(rs.getString("country"));
            accountLocation.getVirtualECC().setSystemType(VirtualECCType.values()[rs.getInt("systemType")]);
            accountLocation.getVirtualECC().setWeatherId(rs.getLong("weatherId"));
            accountLocation.getVirtualECC().setEnergyPlanId(rs.getLong("energyPlan_id"));
            return accountLocation;
        }
    };

    public VirtualECCDAO() {
        super("commander.virtualECC");
    }

    @Override
    public RowMapper<VirtualECC> getRowMapper() {
        return virtualECCRowMapper;
    }


    public synchronized VirtualECC update(final VirtualECC virtualECC) {
        if (virtualECC.getId() == null || virtualECC.getId().equals(0L)) {
            LOGGER.info("Adding new virtual ECC: {}", virtualECC);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(INSERT_VIRTUAL_ECC, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, virtualECC.getAccountId());
                    ps.setString(2, virtualECC.getName());
                    ps.setString(3, virtualECC.getTimezone());
                    ps.setLong(4, virtualECC.getSystemType().ordinal());
                    ps.setString(5, virtualECC.getStreet1());
                    ps.setString(6, virtualECC.getStreet2());
                    ps.setString(7, virtualECC.getCity());
                    ps.setString(8, virtualECC.getState());
                    ps.setString(9, virtualECC.getPostal());
                    ps.setString(10, virtualECC.getCountry());
                    ps.setLong(11, virtualECC.getWeatherId());
                    ps.setLong(12, virtualECC.getEnergyPlanId());
                    return ps;
                }
            }, keyHolder);
            virtualECC.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.info("updating virtual ECC: {}", virtualECC);
            getJdbcTemplate().update(UPDATE_VIRTUAL_ECC,
                    virtualECC.getName(),
                    virtualECC.getTimezone(),
                    virtualECC.getSystemType().ordinal(),
                    virtualECC.getStreet1(),
                    virtualECC.getStreet2(),
                    virtualECC.getCity(),
                    virtualECC.getState(),
                    virtualECC.getPostal(),
                    virtualECC.getCountry(),
                    virtualECC.getWeatherId(),
                    virtualECC.getEnergyPlanId(),
                    virtualECC.getId());
        }
        hazelcastService.getVirtualECCMap().put(virtualECC.getId(), virtualECC);
        return virtualECC;
    }

    public List<VirtualECC> findByAccount(Long accountId) {
        return getJdbcTemplate().query(SELECT_VIRTUAL_ECC_BY_ACCOUNT, new Object[]{accountId}, virtualECCRowMapper);
    }

    public List<AccountLocation> findByUser(Long userId) {
        return getJdbcTemplate().query(SELECT_BY_USERID, new Object[]{userId}, accountLocationsRowMapper);
    }


    private Cache<Long, List<VirtualECC>> getMtuAccountCache(Long accountId){
        Cache<Long, List<VirtualECC>> cache = accountCache.getIfPresent(accountId);
        if (cache == null){
            cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(60, TimeUnit.HOURS).maximumSize(10000).build();
            accountCache.put(accountId, cache);
        }
        return cache;
    }

    public List<VirtualECC> findByMTU(Long accountId, Long mtuId) {
        List<VirtualECC> list = getMtuAccountCache(accountId).getIfPresent(mtuId);
        if (list == null){
            try{
               list = getJdbcTemplate().query(SELECT_VIRTUAL_ECC_BY_MTU, new Object[]{accountId, mtuId}, virtualECCRowMapper);
            }catch(Exception ex){
                list = new ArrayList<>();
            }
            getMtuAccountCache(accountId).put(mtuId, list);
        }

        return list;
    }


    public synchronized VirtualECC findAlexaLocationForUser(Long userId) {
        try {
            return getJdbcTemplate().queryForObject(SELECT_ALEXA_LOCATION, new Object[]{userId}, virtualECCRowMapper);
        } catch (Exception ex) {
            return null;
        }
    }


    public synchronized VirtualECC findOneFromCache(Long id) {
        VirtualECC virtualECC = hazelcastService.getVirtualECCMap().getIfPresent(id);
        if (virtualECC == null) {
            virtualECC = findById(id);
            if (virtualECC != null) hazelcastService.getVirtualECCMap().put(id, virtualECC);
        }
        return virtualECC;
    }

    public List<VirtualECC> findAll() {
        return getJdbcTemplate().query("select * from virtualECC order by account_id", virtualECCRowMapper);
    }

}
