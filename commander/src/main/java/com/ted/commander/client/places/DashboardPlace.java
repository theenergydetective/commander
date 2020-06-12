/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("DashboardPlace")
public class DashboardPlace extends Place {

    final Long locationId;

    public Long getLocationId() {
        return locationId;
    }

    public DashboardPlace(Long locationId) {
        this.locationId = locationId;
    }

    public DashboardPlace(String token) {
        if (token == null || token.isEmpty()) locationId = null;
        else locationId = Long.parseLong(token);
    }

    public DashboardPlace() {
        locationId = null;
    }

    public static DashboardPlace get() {
        return new DashboardPlace("");
    }

    //TODO: Tokenize the options selected

    public static class Tokenizer implements PlaceTokenizer<DashboardPlace> {
        @Override
        public String getToken(DashboardPlace place) {
            if (place.locationId == null) return "";
            else return place.locationId.toString();
        }

        @Override
        public DashboardPlace getPlace(String token) {
            return new DashboardPlace(token);
        }
    }

    @Override
    public String toString() {
        return "DashboardPlace{" +
                "locationId=" + locationId +
                '}';
    }
}
