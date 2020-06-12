/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AccountMTUPlace")
public class AccountMTUPlace extends Place {

    public AccountMTUPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<AccountMTUPlace> {
        @Override
        public String getToken(AccountMTUPlace place) {
            return "";
        }

        @Override
        public AccountMTUPlace getPlace(String token) {
            return new AccountMTUPlace(token);
        }
    }
}
