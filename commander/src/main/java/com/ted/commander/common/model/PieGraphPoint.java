/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by pete on 10/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PieGraphPoint implements Serializable {

    String color;
    double energy;
    double cost;
    long mtuId;
    String label;

    public long getMtuId() {
        return mtuId;
    }

    public void setMtuId(long mtuId) {
        this.mtuId = mtuId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "PieGraphPoint{" +
                "color='" + color + '\'' +
                ", energy=" + energy +
                ", cost=" + cost +
                ", label='" + label + '\'' +
                '}';
    }
}
