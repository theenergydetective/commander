/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("UserSettingsPlace")
public class UserSettingsPlace extends Place {

    public UserSettingsPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<UserSettingsPlace> {
        @Override
        public String getToken(UserSettingsPlace place) {
            return "";
        }

        @Override
        public UserSettingsPlace getPlace(String token) {
            return new UserSettingsPlace(token);
        }
    }
}
