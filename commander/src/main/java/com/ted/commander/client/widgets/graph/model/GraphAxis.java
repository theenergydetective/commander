package com.ted.commander.client.widgets.graph.model;


import com.ted.commander.client.widgets.graph.formatter.GraphFormatter;

import java.util.List;

public class GraphAxis {

    final Double minValue;
    final Double maxValue;
    final String description;
    final String color;
    final GraphFormatter graphFormatter;
    final List<GraphDataPoints> graphDataPointsList;


    public  GraphAxis(Double minValue, Double maxValue, String description, String color, GraphFormatter graphFormatter, List<GraphDataPoints> graphDataPointsList) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.description = description;
        this.color = color;
        this.graphFormatter = graphFormatter;
        this.graphDataPointsList = graphDataPointsList;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public GraphFormatter getGraphFormatter() {
        return graphFormatter;
    }

    public List<GraphDataPoints> getGraphDataPointsList() {
        return graphDataPointsList;
    }

    @Override
    public String toString() {
        return "GraphAxis{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", description='" + description + '\'' +
                ", color='" + color + '\'' +
                ", graphFormatter=" + graphFormatter +
                ", graphDataPointsList=" + graphDataPointsList +
                '}';
    }
}
