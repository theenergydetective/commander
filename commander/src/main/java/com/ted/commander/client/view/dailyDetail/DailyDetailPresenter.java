/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dailyDetail;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DailyDetailPlace;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.DailySummary;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class DailyDetailPresenter implements DailyDetailView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(DailyDetailPresenter.class.getName());

    final ClientFactory clientFactory;
    final DailyDetailView view;
    final DailyDetailPlace place;
    final Long locationId;
    CalendarKey calendarKey;


    public DailyDetailPresenter(final ClientFactory clientFactory, DailyDetailPlace place) {
        LOGGER.fine("CREATING NEW DashboardPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        this.locationId = place.getLocationId();
        this.calendarKey = place.getCalendarKey();


        view = clientFactory.getDailyDetailView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());
        query();

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
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, new DailyDetailPlace(locationId, calendarKey), null));
    }

    @Override
    public void onResize() {
        view.onResize();
    }

    @Override
    public void query() {
        query(calendarKey);
    }

    @Override
    public void query(CalendarKey calendarKey) {
        this.calendarKey = calendarKey;
        view.setLoadingVisible(true);
        String startDate = calendarKey.getYear() + "-" + calendarKey.getMonth() + "-" + calendarKey.getDate();
        RESTFactory.getHistoryClient(clientFactory).getDailySummary(locationId, startDate, new DefaultMethodCallback<DailySummary>() {
            @Override
            public void onSuccess(Method method, DailySummary dailySummary) {
                view.setSummary(dailySummary.getEnergyPlan(), dailySummary);
                view.setLoadingVisible(false);
            }
        });
    }


}
