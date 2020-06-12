/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO for accessing the EnergyPlan object
 */
@Repository
public class ResourceDAO extends SimpleAbstractDAO {

    List<String> utiltiyNameList =null;

    public static final String QUERY_UTILITY = "select distinct utility_name from utility_resource where UPPER(utility_name) like ? or UPPER(utility_loc) like ? order by utility_name limit ?";

    public List<String> findUtilityName(String query, int limit) {
        if (utiltiyNameList == null){
            utiltiyNameList = getJdbcTemplate().queryForList(QUERY_UTILITY, new Object[]{"%" + query.toUpperCase() + "%", "%" + query.toUpperCase() + "%", limit}, String.class);
        }
        return utiltiyNameList;
    }
}
