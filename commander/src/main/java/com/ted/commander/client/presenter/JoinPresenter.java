/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.dialog.Dialogs;
import com.ted.commander.client.callback.DefaultPostMethodCallback;
import com.ted.commander.client.callback.NoActionAlertCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.OAuthEvent;
import com.ted.commander.client.events.OAuthFailEvent;
import com.ted.commander.client.events.OAuthHandler;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.JoinPlace;
import com.ted.commander.client.places.JoinSuccessPlace;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.util.Validator;
import com.ted.commander.client.view.join.JoinView;
import com.ted.commander.common.enums.UserState;
import com.ted.commander.common.model.JoinRequest;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class JoinPresenter implements JoinView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(JoinPresenter.class.getName());

    final ClientFactory clientFactory;
    final JoinView view;
    final JoinPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    public JoinPresenter(ClientFactory clientFactory, JoinPlace place) {
        LOGGER.fine("CREATING NEW LOGIN PRESENTER");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getJoinView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());


    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        //Reset the fields
        view.setLastNameInvalid(false, "");
        view.setFirstNameInvalid(false, "");
        view.setUsernameInvalid(false, "");
        view.setPasswordInvalid(false, "");
        view.setConfirmPasswordInvalid(false, "");
        view.setConfirmUsernameInvalid(false, "");


        //Check required fields
        if (view.getFirstName().isEmpty()) {
            view.setFirstNameInvalid(true, WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        if (view.getLastName().isEmpty()) {
            view.setLastNameInvalid(true, WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        if (view.getConfirmPassword().isEmpty()) {
            view.setConfirmPasswordInvalid(true, WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        if (view.getPassword().isEmpty()) {
            view.setPasswordInvalid(true, WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        if (view.getConfirmUsername().isEmpty()) {
            view.setConfirmUsernameInvalid(true, WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        if (view.getUsername().isEmpty()) {
            view.setUsernameInvalid(true, WebStringResource.INSTANCE.requiredField());
            valid = false;
        }


        //Check that the fields match
        if (!view.getConfirmPassword().equals(view.getPassword())) {
            valid = false;
            view.setConfirmPasswordInvalid(true, WebStringResource.INSTANCE.passwordMatchError());
        }

        if (!view.getConfirmUsername().equals(view.getUsername())) {
            valid = false;
            view.setConfirmUsernameInvalid(true, WebStringResource.INSTANCE.emailMatchError());
        }

        if (valid) {
            if (!Validator.isValidEmail(view.getUsername())) {
                valid = false;
                view.setUsernameInvalid(true, WebStringResource.INSTANCE.invalidEmailError());
            }

            if (!Validator.isValidPassword(view.getPassword())) {
                valid = false;
                view.setPasswordInvalid(true, WebStringResource.INSTANCE.passwordCriteriaError());
            }
        }
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void submit() {
        LOGGER.fine("JoinPresenter:submit called");

        if (isValid()) {
            view.setSubmitButtonEnabled(false);
            final JoinRequest joinRequest = new JoinRequest();
            joinRequest.setUsername(view.getUsername());
            joinRequest.setPassword(view.getPassword());
            joinRequest.setFirstName(view.getFirstName());
            joinRequest.setMiddleName(view.getMiddleName());
            joinRequest.setLastName(view.getLastName());

            final String inviteKey = Window.Location.getParameter("inviteKey");
            if (inviteKey != null) {
                joinRequest.setInvitationKey(inviteKey);
            }
            LOGGER.fine("SUBMITTING NEW USER REQUEST:" + joinRequest);
            RESTFactory.getUserService(clientFactory).createUser(joinRequest, new DefaultPostMethodCallback() {
                @Override
                public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                    if (restPostResponse.getId() == null) {
                        view.setSubmitButtonEnabled(true);
                        Dialogs.alert(stringRes.error(), stringRes.duplicateUserError(), new NoActionAlertCallback());
                    } else {
                        if (restPostResponse.getMsg().equals(UserState.WAITING_ACTIVATION.name())) {
                            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new JoinSuccessPlace(""), place, null));
                        } else {
                            RESTFactory.authenticate(clientFactory, view.getUsername(), view.getPassword(), new OAuthHandler() {
                                @Override
                                public void onAuth(OAuthEvent event) {
                                    if (event.getAuthResponse().getError() == null) {
                                        LOGGER.fine("SUCCESSFUL AUTH:" + event.getAuthResponse());
                                        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new DashboardPlace(""), place, null));
                                    } else {
                                        LOGGER.fine("FAILED AUTH:" + event.getAuthResponse());
                                        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new LoginPlace(""), place, null));
                                    }
                                }

                                @Override
                                public void onFail(OAuthFailEvent event) {
                                    clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(new LoginPlace(""), place, null));
                                }
                            });

                        }

                    }


                }
            });

        } else {
            view.setSubmitButtonEnabled(true);
        }

    }

    @Override
    public void goTo(Place place) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, new DashboardPlace(""), null));
    }

    @Override
    public void onResize() {

    }

}
