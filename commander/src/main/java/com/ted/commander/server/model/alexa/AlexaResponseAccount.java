/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.alexa;

import com.ted.commander.common.enums.AccountRole;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexaResponseAccount implements Serializable {
    Long accountId;
    String accountName;
    AccountRole accountRole;
    List<AlexaResponseLocation> locationList = new ArrayList<>();

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<AlexaResponseLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<AlexaResponseLocation> locationList) {
        this.locationList = locationList;
    }
}
