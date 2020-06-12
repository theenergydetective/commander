/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import com.ted.commander.common.enums.VirtualECCRole;

import java.io.Serializable;

public class VirtualECCPermission implements Serializable {

    private Long id;
    private Long virtualECCId;
    private Long userId;
    private VirtualECCRole role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public VirtualECCRole getRole() {
        return role;
    }

    public void setRole(VirtualECCRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualECCPermission that = (VirtualECCPermission) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (role != that.role) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (virtualECCId != null ? virtualECCId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VirtualECCPermission{" +
                "id=" + id +
                ", virtualECCId=" + virtualECCId +
                ", accountId=" + userId +
                ", role=" + role +
                '}';
    }
}

