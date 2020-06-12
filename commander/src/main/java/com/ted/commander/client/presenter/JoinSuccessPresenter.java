/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.JoinSuccessPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.join.JoinSuccessView;

import java.util.logging.Logger;

public class
        JoinSuccessPresenter implements JoinSuccessView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(JoinSuccessPresenter.class.getName());

    final ClientFactory clientFactory;
    final JoinSuccessView view;
    final JoinSuccessPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public JoinSuccessPresenter(ClientFactory clientFactory, JoinSuccessPlace place) {
        LOGGER.fine("CREATING NEW LOGIN PRESENTER");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getJoinSuccessView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, new DashboardPlace(""), null));
    }

    @Override
    public void onResize() {

    }

}
