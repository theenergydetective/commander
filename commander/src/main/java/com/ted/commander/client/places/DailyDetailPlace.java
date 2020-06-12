/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.ted.commander.common.model.CalendarKey;

import java.util.HashMap;

@Prefix("DailyDetailPlace")
public class DailyDetailPlace extends Place {

    final Long locationId;
    final CalendarKey calendarKey;


    public DailyDetailPlace(Long locationId, CalendarKey date){
        this.locationId = locationId;
        this.calendarKey = date;
    }

    public DailyDetailPlace(String token) {
        HashMap<String, String> parameters = PlaceUtil.parseParameters(token);
        locationId = Long.parseLong(parameters.get("l"));
        calendarKey = new CalendarKey(
                Integer.parseInt(parameters.get("y")),
                Integer.parseInt(parameters.get("m")),
                Integer.parseInt(parameters.get("d")));
    }


    public Long getLocationId() {
        return locationId;
    }

    public CalendarKey getCalendarKey() {
        return calendarKey;
    }



    public static class Tokenizer implements PlaceTokenizer<DailyDetailPlace> {
        @Override
        public String getToken(DailyDetailPlace place) {
            StringBuilder params = new StringBuilder();
            params.append("l=").append(place.locationId.toString());
            params.append("&m=").append(place.calendarKey.getMonth());
            params.append("&d=").append(place.calendarKey.getDate());
            params.append("&y=").append(place.calendarKey.getYear());
            return params.toString();
        }

        @Override
        public DailyDetailPlace getPlace(String token) {
            return new DailyDetailPlace(token);
        }
    }
}
