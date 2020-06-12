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
import com.ted.commander.client.view.userSettings.ChangeEmailView;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class ChangeEmailViewImpl extends Composite implements ChangeEmailView {

    static final Logger LOGGER = Logger.getLogger(ChangeEmailViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    PaperButton saveButton;

    @UiField
    WebStringResource stringRes;



    @UiField
    PaperInputDecorator newEmailField;
    @UiField
    PaperInputDecorator confirmEmailField;
    @UiField
    TitleBar titleBar;

    Presenter presenter;


    public ChangeEmailViewImpl() {
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
                presenter.changeEmail(newEmailField.getValue(), confirmEmailField.getValue());
            }
        });

    }


    @Override
    public void setNewInValid(boolean isInvalid, String msg) {
        newEmailField.setInvalid(msg);
    }

    @Override
    public void setConfirmationInValid(boolean isInvalid, String msg) {
        confirmEmailField.setInvalid(msg);
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void reset() {
        newEmailField.setValue("");
        confirmEmailField.setValue("");
        setConfirmationInValid(false, "");
        setNewInValid(false, "");
    }


    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, ChangeEmailViewImpl> {

    }

}
