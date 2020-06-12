/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.AccountRole;

/**
 * Created by pete on 10/22/2014.
 */
public class AccountLocation {

    String accountName;
    AccountRole accountRole;
    VirtualECC virtualECC;

    public AccountLocation() {
    }

    public AccountLocation(String accountName, AccountRole accountRole, VirtualECC virtualECC) {
        this.accountName = accountName;
        this.accountRole = accountRole;
        this.virtualECC = virtualECC;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    public VirtualECC getVirtualECC() {
        return virtualECC;
    }

    public void setVirtualECC(VirtualECC virtualECC) {
        this.virtualECC = virtualECC;
    }
}
