/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.graphing;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.model.DateRange;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.model.DataExportDataPoint;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.GraphingPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.enums.*;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.export.GraphRequest;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class GraphingOptionsPresenter implements GraphingOptionsView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(GraphingOptionsPresenter.class.getName());

    final ClientFactory clientFactory;
    GraphingOptionsView view;
    final Place place;

    AccountLocation accountLocation;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public GraphingOptionsPresenter(final ClientFactory clientFactory, Place place) {
        LOGGER.fine("CREATING NEW GraphingOptionsPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;


        view = clientFactory.getGraphingOptionsView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        view.getHistoryType().setValue(HistoryType.DAILY);
        view.getExportGraphType().setValue(ExportGraphType.ENERGY);



        Date endDate = new Date();
        CalendarUtil.resetTime(endDate);

        Date startDate = new Date(endDate.getTime());
        CalendarUtil.addDaysToDate(startDate, -7);
        CalendarUtil.resetTime(startDate);

        DateRange dateRange = new DateRange(startDate, endDate);
        view.getDateRangeField().setValue(dateRange);

        view.graphSubloadField().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                List<DataExportDataPoint> dataExportDataPoints = new ArrayList<DataExportDataPoint>();
                for (DataExportDataPoint dataExportDataPoint: view.getSubloadDataExportDataPoints()){
                    dataExportDataPoint.setSelected(false);
                    view.allSubloadField().setValue(false, false);
                    dataExportDataPoints.add(dataExportDataPoint);
                }

                view.getSubloadDataExportDataPoints().clear();

                for (DataExportDataPoint dataExportDataPoint: dataExportDataPoints) {
                    view.getSubloadDataExportDataPoints().add(dataExportDataPoint);
                }

                view.setSubloadDataPointPanelVisibility(view.graphSubloadField().getValue());
            }
        });


        RESTFactory.getVirtualECCService(clientFactory).getForAllAccounts(new DefaultMethodCallback<List<AccountLocation>>() {
            @Override
            public void onSuccess(Method method, List<AccountLocation> accountLocationList) {

                view.setLocationList(accountLocationList);
                //Set the default location
                LOGGER.fine("GO: GRAPH REQUEST: " + clientFactory.getGraphRequest());

                boolean found = false;
                if (clientFactory.getGraphRequest() != null){
                    for (AccountLocation accountLocation : accountLocationList) {

                        if (accountLocation.getVirtualECC().getId().equals(clientFactory.getGraphRequest().getVirtualECCId())) {
                            LOGGER.fine("GO: GRAPH REQUEST: accountLocationList:" + accountLocation);
                            view.getLocationPicker().setValue(accountLocation, false);
                            found = true;
                            break;
                        }
                    }

                }

                if (!found){

                    if (clientFactory.getInstance().getDashboardLocation() == null) {
                        if (accountLocationList != null && accountLocationList.size() > 0)
                            LOGGER.fine("GO: GRAPH REQUEST: accountLocationList.get(0)");
                        view.getLocationPicker().setValue(accountLocationList.get(0), false);
                    } else {
                        for (AccountLocation accountLocation : accountLocationList) {
                            if (accountLocation.getVirtualECC().getId().equals(clientFactory.getInstance().getDashboardLocation().getId())) {
                                LOGGER.fine("GO: GRAPH REQUEST: accountLocationList:" + accountLocation);
                                view.getLocationPicker().setValue(accountLocation, false);
                                break;
                            }
                        }
                    }
                }


                changeLocation(view.getLocationPicker().getValue(), true);
            }
        });
    }


    @Override
    public boolean isValid() {
        boolean valid = true;

        view.getLocationPicker().setInvalid("");
        //Check the location
        if (view.getDataExportDataPoints().size() > 0) {
            boolean isSelected = false;
            for (DataExportDataPoint dataExportDataPoint : view.getDataExportDataPoints()) {
                if (dataExportDataPoint.isSelected()) {
                    isSelected = true;
                    break;
                }
            }

            if (view.graphSubloadField().getValue()) {
                if (view.getSubloadDataExportDataPoints().size() > 0) {
                    for (DataExportDataPoint dataExportDataPoint : view.getSubloadDataExportDataPoints()) {
                        if (dataExportDataPoint.isSelected()) {
                            isSelected = true;
                            break;
                        }
                    }
                }
            }

            if (!isSelected) {
                view.getLocationPicker().setInvalid(WebStringResource.INSTANCE.dataPointMissing());
                valid = false;
                view.scrollToTop();
            }
        }


        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void goTo(Place destinationPage) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, place, null));
    }

    @Override
    public void onResize() {

    }


    @Override
    public void doGraph() {

        if (isValid()) {
            GraphRequest graphRequest = new GraphRequest();
            graphRequest.setStartDate(new CalendarKey(view.getDateRangeField().getValue().getStartDate()));
            if (view.getDateRangeField().getValue().getEndDate() != null) {
                graphRequest.setEndDate(new CalendarKey(view.getDateRangeField().getValue().getEndDate()));
            } else {
                graphRequest.setEndDate(new CalendarKey(view.getDateRangeField().getValue().getStartDate()));
            }

            if (view.getDataExportDataPoints().size() == 0) {
                //Net only
                graphRequest.setExportNet(true);
            } else {
                for (DataExportDataPoint dataExportDataPoint : view.getDataExportDataPoints()) {
                    if (dataExportDataPoint.isSelected()) {
                        //Check if its location wide
                        if (dataExportDataPoint.getMtuId() == null) {
                            switch (dataExportDataPoint.getMtuType()) {
                                case NET:
                                    graphRequest.setExportNet(true);
                                    break;
                                case LOAD:
                                    graphRequest.setExportLoad(true);
                                    break;
                                case GENERATION:
                                    graphRequest.setExportGeneration(true);
                                    break;
                            }
                        }
                    }
                }


                if (view.graphSubloadField().getValue()) {
                    for (DataExportDataPoint dataExportDataPoint : view.getSubloadDataExportDataPoints()) {
                        if (dataExportDataPoint.isSelected()) {
                            graphRequest.getMtuIdList().add(dataExportDataPoint.getMtuId());
                        }
                    }
                }
            }

            graphRequest.setUserId(clientFactory.getUser().getId());
            graphRequest.setAccountId(accountLocation.getVirtualECC().getAccountId());
            graphRequest.setVirtualECCId(accountLocation.getVirtualECC().getId());
            graphRequest.setGraphLineType(view.getGraphLineType().getValue());

            boolean exportWeather = false;
            if (view.getGraphLineType().getValue().equals(GraphLineType.TEMPERATURE)) exportWeather = true;
            if (view.getGraphLineType().getValue().equals(GraphLineType.WIND_SPEED)) exportWeather = true;
            if (view.getGraphLineType().getValue().equals(GraphLineType.CLOUD_COVERAGE)) exportWeather = true;
            graphRequest.setExportWeather(exportWeather);
            graphRequest.setExportDemandCost(view.getGraphLineType().getValue().equals(GraphLineType.DEMAND_COST));

            graphRequest.setHistoryType(view.getHistoryType().getValue());
            graphRequest.setExportGraphType(view.getExportGraphType().getValue());
            clientFactory.setGraphRequest(graphRequest);
            goTo(new GraphingPlace(""));
        }
    }

    @Override
    public void changeLocation(final AccountLocation accountLocation) {
        changeLocation(accountLocation, false);
    }

    public void changeLocation(final AccountLocation accountLocation, final boolean updatePage) {
        LOGGER.severe("____________________CHANGE LOCATION________________________");
        view.getDataExportDataPoints().clear();
        view.getSubloadDataExportDataPoints().clear();
        this.accountLocation = accountLocation;

        RESTFactory.getVirtualECCService(clientFactory).getVirtualECCMTUs(accountLocation.getVirtualECC().getAccountId(), accountLocation.getVirtualECC().getId(), new DefaultMethodCallback<List<VirtualECCMTU>>() {
            @Override
            public void onSuccess(Method method, List<VirtualECCMTU> virtualECCMTUs) {
                view.getGraphLineType().setValue(GraphLineType.NONE);
                view.setNetRadioFieldVisible(false);
                view.setGenRadioFieldVisible(false);
                view.setLoadRadioFieldVisible(false);
                view.setSubloadPanelVisibility(false);
                view.graphSubloadField().setValue(false);
                view.setSubloadDataPointPanelVisibility(false);


                if (virtualECCMTUs.size() > 1) {

                    view.setSubloadPanelVisibility(true);

                    //Add the Location wide values if applicable.
                    DataExportDataPoint netPoint = new DataExportDataPoint(accountLocation.getVirtualECC().getId(), MTUType.NET, WebStringResource.INSTANCE.locationNET());
                    if (updatePage && clientFactory.getGraphRequest() != null) netPoint.setSelected(clientFactory.getGraphRequest().isExportNet());
                    view.getDataExportDataPoints().add(netPoint);

                    if (!accountLocation.getVirtualECC().getSystemType().equals(VirtualECCType.NET_ONLY)) {
                        DataExportDataPoint loadPoint = new DataExportDataPoint(accountLocation.getVirtualECC().getId(), MTUType.LOAD, WebStringResource.INSTANCE.locationLOAD());
                        DataExportDataPoint genPoint = new DataExportDataPoint(accountLocation.getVirtualECC().getId(), MTUType.GENERATION, WebStringResource.INSTANCE.locationGENERATION());

                        if (updatePage && clientFactory.getGraphRequest() != null) {
                            loadPoint.setSelected(clientFactory.getGraphRequest().isExportLoad());
                            genPoint.setSelected(clientFactory.getGraphRequest().isExportGeneration());
                        }

                        view.getDataExportDataPoints().add(loadPoint);
                        view.getDataExportDataPoints().add(genPoint);
                    }

                    for (VirtualECCMTU virtualECCMTU : virtualECCMTUs) {
                        DataExportDataPoint dataExportDataPoint =  new DataExportDataPoint(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId(), virtualECCMTU.getMtuDescription());

                        if (updatePage && clientFactory.getGraphRequest() != null){
                            if (clientFactory.getGraphRequest().getMtuIdList() != null) {
                                for (Long mtuId : clientFactory.getGraphRequest().getMtuIdList()) {
                                    if (virtualECCMTU.getMtuId().equals(mtuId)) {
                                        dataExportDataPoint.setSelected(true);
                                        view.graphSubloadField().setValue(true);
                                        view.setSubloadDataPointPanelVisibility(true);
                                        break;
                                    }
                                }
                            }
                        }

                        view.getSubloadDataExportDataPoints().add(dataExportDataPoint);
                    }


                    view.setNetRadioFieldVisible(true);
                    switch(accountLocation.getVirtualECC().getSystemType()){
                        case LOAD_GEN:
                        case NET_GEN:
                            view.setGenRadioFieldVisible(true);
                            view.setLoadRadioFieldVisible(true);
                            break;
                    }
                }


                if (updatePage && clientFactory.getGraphRequest() != null){

                    Date startDate = new Date(clientFactory.getGraphRequest().getStartDate().getYear()-1900,
                            clientFactory.getGraphRequest().getStartDate().getMonth(),
                            clientFactory.getGraphRequest().getStartDate().getDate());

                    Date endDate = new Date(clientFactory.getGraphRequest().getEndDate().getYear()-1900,
                            clientFactory.getGraphRequest().getEndDate().getMonth(),
                            clientFactory.getGraphRequest().getEndDate().getDate());

                    CalendarUtil.resetTime(startDate);
                    CalendarUtil.resetTime(endDate);

                    view.getHistoryType().setValue(clientFactory.getGraphRequest().getHistoryType(), true);
                    view.getGraphLineType().setValue(clientFactory.getGraphRequest().getGraphLineType());
                    view.getExportGraphType().setValue(clientFactory.getGraphRequest().getExportGraphType());
                    view.getDateRangeField().setValue(new DateRange(startDate, endDate), false);

                }


            }
        });

    }

    @Override
    public void cancel() {
        if (clientFactory.getGraphRequest() == null) goTo(new DashboardPlace(""));
        else goTo(new GraphingPlace(""));
    }
}
