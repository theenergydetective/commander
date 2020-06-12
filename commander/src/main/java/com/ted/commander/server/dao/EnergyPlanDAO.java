/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.dao;

import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanKey;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

/**
 * DAO for accessing the EnergyPlan object
 */
@Repository
public class EnergyPlanDAO extends AbstractDAO<EnergyPlan> {


    public static final String CREATE_QUERY = "insert into commander.energyPlan (account_id, rateType, utilityName, meterReadDate, meterReadCycle, planType,touApplicableSaturday,touApplicableSunday, touApplicableHoliday, holidaySchedule_id, demandPlanType,demandUseActivePower,demandApplicableSaturday,demandApplicableSunday,demandApplicableHoliday,demandApplicableOffPeak, demandAverageTime, description,buybackRate,buyback,numberSeasons,numberTOU,numberTier,numberDemandSteps,surcharge, fixed, minimum, taxes) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String UPDATE_QUERY = "update commander.energyPlan set account_id=?,rateType=?, utilityName=?, meterReadDate=?, meterReadCycle=?, planType=?,touApplicableSaturday=?,touApplicableSunday=?, touApplicableHoliday=?, holidaySchedule_id=?, demandPlanType=?,demandUseActivePower=?,demandApplicableSaturday=?,demandApplicableSunday=?,demandApplicableHoliday=?,demandApplicableOffPeak=?, demandAverageTime=?, description=?,buybackRate=?,buyback=?,numberSeasons=?,numberTOU=?,numberTier=?,numberDemandSteps=?,surcharge=?,fixed=?,minimum=?,taxes=? where id = ?";
    public static final String SELECT_BY_ACCOUNT = "select id, account_id, rateType, utilityName, meterReadDate, meterReadCycle, planType,touApplicableSaturday,touApplicableSunday, touApplicableHoliday, holidaySchedule_id, demandPlanType,demandUseActivePower,demandApplicableSaturday,demandApplicableSunday,demandApplicableHoliday,demandApplicableOffPeak, demandAverageTime, description,buybackRate,buyback,numberSeasons,numberTOU,numberTier,numberDemandSteps,surcharge, fixed, minimum, taxes from commander.energyPlan where account_id=?";

    private RowMapper<EnergyPlan> rowMapper = new RowMapper<EnergyPlan>() {
        public EnergyPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyPlan energyPlan = new EnergyPlan();
            energyPlan.setId(rs.getLong("id"));
            energyPlan.setAccountId(rs.getLong("account_id"));
            energyPlan.setRateType(rs.getString("rateType"));
            energyPlan.setUtilityName(rs.getString("utilityName"));
            energyPlan.setMeterReadDate(rs.getInt("meterReadDate"));
            energyPlan.setMeterReadCycle(MeterReadCycle.values()[rs.getInt("meterReadCycle")]);
            energyPlan.setPlanType(PlanType.values()[rs.getInt("planType")]);
            energyPlan.setTouApplicableSaturday(rs.getBoolean("touApplicableSaturday"));
            energyPlan.setTouApplicableSunday(rs.getBoolean("touApplicableSunday"));
            energyPlan.setTouApplicableHoliday(rs.getBoolean("touApplicableHoliday"));
            energyPlan.setHolidayScheduleId(rs.getLong("holidaySchedule_id"));
            energyPlan.setDemandPlanType(DemandPlanType.values()[rs.getInt("demandPlanType")]);
            energyPlan.setDemandUseActivePower(rs.getBoolean("demandUseActivePower"));
            energyPlan.setDemandApplicableSaturday(rs.getBoolean("demandApplicableSaturday"));
            energyPlan.setDemandApplicableSunday(rs.getBoolean("demandApplicableSunday"));
            energyPlan.setDemandApplicableHoliday(rs.getBoolean("demandApplicableHoliday"));
            energyPlan.setDemandApplicableOffPeak(rs.getBoolean("demandApplicableOffPeak"));
            energyPlan.setDemandAverageTime(rs.getInt("demandAverageTime"));
            energyPlan.setDescription(rs.getString("description"));
            energyPlan.setUtilityName(rs.getString("utilityName"));
            energyPlan.setRateType(rs.getString("rateType"));
            energyPlan.setBuybackRate(rs.getDouble("buybackRate"));
            energyPlan.setBuyback(rs.getBoolean("buyback"));
            energyPlan.setNumberSeasons(rs.getInt("numberSeasons"));
            energyPlan.setNumberTOU(rs.getInt("numberTOU"));
            energyPlan.setNumberTier(rs.getInt("numberTier"));
            energyPlan.setNumberDemandSteps(rs.getInt("numberDemandSteps"));
            energyPlan.setSurcharge(rs.getBoolean("surcharge"));
            energyPlan.setFixed(rs.getBoolean("fixed"));
            energyPlan.setMinimum(rs.getBoolean("minimum"));
            energyPlan.setTaxes(rs.getBoolean("taxes"));

            return energyPlan;
        }
    };

    private RowMapper<EnergyPlanKey> energyPlanKeyRowMapper = new RowMapper<EnergyPlanKey>() {
        public EnergyPlanKey mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyPlanKey energyPlanKey = new EnergyPlanKey();
            energyPlanKey.setId(rs.getLong("id"));
            energyPlanKey.setAccountId(rs.getLong("account_id"));
            energyPlanKey.setDescription(rs.getString("description"));
            energyPlanKey.setUtilityName(rs.getString("utilityName"));
            energyPlanKey.setPlanType(PlanType.values()[rs.getInt("planType")]);
            return energyPlanKey;
        }
    };

    public EnergyPlanDAO() {
        super("commander.energyPlan");
    }

    @Override
    public RowMapper<EnergyPlan> getRowMapper() {
        return rowMapper;
    }

    public EnergyPlan update(final EnergyPlan energyPlan) {
        if (energyPlan.getId() == null || energyPlan.getId() == 0) {
            LOGGER.info("CREATING NEW EnergyPlan: {}", energyPlan);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, energyPlan.getAccountId());
                    ps.setString(2, energyPlan.getRateType());
                    ps.setString(3, energyPlan.getUtilityName());
                    ps.setInt(4, energyPlan.getMeterReadDate());
                    ps.setInt(5, energyPlan.getMeterReadCycle().ordinal());
                    ps.setInt(6, energyPlan.getPlanType().ordinal());
                    ps.setBoolean(7, energyPlan.getTouApplicableSaturday());
                    ps.setBoolean(8, energyPlan.getTouApplicableSunday());
                    ps.setBoolean(9, energyPlan.getTouApplicableHoliday());
                    ps.setLong(10, energyPlan.getHolidayScheduleId());
                    ps.setInt(11, energyPlan.getDemandPlanType().ordinal());
                    ps.setBoolean(12, energyPlan.getDemandUseActivePower());
                    ps.setBoolean(13, energyPlan.getDemandApplicableSaturday());
                    ps.setBoolean(14, energyPlan.getDemandApplicableSunday());
                    ps.setBoolean(15, energyPlan.getDemandApplicableHoliday());
                    ps.setBoolean(16, energyPlan.getDemandApplicableOffPeak());
                    ps.setInt(17, energyPlan.getDemandAverageTime());
                    ps.setString(18, energyPlan.getDescription());
                    ps.setDouble(19, energyPlan.getBuybackRate());
                    ps.setBoolean(20, energyPlan.isBuyback());
                    ps.setInt(21, energyPlan.getNumberSeasons());
                    ps.setInt(22, energyPlan.getNumberTOU());
                    ps.setInt(23, energyPlan.getNumberTier());
                    ps.setInt(24, energyPlan.getNumberDemandSteps());
                    ps.setBoolean(25, energyPlan.isSurcharge());
                    ps.setBoolean(26, energyPlan.isFixed());
                    ps.setBoolean(27, energyPlan.isMinimum());
                    ps.setBoolean(28, energyPlan.isTaxes());
                    return ps;
                }
            }, keyHolder);

            energyPlan.setId(keyHolder.getKey().longValue());
        } else {
            LOGGER.info("UPDATING EnergyPlan: {}", energyPlan);
            getJdbcTemplate().update(UPDATE_QUERY,
                    energyPlan.getAccountId(),
                    energyPlan.getRateType(),
                    energyPlan.getUtilityName(),
                    energyPlan.getMeterReadDate(),
                    energyPlan.getMeterReadCycle().ordinal(),
                    energyPlan.getPlanType().ordinal(),
                    energyPlan.getTouApplicableSaturday(),
                    energyPlan.getTouApplicableSunday(),
                    energyPlan.getTouApplicableHoliday(),
                    energyPlan.getHolidayScheduleId(),
                    energyPlan.getDemandPlanType().ordinal(),
                    energyPlan.getDemandUseActivePower(),
                    energyPlan.getDemandApplicableSaturday(),
                    energyPlan.getDemandApplicableSunday(),
                    energyPlan.getDemandApplicableHoliday(),
                    energyPlan.getDemandApplicableOffPeak(),
                    energyPlan.getDemandAverageTime(),
                    energyPlan.getDescription(),
                    energyPlan.getBuybackRate(),
                    energyPlan.isBuyback(),
                    energyPlan.getNumberSeasons(),
                    energyPlan.getNumberTOU(),
                    energyPlan.getNumberTier(),
                    energyPlan.getNumberDemandSteps(),
                    energyPlan.isSurcharge(),
                    energyPlan.isFixed(),
                    energyPlan.isMinimum(),
                    energyPlan.isTaxes(),
                    energyPlan.getId());
        }
        return energyPlan;
    }


    public List<EnergyPlan> findByAccount(Long accountId) {
        return getJdbcTemplate().query(SELECT_BY_ACCOUNT, new Object[]{accountId}, getRowMapper());
    }

    public List<EnergyPlanKey> findKeysByAccount(Long accountId) {
        return getJdbcTemplate().query(SELECT_BY_ACCOUNT, new Object[]{accountId}, energyPlanKeyRowMapper);
    }
}
