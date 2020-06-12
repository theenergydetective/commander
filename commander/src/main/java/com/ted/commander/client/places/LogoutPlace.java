/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("LogoutPlace")
public class LogoutPlace extends Place {

    public LogoutPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<LogoutPlace> {
        @Override
        public String getToken(LogoutPlace place) {
            return "";
        }

        @Override
        public LogoutPlace getPlace(String token) {
            return new LogoutPlace(token);
        }
    }
}
