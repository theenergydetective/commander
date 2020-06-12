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
import com.ted.commander.client.places.UserSettingsPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.model.User;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class UserSettingsPresenter implements UserSettingsView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(UserSettingsPresenter.class.getName());

    final ClientFactory clientFactory;
    final UserSettingsView view;
    final UserSettingsPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public UserSettingsPresenter(ClientFactory clientFactory, UserSettingsPlace place) {
        LOGGER.fine("CREATING NEW UserSettingsPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getUserSettingsView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        view.setUser(clientFactory.getUser());
    }

    @Override
    public boolean isValid() {
        User user = view.getUser();
        view.setFirstNameInValid(false, "");
        view.setLastNameInValid(false, "");

        boolean reqMissing = false;
        if (user.getFirstName().trim().isEmpty()) {
            view.setFirstNameInValid(true, WebStringResource.INSTANCE.requiredField());
            reqMissing = true;
        }

        if (user.getLastName().trim().isEmpty()) {
            view.setLastNameInValid(true, WebStringResource.INSTANCE.requiredField());
            reqMissing = true;
        }

        return !reqMissing;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void updateUser() {
        if (isValid()) {
            clientFactory.getUser().setFirstName(view.getUser().getFirstName());
            clientFactory.getUser().setLastName(view.getUser().getLastName());
            clientFactory.getUser().setMiddleName(view.getUser().getMiddleName());
            RESTFactory.getUserService(clientFactory).updateUser(clientFactory.getUser(), new DefaultMethodCallback() {
                @Override
                public void onSuccess(Method method, Object o) {
                    LOGGER.fine("User updated");
                }
            });
        }
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
}
