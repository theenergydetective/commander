package com.ted.commander.server.util;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pete on 4/18/2016.
 */
public class AverageUtils {


    public static double calculateAverage(List<Double> values) {
        double sum = 0.0;
        if(!values.isEmpty()) {
            for (Double v: values) {
                sum += v.doubleValue();
            }
            double retVal = sum / (double)values.size();
            return retVal;
        }
        return sum;
    }

    public static int calculateIntAverage(List <Integer> values) {
        int sum = 0;
        if(!values.isEmpty()) {
            for (Integer v: values) {
                sum += v.doubleValue();
            }
            int retVal = sum / values.size();
            return retVal;
        }
        return sum;
    }


    public static int getMaxMinute(long epoch, TimeZone timeZone){
        Calendar timeOfDayCalendar = Calendar.getInstance(timeZone);
        timeOfDayCalendar.setTimeInMillis(epoch * 1000);
        int maxMinute = (timeOfDayCalendar.get(Calendar.HOUR_OF_DAY) * 60) + timeOfDayCalendar.get(Calendar.MINUTE);
        return maxMinute;
    }
}
