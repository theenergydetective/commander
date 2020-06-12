/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;

import java.io.Serializable;

/**
 * This is a model that contains the server runtime properties (mostly used by the
 * client as the Spring properties file can be referenced directly serverside
 */

public class ServerInfo implements Serializable {

    private String serverName;
    private int serverPort;
    private boolean useHttps;
    private int postDelay;
    private boolean highPrecision;
    private String timezone;
    private String fromAddress = "support@theenergydetective.com";


    public ServerInfo() {
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isUseHttps() {
        return useHttps;
    }

    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }

    public int getPostDelay() {
        return postDelay;
    }

    public void setPostDelay(int postDelay) {
        this.postDelay = postDelay;
    }

    public boolean isHighPrecision() {
        return highPrecision;
    }

    public void setHighPrecision(boolean highPrecision) {
        this.highPrecision = highPrecision;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("ServerInfo{");
        b.append("ServerName:" + getServerName());
        b.append(", ServerPort:" + getServerPort());
        b.append(", Use Https:" + isUseHttps());
        b.append(", PostDelay:" + getPostDelay());
        b.append(", HighPrecision:" + isHighPrecision());
        b.append(", timezone:" + getTimezone());
        b.append("}");
        return b.toString();
    }


}
