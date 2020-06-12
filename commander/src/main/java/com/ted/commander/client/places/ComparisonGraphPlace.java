/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ComparisonGraphPlace")
public class ComparisonGraphPlace extends Place {

    public ComparisonGraphPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ComparisonGraphPlace> {
        @Override
        public String getToken(ComparisonGraphPlace place) {
            return "";
        }

        @Override
        public ComparisonGraphPlace getPlace(String token) {
            return new ComparisonGraphPlace(token);
        }
    }
}
