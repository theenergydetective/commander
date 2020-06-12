/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.alexa;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexaRequestLocation implements Serializable {
    long locationId;
    String alexaName;

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getAlexaName() {
        return alexaName;
    }

    public void setAlexaName(String alexaName) {
        this.alexaName = alexaName;
    }

    @Override
    public String toString() {
        return "AlexaRequestLocation{" +
                "locationId=" + locationId +
                ", locationName='" + alexaName + '\'' +
                '}';
    }
}
