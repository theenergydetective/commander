/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard.optionsSettings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.model.DashboardOptions;
import com.ted.commander.common.enums.BillingCycleDataType;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.VirtualECC;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class OptionSettingsViewImpl extends Composite {

    static final Logger LOGGER = Logger.getLogger(OptionSettingsViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final DashboardOptions dashboardOptions;

    @UiField
    PaperButton cancelButton;
    @UiField
    FlexPanel mainPanel;

    @UiField
    FlexPanel optionList;
    @UiField
    PaperButton saveButton;
    BillingCycleDataType gradientOption = null;

    public OptionSettingsViewImpl(final VirtualECC virtualECC, final DashboardOptions dashboardOptions) {
        this.dashboardOptions = dashboardOptions;
        initWidget(defaultBinder.createAndBindUi(this));

        VirtualECCType virtualECCType = virtualECC.getSystemType();

        boolean hasWeather = false;
        if (virtualECC.getPostal() != null && !virtualECC.getPostal().trim().isEmpty()) hasWeather = true;
        if (virtualECC.getCity() != null && !virtualECC.getCity().trim().isEmpty() && virtualECC.getState() != null && !virtualECC.getState().trim().isEmpty()) hasWeather = true;

        optionList.add(new OptionSettingsRowHeader());

        if (!MGWT.getOsDetection().isDesktop()) {
            if (Window.getClientHeight() < 800) {
                mainPanel.setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
            }
        }


        for (BillingCycleDataType dashboardGraphType : BillingCycleDataType.values()) {
            //if (dashboardGraphType.equals(BillingCycleDataType.COST_PER_DAY)) continue;
            if (dashboardGraphType.equals(BillingCycleDataType.TOU_RATES)) continue;
            if (dashboardGraphType.equals(BillingCycleDataType.CLOUD_COVERAGE)) continue;

            if (!hasWeather) {
                if (dashboardGraphType.equals(BillingCycleDataType.PEAK_TEMP)) continue;
                if (dashboardGraphType.equals(BillingCycleDataType.LOW_TEMP)) continue;
            }

            if (virtualECCType.equals(VirtualECCType.NET_ONLY)){
                if (dashboardGraphType.equals(BillingCycleDataType.KWH_CONSUMED)) continue;
                if (dashboardGraphType.equals(BillingCycleDataType.KWH_GENERTED)) continue;
                if (!hasWeather && dashboardGraphType.equals(BillingCycleDataType.CLOUD_COVERAGE)) continue;;
            }


            boolean isSelected = false;
            boolean isGradient = dashboardOptions.getGradientOption() != null && dashboardGraphType.equals(dashboardOptions.getGradientOption());

            for (BillingCycleDataType selectedType : dashboardOptions.getGraphedOptions()) {
                if (selectedType.equals(dashboardGraphType)) {
                    isSelected = true;
                    break;
                }
            }

            OptionSettingsRowView optionSettingsRowView;
            optionSettingsRowView = new OptionSettingsRow(dashboardGraphType, isSelected, isGradient);
            optionList.add(optionSettingsRowView);

            optionSettingsRowView.addValueChangeHandler(new ItemChangedHandler<OptionSettingsRowView>() {
                @Override
                public void onChanged(ItemChangedEvent<OptionSettingsRowView> event) {
                    if (event.getItem().getDashboardGraphType().equals(gradientOption)) {
                        if (event.getItem().isGradient()) gradientOption = event.getItem().dashboardGraphType;
                        else gradientOption = null;
                    } else {
                        if (event.getItem().isGradient()) gradientOption = event.getItem().dashboardGraphType;
                    }

                    balance();
                }
            });
        }

        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dashboardOptions.setGradientOption(gradientOption);
                dashboardOptions.getGraphedOptions().clear();

                for (int i = 1; i < optionList.getWidgetCount(); i++) {
                    OptionSettingsRowView row = (OptionSettingsRowView) optionList.getWidget(i);
                    if (row.isSelected()) dashboardOptions.getGraphedOptions().add(row.getDashboardGraphType());
                }

                handlerManager.fireEvent(new ItemChangedEvent<DashboardOptions>(dashboardOptions));
            }
        });


        gradientOption = dashboardOptions.getGradientOption();
        balance();


    }

    public void balance() {
        //Get selection count
        int count = 0;

        for (int i = 1; i < optionList.getWidgetCount(); i++) {
            OptionSettingsRowView row = (OptionSettingsRowView) optionList.getWidget(i);
            if (!row.getDashboardGraphType().equals(gradientOption)) row.setGradient(false);
            if (row.isSelected()) count++;
        }

        if (count == 0) {
            OptionSettingsRowView row = (OptionSettingsRowView) optionList.getWidget(1);
            row.setSelected(true);
        } else {

            for (int i = 1; i < optionList.getWidgetCount(); i++) {
                OptionSettingsRowView row = (OptionSettingsRowView) optionList.getWidget(i);
                if (!row.isSelected()) row.setEnabled(count <= 1);
                else row.setEnabled(true);
            }
        }


    }

    public void addCloseClickHandler(ClickHandler clickHandler) {
        cancelButton.addClickHandler(clickHandler);
    }

    public void addItemChangedHandler(ItemChangedHandler<DashboardOptions> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, OptionSettingsViewImpl> {
    }


}
