/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.resetPassword;

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
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.ted.commander.client.places.ForgotPasswordPlace;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperDialogElement;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class ResetPasswordViewImpl extends Composite implements ResetPasswordView {

    static final Logger LOGGER = Logger.getLogger(ResetPasswordViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    ResetPasswordView.Presenter presenter;

    @UiField
    TitleBar titleBar;
    @UiField
    PaperInputDecorator passwordField;
    @UiField
    PaperInputDecorator confirmPasswordField;
    @UiField
    PaperButton submitButton;
    @UiField
    PaperDialogElement confirmationDialog;
    @UiField
    PaperDialogElement errorDialog;

    public ResetPasswordViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.back();
            }
        });

        confirmationDialog.addEventListener("iron-overlay-closed", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                presenter.back();
            }
        });

        errorDialog.addEventListener("iron-overlay-closed", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                presenter.goTo(new ForgotPasswordPlace(""));
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
    public HasValidation<String> passwordField() {
        return passwordField;
    }

    @Override
    public HasValidation<String> cofirmPasswordField() {
        return confirmPasswordField;
    }

    @Override
    public void showError() {
        errorDialog.open();
    }

    @Override
    public void showConfirmation() {
        confirmationDialog.open();
    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, ResetPasswordViewImpl> {
    }


}
