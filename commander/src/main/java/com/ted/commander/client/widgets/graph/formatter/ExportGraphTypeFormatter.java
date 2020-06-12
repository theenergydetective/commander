package com.ted.commander.client.widgets.graph.formatter;

import com.github.gwtd3.api.core.Value;
import com.google.gwt.i18n.client.NumberFormat;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.common.enums.ExportGraphType;
import com.ted.commander.common.enums.HistoryType;

public class ExportGraphTypeFormatter implements GraphFormatter {

    static NumberFormat decimalFormat = NumberFormat.getFormat("0.0");

    final ExportGraphType exportGraphType;
    final String currencyCode;
    final NumberFormat currencyFormat;
    final HistoryType historyType;

    public ExportGraphTypeFormatter(HistoryType historyType, ExportGraphType exportGraphType, String currencyCode) {
        this.exportGraphType = exportGraphType;
        this.currencyCode = currencyCode;
        this.currencyFormat = NumberFormat.getSimpleCurrencyFormat(currencyCode);
        this.historyType = historyType;
    }

    public String format(Double value) {
        switch (exportGraphType) {
            case VOLTAGE:
                return CommanderFormats.VOLTAGE_FORMAT.format(value);
            case POWER_FACTOR:
                return CommanderFormats.PF_FORMAT.format(value);
            case COST:
                return currencyFormat.format(value);
            case DEMAND:
                return CommanderFormats.DEMAND_FORMAT.format(value / 1000.0);
            case DEMAND_COST:
                return currencyFormat.format(value);
            default:
                if (historyType.equals(HistoryType.MINUTE)){
                    return CommanderFormats.SHORT_KW_FORMAT.format(value / 1000.0);
                } else {
                    return CommanderFormats.SHORT_KWH_FORMAT.format(value / 1000.0);
                }
        }
    }


    public String format(Value value) {
        if (value == null) return "";
        return format(value.asDouble());
    }
}
