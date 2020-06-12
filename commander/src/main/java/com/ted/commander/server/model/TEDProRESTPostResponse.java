/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;


import com.ted.commander.common.model.Account;

import java.util.ArrayList;
import java.util.List;

public class TEDProRESTPostResponse {


    List<Account> accountList;
    private Long id;
    private String msg;

    public TEDProRESTPostResponse() {
        accountList = new ArrayList<Account>();
    }

    public TEDProRESTPostResponse(Long id, String msg) {
        this.id = id;
        this.msg = msg;
        accountList = new ArrayList<Account>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }
}
