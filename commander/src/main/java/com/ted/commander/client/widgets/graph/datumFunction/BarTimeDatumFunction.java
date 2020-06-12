/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.graph.datumFunction;

import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.time.TimeScale;
import com.google.gwt.dom.client.Element;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.common.model.CalendarKey;

import java.util.Date;
import java.util.logging.Logger;

public class BarTimeDatumFunction implements DatumFunction<Double> {
    static final Logger LOGGER = Logger.getLogger(BarTimeDatumFunction.class.getName());
    final protected TimeScale timeScale;
    final int offset;
    final long minEpoch;
    final int timeInterval;
    final int leftMargin;

    public BarTimeDatumFunction(int leftMargin, TimeScale timeScale, int offset, long minEpoch, int timeInterval) {
        this.timeScale = timeScale;
        this.offset = offset;
        this.minEpoch = minEpoch;
        this.timeInterval = timeInterval;
        this.leftMargin = leftMargin;
    }

    public static boolean isDST(Date date){
        Date newYearsMidnight = new Date(date.getYear(), date.getMonth(), 1, 0, 0 ,0);
        return newYearsMidnight.getTimezoneOffset() == date.getTimezoneOffset();
    };

    @Override
    public Double apply(Element element, Value d, int i) {
        GraphDataPoint dataPoint = ((GraphDataPoint) d.as());
        return apply(dataPoint, i);
    }

    public Double apply(GraphDataPoint dataPoint, int i) {
        if (timeInterval > 1) {

            double timeStamp = dataPoint.getTimestamp();
            timeStamp -= minEpoch;
            timeStamp /= (double)timeInterval;
            Double xApplied = timeScale.apply(Math.ceil(timeStamp)).asDouble();
            return leftMargin + xApplied + offset;


        } else {
            CalendarKey calendarKey = dataPoint.getCalendarKey();
            Date epochDate = new Date(minEpoch * 1000);
            int monthsDiff = calendarKey.monthDiff(new CalendarKey(epochDate));
            //LOGGER.severe(dataPoint.getValue()/1000.0 + " monthsDiff:" + monthsDiff + " offset:" + offset);
            Double xApplied = timeScale.apply(monthsDiff).asDouble();
            return leftMargin + xApplied + offset;



        }



    }
}
