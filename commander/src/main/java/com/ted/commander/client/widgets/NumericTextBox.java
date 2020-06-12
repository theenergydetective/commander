package com.ted.commander.client.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.TextBox;

import java.util.logging.Logger;

/**
 * Created by pete on 10/1/2014.
 */
public class NumericTextBox extends TextBox {
    static final Logger LOGGER = Logger.getLogger(NumericTextBox.class.toString());

    protected double maxValue;
    protected double minValue;
    private int decimalPlaces;

    public NumericTextBox() {


        addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                char keyCode = event.getCharCode();
                int nativeCode = event.getNativeEvent().getKeyCode();
                Object sender = event.getSource();

                if (keyCode == '.') {
                    if ((decimalPlaces == 0) || (getText().contains("."))) {
                        ((TextBox) sender).cancelKey();
                    }
                    return;
                }

                if (keyCode == '-') {
                    if ((minValue >= 0) || (getText().contains("-"))) {
                        ((TextBox) sender).cancelKey();
                    }
                    return;
                }

                if ((!Character.isDigit(keyCode)) && (keyCode != KeyCodes.KEY_TAB) && (keyCode != 0)
                        && (nativeCode != KeyCodes.KEY_BACKSPACE)
                        && (nativeCode != KeyCodes.KEY_DELETE) && (nativeCode != KeyCodes.KEY_ENTER)
                        && (nativeCode != KeyCodes.KEY_HOME) && (nativeCode != KeyCodes.KEY_END)
                        && (nativeCode != KeyCodes.KEY_LEFT) && (nativeCode != KeyCodes.KEY_UP)
                        && (nativeCode != KeyCodes.KEY_RIGHT) && (nativeCode != KeyCodes.KEY_DOWN)) {
                    // cancel the current keyboard event.
                    ((TextBox) sender).cancelKey();
                }

            }
        });

        addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                checkMinMax();
            }
        });
    }

    public void setPlaceHolder(String placeHolder) {
        getElement().setPropertyString("placeholder", placeHolder);
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public void setValue(Integer value) {
        if (value != null) {
            setValue(value.doubleValue());
        } else {
            setValue(0);
        }
    }

    public void setValue(Double value) {
        StringBuilder format = new StringBuilder("0.");
        for (int i = 0; i < decimalPlaces; i++) format.append("0");
        setValue(NumberFormat.getFormat(format.toString()).format(value));
    }

    protected void checkMinMax() {
        try {
            Float f = Float.parseFloat(getText());
            if (f > maxValue) setText(getFormat().format(maxValue));
            else if (f < minValue) setText(getFormat().format(minValue));
            else {
                setText(getFormat().format(f));
            }
        } catch (Exception e) {
            setText(getFormat().format(minValue));
        }
    }


    protected NumberFormat getFormat() {
        if (decimalPlaces > 0) {
            String fs = "0.";
            for (int d = 0; d < decimalPlaces; d++) fs += "0";
            return NumberFormat.getFormat(fs);
        } else {
            return NumberFormat.getFormat("0");
        }
    }

    public Double getDoubleValue() {
        try {
            String text = getText().replace("%", "").trim();
            return Double.parseDouble(text);
        } catch (Exception e) {
            return new Double(0);
        }
    }


}
