/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.confirmEmail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.ted.commander.client.presenter.ConfirmEmailPresenter;
import com.ted.commander.client.resources.DefaultImageResource;
import com.ted.commander.client.resources.WebStringResource;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class ConfirmEmailViewImpl extends Composite implements ConfirmEmailView {
    static final Logger LOGGER = Logger.getLogger(ConfirmEmailViewImpl.class.getName());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    PaperButton forgotPasswordButton;
    @UiField
    PaperButton loginButton;
    @UiField
    PaperInputDecorator emailAddressField;
    @UiField
    PaperInputDecorator passwordField;
    @UiField
    WebStringResource stringRes;
    @UiField
    DefaultImageResource imageRes;
    @UiField
    ImageElement logo;
    @UiField
    ImageElement joinIconImg;

    ConfirmEmailPresenter presenter;

    public ConfirmEmailViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        joinIconImg.setSrc(DefaultImageResource.INSTANCE.smallDetective().getSafeUri().asString());

//        forgotPasswordButton.getElement().getStyle().setBackgroundImage("url(" + DefaultImageResource.INSTANCE.smallDetective().getSafeUri().asString() + ")");

        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                LOGGER.fine("sign up button clicked");
                presenter.authenticate();
            }
        });

        forgotPasswordButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.requestPassword();
            }
        });
    }


    @Override
    public HasValidation<String> getUsername() {
        return emailAddressField;
    }

    @Override
    public HasValidation<String> getPassword() {
        return passwordField;
    }

    @Override
    public void setLogo(ImageResource imageResource) {
        logo.setSrc(imageResource.getSafeUri().asString());
    }


    @Override
    public void setPresenter(ConfirmEmailPresenter presenter) {
        this.presenter = presenter;
    }

    interface MyUiBinder extends UiBinder<Widget, ConfirmEmailViewImpl> {
    }

}
