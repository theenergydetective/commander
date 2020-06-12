/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.model.DashboardDateRange;
import com.ted.commander.client.model.DashboardOptions;
import com.ted.commander.client.resources.enumMapper.BillingCycleDataTypeEnumMapper;
import com.ted.commander.common.enums.BillingCycleDataType;
import com.ted.commander.common.model.VirtualECC;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.Date;
import java.util.logging.Logger;


public class DashboardNavigation extends Composite {

    static final Logger LOGGER = Logger.getLogger(DashboardNavigation.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    PaperIconButton nextMonthButton;
    @UiField
    PaperIconButton nextWeekButton;
    @UiField
    PaperIconButton prevWeekButton;
    @UiField
    PaperIconButton prevMonthButton;
    @UiField
    DivElement selectedFieldLabel;
    @UiField
    DivElement locationLabel;
    @UiField
    DivElement mainPanel;
    @UiField
    PaperIconButton locationButton;
    @UiField
    PaperIconButton optionButton;
    DashboardView.Presenter presenter;
    private DashboardDateRange dashboardDateRange;
    private VirtualECC location;
    private DashboardOptions dashboardOptions;


    public DashboardNavigation() {

        initWidget(defaultBinder.createAndBindUi(this));

        setDashboardDateRange(new DashboardDateRange(new Date()));

        nextMonthButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Date theDate = new Date((dashboardDateRange.getEndDate().getTime() + dashboardDateRange.getStartDate().getTime())/2);
                theDate.setDate(1);
                CalendarUtil.addMonthsToDate(theDate, 1);
                setDashboardDateRange(new DashboardDateRange(theDate));
                handlerManager.fireEvent(new ItemChangedEvent<DashboardDateRange>(dashboardDateRange));
            }
        });

        prevMonthButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Date theDate = new Date((dashboardDateRange.getEndDate().getTime() + dashboardDateRange.getStartDate().getTime())/2);
                theDate.setDate(1);
                CalendarUtil.addMonthsToDate(theDate, -1);
                setDashboardDateRange(new DashboardDateRange(theDate));
                handlerManager.fireEvent(new ItemChangedEvent<DashboardDateRange>(dashboardDateRange));
            }
        });

        nextWeekButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Date theDate = dashboardDateRange.getStartDate();
                CalendarUtil.addDaysToDate(theDate, 7);
                setDashboardDateRange(new DashboardDateRange(theDate));
                handlerManager.fireEvent(new ItemChangedEvent<DashboardDateRange>(dashboardDateRange));
            }
        });

        prevWeekButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Date theDate = dashboardDateRange.getStartDate();
                CalendarUtil.addDaysToDate(theDate, -7);
                setDashboardDateRange(new DashboardDateRange(theDate));
                handlerManager.fireEvent(new ItemChangedEvent<DashboardDateRange>(dashboardDateRange));
            }
        });


        locationButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.queryLocations();
            }
        });

        optionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.queryOptions();
            }
        });



    }


    public DashboardDateRange getDashboardDateRange() {
        return dashboardDateRange;
    }

    public void setDashboardDateRange(DashboardDateRange dashboardDateRange) {
        this.dashboardDateRange = dashboardDateRange;
        LOGGER.fine("DashboardNavigation: " + dashboardDateRange);
    }

    public void setPresenter(DashboardView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setOptions(DashboardOptions dashboardOptions) {
        this.dashboardOptions = dashboardOptions;

        StringBuilder optionStringBuilder = new StringBuilder();

        for (BillingCycleDataType dashboardGraphType : dashboardOptions.getGraphedOptions()) {
            if (optionStringBuilder.length() != 0) optionStringBuilder.append(", ");
            optionStringBuilder.append(BillingCycleDataTypeEnumMapper.getDescription(dashboardGraphType));
        }

        LOGGER.fine("OPTION LABEL:" + optionStringBuilder.toString());
        selectedFieldLabel.setInnerText("(" + optionStringBuilder.toString() + ")");
    }

    public DashboardOptions getDashboardOptions() {
        return dashboardOptions;
    }

    public VirtualECC getLocation() {
        return location;
    }

    public void setLocation(VirtualECC location) {
        if (location != null){
            this.location = location;
            locationLabel.setInnerText(location.getName());
        }
    }

    public void addItemChangedHandler(ItemChangedHandler<DashboardDateRange> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    public void setWidth(Integer width) {
        mainPanel.getStyle().setWidth(width, Style.Unit.PX);
    }


    public int getAbsoluteBottom() {
        return mainPanel.getAbsoluteBottom();
    }

    interface DefaultBinder extends UiBinder<Widget, DashboardNavigation> {
    }

}
