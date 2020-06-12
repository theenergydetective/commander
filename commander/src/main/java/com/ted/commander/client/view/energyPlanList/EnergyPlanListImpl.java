/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlanList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.widgets.AccountPicker;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.EnergyPlanKey;
import com.vaadin.polymer.paper.widget.PaperFab;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.logging.Logger;


public class EnergyPlanListImpl extends Composite implements EnergyPlanListView {

    static final Logger LOGGER = Logger.getLogger(EnergyPlanListImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;
    @UiField
    AccountPicker accountPicker;
    @UiField
    DivElement noPlansField;
    @UiField
    FlowPanel planFlowPanel;
    @UiField
    PaperIconButton backButton;
    @UiField
    PaperFab addButton;


    public EnergyPlanListImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        accountPicker.addValueChangeHandler(new ValueChangeHandler<AccountMembership>() {
            @Override
            public void onValueChange(ValueChangeEvent<AccountMembership> valueChangeEvent) {
                presenter.selectAccount(valueChangeEvent.getValue());
            }
        });

        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new DashboardPlace(""));
            }
        });

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.addEnergyPlan();
            }
        });
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    protected void onAttach() {
        super.onAttach();
        onResize();
    }

    @Override
    public void onResize() {
    }


    @Override
    public void setAccountMemberships(AccountMemberships accountMemberships) {
        accountPicker.clear();
        for (AccountMembership accountMembership: accountMemberships.getAccountMemberships()){
            accountPicker.addItem(accountMembership);
        }
    }

    @Override
    public void setAccountMembership(AccountMembership accountMembership) {
        accountPicker.setValue(accountMembership, false);
    }

    @Override
    public void clearEnergyPlans() {
        planFlowPanel.clear();
        noPlansField.getStyle().clearDisplay();
    }

    @Override
    public void addEnergyPlan(EnergyPlanRowPresenter energyPlanRow) {
        noPlansField.getStyle().setDisplay(Style.Display.NONE);
        energyPlanRow.addItemSelectedHandler(new ItemSelectedHandler<EnergyPlanKey>() {
            @Override
            public void onSelected(ItemSelectedEvent<EnergyPlanKey> event) {
                presenter.selectEnergyPlan(event.getItem());
            }
        });
        planFlowPanel.add(energyPlanRow);
        if (planFlowPanel.getWidgetCount() == 1) energyPlanRow.asWidget().getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
    }


    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, EnergyPlanListImpl> {
    }


}
