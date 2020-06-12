package com.ted.commander.server.model;

/**
 * Created by pete on 11/25/2015.
 */
public class EpochExcludes {
    final long startEpoch;
    final long endEpoch;


    public EpochExcludes(long startEpoch, long endEpoch) {
        this.startEpoch = startEpoch;
        this.endEpoch = endEpoch;
    }

    public long getStartEpoch() {
        return startEpoch;
    }

    public long getEndEpoch() {
        return endEpoch;
    }

    @Override
    public String toString() {
        return "EpochExcludes{" +
                "startEpoch=" + startEpoch +
                ", endEpoch=" + endEpoch +
                '}';
    }
}
