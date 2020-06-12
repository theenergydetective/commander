/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.ted.commander.common.enums.AccountRole;

import java.io.Serializable;

/**
 * Represents an account member in an acocunt
 */

public class AccountMember implements Serializable {

    private Long id;
    private Long accountId;
    private Long userId;
    private AccountRole accountRole;
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        if (user != null) return user.getId();
        return userId;
    }

    public void setUserId(Long userId) {
        if (user == null) this.userId = userId;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountMember that = (AccountMember) o;

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (accountRole != that.accountRole) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (accountRole != null ? accountRole.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccountMember{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", userId=" + userId +
                ", accountRole=" + accountRole +
                '}';
    }

    public boolean canEditUsers() {
        if (accountRole.equals(AccountRole.OWNER)) return true;
        if (accountRole.equals(AccountRole.ADMIN)) return true;
        return false;
    }
}
