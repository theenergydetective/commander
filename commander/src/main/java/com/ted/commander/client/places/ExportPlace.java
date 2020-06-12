/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ExportPlace")
public class ExportPlace extends Place {

    public ExportPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ExportPlace> {
        @Override
        public String getToken(ExportPlace place) {
            return "";
        }

        @Override
        public ExportPlace getPlace(String token) {
            return new ExportPlace(token);
        }
    }
}
