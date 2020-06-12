/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.admin;


import java.io.Serializable;


public class MTUAlarmEntry implements Serializable {

    String mtuId;
    String lastPostTime;

    public String getMtuId() {
        return mtuId;
    }

    public void setMtuId(String mtuId) {
        this.mtuId = mtuId;
    }

    public String getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(String lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

    @Override
    public String toString() {
        return "MTUAlarmEntry{" +
                "mtuId='" + mtuId + '\'' +
                ", lastPostTime='" + lastPostTime + '\'' +
                '}';
    }
}
