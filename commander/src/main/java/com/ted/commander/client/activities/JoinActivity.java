/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ted.commander.client.callback.DefaultRunAsyncCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.places.JoinPlace;
import com.ted.commander.client.presenter.JoinPresenter;


public class JoinActivity extends AbstractActivity {

    final JoinPlace place;
    private final ClientFactory clientFactory;

    public JoinActivity(JoinPlace place, ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        this.place = place;
    }

    @Override
    public void start(final AcceptsOneWidget container, final EventBus eventBus) {
        GWT.runAsync(new DefaultRunAsyncCallback() {
            @Override
            public void onSuccess() {
                JoinPresenter presenter = new JoinPresenter(clientFactory, place);
                container.setWidget(presenter);
            }
        });
    }

}
