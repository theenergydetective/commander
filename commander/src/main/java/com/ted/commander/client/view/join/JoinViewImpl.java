/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.join;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class JoinViewImpl extends Composite implements JoinView {

    static final Logger LOGGER = Logger.getLogger(JoinViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    @UiField
    WebStringResource stringRes;
    @UiField
    PaperInputDecorator emailField;
    @UiField
    PaperInputDecorator confirmEmailField;
    @UiField
    PaperInputDecorator confirmPasswordField;
    @UiField
    PaperInputDecorator passwordField;
    @UiField
    PaperInputDecorator middleNameField;
    @UiField
    PaperInputDecorator lastNameField;
    @UiField
    PaperInputDecorator firstNameField;
    @UiField
    PaperButton submitButton;
    @UiField
    TitleBar titleBar;

    JoinView.Presenter presenter;

    public JoinViewImpl() {

        initWidget(defaultBinder.createAndBindUi(this));

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new LoginPlace(""));
            }
        });

        submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.submit();
            }
        });
    }


    @Override
    public String getUsername() {
        return emailField.getValue().toLowerCase().trim();
    }

    @Override
    public String getConfirmUsername() {
        return confirmEmailField.getValue().toLowerCase().trim();
    }

    @Override
    public String getConfirmPassword() {
        return confirmPasswordField.getValue();
    }

    @Override
    public String getPassword() {
        return passwordField.getValue();
    }

    @Override
    public String getLastName() {
        return lastNameField.getValue();
    }

    @Override
    public String getFirstName() {
        return firstNameField.getValue();
    }

    @Override
    public String getMiddleName() {
        return middleNameField.getValue();
    }

    @Override
    public void setSubmitButtonEnabled(boolean enabled) {
        submitButton.setDisabled(!enabled);
    }


    @Override
    public void setLastNameInvalid(boolean invalid, String msg) {
        lastNameField.setInvalid( msg);
    }

    @Override
    public void setFirstNameInvalid(boolean invalid, String msg) {
        firstNameField.setInvalid( msg);
    }

    @Override
    public void setUsernameInvalid(boolean invalid, String msg) {
        emailField.setInvalid(msg);
    }

    @Override
    public void setPasswordInvalid(boolean invalid, String msg) {
        passwordField.setInvalid(msg);
    }

    @Override
    public void setConfirmPasswordInvalid(boolean invalid, String msg) {
        confirmPasswordField.setInvalid(msg);
    }

    @Override
    public void setConfirmUsernameInvalid(boolean invalid, String msg) {
        confirmEmailField.setInvalid(msg);
    }


    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }

    @Override
    public void setPresenter(JoinView.Presenter presenter) {
        this.presenter = presenter;
    }


    interface DefaultBinder extends UiBinder<Widget, JoinViewImpl> {
    }


}
