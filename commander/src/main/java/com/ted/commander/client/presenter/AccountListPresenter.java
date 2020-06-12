/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.AccountListPlace;
import com.ted.commander.client.places.AccountPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.accountList.AccountListView;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class AccountListPresenter implements AccountListView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AccountListPresenter.class.getName());

    final ClientFactory clientFactory;
    final AccountListView view;
    final AccountListPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public AccountListPresenter(ClientFactory clientFactory, AccountListPlace place) {
        LOGGER.fine("CREATING NEW AccountSettingsPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getAccountListView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());


        //Look up the account memberships
        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
            @Override
            public void onSuccess(Method method, AccountMemberships accountMemberships) {
                view.setAccountMemberships(accountMemberships);
            }
        });

    }


    @Override
    public void goTo(Place place) {
        if (isValid()) {
            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, this.place, null));
        }
    }

    @Override
    public void onResize() {

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
    public void edit(AccountMembership accountMembership) {
        clientFactory.getInstance().setLastEditedAccountMembership(accountMembership);
        goTo(new AccountPlace(accountMembership.getAccount().getId()));
    }
}
