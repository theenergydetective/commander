/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.widgets.AccountPicker;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.VirtualECC;
import com.vaadin.polymer.paper.widget.PaperFab;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.List;
import java.util.logging.Logger;


public class LocationListViewImpl extends Composite implements LocationListView {

    static final Logger LOGGER = Logger.getLogger(LocationListViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;


    @UiField
    VerticalPanel locationTiles;

    @UiField
    PaperFab addButton;
    @UiField
    PaperIconButton backButton;
    @UiField
    DivElement noLocationDiv;
    @UiField
    AccountPicker accountPicker;

    ItemSelectedHandler<VirtualECC> itemSelectedHandler = new ItemSelectedHandler<VirtualECC>() {
        @Override
        public void onSelected(ItemSelectedEvent<VirtualECC> event) {
            LOGGER.fine("EDIT CLICKED: " + presenter);

            presenter.edit(event.getItem());
        }
    };

    public LocationListViewImpl() {

        initWidget(defaultBinder.createAndBindUi(this));
        accountPicker.useTitle();

        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new DashboardPlace(""));
            }
        });

        accountPicker.addValueChangeHandler(new ValueChangeHandler<AccountMembership>() {
            @Override
            public void onValueChange(ValueChangeEvent<AccountMembership> valueChangeEvent) {
                presenter.listLocations(valueChangeEvent.getValue());
            }
        });


        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.newLocation();
            }
        });
    }

    @Override
    public void setLogo(ImageResource imageResource) {
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLocations(List<VirtualECC> virtualECCList) {
        locationTiles.clear();
        if (virtualECCList != null && virtualECCList.size() > 0){
            noLocationDiv.getStyle().setDisplay(Style.Display.NONE);
        } else {
            noLocationDiv.getStyle().clearDisplay();
        }

        for (VirtualECC virtualECC : virtualECCList) {
            LocationTile locationTile = new LocationTile(virtualECC);
            locationTiles.add(locationTile);
            locationTile.addSelectedHandler(itemSelectedHandler);
        }
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

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, LocationListViewImpl> {
    }


}
