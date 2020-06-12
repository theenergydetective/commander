/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard.optionsSettings;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.common.enums.BillingCycleDataType;

public abstract class OptionSettingsRowView extends Composite {

    final BillingCycleDataType dashboardGraphType;
    final HandlerManager handlerManager = new HandlerManager(this);
    protected ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            setGradient(isGradient());
            setSelected(isSelected());
            fireEvent();
        }
    };

    protected OptionSettingsRowView(BillingCycleDataType dashboardGraphType) {
        this.dashboardGraphType = dashboardGraphType;
    }

    public abstract boolean isGradient();

    public abstract void setGradient(boolean isGradient);

    public abstract boolean isSelected();

    public abstract void setSelected(boolean isSelected);

    public abstract void setEnabled(boolean isEnabled);

    public void addValueChangeHandler(ItemChangedHandler<OptionSettingsRowView> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }


    private void fireEvent() {
        handlerManager.fireEvent(new ItemChangedEvent<OptionSettingsRowView>(this));
    }

    public BillingCycleDataType getDashboardGraphType() {
        return dashboardGraphType;
    }
}
