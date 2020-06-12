/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.userSettings.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.places.ChangeEmailPlace;
import com.ted.commander.client.places.ChangePasswordPlace;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.view.userSettings.UserSettingsView;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.model.User;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class UserSettingsViewImpl extends Composite implements UserSettingsView {

    static final Logger LOGGER = Logger.getLogger(UserSettingsViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    Presenter presenter;

    @UiField
    PaperLabel passwordField;
    @UiField
    PaperInputDecorator firstNameField;
    @UiField
    PaperInputDecorator middleNameField;
    @UiField
    PaperInputDecorator lastNameField;
    @UiField
    PaperLabel emailField;
    @UiField
    PaperButton passwordButton;
    @UiField
    PaperButton emailButton;
    @UiField
    TitleBar titleBar;

    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            LOGGER.fine("Updating user");
            presenter.updateUser();
        }
    };

    public UserSettingsViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        firstNameField.addValueChangeHandler(valueChangeHandler);
        middleNameField.addValueChangeHandler(valueChangeHandler);
        lastNameField.addValueChangeHandler(valueChangeHandler);


        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new DashboardPlace(""));
            }
        });

        emailButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new ChangeEmailPlace(""));
            }
        });

        passwordButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new ChangePasswordPlace(""));
            }
        });

    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public User getUser() {
        User user = new User();
        user.setFirstName(firstNameField.getValue());
        user.setMiddleName(middleNameField.getValue());
        user.setLastName(lastNameField.getValue());
        return user;
    }

    @Override
    public void setUser(User user) {
        lastNameField.setValue(user.getLastName());
        firstNameField.setValue(user.getFirstName());
        middleNameField.setValue(user.getMiddleName());
        emailField.setValue(user.getUsername());
    }

    @Override
    public void setFirstNameInValid(boolean isValid, String msg) {
        firstNameField.setInvalid(msg);
    }

    @Override
    public void setLastNameInValid(boolean isValid, String msg) {
        lastNameField.setInvalid(msg);
    }




    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, UserSettingsViewImpl> {
    }


}
