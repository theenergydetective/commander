/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;


public class TEDProRESTJoinRequest {

    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private Boolean uploadSystem;
    private Boolean uploadUtility;

    private Long accountId;
    private Long eccId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getEccId() {
        return eccId;
    }

    public void setEccId(Long eccId) {
        this.eccId = eccId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUploadSystem() {
        return uploadSystem;
    }

    public void setUploadSystem(Boolean uploadSystem) {
        this.uploadSystem = uploadSystem;
    }

    public Boolean getUploadUtility() {
        return uploadUtility;
    }

    public void setUploadUtility(Boolean uploadUtility) {
        this.uploadUtility = uploadUtility;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
