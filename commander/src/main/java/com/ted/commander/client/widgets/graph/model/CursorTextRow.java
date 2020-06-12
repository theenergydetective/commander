package com.ted.commander.client.widgets.graph.model;

/**
 * Created by pete on 8/21/2015.
 */

public class CursorTextRow {
    final String label;
    final String color;
    public CursorTextRow(String label, String color){
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
}