/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.*;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.widgets.dialogs.AlertDialog;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.logging.Logger;

public class AdviceListPresenter implements AdviceListView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AdviceListPresenter.class.getName());

    final ClientFactory clientFactory;
    final AdviceListView view;
    final Place place;
    private AccountMembership selectedAccount;


    public AdviceListPresenter(final ClientFactory clientFactory, final AdviceListPlace place) {
        LOGGER.fine("CREATING NEW AdviceEditPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getAdviceListView();
        view.setPresenter(this);


        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
            @Override
            public void onSuccess(Method method, AccountMemberships accountMemberships) {
                view.setAccountMemberships(accountMemberships);
                boolean found = false;
                if (place.getAccountId() != null) {
                    for (AccountMembership am : accountMemberships.getAccountMemberships()) {
                        if (am.getAccount().getId().equals(place.getAccountId())) {
                            selectAccount(am);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) selectAccount(accountMemberships.getAccountMemberships().get(0));
            }
        });
    }


    @Override
    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void goTo(Place destinationPage) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, new ComparisonPlace(""), null));
    }

    @Override
    public void onResize() {

    }

    @Override
    public void selectAdvice(Advice item) {
        this.goTo(new AdviceEditPlace(item.getId()));
    }

    @Override
    public void selectAccount(AccountMembership accountMembership) {

        selectedAccount = accountMembership;

        view.setAccountMembership(accountMembership);
        view.showCreateIcon(!accountMembership.getAccountRole().equals(AccountRole.READ_ONLY));

        RESTFactory.getAdviceService(clientFactory).getForAccount(accountMembership.getAccount().getId(), new DefaultMethodCallback<List<Advice>>() {
            @Override
            public void onSuccess(Method method, List<Advice> advices) {
                view.setAdvice(advices);
            }
        });
    }

    @Override
    public void createAdvice() {

        RESTFactory.getAdviceService(clientFactory).create(new Advice(selectedAccount.getAccount().getId()), new DefaultMethodCallback<RESTPostResponse>() {
            @Override
            public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                if (restPostResponse.getId().equals(null)){
                    AlertDialog alertDialog = new AlertDialog();
                    if (restPostResponse.getMsg().equals("No Locations")){
                        alertDialog.open("No Locations", "You do not have any locations specified for this account. Please add a location.");
                        alertDialog.addCloseHandler(new CloseHandler() {
                            @Override
                            public void onClose(CloseEvent event) {
                                goTo(new DashboardPlace());
                            }
                        });
                    } else {
                        goTo(new LocationListPlace(""));
                    }
                } else {
                    goTo(new AdviceEditPlace(restPostResponse.getId()));
                }
            }
        });
    }
}
