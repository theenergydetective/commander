/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model;


public class BillingCycleCacheTotalEnergy {
    long maxEpochTime;
    double totalEnergy;

    public BillingCycleCacheTotalEnergy(long maxEpochTime, double totalEnergy) {
        this.maxEpochTime = maxEpochTime;
        this.totalEnergy = totalEnergy;
    }

    public long getMaxEpochTime() {
        return maxEpochTime;
    }

    public void setMaxEpochTime(long maxEpochTime) {
        this.maxEpochTime = maxEpochTime;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(double totalEnergy) {
        this.totalEnergy = totalEnergy;
    }


    @Override
    public String toString() {
        return "BillingCycleCacheTotalEnergy{" +
                "maxEpochTime=" + maxEpochTime +
                ", totalEnergy=" + totalEnergy +
                '}';
    }
}
