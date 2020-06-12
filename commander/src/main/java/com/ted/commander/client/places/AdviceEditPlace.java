/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

@Prefix("AdviceEditPlace")
public class AdviceEditPlace extends Place {

    final Long adviceId;

    public AdviceEditPlace(Long adviceId){
        this.adviceId = adviceId;
    }

    public AdviceEditPlace(String token) {
        this.adviceId = Long.parseLong(token);
    }

    public static class Tokenizer implements PlaceTokenizer<AdviceEditPlace> {
        @Override
        public String getToken(AdviceEditPlace place) {
            return place.adviceId.toString();
        }

        @Override
        public AdviceEditPlace getPlace(String token) {
            return new AdviceEditPlace(token);
        }
    }

    public Long getAdviceId() {
        return adviceId;
    }
}
