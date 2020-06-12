package com.ted.commander.client.widgets.graph.formatter;

import com.github.gwtd3.api.core.Value;
import com.google.gwt.i18n.client.NumberFormat;
import com.ted.commander.common.enums.GraphLineType;

public class GraphLineTypeFormatter implements GraphFormatter {

    static NumberFormat decimalFormat = NumberFormat.getFormat("0.0");

    final GraphLineType graphLineType;
    final Boolean isMetric;
    final String currencyCode;
    final NumberFormat currencyFormat;

    public GraphLineTypeFormatter(GraphLineType graphLineType, Boolean isMetric, String currencyCode) {
        this.graphLineType = graphLineType;
        this.isMetric = isMetric;
        this.currencyCode = currencyCode;
        if (currencyCode != null && !currencyCode.isEmpty()) {
            this.currencyFormat = NumberFormat.getSimpleCurrencyFormat(currencyCode);
        } else {
            this.currencyFormat = null;
        }
    }

    public String format(Value value) {
        if (value == null) return "";
        return format(value.asDouble());
    }

    public String format(Double value) {
        switch (graphLineType) {
            case DEMAND_COST:
               return currencyFormat.format(value);
            case TEMPERATURE:
                if (!isMetric) {
                    return decimalFormat.format(value) + " F";
                } else {
                    return decimalFormat.format(value) + " C";
                }
            case WIND_SPEED:
                if (!isMetric) {
                    return decimalFormat.format(value) + " mph";
                } else {
                    return decimalFormat.format(value) + " kph";
                }
            case CLOUD_COVERAGE:
                return decimalFormat.format(value) + "%";
            default:
                return decimalFormat.format(value) + "kWh";
        }
    }

}
