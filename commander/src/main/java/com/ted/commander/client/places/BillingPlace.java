/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("BillingPlace")
public class BillingPlace extends Place {

    public BillingPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<BillingPlace> {
        @Override
        public String getToken(BillingPlace place) {
            return "";
        }

        @Override
        public BillingPlace getPlace(String token) {
            return new BillingPlace(token);
        }
    }
}
