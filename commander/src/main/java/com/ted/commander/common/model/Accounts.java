/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import java.util.ArrayList;
import java.util.List;


public class Accounts {
    private List<Account> accounts = new ArrayList<Account>();

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Accounts accounts = (Accounts) o;

        if (this.accounts != null ? !this.accounts.equals(accounts.accounts) : accounts.accounts != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accounts != null ? accounts.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Accounts{" +
                "accounts=" + accounts +
                '}';
    }
}
