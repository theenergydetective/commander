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

public class LineTimeDatumFunction implements DatumFunction<Double> {

    final protected TimeScale timeScale;
    final long minEpoch;
    final long timeInterval;
    final int leftMargin;


    public LineTimeDatumFunction(int leftMargin, TimeScale timeScale, long minEpoch, long timeInterval) {
        this.timeScale = timeScale;
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
            timeStamp /= timeInterval;
            Double xApplied = timeScale.apply(Math.ceil(timeStamp)).asDouble();
            return leftMargin + xApplied;
        } else {
            CalendarKey calendarKey = dataPoint.getCalendarKey();
            Date epochDate = new Date(minEpoch * 1000);
            int monthsDiff = calendarKey.monthDiff(new CalendarKey(epochDate));
            Double xApplied = timeScale.apply(monthsDiff).asDouble();
            return leftMargin + xApplied;
        }
    }
}
