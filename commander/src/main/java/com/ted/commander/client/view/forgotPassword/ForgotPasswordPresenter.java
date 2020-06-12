/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.forgotPassword;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.ForgotPasswordPlace;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class ForgotPasswordPresenter implements ForgotPasswordView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ForgotPasswordPresenter.class.getName());

    final ClientFactory clientFactory;
    final ForgotPasswordView view;
    final ForgotPasswordPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public ForgotPasswordPresenter(ClientFactory clientFactory, ForgotPasswordPlace place) {
        LOGGER.fine("CREATING ForgotPasswordPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getForgotPasswordView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

    }

    @Override
    public boolean isValid() {
        view.getUsername().setInvalid("");
        if (view.getUsername().getValue() == null || view.getUsername().getValue().trim().length() == 0){
            view.getUsername().setInvalid(WebStringResource.INSTANCE.requiredField());
            return false;
        }
        return true;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, new DashboardPlace(""), null));
    }

    @Override
    public void onResize() {
    }

    @Override
    public void submit() {
        if (isValid()) {
            RESTFactory.getPasswordService(clientFactory).requestPasswordReset(view.getUsername().getValue(), new DefaultMethodCallback<RESTPostResponse>() {
                @Override
                public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                    if (restPostResponse.getMsg().equals("SUCCESS")) {
                        view.showConfirm();
                    } else {
                        view.showError();
                    }
                }
            });
        }
    }

    @Override
    public void back() {
        goTo(new LoginPlace(""));
    }
}
