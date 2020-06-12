/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparisonGraph;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.enums.ComparisonGraphType;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.ComparisonGraphPlace;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.ComparisonQueryRequest;
import com.ted.commander.common.model.ComparisonQueryResponse;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.History;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class ComparisonGraphPresenter implements ComparisonGraphView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ComparisonGraphPresenter.class.getName());
    static DateTimeFormat titleDateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_FULL);
    static DateTimeFormat billingCycleDateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH);
    static final DateTimeFormat myf = DateTimeFormat.getFormat("MMM yyyy");
    static final DateTimeFormat dmyf = DateTimeFormat.getFormat("MMM dd yyyy");


    final ClientFactory clientFactory;
    final ComparisonGraphView view;
    final ComparisonGraphPlace place;
    final ComparisonQueryRequest comparisonQueryRequest;
    final List<History> historyList[];
    long currentQueryId = 0;
    int locationIndex = 0;
    String currencyCode = "USD";

    public ComparisonGraphPresenter(final ClientFactory clientFactory, ComparisonGraphPlace place) {
        LOGGER.fine("CREATING NEW ComparisonGraphPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getComparisonGraphView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());
        comparisonQueryRequest = clientFactory.getInstance().getComparisonQueryRequest();
        historyList = new ArrayList[comparisonQueryRequest.getLocationList().size()];



        //view.getGraphDataType().addValueChangeHandler(modeButtonChangeHandler);

        view.setMode(clientFactory.getInstance().getComparisonQueryRequest().getHistoryType());

        StringBuilder titleStringBuilder = new StringBuilder();

        DateTimeFormat dtf = titleDateTimeFormat;

        switch (clientFactory.getInstance().getComparisonQueryRequest().getHistoryType()) {
            case MINUTE:
                titleStringBuilder.append(WebStringResource.INSTANCE.minuteGraphResolution());
                break;
            case HOURLY:
                titleStringBuilder.append(WebStringResource.INSTANCE.hourGraphResolution());
                break;
            case DAILY:
                titleStringBuilder.append(WebStringResource.INSTANCE.dayGraphResolution());
                break;
            case BILLING_CYCLE:
                titleStringBuilder.append(WebStringResource.INSTANCE.monthGraphResolution());
                dtf = billingCycleDateTimeFormat;
                break;
        }



        titleStringBuilder.append(": ");
        LOGGER.warning(">>>>>>SD: " + comparisonQueryRequest.getStartDate());
        LOGGER.warning(">>>>>>ED: " + comparisonQueryRequest.getEndDate());
        if (comparisonQueryRequest.getEndDate() == null || comparisonQueryRequest.getStartDate().equals(comparisonQueryRequest.getEndDate())){
            switch (clientFactory.getInstance().getComparisonQueryRequest().getHistoryType()) {
                case MINUTE:
                case HOURLY:
                case DAILY:
                    titleStringBuilder.append(dmyf.format(toTitleDate(comparisonQueryRequest.getStartDate())));
                    break;
                case BILLING_CYCLE:
                    titleStringBuilder.append(myf.format(toTitleDate(comparisonQueryRequest.getStartDate())));
                    break;
            }
        } else {
            titleStringBuilder.append(dtf.format(toTitleDate(comparisonQueryRequest.getStartDate())));
            titleStringBuilder.append(" - ");
            titleStringBuilder.append(dtf.format(toTitleDate(comparisonQueryRequest.getEndDate())));
        }

        view.setTitleField(titleStringBuilder.toString());
        view.setGraphDataType(ComparisonGraphType.POWER);

        query();
    }


    private Date toTitleDate(CalendarKey calendarKey) {
        Date date = new Date((calendarKey.getYear() - 1900), calendarKey.getMonth(), calendarKey.getDate(), 0, 0, 0);
        CalendarUtil.resetTime(date);
        return date;
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void goTo(Place destinationPage) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, new ComparisonPlace(""), null));
    }

    @Override
    public void onResize() {
//        query();
    }



    public void query() {
        LOGGER.fine("EXECUTING DATA QUERY");
        view.setLoadingVisible(true);
        RESTFactory.getComparisonClient(clientFactory).generate(comparisonQueryRequest, new DefaultMethodCallback<ComparisonQueryResponse>() {
            @Override
            public void onSuccess(Method method, ComparisonQueryResponse comparisonQueryResponse) {
                locationIndex = 0;
                for (VirtualECC virtualECC: comparisonQueryResponse.getVirtualECCList()){
                    historyList[locationIndex] = new ArrayList<History>();
                    switch (comparisonQueryRequest.getHistoryType()){
                        case MINUTE:
                            historyList[locationIndex].addAll(comparisonQueryResponse.getMinuteHistoryList().get(virtualECC.getId()));
                            break;
                        case HOURLY:
                            historyList[locationIndex].addAll(comparisonQueryResponse.getHourHistoryList().get(virtualECC.getId()));
                            break;
                        case DAILY:
                            historyList[locationIndex].addAll(comparisonQueryResponse.getDayHistoryList().get(virtualECC.getId()));
                            break;
                        case BILLING_CYCLE:
                            historyList[locationIndex].addAll(comparisonQueryResponse.getBillingCycleHistoryList().get(virtualECC.getId()));
                            break;
                    }
                    locationIndex++;
                }
                view.drawGraph(comparisonQueryRequest, comparisonQueryResponse, historyList, currencyCode);
                view.setLoadingVisible(false);
            }
        });
    }

}
