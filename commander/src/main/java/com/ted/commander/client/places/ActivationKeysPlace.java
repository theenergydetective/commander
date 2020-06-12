/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ActivationSettingsPlace")
public class ActivationKeysPlace extends Place {

    public ActivationKeysPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ActivationKeysPlace> {
        @Override
        public String getToken(ActivationKeysPlace place) {
            return "";
        }

        @Override
        public ActivationKeysPlace getPlace(String token) {
            return new ActivationKeysPlace(token);
        }
    }
}
