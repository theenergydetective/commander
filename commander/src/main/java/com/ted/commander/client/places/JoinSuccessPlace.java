/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("JoinSuccessPlace")
public class JoinSuccessPlace extends Place {

    public JoinSuccessPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<JoinSuccessPlace> {
        @Override
        public String getToken(JoinSuccessPlace place) {
            return "";
        }

        @Override
        public JoinSuccessPlace getPlace(String token) {
            return new JoinSuccessPlace(token);
        }
    }
}
