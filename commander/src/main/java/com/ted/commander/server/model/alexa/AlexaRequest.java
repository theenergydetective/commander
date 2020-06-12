/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.alexa;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexaRequest implements Serializable {
    String email;
    String password;
    Long locationId;
    AlexaTokens alexaTokens;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public AlexaTokens getAlexaTokens() {
        return alexaTokens;
    }

    public void setAlexaTokens(AlexaTokens alexaTokens) {
        this.alexaTokens = alexaTokens;
    }

    @Override
    public String toString() {
        return "AlexaRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", locationId=" + locationId +
                ", alexaTokens=" + alexaTokens +
                '}';
    }
}
