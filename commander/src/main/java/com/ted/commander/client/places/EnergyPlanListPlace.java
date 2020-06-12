/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("EnergyPlanListPlace")
public class EnergyPlanListPlace extends Place {

    final Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public EnergyPlanListPlace(String token) {
        if (token != null && !token.isEmpty()) {
            accountId = Long.parseLong(token);
        } else accountId = null;
    }

    public EnergyPlanListPlace(Long accountId) {
        this.accountId = accountId;
    }

    public static class Tokenizer implements PlaceTokenizer<EnergyPlanListPlace> {
        @Override
        public String getToken(EnergyPlanListPlace place) {
            if (place.accountId == null) return "";
            return place.accountId.toString();
        }

        @Override
        public EnergyPlanListPlace getPlace(String token) {
            return new EnergyPlanListPlace(token);
        }
    }
}
