package com.ted.commander.client.widgets.graph.model;


public class PiePoint {

    final String id;
    final String name;
    final Double innerValue;
    final String innerString;
    final Double outerValue;
    final String outerString;
    final String color;

    public PiePoint(String id, String name, Double innerValue, String innerString, Double outerValue, String outerString, String color) {
        this.id = id;
        this.name = name;
        this.innerValue = innerValue;
        this.innerString = innerString;
        this.outerValue = outerValue;
        this.outerString = outerString;
        this.color = color;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getInnerValue() {
        return innerValue;
    }

    public Double getOuterValue() {
        return outerValue;
    }

    public String getColor() {
        return color;
    }

    public String getInnerString() {
        return innerString;
    }

    public String getOuterString() {
        return outerString;
    }
}
