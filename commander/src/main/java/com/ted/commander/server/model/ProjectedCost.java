package com.ted.commander.server.model;

/**
 * Created by pete on 1/29/2016.
 */
public class ProjectedCost {
    final double cost;
    final double energy;

    public ProjectedCost(double energy, double cost) {
        this.cost = cost;
        this.energy = energy;
    }

    public double getCost() {
        return cost;
    }

    public double getEnergy() {
        return energy;
    }
}
