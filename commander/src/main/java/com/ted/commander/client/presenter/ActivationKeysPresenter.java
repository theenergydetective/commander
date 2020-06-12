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
import com.ted.commander.client.places.ActivationKeysPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.activationKeys.ActivationKeysView;
import com.ted.commander.common.model.AccountMemberships;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class ActivationKeysPresenter implements ActivationKeysView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ActivationKeysPresenter.class.getName());

    final ClientFactory clientFactory;
    final ActivationKeysView view;
    final ActivationKeysPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public ActivationKeysPresenter(ClientFactory clientFactory, ActivationKeysPlace place) {
        LOGGER.fine("CREATING NEW ActivationKeysPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getActivationKeysView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
                    @Override
                    public void onSuccess(Method method, AccountMemberships accounts) {
                        view.setMemberships(accounts.getAccountMemberships());
                    }
                }
        );

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
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, this.place, null));
    }

    @Override
    public void onResize() {

    }


}
