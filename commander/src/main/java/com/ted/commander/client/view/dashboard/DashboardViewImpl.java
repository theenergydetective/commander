/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.model.DashboardDateRange;
import com.ted.commander.client.model.DashboardOptions;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.view.dashboard.optionsSettings.OptionSettingsOverlay;
import com.ted.commander.client.widgets.calendar.MonthlyCalendar;
import com.ted.commander.client.widgets.dialogs.AlertDialog;
import com.ted.commander.client.widgets.dialogs.LocationSelectionPopup;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.DashboardResponse;
import com.ted.commander.common.model.VirtualECC;
import com.vaadin.polymer.paper.widget.PaperMaterial;

import java.util.List;
import java.util.logging.Logger;


public class DashboardViewImpl extends Composite implements DashboardView {

    static final Logger LOGGER = Logger.getLogger(DashboardViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    DashboardView.Presenter presenter;


    @UiField
    DashboardNavigation dashboardNavigation;

    @UiField
    DashboardSummary dashboardSummary;

    @UiField
    MonthlyCalendar monthlyCalendar;

    @UiField
    PaperMaterial dashboardSummaryCard;
    @UiField
    TitleBar titleBar;


    int dashboardNavigationBottom = 0;
    int summaryCardHeight = 0;


    public DashboardViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        titleBar.setSelectedPlace(new DashboardPlace(""));

        dashboardNavigation.addItemChangedHandler(new ItemChangedHandler<DashboardDateRange>() {
            @Override
            public void onChanged(ItemChangedEvent<DashboardDateRange> event) {
                presenter.queryLocation(
                        dashboardNavigation.getLocation().getId(),
                        dashboardNavigation.getDashboardDateRange(),
                        dashboardNavigation.getDashboardOptions());

            }
        });

        monthlyCalendar.addItemSelectedHandler(new ItemSelectedHandler<CalendarKey>() {
            @Override
            public void onSelected(ItemSelectedEvent<CalendarKey> event) {
                presenter.queryDailyDisplay(event.getItem());

            }
        });

        titleBar.addItemSelectedHandler(new ItemSelectedHandler<Place>() {
            @Override
            public void onSelected(ItemSelectedEvent<Place> event) {
                presenter.goTo(event.getItem());
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
        dashboardNavigation.setPresenter(presenter);
    }

    @Override
    public void setDateRage(DashboardDateRange dashboardDateRange) {
        dashboardNavigation.setDashboardDateRange(dashboardDateRange);
        monthlyCalendar.setDashboardDateRange(dashboardDateRange);

    }

    @Override
    public void setLocation(VirtualECC location) {
        dashboardNavigation.setLocation(location);
    }

    @Override
    public void setOptions(DashboardOptions dashboardOptions) {
        dashboardNavigation.setOptions(dashboardOptions);
    }

    @Override
    public void clearCalendar() {
        monthlyCalendar.clear();
    }


    @Override
    public void setCalendarValue(CalendarKey key, List<String> value, Double gradient, String color) {
        monthlyCalendar.setValue(key, value, gradient, color);
    }

    @Override
    public void setSummary(DashboardResponse dashboardResponse) {
        dashboardSummary.setSummary(dashboardResponse);
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
                        dashboardNavigation.setLocation(accountLocation.getVirtualECC());
                        presenter.queryLocation( dashboardNavigation.getLocation().getId(),  dashboardNavigation.getDashboardDateRange(),  dashboardNavigation.getDashboardOptions());
                    }
                });
            }
        });
    }


    @Override
    public void setOptionSelector(VirtualECC virtualECC, DashboardOptions dashboardOptions) {
        final OptionSettingsOverlay overlay = new OptionSettingsOverlay(virtualECC, dashboardOptions);
        overlay.show();
        overlay.addItemChangedHandler(new ItemChangedHandler<DashboardOptions>() {
            @Override
            public void onChanged(ItemChangedEvent<DashboardOptions> event) {
                dashboardNavigation.setOptions(event.getItem());
                presenter.queryLocation(
                        dashboardNavigation.getLocation().getId(),
                        dashboardNavigation.getDashboardDateRange(),
                        dashboardNavigation.getDashboardOptions());
            }
        });

    }

    @Override
    public void setLoadingVisible(boolean visible) {
        if (visible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();
    }

    @Override
    public void onResize() {
        if (dashboardNavigationBottom == 0) {
            LOGGER.fine("DOING INITIAL SIZE CALCULATIONS");
            dashboardNavigationBottom = dashboardNavigation.getAbsoluteBottom();
            summaryCardHeight = dashboardSummaryCard.getElement().getClientHeight();
        }

        int maxCalendarHeight = Window.getClientHeight();
        maxCalendarHeight -= dashboardNavigationBottom;
        maxCalendarHeight -= summaryCardHeight;
        maxCalendarHeight -= 75; //padding and Copyright

        LOGGER.fine("Window.getClientHeight():" + Window.getClientHeight());
        LOGGER.fine("dashboardNavigation:" + dashboardNavigation.getAbsoluteBottom());
        LOGGER.fine("dashboardSummaryCard:" + dashboardSummaryCard.getElement().getClientHeight());

        //Calculate Width
        int maxCalendarWidth = Window.getClientWidth();
        if (maxCalendarWidth >= 1160) maxCalendarWidth = 1160;
        LOGGER.fine("MAX CALENDAR SIZE: " + maxCalendarWidth + " X " + maxCalendarHeight);

        monthlyCalendar.setCalendarSize(maxCalendarWidth, maxCalendarHeight);
        dashboardSummary.setWidth(maxCalendarWidth + "px");
        dashboardNavigation.setWidth(maxCalendarWidth);
    }

    @Override
    public void showMaintenanceDialog() {
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.open(WebStringResource.INSTANCE.maintenance(), WebStringResource.INSTANCE.maintenanceBody());
    }

    @Override
    public void showNoNetError() {
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.open(WebStringResource.INSTANCE.noMonitoringPoints(), WebStringResource.INSTANCE.noMonitoringPointsBody());
    }

    @Override
    public void clearCalendarValues() {
        monthlyCalendar.clearValues();
    }

    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, DashboardViewImpl> {
    }


}
