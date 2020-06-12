/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.ted.commander.common.enums.AccountRole;

import java.io.Serializable;

/**
 * Represents an account member in an acocunt
 */

public class AccountMembership implements Serializable {

    private Account account;
    private AccountRole accountRole;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountMembership that = (AccountMembership) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (accountRole != that.accountRole) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + (accountRole != null ? accountRole.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccountMembership{" +
                "account=" + account +
                ", accountRole=" + accountRole +
                '}';
    }

    public boolean isAdmin() {
        if (getAccountRole().equals(AccountRole.OWNER)) return true;
        if (getAccountRole().equals(AccountRole.ADMIN)) return true;
        return false;
    }

    public boolean isEditor() {
        if (getAccountRole().equals(AccountRole.OWNER)) return true;
        if (getAccountRole().equals(AccountRole.ADMIN)) return true;
        if (getAccountRole().equals(AccountRole.EDIT_ECCS)) return true;
        return false;

    }
}
