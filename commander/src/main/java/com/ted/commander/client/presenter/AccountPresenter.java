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
import com.ted.commander.client.places.AccountMTUPlace;
import com.ted.commander.client.places.AccountMembershipPlace;
import com.ted.commander.client.places.AccountPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.accountView.AccountView;
import com.ted.commander.client.view.accountView.accountMembers.addMember.AddMemberView;
import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.AccountMembers;
import com.ted.commander.common.model.MTU;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.logging.Logger;

public class AccountPresenter implements AccountView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AccountPresenter.class.getName());

    final ClientFactory clientFactory;
    final AccountView view;
    final AccountPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    ValueChangeHandler accountInformationChangedHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            if (clientFactory.getInstance().getLastEditedAccountMembership().isAdmin() && isValid()) {
                LOGGER.fine("Updating account");
                Account account = clientFactory.getInstance().getLastEditedAccountMembership().getAccount();
                account.setName(view.getNameField().getValue());
                account.setPhoneNumber(view.getPhoneField().getValue());

                RESTFactory.getAccountService(clientFactory).updateAccount(account, account.getId(), new DefaultMethodCallback<Void>() {
                    @Override
                    public void onSuccess(Method method, Void aVoid) {
                        LOGGER.fine("Record saved");
                    }
                });
            }
        }
    };

    public AccountPresenter(final ClientFactory clientFactory, AccountPlace place) {
        LOGGER.fine("CREATING NEW AccountSettingsPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getAccountView(clientFactory.getInstance().getLastEditedAccountMembership().getAccountRole());
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        view.getNameField().setValue(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getName());
        view.getPhoneField().setValue(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getPhoneNumber());
        //TODO: Add pull/pagination

        getAccountMembers(0, 0, "", "");
        getMTUs(0, 0, "", "");

        view.getNameField().addValueChangeHandler(accountInformationChangedHandler);
        view.getPhoneField().addValueChangeHandler(accountInformationChangedHandler);
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

        view.getNameField().setInvalid("");
        view.getPhoneField().setInvalid("");

        boolean valid = true;
        if (view.getNameField().getValue().trim().isEmpty()) {
            valid = false;
            view.getNameField().setInvalid(WebStringResource.INSTANCE.requiredField());
        }

        if (view.getPhoneField().getValue().trim().isEmpty()) {
            valid = false;
            view.getPhoneField().setInvalid(WebStringResource.INSTANCE.requiredField());
        }


        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void getAccountMembers(int start, int limit, String sort, String sortOrder) {
        RESTFactory.getAccountService(clientFactory).getAccountMembers(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId(), new DefaultMethodCallback<AccountMembers>() {
            @Override
            public void onSuccess(Method method, AccountMembers accountMembers) {
                view.setAccountMembers(accountMembers);
            }
        });
    }

    @Override
    public void getMTUs(int start, int limit, String sort, String sortOrder) {
        RESTFactory.getMTUService(clientFactory).getMTUs(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId(), start, limit, sort, sortOrder, new DefaultMethodCallback<List<MTU>>() {
            @Override
            public void onSuccess(Method method, List<MTU> mtus) {
                view.setMTUs(mtus);
            }
        });
    }

    @Override
    public AddMemberView getAddMemberView() {
        AddMemberPresenter presenter = new AddMemberPresenter(clientFactory, clientFactory.getInstance().getLastEditedAccountMembership().getAccount());
        return presenter.view;
    }

    @Override
    public void editMember(AccountMember accountMember) {
        clientFactory.getInstance().setLastEditedAccountMember(accountMember);
        goTo(new AccountMembershipPlace(""));
    }

    @Override
    public void editMTU(MTU mtu) {
        LOGGER.fine("EDITING MTU: " + mtu);
        clientFactory.getInstance().setLastEditedMTU(mtu);
        goTo(new AccountMTUPlace(""));
    }



}
