/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AccountListPlace")
public class LocationListPlace extends Place {

    public LocationListPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<LocationListPlace> {
        @Override
        public String getToken(LocationListPlace place) {
            return "";
        }

        @Override
        public LocationListPlace getPlace(String token) {
            return new LocationListPlace(token);
        }
    }
}
