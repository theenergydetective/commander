package com.ted.commander.client.view.energyPlan.rates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.widgets.NumericTextBox;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyRate;

public class EnergyRateCell extends Composite {
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    final EnergyRate energyRate;
    @UiField
    NumericTextBox cellField;
    @UiField
    DivElement currencyType;

    final HandlerManager handlerManager = new HandlerManager(this);

    public EnergyRateCell(final EnergyPlan energyPlan, final EnergyRate energyRate) {
        initWidget(defaultBinder.createAndBindUi(this));
        this.energyRate = energyRate;
        cellField.setValue(energyRate.getRate());

        CurrencyData currency = CurrencyList.get().lookup(energyPlan.getRateType());
        currencyType.setInnerText(currency.getSimpleCurrencySymbol() + "/kWh");


        cellField.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                energyRate.setRate(cellField.getDoubleValue());
                handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));
            }
        });
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> itemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, itemChangedHandler);
    }


    interface DefaultBinder extends UiBinder<Widget, EnergyRateCell> {
    }

}
