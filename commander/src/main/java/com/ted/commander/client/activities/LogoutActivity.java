/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.petecode.common.client.logging.ConsoleLoggerFactory;
import com.ted.commander.client.callback.DefaultRunAsyncCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.places.LogoutPlace;

import java.util.logging.Logger;


public class LogoutActivity extends AbstractActivity {

    private static Logger LOGGER = ConsoleLoggerFactory.getLogger(LogoutActivity.class);
    private final LogoutPlace place;
    private final ClientFactory clientFactory;

    public LogoutActivity(LogoutPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(final AcceptsOneWidget container, final EventBus eventBus) {
        GWT.runAsync(new DefaultRunAsyncCallback() {
            @Override
            public void onSuccess() {
                LOGGER.fine("PROCESSING LOGOUT");
                clientFactory.clearInstance();
                LOGGER.fine("Redirecting to LOGIN");
                eventBus.fireEvent(new PlaceRequestEvent(new LoginPlace(""), place, null));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        clientFactory.setResizeHandler(null);
    }
}

