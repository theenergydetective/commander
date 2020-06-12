/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model.history;


import com.ted.commander.common.model.CalendarKey;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface History extends Serializable {
    public CalendarKey getCalendarKey();

    public void setCalendarKey(CalendarKey calendarKey);
}
