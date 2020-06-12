/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("GraphingOptionsPlace")
public class GraphingOptionsPlace extends Place {

    public GraphingOptionsPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<GraphingOptionsPlace> {
        @Override
        public String getToken(GraphingOptionsPlace place) {
            return "";
        }

        @Override
        public GraphingOptionsPlace getPlace(String token) {
            return new GraphingOptionsPlace(token);
        }
    }
}
