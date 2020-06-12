/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dailyDetail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.widget.paper.PaperLabel;


public class DailyStatRow extends Composite implements HasText {

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    PaperLabel recentField;
    @UiField
    PaperLabel averageField;
    @UiField
    DivElement fieldHeader;
    @UiField
    HTMLPanel mainPanel;

    public DailyStatRow() {
        initWidget(defaultBinder.createAndBindUi(this));
    }

    public void setValue(String v) {
        recentField.setValue(v);
    }

    public void setAvg(String v) {
        averageField.setValue(v);
    }

    @Override
    public String getText() {
        return fieldHeader.getInnerText();
    }

    @Override
    public void setText(String s) {
        fieldHeader.setInnerText(s);
    }

    @Override
    public void setVisible(boolean visible) {
            mainPanel.setVisible(visible);
    }

    public void setCurrentText(String s) {
        recentField.setText(s);
    }


    interface DefaultBinder extends UiBinder<Widget, DailyStatRow> {
    }
}
