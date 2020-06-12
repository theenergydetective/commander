/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.export;

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
import com.ted.commander.client.places.ExportPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.export.ExportRequest;
import com.ted.commander.common.model.export.ExportResponse;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class ExportPresenter implements ExportView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ExportPresenter.class.getName());

    final ClientFactory clientFactory;
    ExportView view;
    final ExportPlace place;


    public ExportPresenter(final ClientFactory clientFactory, ExportPlace place) {
        LOGGER.fine("CREATING NEW DashboardPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;


        view = clientFactory.getExportView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());


        view.getHistoryType().setValue(HistoryType.DAILY);
        view.getDataExportFileType().setValue(DataExportFileType.CSV);
        view.weatherField().setValue(Boolean.FALSE);

        Date endDate = new Date();
        CalendarUtil.addDaysToDate(endDate, 1);
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
                for (DataExportDataPoint dataExportDataPoint : view.getSubloadDataExportDataPoints()) {
                    dataExportDataPoint.setSelected(false);
                    view.allSubloadField().setValue(false, false);
                    dataExportDataPoints.add(dataExportDataPoint);
                }

                view.getSubloadDataExportDataPoints().clear();

                for (DataExportDataPoint dataExportDataPoint : dataExportDataPoints) {
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
                if (clientFactory.getInstance().getDashboardLocation() == null) {
                    if (accountLocationList != null && accountLocationList.size() > 0)
                        view.getLocationPicker().setValue(accountLocationList.get(0), false);
                } else {
                    for (AccountLocation accountLocation : accountLocationList) {
                        if (accountLocation.getVirtualECC().getId().equals(clientFactory.getInstance().getDashboardLocation().getId())) {
                            view.getLocationPicker().setValue(accountLocation, false);
                            break;
                        }
                    }
                }
                changeLocation(view.getLocationPicker().getValue());
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

        LOGGER.fine("EXPORT: " + valid);

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
    public void doExport() {

        if (isValid()) {

            //Set up the base parameters
            ExportRequest dataExportRequest = new ExportRequest();
            dataExportRequest.setAccountId(view.getLocationPicker().getValue().getVirtualECC().getAccountId());
            dataExportRequest.setVirtualECCId(view.getLocationPicker().getValue().getVirtualECC().getId());
            dataExportRequest.setStartDate(new CalendarKey(view.getDateRangeField().getValue().getStartDate()));
            if (view.getDateRangeField().getValue().getEndDate() != null) {
                dataExportRequest.setEndDate(new CalendarKey(view.getDateRangeField().getValue().getEndDate()));
            } else {
                dataExportRequest.setEndDate(new CalendarKey(view.getDateRangeField().getValue().getStartDate()));
            }
            dataExportRequest.setDataExportFileType(view.getDataExportFileType().getValue());
            dataExportRequest.setHistoryType(view.getHistoryType().getValue());
            dataExportRequest.setExportWeather(view.weatherField().getValue());
            dataExportRequest.setExportDemandCost(view.demandCostField().getValue());

            if (view.getDataExportDataPoints().size() == 0) {
                //Net only
                dataExportRequest.setExportNet(true);
            } else {
                for (DataExportDataPoint dataExportDataPoint : view.getDataExportDataPoints()) {
                    if (dataExportDataPoint.isSelected()) {
                        //Check if its location wide
                        if (dataExportDataPoint.getMtuId() == null) {
                            switch (dataExportDataPoint.getMtuType()) {
                                case NET:
                                    dataExportRequest.setExportNet(true);
                                    break;
                                case LOAD:
                                    dataExportRequest.setExportLoad(true);
                                    break;
                                case GENERATION:
                                    dataExportRequest.setExportGeneration(true);
                                    break;
                            }
                        }
                    }
                }

                if (view.graphSubloadField().getValue()) {
                    for (DataExportDataPoint dataExportDataPoint : view.getSubloadDataExportDataPoints()) {
                        if (dataExportDataPoint.isSelected()) {
                            dataExportRequest.getMtuIdList().add(dataExportDataPoint.getMtuId());
                        }
                    }
                }
            }

            view.setLoadingVisible(true);

            LOGGER.fine("Performing Export " + dataExportRequest);

            RESTFactory.getExportClient(clientFactory).getExportStatus(dataExportRequest, dataExportRequest.getVirtualECCId(), dataExportRequest.getDataExportFileType(), new DefaultMethodCallback<ExportResponse>() {
                @Override
                public void onSuccess(Method method, ExportResponse exportResponse) {
                    view.setLoadingVisible(false);
                    view.setDownloadUrl(exportResponse.getUrl());
                }
            });

        }
    }

    @Override
    public void changeLocation(final AccountLocation accountLocation) {
        view.getDataExportDataPoints().clear();


        RESTFactory.getVirtualECCService(clientFactory).getVirtualECCMTUs(accountLocation.getVirtualECC().getAccountId(), accountLocation.getVirtualECC().getId(), new DefaultMethodCallback<List<VirtualECCMTU>>() {
            @Override
            public void onSuccess(Method method, List<VirtualECCMTU> virtualECCMTUs) {

                view.setSubloadPanelVisibility(false);
                view.graphSubloadField().setValue(false);
                view.setSubloadDataPointPanelVisibility(false);


                if (virtualECCMTUs.size() > 1) {
                    view.setSubloadPanelVisibility(true);

                    view.clearDataExportPoints();

                    //Add the Location wide values if applicable.
                    view.getDataExportDataPoints().add(new DataExportDataPoint(accountLocation.getVirtualECC().getId(), MTUType.NET, WebStringResource.INSTANCE.locationNET()));
                    if (!accountLocation.getVirtualECC().getSystemType().equals(VirtualECCType.NET_ONLY)) {
                        view.getDataExportDataPoints().add(new DataExportDataPoint(accountLocation.getVirtualECC().getId(), MTUType.LOAD, WebStringResource.INSTANCE.locationLOAD()));
                        view.getDataExportDataPoints().add(new DataExportDataPoint(accountLocation.getVirtualECC().getId(), MTUType.GENERATION, WebStringResource.INSTANCE.locationGENERATION()));
                    }


                    for (VirtualECCMTU virtualECCMTU : virtualECCMTUs) {
                        view.getSubloadDataExportDataPoints().add(new DataExportDataPoint(virtualECCMTU.getVirtualECCId(), virtualECCMTU.getMtuId(), virtualECCMTU.getMtuDescription()));
                    }
                }
            }
        });

    }
}
