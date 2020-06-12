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
import com.ted.commander.client.places.AccountMTUPlace;
import com.ted.commander.client.places.AccountPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.accountMTU.AccountMTUView;
import com.ted.commander.common.model.MTU;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class AccountMTUPresenter implements AccountMTUView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AccountMTUPresenter.class.getName());

    final ClientFactory clientFactory;
    final AccountMTUView view;
    final AccountMTUPlace place;
    WebStringResource stringRes = WebStringResource.INSTANCE;



    public AccountMTUPresenter(final ClientFactory clientFactory, AccountMTUPlace place) {
        LOGGER.fine("CREATING NEW AccountMTUPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getAccountMTUView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        MTU mtu = clientFactory.getInstance().getLastEditedMTU();

        if (mtu.getDescription() == null) mtu.setDescription(mtu.getHexId());

        view.getDescriptionField().setValue(mtu.getDescription());
        view.getSerialNumberField().setValue(mtu.getHexId());
        view.getTypeField().setValue(mtu.isSpyder() ? WebStringResource.INSTANCE.spyder() : WebStringResource.INSTANCE.mtu());

        view.getMTUTypeValue().setValue(mtu.getMtuType().toString());

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
        boolean valid = true;
        view.getDescriptionField().setInvalid("");
        if (view.getDescriptionField().getValue().trim().isEmpty()) {
            view.getDescriptionField().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void deleteMTU() {
        final MTU mtu = clientFactory.getInstance().getLastEditedMTU();
        RESTFactory.getMTUService(clientFactory).deleteMTU(mtu, new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                goTo(new AccountPlace(mtu.getAccountId()));
            }
        });
    }

    @Override
    public void goToAccountPlace() {
        final MTU mtu = clientFactory.getInstance().getLastEditedMTU();
        goTo(new AccountPlace(mtu.getAccountId()));
    }
}
