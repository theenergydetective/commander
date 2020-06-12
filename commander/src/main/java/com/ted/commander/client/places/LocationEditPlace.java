/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("LocationEditPlace")
public class LocationEditPlace extends Place {

    final Long virtualECCId;

    public LocationEditPlace(Long id){
        this.virtualECCId = id;
    }

    public LocationEditPlace(String token) {
        if (token != null && !token.trim().isEmpty()){
            virtualECCId = Long.parseLong(token);
        } else {
            virtualECCId = null;
        }
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public static class Tokenizer implements PlaceTokenizer<LocationEditPlace> {
        @Override
        public String getToken(LocationEditPlace place) {
            return place.virtualECCId.toString();
        }

        @Override
        public LocationEditPlace getPlace(String token) {
            return new LocationEditPlace(token);
        }
    }
}
