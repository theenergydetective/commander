/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.petecode.common.client.logging.ConsoleLoggerFactory;
import com.ted.commander.client.callback.DefaultRunAsyncCallback;
import com.ted.commander.client.callback.SimpleCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.view.login.LoginPresenter;

import java.util.logging.Logger;


public class LoginActivity extends AuthorizedActivity {

    static final Logger LOGGER = ConsoleLoggerFactory.getLogger(LoginPresenter.class);

    public LoginActivity(final Place place, final ClientFactory clientFactory) {
        super(place, clientFactory);
    }

    @Override
    public void start(final AcceptsOneWidget container, final EventBus eventBus) {
        LOGGER.fine("Checking remember me authorization");
        authorize(new SimpleCallback<Boolean>() {
            @Override
            public void onCallback(final Boolean value) {
                LOGGER.fine("Checking remember me authorization: " + value);
                if (value) {
                    LOGGER.fine("Already authorized. Redirecting to dashboard.");
                    clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new DashboardPlace(""), place, null));
                } else {
                    GWT.runAsync(new DefaultRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            LOGGER.fine("Not authenticated. Directing to Login Page");
                            clientFactory.clearInstance();
                            final LoginPresenter presenter = new LoginPresenter(clientFactory, (LoginPlace) place);
                            container.setWidget(presenter);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        clientFactory.setResizeHandler(null);
    }
}
