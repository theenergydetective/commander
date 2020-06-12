/*
 * Copyright (c) 2014. The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan.additionalCharges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.widgets.NumericTextBox;
import com.ted.commander.common.model.AdditionalCharge;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;

import java.util.logging.Logger;


public class AdditionalChargeCell extends Composite {

    static final Logger LOGGER = Logger.getLogger(AdditionalChargeCell.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    private final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    DivElement seasonLabel;
    @UiField
    DivElement currencyLabel;
    @UiField
    NumericTextBox valueField;


    @UiConstructor
    public AdditionalChargeCell( final EnergyPlan energyPlan, final EnergyPlanSeason energyPlanSeason, final AdditionalCharge additionalCharge) {
        initWidget(defaultBinder.createAndBindUi(this));
        getElement().getStyle().setProperty("float", "left");
        getElement().getStyle().setMargin(8, Style.Unit.PX);

        seasonLabel.setInnerText(energyPlanSeason.getSeasonName());
        CurrencyData currency = CurrencyList.get().lookup(energyPlan.getRateType());

        valueField.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                Double d = valueField.getDoubleValue();
                additionalCharge.setRate(d);
                handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));
            }
        });

        switch (additionalCharge.getAdditionalChargeType()) {
            case SURCHARGE:
                currencyLabel.setInnerText(currency.getSimpleCurrencySymbol() + "/kWh");
                break;
            case TAX:
                currencyLabel.setInnerText("%");
                break;
            default:
                currencyLabel.setInnerText(currency.getSimpleCurrencySymbol());
                break;
        }

        valueField.setValue(additionalCharge.getRate());
    }

    public void setEnabled(boolean enabled) {
        valueField.setEnabled(enabled);
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> itemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, itemChangedHandler);
    }


    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, AdditionalChargeCell> {
    }


}

