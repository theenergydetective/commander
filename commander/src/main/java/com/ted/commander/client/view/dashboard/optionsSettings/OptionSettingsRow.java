/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard.optionsSettings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.resources.enumMapper.BillingCycleDataTypeEnumMapper;
import com.ted.commander.common.enums.BillingCycleDataType;

import java.util.logging.Logger;


public class OptionSettingsRow extends OptionSettingsRowView {

    static final Logger LOGGER = Logger.getLogger(OptionSettingsRow.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    CheckBox useOption;
    @UiField
    CheckBox useGradient;
    @UiField
    Label optionNameField;

    public OptionSettingsRow(BillingCycleDataType dashboardGraphType, boolean isSelected, boolean isGradient) {
        super(dashboardGraphType);
        initWidget(defaultBinder.createAndBindUi(this));
        optionNameField.setText(BillingCycleDataTypeEnumMapper.getDescription(dashboardGraphType));
        setGradient(isGradient);
        setSelected(isSelected);

        useOption.addValueChangeHandler(valueChangeHandler);
        useGradient.addValueChangeHandler(valueChangeHandler);
    }

    public boolean isGradient() {
        return useGradient.getValue();
    }

    @Override
    public void setGradient(boolean isGradient) {
        useGradient.setValue(isGradient, false);
    }

    public boolean isSelected() {
        return useOption.getValue();
    }

    @Override
    public void setSelected(boolean isSelected) {
        useOption.setValue(isSelected, false);
        if (!isSelected) {
            setGradient(false);
        }
        useGradient.setEnabled(isSelected);
        useGradient.setVisible(isSelected);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        useOption.setEnabled(isEnabled);
        if (!isEnabled) {
            setSelected(false);
        }
    }

    interface DefaultBinder extends UiBinder<Widget, OptionSettingsRow> {

    }

}
