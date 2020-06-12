/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.MTU;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.server.dao.LastPostDAO;
import com.ted.commander.server.dao.MTUDAO;
import com.ted.commander.server.dao.VirtualECCDAO;
import com.ted.commander.server.dao.VirtualECCMTUDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Interface for getting data from openweather.org
 */
@Service
public class MaintenanceService {

    static Logger LOGGER = LoggerFactory.getLogger(MaintenanceService.class);
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
    @Autowired
    LastPostDAO lastPostDAO;
    @Autowired
    EmailService emailService;
    @Autowired
    ServerService serverService;

    @Autowired
    private DataSource commanderDataSource;

    @Autowired
    MTUDAO mtuDAO;

    @Autowired
    VirtualECCMTUDAO virtualECCMTUDAO;

    @Autowired
    VirtualECCDAO virtualECCDAO;


    boolean inAlarm = false;


    private void executeQuery(String q){
        LOGGER.warn("Executing {}", q);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(commanderDataSource);
        jdbcTemplate.setQueryTimeout(24 * 3600 * 30 * 1000);
        try {
            jdbcTemplate.execute(q);

        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
        }
    }

    //@Scheduled(cron = "00 15 04 * * * ")
    public void deleteOldData() {

        Calendar twoYearCalendar = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        twoYearCalendar.setTime(new Date());
        twoYearCalendar.set(Calendar.HOUR_OF_DAY, 0);
        twoYearCalendar.set(Calendar.MINUTE, 0);
        twoYearCalendar.set(Calendar.HOUR_OF_DAY, 0);
        twoYearCalendar.set(Calendar.SECOND, 0);
        twoYearCalendar.set(Calendar.MILLISECOND, 0);
        twoYearCalendar.add(Calendar.DATE, -1);
        twoYearCalendar.add(Calendar.YEAR, -2);



        Calendar dukeCalendar = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        dukeCalendar.setTime(new Date());
        dukeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        dukeCalendar.set(Calendar.MINUTE, 0);
        dukeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        dukeCalendar.set(Calendar.SECOND, 0);
        dukeCalendar.set(Calendar.MILLISECOND, 0);
        dukeCalendar.add(Calendar.DATE, -1);
        dukeCalendar.add(Calendar.YEAR, -1);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -1);
        calendar.add(Calendar.MONTH, -6);

        List<MTU> mtuList = mtuDAO.findAll();


        for (MTU mtu: mtuList){

            StringBuilder energyDataDelete = new StringBuilder();
            energyDataDelete.append("delete from energydata ");
            energyDataDelete.append(" where mtu_id = ").append(mtu.getId());
            energyDataDelete.append(" and account_id = ").append(mtu.getAccountId());
            if (mtu.getAccountId().equals(164l)) {
                energyDataDelete.append(" and timestamp < ").append((long)(dukeCalendar.getTimeInMillis() / 1000));
            } else if (mtu.getAccountId().equals(164l)) {
                energyDataDelete.append(" and timestamp < ").append((long)(twoYearCalendar.getTimeInMillis() / 1000));
            } else {
                energyDataDelete.append(" and timestamp < ").append((long)(calendar.getTimeInMillis() / 1000));
            }
            executeQuery(energyDataDelete.toString());

        }

        List<VirtualECC> virtualECCList = virtualECCDAO.findAll();
        for (VirtualECC ecc: virtualECCList){
            StringBuilder deleteQ =new StringBuilder();
            deleteQ.append("delete from history_minute2 where virtualECC_id=").append(ecc.getId());
            if (ecc.getAccountId().equals(164l)) {
                deleteQ.append(" and startEpoch < ").append((long) (dukeCalendar.getTimeInMillis() / 1000));
            } else {
                deleteQ.append(" and startEpoch < ").append((long) (calendar.getTimeInMillis() / 1000));
            }

            executeQuery(deleteQ.toString());
        }

//        executeQuery("OPTIMIZE table history_billingCycle;");
//        executeQuery("OPTIMIZE table history_day2;");
//        executeQuery("OPTIMIZE table history_hour2;");
//        executeQuery("OPTIMIZE table history_minute2;");
//
//        executeQuery("OPTIMIZE table history_mtu_billingCycle;");
//        executeQuery("OPTIMIZE table history_mtu_day2;");
//        executeQuery("OPTIMIZE table history_mtu_hour2;");
//        executeQuery("OPTIMIZE table energydata;");



    }

}
