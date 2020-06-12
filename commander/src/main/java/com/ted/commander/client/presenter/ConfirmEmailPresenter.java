/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.OAuthEvent;
import com.ted.commander.client.events.OAuthFailEvent;
import com.ted.commander.client.events.OAuthHandler;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.ConfirmEmailPlace;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.JoinPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.confirmEmail.ConfirmEmailView;

import java.util.logging.Logger;


public class ConfirmEmailPresenter implements ConfirmEmailView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ConfirmEmailPresenter.class.getName());


    final ClientFactory clientFactory;
    final ConfirmEmailView confirmEmailView;
    final ConfirmEmailPlace place;

    public ConfirmEmailPresenter(ClientFactory clientFactory, ConfirmEmailPlace place) {
        LOGGER.fine("CREATING NEW LOGIN PRESENTER");
        this.clientFactory = clientFactory;
        this.place = place;
        confirmEmailView = clientFactory.getConfirmEmailView();
        confirmEmailView.setPresenter(this);
        confirmEmailView.setLogo(clientFactory.getInstance().getLogo().getBigLogo());


    }


    @Override
    public boolean isValid() {
        boolean valid = true;
        confirmEmailView.getUsername().setInvalid("");
        confirmEmailView.getPassword().setInvalid("");

        if (confirmEmailView.getPassword().getValue().isEmpty()) {
            valid = false;
            confirmEmailView.getPassword().setInvalid(WebStringResource.INSTANCE.requiredField());
        }

        if (confirmEmailView.getUsername().getValue().isEmpty()) {
            valid = false;
            confirmEmailView.getUsername().setInvalid(WebStringResource.INSTANCE.requiredField());
        }
        return valid;
    }


    @Override
    public void authenticate() {
        LOGGER.fine("AUTHENTICATE CALLED");
        if (isValid()) {

            RESTFactory.authenticate(clientFactory, confirmEmailView.getUsername().getValue().toLowerCase(), confirmEmailView.getPassword().getValue(), new OAuthHandler() {
                @Override
                public void onAuth(OAuthEvent event) {
                    if (event.getAuthResponse().getError() == null) {
                        LOGGER.fine("SUCCESSFUL AUTH:" + event.getAuthResponse());
                        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new DashboardPlace(""), place, null));
                    } else {
                        LOGGER.fine("FAILED AUTH:" + event.getAuthResponse());
                        confirmEmailView.getPassword().setInvalid(WebStringResource.INSTANCE.authError());
                    }
                }

                @Override
                public void onFail(OAuthFailEvent event) {
                    LOGGER.fine("SYSTEM FAILED AUTH");
                    confirmEmailView.getPassword().setInvalid(WebStringResource.INSTANCE.systemError());
                }
            });


        }

    }

    @Override
    public void requestPassword() {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new JoinPlace(""), place, null));
    }

    @Override
    public Widget asWidget() {
        return confirmEmailView.asWidget();
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, new DashboardPlace(""), null));
    }

    @Override
    public void onResize() {

    }

}
