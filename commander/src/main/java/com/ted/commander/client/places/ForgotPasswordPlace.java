/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("ForgotPasswordPlace")
public class ForgotPasswordPlace extends Place {

    public ForgotPasswordPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<ForgotPasswordPlace> {
        @Override
        public String getToken(ForgotPasswordPlace place) {
            return "";
        }

        @Override
        public ForgotPasswordPlace getPlace(String token) {
            return new ForgotPasswordPlace(token);
        }
    }
}
