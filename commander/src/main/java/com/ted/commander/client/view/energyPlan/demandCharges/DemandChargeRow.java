package com.ted.commander.client.view.energyPlan.demandCharges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.widgets.NumericTextBox;
import com.ted.commander.common.model.DemandChargeTier;
import com.ted.commander.common.model.EnergyPlan;

import java.util.logging.Logger;

public class DemandChargeRow extends Composite {

    static Logger LOGGER = Logger.getLogger(DemandChargeRow.class.toString());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final DemandChargeTier demandChargeTier;
    @UiField
    DivElement stepLabel;
    @UiField
    NumericTextBox stepField;
    @UiField
    NumericTextBox costField;

    ChangeHandler changeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent changeEvent) {
            demandChargeTier.setRate(costField.getDoubleValue());
            demandChargeTier.setPeak(stepField.getDoubleValue());
        }
    };

    public DemandChargeRow(final EnergyPlan energyPlan, final DemandChargeTier demandChargeTier) {
        initWidget(uiBinder.createAndBindUi(this));
        this.demandChargeTier = demandChargeTier;

        stepLabel.setInnerText(WebStringResource.INSTANCE.step() + " " + (demandChargeTier.getId() + 1));
        stepField.setVisible(true);

        stepField.addChangeHandler(changeHandler);
        costField.addChangeHandler(changeHandler);

        stepField.setValue(demandChargeTier.getPeak());
        costField.setValue(demandChargeTier.getRate());


    }

    interface MyUiBinder extends UiBinder<Widget, DemandChargeRow> {
    }

}
