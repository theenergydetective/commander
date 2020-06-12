/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.energyPost;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by pete on 9/12/2014.
 */
@XmlRootElement(name = "cumulative")
public class EnergyCumulativePost implements Serializable {


    private long timestamp;
    private Double watts;
    private Double powerFactor;
    private Double voltage;

    public EnergyCumulativePost() {
    }

    ;

    public EnergyCumulativePost(long timestamp, Double watts, Double powerFactor, Double voltage) {
        this.timestamp = timestamp;
        this.watts = watts;
        this.powerFactor = powerFactor;
        this.voltage = voltage;
    }

    @XmlAttribute(name = "timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @XmlAttribute(name = "watts")
    public Double getWatts() {
        return watts;
    }

    public void setWatts(Double watts) {
        this.watts = watts;
    }

    @XmlAttribute(name = "pf")
    public Double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(Double powerFactor) {
        this.powerFactor = powerFactor;
    }

    @XmlAttribute(name = "voltage")
    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    @Override
    public String toString() {
        return "EnergyCumulativePost{" +
                "timestamp=" + timestamp +
                ", watts=" + watts +
                ", powerFactor=" + powerFactor +
                ", voltage=" + voltage +
                '}';
    }
}
