/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AdviceListPlace")
public class AdviceListPlace extends Place {

    final Long accountId;

    public AdviceListPlace(Long accountId) {
        this.accountId = accountId;
    }

    public AdviceListPlace(String token) {
        if (token != null && token.trim().length() != 0) {
            this.accountId = Long.parseLong(token);
        }
        else {
            accountId = null;
        }
    }

    public Long getAccountId() {
        return accountId;
    }

    public static class Tokenizer implements PlaceTokenizer<AdviceListPlace> {
        @Override
        public String getToken(AdviceListPlace place) {
            if (place.accountId != null) return place.accountId.toString();
            else return "";
        }

        @Override
        public AdviceListPlace getPlace(String token) {
            return new AdviceListPlace(token);
        }
    }
}
