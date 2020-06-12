/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.resetPassword;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.places.ResetPasswordPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.util.Validator;
import com.ted.commander.common.model.PasswordResetRequest;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class ResetPasswordPresenter implements ResetPasswordView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(ResetPasswordPresenter.class.getName());

    final ClientFactory clientFactory;
    final ResetPasswordView view;
    final ResetPasswordPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public ResetPasswordPresenter(ClientFactory clientFactory, ResetPasswordPlace place) {
        LOGGER.fine("CREATING ResetPasswordPresenter");
        this.clientFactory = clientFactory;
        this.place = place;

        LOGGER.fine("Reset Password code: " + place.getToken());

        view = clientFactory.getResetPasswordView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

    }

    @Override
    public boolean isValid() {
        boolean valid = true;

        view.passwordField().setInvalid("");
        view.cofirmPasswordField().setInvalid("");

        if (view.passwordField().getValue().trim().isEmpty()) {
            valid = false;
            view.passwordField().setInvalid(WebStringResource.INSTANCE.requiredField());
        }

        if (view.cofirmPasswordField().getValue().trim().isEmpty()) {
            valid = false;
            view.cofirmPasswordField().setInvalid(WebStringResource.INSTANCE.requiredField());
        }

        if (!view.passwordField().getValue().equals(view.cofirmPasswordField().getValue())) {
            valid = false;
            view.cofirmPasswordField().setInvalid(WebStringResource.INSTANCE.passwordMatchError());
        }

        if (!Validator.isValidPassword(view.passwordField().getValue())) {
            valid = false;
            view.passwordField().setInvalid(WebStringResource.INSTANCE.passwordCriteriaError());
        }
        return valid;

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
            PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
            passwordResetRequest.setActivationKey(place.getToken());
            passwordResetRequest.setPassword(view.passwordField().getValue());
            RESTFactory.getPasswordService(clientFactory).resetPassword(passwordResetRequest, new DefaultMethodCallback<RESTPostResponse>() {
                @Override
                public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                    if (restPostResponse.getMsg().equals("SUCCESS")){
                        view.showConfirmation();
                    } else {
                        view.showError();;
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
