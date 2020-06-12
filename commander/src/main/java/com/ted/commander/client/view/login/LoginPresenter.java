/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.login;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.OAuthEvent;
import com.ted.commander.client.events.OAuthFailEvent;
import com.ted.commander.client.events.OAuthHandler;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.*;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;

import java.util.logging.Logger;


public class LoginPresenter implements LoginView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(LoginPresenter.class.getName());


    final ClientFactory clientFactory;
    final LoginView loginView;
    final LoginPlace place;

    public LoginPresenter(ClientFactory clientFactory, LoginPlace place) {
        LOGGER.fine("CREATING NEW LOGIN PRESENTER");
        this.clientFactory = clientFactory;
        this.place = place;
        loginView = clientFactory.getLoginView();
        loginView.setPresenter(this);
        loginView.setLogo(clientFactory.getInstance().getLogo().getBigLogo());

    }


    @Override
    public boolean isValid() {
        boolean valid = true;
        loginView.getUsername().setInvalid("");
        loginView.getPassword().setInvalid("");

        if (loginView.getPassword().getValue().isEmpty()) {
            valid = false;
            loginView.getPassword().setInvalid(WebStringResource.INSTANCE.requiredField());
        }

        if (loginView.getUsername().getValue().isEmpty()) {
            valid = false;
            loginView.getUsername().setInvalid(WebStringResource.INSTANCE.requiredField());
        }
        return valid;
    }


    @Override
    public void authenticate() {
        LOGGER.fine("AUTHENTICATE CALLED");
        if (isValid()) {
            RESTFactory.authenticate(clientFactory, loginView.getUsername().getValue().toLowerCase(), loginView.getPassword().getValue(), new OAuthHandler() {
                @Override
                public void onAuth(OAuthEvent event) {
                    if (event.getAuthResponse().getError() == null) {
                        LOGGER.fine("SUCCESSFUL AUTH:" + event.getAuthResponse());
                        if (place.getToken() != null && place.getToken().equals("nopost")){
                            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new NoPostPlace(""), place, null));
                        } else {
                            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new DashboardPlace(""), place, null));
                        }
                    } else {
                        LOGGER.fine("FAILED AUTH:" + event.getAuthResponse());
                        loginView.getPassword().setInvalid(WebStringResource.INSTANCE.authError());
                    }
                }

                @Override
                public void onFail(OAuthFailEvent event) {
                    if (event.toString().contains("invalid_grant")){
                        loginView.getPassword().setInvalid(WebStringResource.INSTANCE.authError());
                    } else {
                        loginView.getPassword().setInvalid(WebStringResource.INSTANCE.systemError());
                    }
                }
            });


        }

    }

    @Override
    public void join() {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new JoinPlace(""), place, null));
    }

    @Override
    public void forgotPassword() {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new ForgotPasswordPlace(""), place, null));
    }

    @Override
    public Widget asWidget() {
        return loginView.asWidget();
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, new LoginPlace(""), null));
    }

    @Override
    public void onResize() {

    }

}
