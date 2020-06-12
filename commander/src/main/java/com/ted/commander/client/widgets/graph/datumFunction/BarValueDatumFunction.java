/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.graph.datumFunction;

import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.scales.LinearScale;
import com.google.gwt.dom.client.Element;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;


public class BarValueDatumFunction implements DatumFunction<Double> {

    final protected LinearScale linearScale;

    public BarValueDatumFunction(LinearScale linearScale) {
        this.linearScale = linearScale;
    }

    @Override
    public Double apply(Element element, Value d, int i) {
        GraphDataPoint dataPoint = ((GraphDataPoint) d.as());
        return apply(dataPoint, i);
    }

    public Double apply(GraphDataPoint dataPoint, int i) {
        return apply(dataPoint.getValue(), i);
    }


    public Double apply(Double dataPoint, int i) {
        Double yApplied =  linearScale.apply(dataPoint).asDouble();
        return yApplied;
    }
}
