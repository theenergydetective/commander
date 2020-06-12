package com.ted.commander.client.view.energyPlan.demandCharges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.widgets.NumericTextBox;
import com.ted.commander.common.model.DemandChargeTOU;
import com.ted.commander.common.model.EnergyPlan;

import java.util.logging.Logger;

public class DemandChargeTOURow extends Composite {

    static Logger LOGGER = Logger.getLogger(DemandChargeTOURow.class.toString());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final DemandChargeTOU demandChargeTOU;
    @UiField
    DivElement stepLabel;

    @UiField
    NumericTextBox costField;
    ChangeHandler changeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent changeEvent) {
            demandChargeTOU.setRate(costField.getDoubleValue());
        }
    };

    public DemandChargeTOURow(final EnergyPlan energyPlan, final DemandChargeTOU demandChargeTOU) {
        this.demandChargeTOU = demandChargeTOU;
        initWidget(uiBinder.createAndBindUi(this));
        stepLabel.setInnerText(energyPlan.getTouLevels().get(demandChargeTOU.getPeakType().ordinal()).getTouLevelName());
        costField.addChangeHandler(changeHandler);
        costField.setValue(demandChargeTOU.getRate());
    }

    interface MyUiBinder extends UiBinder<Widget, DemandChargeTOURow> {
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler changeHandler){
        return costField.addValueChangeHandler(changeHandler);
    }
}
