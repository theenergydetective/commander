package com.ted.commander.client.widgets.graph.formatter;


import com.github.gwtd3.api.core.Value;

public interface GraphFormatter {
    public String format(Value value);
    public String format(Double value);
}
