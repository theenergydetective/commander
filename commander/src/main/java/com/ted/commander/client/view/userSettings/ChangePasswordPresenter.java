/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.userSettings;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.ChangePasswordPlace;
import com.ted.commander.client.places.UserSettingsPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.util.Validator;
import com.ted.commander.common.model.UserPassword;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class ChangePasswordPresenter implements ChangePasswordView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ChangePasswordPresenter.class.getName());

    final ClientFactory clientFactory;
    final ChangePasswordView view;
    final ChangePasswordPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public ChangePasswordPresenter(ClientFactory clientFactory, ChangePasswordPlace place) {
        LOGGER.fine("CREATING NEW ChangePasswordPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getChangePasswordView();
        view.setPresenter(this);
        view.reset();

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

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {

        LOGGER.fine("Change Password");
        view.setExistingInValid(false, "");
        view.setConfirmationInValid(false, "");
        view.setNewInValid(false, "");


        boolean reqMissing = false;
        boolean noMatch = false;
        boolean invalid = false;

        if (oldPassword.trim().isEmpty()) {
            LOGGER.fine("OLD REQUIRED");
            reqMissing = true;
            view.setExistingInValid(true, WebStringResource.INSTANCE.requiredField());
        }
        if (newPassword.trim().isEmpty()) {
            reqMissing = true;
            view.setNewInValid(true, WebStringResource.INSTANCE.requiredField());
        }

        if (confirmPassword.trim().isEmpty()) {
            reqMissing = true;
            view.setConfirmationInValid(true, WebStringResource.INSTANCE.requiredField());
        }

        if (!newPassword.equals(confirmPassword)) {
            noMatch = true;
            view.setConfirmationInValid(true, WebStringResource.INSTANCE.passwordMatchError());
        }

        if (!Validator.isValidPassword(newPassword)) {
            invalid = true;
            view.setNewInValid(true, WebStringResource.INSTANCE.passwordCriteriaError());
        }

        if (!reqMissing && !noMatch && !invalid) {
            final UserPassword userPassword = new UserPassword();
            userPassword.setOldPassword(oldPassword);
            userPassword.setNewPassword(newPassword);

            RESTFactory.getUserService(clientFactory).changePassword(userPassword, clientFactory.getUser().getId(), new DefaultMethodCallback() {
                public void onSuccess(Method method, Object o) {
                    LOGGER.fine("User updated");
                    goTo(new UserSettingsPlace(""));
                }
            });
        }


    }
}
