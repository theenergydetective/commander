/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.alexa;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexaResponse implements Serializable {
    AlexaResponseStatus alexaResponseStatus = AlexaResponseStatus.INVALID_AUTH;
    String token;
    List<AlexaResponseAccount> accountList = new ArrayList<>();

    public AlexaResponseStatus getAlexaResponseStatus() {
        return alexaResponseStatus;
    }

    public void setAlexaResponseStatus(AlexaResponseStatus alexaResponseStatus) {
        this.alexaResponseStatus = alexaResponseStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<AlexaResponseAccount> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AlexaResponseAccount> accountList) {
        this.accountList = accountList;
    }
}
