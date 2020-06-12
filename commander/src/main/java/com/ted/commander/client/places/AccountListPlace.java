/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AccountListPlace")
public class AccountListPlace extends Place {

    public AccountListPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<AccountListPlace> {
        @Override
        public String getToken(AccountListPlace place) {
            return "";
        }

        @Override
        public AccountListPlace getPlace(String token) {
            return new AccountListPlace(token);
        }
    }
}
