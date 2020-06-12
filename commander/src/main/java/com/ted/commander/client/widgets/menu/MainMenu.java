/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.resources.LogoImageResource;
import com.ted.commander.client.resources.WebStringResource;

import java.util.logging.Logger;

public class MainMenu extends Composite {

    static final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    private final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    Image logo;

    @UiField
    LogoImageResource logoResource;
    @UiField
    WebStringResource stringRes;
    @UiField
    DashboardMenuItem dashboardMenuItem;
    @UiField
    UserSettingsMenuItem userSettingsMenuItem;
    @UiField
    AccountSettingsMenuItem accountSettingsMenuItem;
    @UiField
    LocationsMenuItem locationsMenuItem;
    @UiField
    EnergyPlanSettingsMenuItem energyPlanSettingsMenuItem;
    @UiField
    ComparisonMenuItem comparisonMenuItem;
    @UiField
    LogoutMenuItem logoutMenuItem;
    @UiField
    ActivateMenuItem activateMenuItem;
    @UiField
    GraphingMenuItem graphingMenuItem;
    @UiField
    DataExportMenuItem dataExportMenuItem;
    @UiField
    AdvisorMenuItem advisorMenuItem;
    @UiField
    BillingMenuItem billingMenuItem;

    ItemSelectedHandler<Place> itemSelectedHandler = new ItemSelectedHandler<Place>() {
        @Override
        public void onSelected(ItemSelectedEvent<Place> event) {
            handlerManager.fireEvent(event);
        }
    };

    public MainMenu(Place selectedPlace) {

        initWidget(defaultBinder.createAndBindUi(this));
        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new CloseEvent());
            }
        });

        dashboardMenuItem.addItemSelectedHandler(itemSelectedHandler);
        userSettingsMenuItem.addItemSelectedHandler(itemSelectedHandler);
        accountSettingsMenuItem.addItemSelectedHandler(itemSelectedHandler);
        locationsMenuItem.addItemSelectedHandler(itemSelectedHandler);
        energyPlanSettingsMenuItem.addItemSelectedHandler(itemSelectedHandler);
        comparisonMenuItem.addItemSelectedHandler(itemSelectedHandler);
        logoutMenuItem.addItemSelectedHandler(itemSelectedHandler);
        activateMenuItem.addItemSelectedHandler(itemSelectedHandler);
        dataExportMenuItem.addItemSelectedHandler(itemSelectedHandler);
        graphingMenuItem.addItemSelectedHandler(itemSelectedHandler);
        advisorMenuItem.addItemSelectedHandler(itemSelectedHandler);
        billingMenuItem.addItemSelectedHandler(itemSelectedHandler);

//        billingMenuItem.getElement().getStyle().setDisplay(Style.Display.NONE);
//        if (Commander.clientFactory.getUser().isBillingEnabled()){
//            billingMenuItem.getElement().getStyle().clearDisplay();
//        }



        dashboardMenuItem.setSelected(selectedPlace);
        userSettingsMenuItem.setSelected(selectedPlace);
        accountSettingsMenuItem.setSelected(selectedPlace);
        locationsMenuItem.setSelected(selectedPlace);
        energyPlanSettingsMenuItem.setSelected(selectedPlace);
        comparisonMenuItem.setSelected(selectedPlace);
        logoutMenuItem.setSelected(selectedPlace);
        activateMenuItem.setSelected(selectedPlace);
        dataExportMenuItem.setSelected(selectedPlace);
        graphingMenuItem.setSelected(selectedPlace);
        advisorMenuItem.setSelected(selectedPlace);
        billingMenuItem.setSelected(selectedPlace);




    }

    public void addItemSelectedHandler(ItemSelectedHandler<Place> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    public void addCloseHandler(CloseHandler closeHandler) {
        handlerManager.addHandler(CloseEvent.TYPE, closeHandler);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, MainMenu> {
    }


}
