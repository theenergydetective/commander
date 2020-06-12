/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.AccountMembershipPlace;
import com.ted.commander.client.places.AccountPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.accountMembership.AccountMembershipView;
import com.ted.commander.common.model.AccountMember;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class AccountMembershipPresenter implements AccountMembershipView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AccountMembershipPresenter.class.getName());

    final ClientFactory clientFactory;
    final AccountMembershipView view;
    final AccountMembershipPlace place;
    WebStringResource stringRes = WebStringResource.INSTANCE;

    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            updateAccount();
        }
    };

    public AccountMembershipPresenter(final ClientFactory clientFactory, AccountMembershipPlace place) {
        LOGGER.fine("CREATING NEW AccountMembershipPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getAccountMembershipView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        view.getNameField().setValue(clientFactory.getInstance().getLastEditedAccountMember().getUser().getFormattedName());
        view.getEmailField().setValue(clientFactory.getInstance().getLastEditedAccountMember().getUser().getUsername());
        view.getAccountNameField().setValue(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getName());

        LOGGER.fine("SETTING ACCOUNT ROLE:" + clientFactory.getInstance().getLastEditedAccountMember().getAccountRole());

        view.getAccountRoleValue().setValue(clientFactory.getInstance().getLastEditedAccountMember().getAccountRole());
        view.getAccountRoleValue().addValueChangeHandler(valueChangeHandler);

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
    public void updateAccount() {
        AccountMember accountMember = clientFactory.getInstance().getLastEditedAccountMember();
        accountMember.setAccountRole(view.getAccountRoleValue().getValue());

        RESTFactory.getAccountService(clientFactory).updateAccountMember(accountMember, accountMember.getAccountId(), accountMember.getId(), new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                LOGGER.fine("Account updated");
            }
        });
    }

    @Override
    public void deleteAccount() {
        RESTFactory.getAccountService(clientFactory).deleteAccountMember(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId(), clientFactory.getInstance().getLastEditedAccountMember().getId(), new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                goTo(new AccountPlace(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId()));
            }
        });

    }

    @Override
    public void goToAccountPlace() {
        goTo(new AccountPlace(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId()));
    }
}
