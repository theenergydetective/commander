/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import java.io.Serializable;

public class GroupVirtualECC implements Serializable {

    private Long id;
    private Long groupId;
    private Long virtualECCId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getVirtualECCId() {
        return virtualECCId;
    }

    public void setVirtualECCId(Long virtualECCId) {
        this.virtualECCId = virtualECCId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupVirtualECC that = (GroupVirtualECC) o;

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (virtualECCId != null ? !virtualECCId.equals(that.virtualECCId) : that.virtualECCId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (virtualECCId != null ? virtualECCId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GroupVirtualECC{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", virtualECCId=" + virtualECCId +
                '}';
    }
}

