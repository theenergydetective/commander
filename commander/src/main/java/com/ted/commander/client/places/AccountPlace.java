/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AccountPlace")
public class AccountPlace extends Place {
    final Long accountId;

    public AccountPlace(Long id){
        this.accountId = id;
    }

    public AccountPlace(String token) {
        if (token != null && !token.trim().isEmpty()){
            accountId = Long.parseLong(token);
        } else {
            accountId = null;
        }
    }

    public Long getAccountId() {
        return accountId;
    }

    public static class Tokenizer implements PlaceTokenizer<AccountPlace> {
        @Override
        public String getToken(AccountPlace place) {
            return place.accountId + "";
        }

        @Override
        public AccountPlace getPlace(String token) {
            return new AccountPlace(token);
        }
    }
}
