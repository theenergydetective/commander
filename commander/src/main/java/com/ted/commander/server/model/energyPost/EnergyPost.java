/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.energyPost;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ted.commander.common.model.Account;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pete on 9/12/2014.
 */
@XmlRootElement(name = "ted5000")
public class EnergyPost implements Serializable {


    private String gateway;
    private String securityKey;
    private List<EnergyMTUPost> mtuList;
    private List<EnergyMTUPost> spyderList;
    private Account account;
    private String version;

    @JsonIgnore
    public Long getECCId() {
        return Long.parseLong(gateway, 16);
    }

    @XmlAttribute(name = "GWID")
    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    @XmlAttribute(name = "ver", required = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute(name = "auth")
    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @XmlElement(name = "MTU")
    public List<EnergyMTUPost> getMtuList() {
        if (mtuList == null) mtuList = new ArrayList<EnergyMTUPost>();
        return mtuList;
    }

    public void setMtuList(List<EnergyMTUPost> mtuList) {
        this.mtuList = mtuList;
    }

    @XmlElement(name = "SPY")
    public List<EnergyMTUPost> getSpyderList() {
        if (spyderList == null) spyderList = new ArrayList<EnergyMTUPost>();
        return spyderList;
    }

    public void setSpyderList(List<EnergyMTUPost> spyderList) {
        this.spyderList = spyderList;
    }

    @Override
    public String toString() {
        return "EnergyPost{" +
                "gateway='" + gateway + '\'' +
                ", securityKey='" + securityKey + '\'' +
                ", mtuList=" + mtuList +
                ", spyderList=" + spyderList +
                ", account=" + account +
                '}';
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
