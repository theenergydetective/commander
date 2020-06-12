/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Calendar;


public class EnergyPostUtilTest {





    @Test
    public void testMeterMonth() {

        //Test Data
        TestData testDataArray[] = {
                //Base Test
                new TestData(1347598800, "US/Eastern", 1, 9),  //1am Sep 14 2012 EST   (Sep Bill)
                new TestData(1346475600, "US/Eastern", 1, 9),  //1am Sep 1 2012 EST    (Sep Bill)
                new TestData(1348981200, "US/Eastern", 1, 9),  //1am Sep 30 2012 EST   (Sep Bill)
                new TestData(1349067600, "US/Eastern", 1, 10),  //1am Oct 1 2012 EST   (Sep Bill)

                new TestData(1347598800, "US/Eastern", 15, 8),  //1am Sep 14 2012 EST   (August Bill)
                new TestData(1347685200, "US/Eastern", 15, 9),  //1am Sep 15 2012 EST
                new TestData(1347771600, "US/Eastern", 15, 9),  //1am Sep 16 2012 EST


                //Time zone test
                new TestData(1347598800, "US/Pacific", 1, 9),  //1am Sep 14 2012 EST   (Sep Bill)
                new TestData(1346475600, "US/Pacific", 1, 8),  //1am Sep 1 2012 EST    (Sep Bill)
                new TestData(1348981200, "US/Pacific", 1, 9),  //1am Sep 30 2012 EST   (Sep Bill)
                new TestData(1349067600, "US/Pacific", 1, 9),  //1am Oct 1 2012 EST   (Sep Bill)
                new TestData(1349082000, "US/Pacific", 1, 10),  //5am Oct 1 2012 EST   (Sep Bill)

                new TestData(1347598800, "US/Pacific", 15, 8),  //1am Sep 14 2012 EST   (August Bill)
                new TestData(1347685200, "US/Pacific", 15, 8),  //1am Sep 15 2012 EST
                new TestData(1347699600, "US/Pacific", 15, 9),  //5am Sep 15 2012 EST
                new TestData(1347771600, "US/Pacific", 15, 9),  //1am Sep 16 2012 EST

                //Short Month Test
                new TestData(1330408800, "US/Eastern", 31, 1),   //1am Feb 28 2012 EST  (Jan Bill)
                new TestData(1330581600, "US/Eastern", 31, 2),   //1am March 1 2012 EST (Feb Bill)
                new TestData(1333170000, "US/Eastern", 31, 3)    //1am March 31 2012 ESt (March Bill)
        };

        for (TestData testData : testDataArray) {

            Calendar calendar = EnergyPostUtil.getMeterMonth(testData.meterReadDay, testData.timeZone, testData.timestamp);
            Assert.assertEquals(calendar.get(Calendar.YEAR), 2012);
            Assert.assertEquals(calendar.get(Calendar.MONTH) + 1, testData.expectedMeterMonth);
        }

    }

    class TestData {
        int timestamp;
        String timeZone;
        int meterReadDay;
        int expectedMeterMonth;

        TestData(int timestamp, String timeZone, int meterReadDay, int expectedMeterMonth) {
            this.timestamp = timestamp;
            this.timeZone = timeZone;
            this.meterReadDay = meterReadDay;
            this.expectedMeterMonth = expectedMeterMonth;
        }
    }
}
