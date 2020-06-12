/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("JoinPlace")
public class JoinPlace extends Place {

    public JoinPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<JoinPlace> {
        @Override
        public String getToken(JoinPlace place) {
            return "";
        }

        @Override
        public JoinPlace getPlace(String token) {
            return new JoinPlace(token);
        }
    }
}
