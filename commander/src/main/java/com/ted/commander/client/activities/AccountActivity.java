/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ted.commander.client.callback.DefaultRunAsyncCallback;
import com.ted.commander.client.callback.SimpleCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.places.AccountPlace;
import com.ted.commander.client.presenter.AccountPresenter;

public class AccountActivity extends AuthorizedActivity {


    public AccountActivity(AccountPlace place, ClientFactory clientFactory) {
        super(place, clientFactory);
    }

    @Override
    public void start(final AcceptsOneWidget container, EventBus eventBus) {

        authorize(new SimpleCallback<Boolean>() {
            @Override
            public void onCallback(Boolean value) {
                GWT.runAsync(new DefaultRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        final AccountPresenter presenter = new AccountPresenter(clientFactory, (AccountPlace) place);
                        container.setWidget(presenter);

                        clientFactory.setResizeHandler(new ResizeHandler() {
                            @Override
                            public void onResize(ResizeEvent resizeEvent) {
                                presenter.onResize();
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        clientFactory.setResizeHandler(null);
    }


}
