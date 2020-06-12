/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("EnergyPlanPlace")
public class EnergyPlanPlace extends Place {

    final Long energyPlanId;

    public Long getEnergyPlanId() {
        return energyPlanId;
    }

    public EnergyPlanPlace(Long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    public EnergyPlanPlace(String token) {
        if (token != null && !token.isEmpty()) {
            energyPlanId = Long.parseLong(token);
        } else energyPlanId = 0l;
    }

    public static class Tokenizer implements PlaceTokenizer<EnergyPlanPlace> {
        @Override
        public String getToken(EnergyPlanPlace place) {
            if (place.energyPlanId == null) return "";
            return place.energyPlanId.toString();
        }

        @Override
        public EnergyPlanPlace getPlace(String token) {
            return new EnergyPlanPlace(token);
        }
    }
}
