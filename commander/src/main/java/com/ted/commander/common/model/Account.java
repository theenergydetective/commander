/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.ted.commander.common.enums.AccountState;

import java.io.Serializable;

/**
 * Represents an account enrolled in the com.com.ted.commander system.
 */

public class Account implements Serializable {

    private Long id;
    private String name;
    private long createDate;
    private String activationKey;
    private String phoneNumber;
    private AccountState accountState;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (createDate != account.createDate) return false;
        if (accountState != account.accountState) return false;
        if (activationKey != null ? !activationKey.equals(account.activationKey) : account.activationKey != null)
            return false;
        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (name != null ? !name.equals(account.name) : account.name != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(account.phoneNumber) : account.phoneNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (createDate ^ (createDate >>> 32));
        result = 31 * result + (activationKey != null ? activationKey.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (accountState != null ? accountState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createDate=" + createDate +
                ", activationKey='" + activationKey + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", accountState=" + accountState +
                '}';
    }
}
