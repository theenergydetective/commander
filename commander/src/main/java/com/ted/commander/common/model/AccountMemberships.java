/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.util.ArrayList;
import java.util.List;


public class AccountMemberships {
    private List<AccountMembership> accountMemberships = new ArrayList<AccountMembership>();

    public List<AccountMembership> getAccountMemberships() {
        return accountMemberships;
    }

    public void setAccountMemberships(List<AccountMembership> accountMemberships) {
        this.accountMemberships = accountMemberships;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountMemberships that = (AccountMemberships) o;

        if (accountMemberships != null ? !accountMemberships.equals(that.accountMemberships) : that.accountMemberships != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accountMemberships != null ? accountMemberships.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AccountMemberships{" +
                "accountMemberships=" + accountMemberships +
                '}';
    }
}
