/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;

import java.io.Serializable;

public class PushId implements Serializable {

    private Long userId;
    private String registrationId;
    private boolean ios = false;
    private boolean adm = false;

    public PushId() {
    }

    public PushId(long userId, String registrationid) {
        this.userId = userId;
        this.registrationId = registrationid;
    }

    public boolean isAdm() {
        return adm;
    }

    public void setAdm(boolean adm) {
        this.adm = adm;
    }

    public boolean isIos() {
        return ios;
    }

    public void setIos(boolean ios) {
        this.ios = ios;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PushId pushId = (PushId) o;

        if (ios != pushId.ios) return false;
        if (adm != pushId.adm) return false;
        if (userId != null ? !userId.equals(pushId.userId) : pushId.userId != null) return false;
        return registrationId != null ? registrationId.equals(pushId.registrationId) : pushId.registrationId == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (registrationId != null ? registrationId.hashCode() : 0);
        result = 31 * result + (ios ? 1 : 0);
        result = 31 * result + (adm ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PushId{" +
                "userId=" + userId +
                ", registrationId='" + registrationId + '\'' +
                ", ios=" + ios +
                ", adm=" + adm +
                '}';
    }
}
