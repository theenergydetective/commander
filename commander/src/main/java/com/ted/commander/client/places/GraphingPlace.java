/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("GraphingPlace")
public class GraphingPlace extends Place {

    public GraphingPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<GraphingPlace> {
        @Override
        public String getToken(GraphingPlace place) {
            return "";
        }

        @Override
        public GraphingPlace getPlace(String token) {
            return new GraphingPlace(token);
        }
    }
}
