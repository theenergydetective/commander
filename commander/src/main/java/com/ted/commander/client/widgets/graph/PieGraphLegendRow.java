package com.ted.commander.client.widgets.graph;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by pete on 3/13/2015.
 */
public class PieGraphLegendRow extends Composite {

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    DivElement nameField;
    @UiField
    DivElement energyField;
    @UiField
    DivElement costField;


    public PieGraphLegendRow(final String name, final String energyValue, final String costValue, final String color){
        initWidget(defaultBinder.createAndBindUi(this));
        nameField.setInnerText(name);
        energyField.setInnerText(energyValue);
        costField.setInnerText(costValue);
        nameField.getStyle().setColor(color);
        energyField.getStyle().setColor(color);
        costField.getStyle().setColor(color);
    }

    interface DefaultBinder extends UiBinder<Widget, PieGraphLegendRow> {
    }


}
