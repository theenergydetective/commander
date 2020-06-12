package com.ted.commander.client.widgets.graph.model;

import com.ted.commander.common.model.CalendarKey;

/**
 * Created by pete on 8/13/2015.
 */
public class GraphDataPoint {

    final CalendarKey calendarKey;
    final Long timestamp;
    final Double value;

    public GraphDataPoint(CalendarKey calendarKey, Double value) {
        this.value = value;
        this.calendarKey = calendarKey;
        //Convert to a long value. Time zone isn't important here as the CalendarKey-> Date conversion is timezone agnostic (CalendarKey already
        //has the timezone applied to it).
        this.timestamp = calendarKey.toLocalEpoch();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GraphDataPoint{" +
                "calendarKey=" + calendarKey +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
