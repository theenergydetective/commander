package com.ted.commander.client.widgets.graph.formatter;

import com.github.gwtd3.api.core.Value;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.ted.commander.common.enums.HistoryType;

import java.util.Date;

public class DateFormatter implements GraphFormatter {

    HistoryType historyType;

    public DateFormatter(HistoryType historyType) {
        this.historyType = historyType;
    }




    public String format(Long epoch) {
        switch (historyType) {
            case DAILY: {
                return DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(new Date(epoch * 1000));
            }
            case BILLING_CYCLE: {
                return DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.YEAR_MONTH).format(new Date(epoch * 1000));
            }
            default:
                return DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(new Date(epoch  * 1000));
        }
    }


    public String format(Value value) {
        if (value == null) return "";
        return format(value.asLong());
    }

    @Override
    public String format(Double value) {
        if (value == null) return "";
        return format(value.longValue());
    }
}
