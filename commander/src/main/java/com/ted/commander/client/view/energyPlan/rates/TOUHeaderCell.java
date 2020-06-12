package com.ted.commander.client.view.energyPlan.rates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TOUHeaderCell extends Composite {

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    @UiField
    DivElement cellLabel;

    public TOUHeaderCell(String label) {
        initWidget(defaultBinder.createAndBindUi(this));
        cellLabel.setInnerText(label);

    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, TOUHeaderCell> {
    }
}
