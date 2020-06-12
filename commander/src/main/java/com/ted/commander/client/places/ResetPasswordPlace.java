/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ResetPasswordPlace")
public class ResetPasswordPlace extends Place {

    final String token;
    public ResetPasswordPlace(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static class Tokenizer implements PlaceTokenizer<ResetPasswordPlace> {
        @Override
        public String getToken(ResetPasswordPlace place) {
            return place.token;
        }

        @Override
        public ResetPasswordPlace getPlace(String token) {
            return new ResetPasswordPlace(token);
        }
    }
}
