/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparison;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.model.DateRange;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.model.Instance;
import com.ted.commander.client.places.ComparisonGraphPlace;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.ComparisonQueryRequest;
import com.ted.commander.common.model.VirtualECC;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComparisonPresenter implements ComparisonView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ComparisonPresenter.class.getName());

    final ClientFactory clientFactory;
    final ComparisonView view;
    final ComparisonPlace place;

    List<AccountLocation> accountLocations = null;
    List<VirtualECC> selectedLocations = new ArrayList<VirtualECC>();

    WebStringResource stringRes = WebStringResource.INSTANCE;


    public ComparisonPresenter(final ClientFactory clientFactory, ComparisonPlace place) {
        LOGGER.fine("CREATING NEW ComparisonPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getComparisonView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        Date endDate = new Date();
        CalendarUtil.addDaysToDate(endDate, 1);
        CalendarUtil.resetTime(endDate);

        Date startDate = new Date(endDate.getTime());
        CalendarUtil.addDaysToDate(startDate, -7);
        CalendarUtil.resetTime(startDate);

        DateRange dateRange = new DateRange(startDate, endDate);
        view.datePicker().setValue(dateRange);

        if (clientFactory.getInstance().getComparisonQueryRequest() != null) {
            try {
                LOGGER.fine("Using existing query");
                ComparisonQueryRequest comparisonQueryRequest = clientFactory.getInstance().getComparisonQueryRequest();
                selectedLocations.addAll(comparisonQueryRequest.getLocationList());
                if (selectedLocations.size() > 0) {
                    LOGGER.fine("EXISTING HISTORY TYPE: " + comparisonQueryRequest.getHistoryType().toString());
                    view.getUnitOfTime().setValue(comparisonQueryRequest.getHistoryType());
                    if (comparisonQueryRequest.getStartDate() != null) {

                        DateRange newDateRange = new DateRange(new Date(comparisonQueryRequest.getStartDate().getYear() - 1900, comparisonQueryRequest.getStartDate().getMonth(), comparisonQueryRequest.getStartDate().getDate()),
                                                                new Date(comparisonQueryRequest.getEndDate().getYear() - 1900, comparisonQueryRequest.getEndDate().getMonth(), comparisonQueryRequest.getEndDate().getDate()));
                        view.datePicker().setValue(newDateRange);
                    }
                    view.setLocations(selectedLocations);
                } else {
                    view.getUnitOfTime().setValue(HistoryType.HOURLY);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }


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

    }


    @Override
    public void queryLocations() {
        if (accountLocations == null) {
            RESTFactory.getVirtualECCService(clientFactory).getForAllAccounts(new DefaultMethodCallback<List<AccountLocation>>() {
                @Override
                public void onSuccess(Method method, List<AccountLocation> accountLocationList) {
                    accountLocations = accountLocationList;
                    view.showLocationSelector(accountLocations);
                }
            });
        } else {
            view.showLocationSelector(accountLocations);
        }
    }

    @Override
    public void addLocation(VirtualECC addedLocation) {
        boolean contains = false;
        for (VirtualECC a : selectedLocations) {
            if (a.getId().equals(addedLocation.getId())) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            selectedLocations.add(addedLocation);
            view.setLocations(selectedLocations);
        }
    }

    @Override
    public void removeLocation(VirtualECC deletedLocation) {
        List<VirtualECC> oldList = selectedLocations;

        selectedLocations = new ArrayList<VirtualECC>();
        for (VirtualECC a : oldList) {
            if (!a.getId().equals(deletedLocation.getId())) {
                selectedLocations.add(a);
            }
        }
        view.setLocations(selectedLocations);
    }

    @Override
    public void generate() {
        ComparisonQueryRequest comparisonQueryRequest = new ComparisonQueryRequest();
        comparisonQueryRequest.getLocationList().addAll(selectedLocations);
        comparisonQueryRequest.setHistoryType(view.getUnitOfTime().getValue());

        Date startDate = view.datePicker().getValue().getStartDate();
        Date endDate = view.datePicker().getValue().getEndDate();
        if (endDate == null) endDate = new Date(startDate.getTime());


        comparisonQueryRequest.setStartDate(new CalendarKey(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate()));
        comparisonQueryRequest.setEndDate(new CalendarKey(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate()));
        clientFactory.getInstance().setComparisonQueryRequest(comparisonQueryRequest);
        Instance.store(clientFactory.getInstance());
        goTo(new ComparisonGraphPlace(""));
    }
}
