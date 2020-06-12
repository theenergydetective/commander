/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.billing;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.model.DateRange;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.BillingPlace;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.export.BillingRequest;
import com.ted.commander.common.model.export.ExportResponse;
import org.fusesource.restygwt.client.Method;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class BillingPresenter implements BillingView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(BillingPresenter.class.getName());

    final ClientFactory clientFactory;
    BillingView view;
    final BillingPlace place;

    public BillingPresenter(final ClientFactory clientFactory, BillingPlace place) {
        LOGGER.fine("CREATING NEW DashboardPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;


        view = clientFactory.getBillingView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        view.getDataExportFileType().setValue(DataExportFileType.CSV);
        view.getHistoryType().setValue(HistoryType.BILLING_CYCLE);
        Date endDate = new Date();
        CalendarUtil.addDaysToDate(endDate, 1);
        CalendarUtil.resetTime(endDate);

        Date startDate = new Date(endDate.getTime());
        CalendarUtil.addDaysToDate(startDate, -7);
        CalendarUtil.resetTime(startDate);

        DateRange dateRange = new DateRange(startDate, endDate);
        view.getDateRangeField().setValue(dateRange);

        RESTFactory.getVirtualECCService(clientFactory).getForAllAccounts(new DefaultMethodCallback<List<AccountLocation>>() {
            @Override
            public void onSuccess(Method method, List<AccountLocation> accountLocationList) {
                view.setLocationList(accountLocationList);
            }
        });
    }


    @Override
    public boolean isValid() {
        boolean valid = true;
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
            BillingRequest dataExportRequest = new BillingRequest();
            dataExportRequest.setStartDate(new CalendarKey(view.getDateRangeField().getValue().getStartDate()));
            if (view.getDateRangeField().getValue().getEndDate() != null) {
                dataExportRequest.setEndDate(new CalendarKey(view.getDateRangeField().getValue().getEndDate()));
            } else {
                dataExportRequest.setEndDate(new CalendarKey(view.getDateRangeField().getValue().getStartDate()));
            }
            dataExportRequest.setHistoryType(view.getHistoryType().getValue());
            dataExportRequest.setDataExportFileType(view.getDataExportFileType().getValue());
            List<AccountLocation> accountLocationsForExport = view.getLocationsForExport();
            for (int i=0; i < accountLocationsForExport.size(); i++){
                dataExportRequest.getVirtualECCIdList().add(accountLocationsForExport.get(i).getVirtualECC().getId());
            }
            view.setLoadingVisible(true);

            LOGGER.fine("Performing Export " + dataExportRequest);

            RESTFactory.getExportClient(clientFactory).getBllingStatus(dataExportRequest, dataExportRequest.getDataExportFileType(), new DefaultMethodCallback<ExportResponse>() {
                @Override
                public void onSuccess(Method method, ExportResponse exportResponse) {
                    view.setLoadingVisible(false);
                    view.setDownloadUrl(exportResponse.getUrl());
                }
            });

        }
    }
}
