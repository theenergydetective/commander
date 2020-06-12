/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.userSettings.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.ted.commander.client.places.UserSettingsPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.userSettings.ChangePasswordView;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class ChangePasswordViewImpl extends Composite implements ChangePasswordView {

    static final Logger LOGGER = Logger.getLogger(ChangePasswordViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    PaperButton saveButton;
    @UiField
    WebStringResource stringRes;

    @UiField
    PaperInputDecorator oldPasswordField;
    @UiField
    PaperInputDecorator newPasswordField;
    @UiField
    PaperInputDecorator confirmPasswordField;
    @UiField
    TitleBar titleBar;

    Presenter presenter;


    public ChangePasswordViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));


        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new UserSettingsPlace(""));
            }
        });

        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.changePassword(oldPasswordField.getValue(), newPasswordField.getValue(), confirmPasswordField.getValue());
            }
        });

    }


    @Override
    public void setExistingInValid(boolean isInvalid, String msg) {
        LOGGER.fine("Setting Invalid:" + msg);
        oldPasswordField.setInvalid(msg);
    }

    @Override
    public void setNewInValid(boolean isInvalid, String msg) {
        newPasswordField.setInvalid(msg);
    }

    @Override
    public void setConfirmationInValid(boolean isInvalid, String msg) {
        confirmPasswordField.setInvalid(msg);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void reset() {
        newPasswordField.setValue("");
        confirmPasswordField.setValue("");
        oldPasswordField.setValue("");
        setExistingInValid(false, "");
        setConfirmationInValid(false, "");
        setNewInValid(false, "");

    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, ChangePasswordViewImpl> {

    }

}
