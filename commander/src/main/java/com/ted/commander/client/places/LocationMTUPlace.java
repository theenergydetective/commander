/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("LocationMTUPlace")
public class LocationMTUPlace extends Place {

    public LocationMTUPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<LocationMTUPlace> {
        @Override
        public String getToken(LocationMTUPlace place) {
            return "";
        }

        @Override
        public LocationMTUPlace getPlace(String token) {
            return new LocationMTUPlace(token);
        }
    }
}
