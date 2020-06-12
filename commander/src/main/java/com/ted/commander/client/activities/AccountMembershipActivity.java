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
import com.ted.commander.client.places.AccountMembershipPlace;
import com.ted.commander.client.presenter.AccountMembershipPresenter;

public class AccountMembershipActivity extends AuthorizedActivity {

    public AccountMembershipActivity(AccountMembershipPlace place, ClientFactory clientFactory) {
        super(place, clientFactory);
    }

    @Override
    public void start(final AcceptsOneWidget container, final EventBus eventBus) {

        authorize(new SimpleCallback<Boolean>() {
            @Override
            public void onCallback(Boolean value) {
                GWT.runAsync(new DefaultRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        final AccountMembershipPresenter presenter = new AccountMembershipPresenter(clientFactory, (AccountMembershipPlace) place);
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
