/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.model.EnergyData;
import com.ted.commander.server.dao.BigNumberDAO;
import com.ted.commander.server.dao.EnergyDataDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service for fixing big number glitches in energy data
 */
@Service
public class BigNumberService {

    static Logger LOGGER = LoggerFactory.getLogger(BigNumberService.class);

    @Autowired
    BigNumberDAO bigNumberDAO;

    @Autowired
    EnergyDataDAO energyDataDAO;

    public void findBigNumbers(long accountId, long mtuId, long start, long stop, double thresh){
        List<EnergyData> bigNumbers = bigNumberDAO.findBigNumber(accountId, mtuId, start, stop);
        for (EnergyData ed:bigNumbers){
            if (ed.getEnergyDifference().doubleValue() >= thresh || ed.getEnergyDifference().doubleValue() <= (thresh * -1)){
                LOGGER.info("FIXING: {}", ed);
                fixOneBigNumber(ed);
            } else {
                LOGGER.info("SKIPPING: {}", ed);
            }
        }
    }


    public void fixOneBigNumber(EnergyData bigNumber){
        long accountId = bigNumber.getAccountId();
        long mtuId = bigNumber.getMtuId();

        LOGGER.info("Looking for next good number");
        int rowCount = 0;
        EnergyData goodRow = null;
        while (true){
            rowCount++;
            EnergyData testRow = energyDataDAO.findById(bigNumber.getTimeStamp() + (rowCount * 60), accountId, mtuId);
            if (testRow.getEnergyDifference() < 10000 && testRow.getEnergyDifference() > -10000){
                goodRow = testRow;
                break;
            }
            if (rowCount > 100) break;
        }
        LOGGER.info("GOOD ROW: COUNT:{} DATA:{}", rowCount, goodRow);


        EnergyData prevGoodRecord = energyDataDAO.findById(bigNumber.getTimeStamp()-60L, accountId, mtuId);

        double difference = (goodRow.getEnergy() - prevGoodRecord.getEnergy())/(rowCount+1);
        LOGGER.info("PROPER ENERGY DIFFERENCE IS {}", difference);



        for (int i=0; i < rowCount; i++){
            long timestamp = bigNumber.getTimeStamp() + (i*60);
            EnergyData recordToFix = energyDataDAO.findById(timestamp, accountId, mtuId);
            recordToFix.setEnergy(prevGoodRecord.getEnergy() + (difference * (i+1)));
            recordToFix.setEnergyDifference(difference);
            recordToFix.setPowerFactor(prevGoodRecord.getPowerFactor());
            recordToFix.setVoltage(prevGoodRecord.getVoltage());
            LOGGER.info("FIXED RECORD:{}", recordToFix);
            bigNumberDAO.update(recordToFix);

        }


        LOGGER.debug("Fixing Averages: 5");
        for (int i=0; i < 35; i++){
            long currTimestamp = bigNumber.getTimeStamp() + (i*60);
            long last5Timestamp = currTimestamp - (5*60);
            long last10Timestamp = currTimestamp - (10*60);
            long last15Timestamp = currTimestamp - (15*60);
            long last20Timestamp = currTimestamp - (20*60);
            long last30Timestamp = currTimestamp - (30*60);

            EnergyData currRecord = energyDataDAO.findById(currTimestamp, accountId, mtuId);
            EnergyData old5Record = energyDataDAO.findById(last5Timestamp, accountId, mtuId);
            EnergyData old10Record = energyDataDAO.findById(last10Timestamp, accountId, mtuId);
            EnergyData old15Record = energyDataDAO.findById(last15Timestamp, accountId, mtuId);
            EnergyData old20Record = energyDataDAO.findById(last20Timestamp, accountId, mtuId);
            EnergyData old30Record = energyDataDAO.findById(last30Timestamp, accountId, mtuId);

            Double avg5 = (currRecord.getEnergy() - old5Record.getEnergy())/5.0;
            Double avg10 = (currRecord.getEnergy() - old10Record.getEnergy())/10.0;
            Double avg15 = (currRecord.getEnergy() - old15Record.getEnergy())/15.0;
            Double avg20 = (currRecord.getEnergy() - old20Record.getEnergy())/20.0;
            Double avg30 = (currRecord.getEnergy() - old30Record.getEnergy())/30.0;

            currRecord.setAvg5(avg5);
            currRecord.setAvg10(avg10);
            currRecord.setAvg15(avg15);
            currRecord.setAvg20(avg20);
            currRecord.setAvg30(avg30);
            bigNumberDAO.update(currRecord);
        }

    }
}
