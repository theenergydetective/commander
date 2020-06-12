/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.LocationEditPlace;
import com.ted.commander.client.places.LocationListPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.RESTPostResponse;
import com.ted.commander.common.model.VirtualECC;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.logging.Logger;

public class LocationListPresenter implements LocationListView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(LocationListPresenter.class.getName());

    final ClientFactory clientFactory;
    final LocationListView view;
    final LocationListPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;
    private AccountMembership selectedAccount;

    public LocationListPresenter(ClientFactory clientFactory, LocationListPlace place) {
        LOGGER.fine("CREATING NEW LocationSettingsPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getLocationListView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());




        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
            @Override
            public void onSuccess(Method method, AccountMemberships accountMemberships) {
                view.setAccountMemberships(accountMemberships);
                selectAccount(accountMemberships.getAccountMemberships().get(0));
            }
        });


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
        if (isValid()) {
            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, this.place, null));
        }
    }

    @Override
    public void onResize() {

    }

    @Override
    public void newLocation() {
        AccountMembership accountMembership = clientFactory.getInstance().getLastEditedAccountMembership();
        final VirtualECC virtualECC = new VirtualECC(accountMembership.getAccount().getId(), WebStringResource.INSTANCE.newLocation(),  VirtualECCType.NET_ONLY, "US/Eastern");
        RESTFactory.getVirtualECCService(clientFactory).create(virtualECC, new DefaultMethodCallback<RESTPostResponse>() {
            @Override
            public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                virtualECC.setId(restPostResponse.getId());
                clientFactory.getInstance().setLastEditLocation(virtualECC);
                goTo(new LocationEditPlace(virtualECC.getId()));
            }
        });

    }

    @Override
    public void edit(VirtualECC virtualECC) {
        if (virtualECC.getAccountId().equals(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId())) {
            LOGGER.fine("EDITING LOCATION");
            clientFactory.getInstance().setLastEditLocation(virtualECC);
            goTo(new LocationEditPlace(virtualECC.getId()));
        } else {
            LOGGER.severe("LOCATION AND ACCOUNT DO NOT MATCH");
        }
    }

    @Override
    public void listLocations(AccountMembership accountMembership) {
        clientFactory.getInstance().setLastEditedAccountMembership(accountMembership);
        RESTFactory.getVirtualECCService(clientFactory).getForAccount(accountMembership.getAccount().getId(), new DefaultMethodCallback<List<VirtualECC>>() {
            @Override
            public void onSuccess(Method method, List<VirtualECC> virtualECCs) {
                view.setLocations(virtualECCs);
            }
        });

    }

    @Override
    public void selectAccount(AccountMembership accountMembership) {
        selectedAccount = accountMembership;
        view.setAccountMembership(accountMembership);
        listLocations(accountMembership);
    }
}
