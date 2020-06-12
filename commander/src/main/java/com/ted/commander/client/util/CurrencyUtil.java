package com.ted.commander.client.util;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * Created by pete on 6/10/2016.
 */
public class CurrencyUtil
{
    public static String getCurrencySymbol(String currencyCode){
        NumberFormat format = NumberFormat.getSimpleCurrencyFormat(currencyCode);
        String n = format.format(0);
        return n.replace("0", "").replace(".", "").replace(",","").trim();
    }


    public static String format(String currencyCode, double amount) {
        NumberFormat format = NumberFormat.getSimpleCurrencyFormat(currencyCode);
        return format.format(amount);

    }
}
