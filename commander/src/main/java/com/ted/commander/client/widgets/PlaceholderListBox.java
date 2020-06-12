/*
 * Copyright (c) 2014. The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.ListBox;


public class PlaceholderListBox extends ListBox {

    @UiConstructor
    public PlaceholderListBox() {
    }

    public void setPlaceHolder(String placeHolder) {
        getElement().setPropertyString("placeholder", placeHolder);
    }


    public void setValue(Integer value) {
        setSelectedIndex(-1);
        if (value == null) {
            return;
        }

        for (int i = 0; i < getItemCount(); i++) {
            String s = getValue(i);
            Integer iVal = Integer.parseInt(s);
            if (value.equals(iVal)) {
                setSelectedIndex(i);
                break;
            }
        }
    }

    public void setValue(String value) {
        setSelectedIndex(-1);
        if (value == null) {
            return;
        }
        for (int i = 0; i < getItemCount(); i++) {
            if (value.equals(getValue(i))) {
                setSelectedIndex(i);
                break;
            }
        }
    }

    public String getSelectedValue() {
        if (getSelectedIndex() == -1) return null;
        return getValue(getSelectedIndex());
    }

    public String getSelectedLabel() {
        if (getSelectedIndex() == -1) return null;
        return getItemText(getSelectedIndex());
    }

    public Integer getSelectedInteger() {
        String s = getSelectedValue();
        if (s == null) return null;
        return Integer.parseInt(s);
    }
}
