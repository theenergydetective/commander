/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.util.ArrayList;
import java.util.List;


public class AccountMembers {
    private List<AccountMember> accountMembers = new ArrayList<AccountMember>();

    public List<AccountMember> getAccountMembers() {
        return accountMembers;
    }

    public void setAccountMembers(List<AccountMember> accountMembers) {
        this.accountMembers = accountMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountMembers that = (AccountMembers) o;

        if (accountMembers != null ? !accountMembers.equals(that.accountMembers) : that.accountMembers != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accountMembers != null ? accountMembers.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AccountMembers{" +
                "accountMembers=" + accountMembers +
                '}';
    }
}
