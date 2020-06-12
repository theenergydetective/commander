/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("NoPostPlace")
public class NoPostPlace extends Place {

    public NoPostPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<NoPostPlace> {
        @Override
        public String getToken(NoPostPlace place) {
            return "";
        }

        @Override
        public NoPostPlace getPlace(String token) {
            return new NoPostPlace(token);
        }
    }
}
