/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ChangeEmailPlace")
public class ChangeEmailPlace extends Place {

    public ChangeEmailPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ChangeEmailPlace> {
        @Override
        public String getToken(ChangeEmailPlace place) {
            return "";
        }

        @Override
        public ChangeEmailPlace getPlace(String token) {
            return new ChangeEmailPlace(token);
        }
    }
}
