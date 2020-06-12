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
import com.ted.commander.client.places.ChangeEmailPlace;
import com.ted.commander.client.places.UserSettingsPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.util.Validator;
import com.ted.commander.common.model.UserEmail;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class ChangeEmailPresenter implements ChangeEmailView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ChangeEmailPresenter.class.getName());

    final ClientFactory clientFactory;
    final ChangeEmailView view;
    final ChangeEmailPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public ChangeEmailPresenter(ClientFactory clientFactory, ChangeEmailPlace place) {
        LOGGER.fine("CREATING NEW UserSettingsPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getChangeEmailView();
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
    public void changeEmail(String newEmail, String confirmEmail) {


        view.setNewInValid(false, "");
        view.setConfirmationInValid(false, "");

        boolean reqMissing = false;
        boolean noMatch = false;
        boolean invalidEmail = false;

        if (newEmail.trim().isEmpty()) {
            reqMissing = false;
            view.setNewInValid(true, WebStringResource.INSTANCE.requiredField());
        }

        if (confirmEmail.trim().isEmpty()) {
            reqMissing = false;
            view.setConfirmationInValid(true, WebStringResource.INSTANCE.requiredField());
        }

        if (!reqMissing) {
            if (!Validator.isValidEmail(newEmail)) {
                invalidEmail = true;
                view.setNewInValid(true, WebStringResource.INSTANCE.invalidEmailError());
            }
        }

        if (!invalidEmail && !newEmail.trim().equals(confirmEmail.trim())) {
            noMatch = true;
            view.setConfirmationInValid(true, WebStringResource.INSTANCE.emailMatchError());
            //view.setNewInValid(true, "");
        }

        if (!reqMissing && !noMatch && !invalidEmail) {
            final UserEmail userEmail = new UserEmail();
            userEmail.setOldEmail(clientFactory.getUser().getUsername());
            userEmail.setNewEmail(newEmail.trim());

            RESTFactory.getUserService(clientFactory).changeEmail(userEmail, clientFactory.getUser().getId(), new DefaultMethodCallback() {
                public void onSuccess(Method method, Object o) {
                    clientFactory.getUser().setUsername(userEmail.getNewEmail());
                    LOGGER.fine("User updated");
                    goTo(new UserSettingsPlace(""));
                }
            });
        }

    }
}
