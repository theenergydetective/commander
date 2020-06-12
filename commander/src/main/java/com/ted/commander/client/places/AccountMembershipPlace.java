/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AccountMembershipPlace")
public class AccountMembershipPlace extends Place {

    public AccountMembershipPlace(String token) {
    }

    public static class Tokenizer implements PlaceTokenizer<AccountMembershipPlace> {
        @Override
        public String getToken(AccountMembershipPlace place) {
            return "";
        }

        @Override
        public AccountMembershipPlace getPlace(String token) {
            return new AccountMembershipPlace(token);
        }
    }
}
