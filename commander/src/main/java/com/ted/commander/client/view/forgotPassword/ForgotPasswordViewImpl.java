/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.forgotPassword;

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
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperDialogElement;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class ForgotPasswordViewImpl extends Composite implements ForgotPasswordView {

    static final Logger LOGGER = Logger.getLogger(ForgotPasswordViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    ForgotPasswordView.Presenter presenter;
    @UiField
    TitleBar titleBar;
    @UiField
    PaperButton requestButton;
    @UiField
    PaperDialogElement confirmationDialog;
    @UiField
    PaperDialogElement errorDialog;
    @UiField
    PaperInputDecorator emailAddressField;

    public ForgotPasswordViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.back();
            }
        });

        requestButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.submit();
            }
        });

        confirmationDialog.addEventListener("iron-overlay-closed", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                presenter.back();
            }
        });

    }


    @Override
    public HasValidation<String> getUsername() {
        return emailAddressField;
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
    public void showConfirm() {
        confirmationDialog.open();
    }

    @Override
    public void showError() {

    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, ForgotPasswordViewImpl> {
    }


}
