/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by pete on 10/10/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastPost implements Serializable {
    String accountName;
    String eccName;
    private long lastPost;
    private long id;
    private int activeCount;
    private String mtuId;
    private String mtuDescription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEccName() {
        return eccName;
    }

    public void setEccName(String eccName) {
        this.eccName = eccName;
    }

    public String getMtuId() {
        return mtuId;
    }

    public void setMtuId(String mtuId) {
        this.mtuId = mtuId;
    }

    public String getMtuDescription() {
        return mtuDescription;
    }

    public void setMtuDescription(String mtuDescription) {
        this.mtuDescription = mtuDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LastPost lastPost1 = (LastPost) o;

        if (lastPost != lastPost1.lastPost) return false;
        if (accountName != null ? !accountName.equals(lastPost1.accountName) : lastPost1.accountName != null)
            return false;
        return eccName != null ? eccName.equals(lastPost1.eccName) : lastPost1.eccName == null;

    }

    @Override
    public int hashCode() {
        int result = accountName != null ? accountName.hashCode() : 0;
        result = 31 * result + (eccName != null ? eccName.hashCode() : 0);
        result = 31 * result + (int) (lastPost ^ (lastPost >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LastPost{" +
                "accountName='" + accountName + '\'' +
                ", eccName='" + eccName + '\'' +
                ", lastPost=" + lastPost +
                '}';
    }

    public long getLastPost() {
        return lastPost;
    }

    public void setLastPost(long lastPost) {
        this.lastPost = lastPost;
    }
}
