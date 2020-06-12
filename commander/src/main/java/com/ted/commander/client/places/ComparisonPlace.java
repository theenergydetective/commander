/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ComparisonPlace")
public class ComparisonPlace extends Place {

    public ComparisonPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ComparisonPlace> {
        @Override
        public String getToken(ComparisonPlace place) {
            return "";
        }

        @Override
        public ComparisonPlace getPlace(String token) {
            return new ComparisonPlace(token);
        }
    }
}
