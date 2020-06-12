/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.dialog.Dialogs;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.model.DateRange;
import com.ted.commander.client.callback.NoActionAlertCallback;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.radioManager.HistoryTypeRadioManager;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.datePicker.DatePicker;
import com.ted.commander.client.widgets.dialogs.LocationSelectionPopup;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.VirtualECC;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.List;
import java.util.logging.Logger;


public class ComparisonViewImpl extends Composite implements ComparisonView {

    static final Logger LOGGER = Logger.getLogger(ComparisonViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;

    @UiField
    TitleBar titleBar;


    @UiField
    PaperIconButton addButton;
    @UiField
    VerticalPanel locationListPanel;
    @UiField
    PaperButton generateButton;
    @UiField
    PaperRadioGroupElement unitGroup;

    @UiField
    DatePicker datePicker;

    final HistoryTypeRadioManager historyTypeRadioManager;


    ItemSelectedHandler<VirtualECC> deleteHandler = new ItemSelectedHandler<VirtualECC>() {
        @Override
        public void onSelected(ItemSelectedEvent<VirtualECC> event) {
            presenter.removeLocation(event.getItem());
        }
    };

    public ComparisonViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        titleBar.setSelectedPlace(new ComparisonPlace(""));
        titleBar.addItemSelectedHandler(new ItemSelectedHandler<Place>() {
            @Override
            public void onSelected(ItemSelectedEvent<Place> event) {
                presenter.goTo(event.getItem());
            }
        });


        historyTypeRadioManager = new HistoryTypeRadioManager(unitGroup);

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.queryLocations();
            }
        });


        generateButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (locationListPanel.getWidgetCount() == 0) {
                    Dialogs.alert(WebStringResource.INSTANCE.error(), WebStringResource.INSTANCE.locationsNeeded(), new NoActionAlertCallback());
                } else {
                    presenter.generate();
                }
            }
        });


        historyTypeRadioManager.addValueChangeHandler(new ValueChangeHandler<HistoryType>() {
            @Override
            public void onValueChange(ValueChangeEvent<HistoryType> valueChangeEvent) {
                datePicker.setHistoryType(valueChangeEvent.getValue());
            }
        });
    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void showLocationSelector(final List<AccountLocation> accountLocationList) {


        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable throwable) {
                GWT.log("Async Callback Failure");
            }

            public void onSuccess() {
                final LocationSelectionPopup locationSelectionPopup = new LocationSelectionPopup(accountLocationList);
                locationSelectionPopup.open();
                locationSelectionPopup.addItemSelectedHandler(new ItemSelectedHandler<AccountLocation>() {
                    @Override
                    public void onSelected(ItemSelectedEvent<AccountLocation> itemSelectedEvent) {
                        AccountLocation accountLocation = itemSelectedEvent.getItem();
                        VirtualECC selectedVirtualECC = null;
                        presenter.addLocation(accountLocation.getVirtualECC());
                    }
                });
            }
        });

    }

    @Override
    public void setLocations(List<VirtualECC> accountLocationList) {
        locationListPanel.clear();
        for (VirtualECC v : accountLocationList) {
            //Make a location tile w/ a delete button
            ComparisonLocationTile comparisonLocationTile = new ComparisonLocationTile(v);
            comparisonLocationTile.addSelectedHandler(deleteHandler);
            locationListPanel.add(comparisonLocationTile);
        }
        addButton.setVisible(accountLocationList.size() < 10);
    }

    @Override
    public HasValue<DateRange> datePicker() {
        return datePicker;
    }


    @Override
    public HasValue<HistoryType> getUnitOfTime() {
        return historyTypeRadioManager;
    }

    @Override
    public void setLoadingVisible(boolean visible) {
        if (visible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();

    }

    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, ComparisonViewImpl> {
    }


}
