/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;


public class ActivationConfigPost {

    private Long account;
    private Long energyPlanId;
    private String xmlPayload;
    private String description;
    private String username;
    private String password;

    public Long getEnergyPlanId() {
        return energyPlanId;
    }

    public void setEnergyPlanId(Long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public String getXmlPayload() {
        return xmlPayload;
    }

    public void setXmlPayload(String xmlPayload) {
        this.xmlPayload = xmlPayload;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ActivationConfigPost{" +
                "account=" + username +
                ",account=" + account +
                ", energyPlanId=" + energyPlanId +
                ", xmlPayload='" + xmlPayload + '\'' +
                ", getDescription='" + description + '\'' +
                '}';
    }
}
