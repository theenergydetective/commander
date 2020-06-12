package com.ted.commander.common.model.history;

import java.io.Serializable;

/**
 * Created by pete on 1/29/2016.
 */
public class CostDifference implements Serializable {
    double rate = 0.0;
    double net = 0.0;
    double load = 0.0;
    double generation = 0.0;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CostDifference that = (CostDifference) o;

        if (Double.compare(that.rate, rate) != 0) return false;
        if (Double.compare(that.net, net) != 0) return false;
        if (Double.compare(that.load, load) != 0) return false;
        return Double.compare(that.generation, generation) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(rate);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(net);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(load);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(generation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CostDifference{" +
                "rate=" + rate +
                ", net=" + net +
                ", load=" + load +
                ", generation=" + generation +
                '}';
    }
}
