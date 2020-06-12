/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ConfirmEmailPlace")
public class ConfirmEmailPlace extends Place {

    public ConfirmEmailPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ConfirmEmailPlace> {
        @Override
        public String getToken(ConfirmEmailPlace place) {
            return "";
        }

        @Override
        public ConfirmEmailPlace getPlace(String token) {
            return new ConfirmEmailPlace(token);
        }
    }
}
