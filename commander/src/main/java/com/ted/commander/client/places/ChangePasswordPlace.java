/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ChangePasswordPlace")
public class ChangePasswordPlace extends Place {

    public ChangePasswordPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ChangePasswordPlace> {
        @Override
        public String getToken(ChangePasswordPlace place) {
            return "";
        }

        @Override
        public ChangePasswordPlace getPlace(String token) {
            return new ChangePasswordPlace(token);
        }
    }
}
