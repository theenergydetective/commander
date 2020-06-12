/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.admin;


import java.io.Serializable;


public class MTULastPost implements Serializable {

    Long id;
    String description;
    Long lastPostTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(Long lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

}
