/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;

/**
 * Created by pete on 10/10/2014.
 */
public class DailySummeryRecent {
    double recent = 0;
    double average = 0;

    public double getRecent() {
        return recent;
    }

    public void setRecent(double recent) {
        this.recent = recent;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return "DailySummeryRecent{" +
                "recent=" + recent +
                ", average=" + average +
                '}';
    }
}
