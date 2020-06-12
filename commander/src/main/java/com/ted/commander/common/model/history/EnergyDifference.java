package com.ted.commander.common.model.history;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by pete on 1/29/2016.
 */
public class EnergyDifference implements Serializable {
    double netTotal = 0.0;

    double net = 0.0;
    double load = 0.0;
    double generation = 0.0;

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public double getGeneration() {
        return generation;
    }

    public void setGeneration(double generation) {
        this.generation = generation;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    @JsonIgnore
    public void addTo(HistoryNetGenLoad historyNetGenLoad) {
        historyNetGenLoad.setNet(historyNetGenLoad.getNet() + net);
        historyNetGenLoad.setGeneration(historyNetGenLoad.getGeneration() + generation);
        historyNetGenLoad.setLoad(historyNetGenLoad.getLoad() + load);
    }

    @Override
    public String toString() {
        return "EnergyDifference{" +
                "netTotal=" + netTotal +
                ", net=" + net +
                ", load=" + load +
                ", generation=" + generation +
                '}';
    }
}
