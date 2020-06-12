/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.ted.commander.client.resources.DefaultImageResource;
import com.ted.commander.client.resources.WebStringResource;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class LoginViewImpl extends Composite implements LoginView {
    static final Logger LOGGER = Logger.getLogger(LoginViewImpl.class.getName());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    PaperButton signUpButton;
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
    @UiField
    ImageElement signUpButtonImg;
    @UiField
    Image appleBadge;
    @UiField
    Image googleBadge;
    @UiField
    Image fireBadge;

    LoginView.Presenter  loginPresenter;

    public LoginViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        googleBadge.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open("https://play.google.com/store/apps/details?id=com.ted.commander&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1", "_blank", "");
            }
        });

        appleBadge.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open("https://geo.itunes.apple.com/us/app/ted-commander/id1107055214?mt=8", "_blank", "");
            }
        });

        fireBadge.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open("https://www.amazon.com/gp/product/B01KBDL7I4/ref=mas_TED+Commander", "_blank", "");
            }
        });

        addDomHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent keyPressEvent) {
                if (keyPressEvent.getUnicodeCharCode() == 13) {
                    LOGGER.fine("ENTER FORM SUBMIT");
                    keyPressEvent.stopPropagation();
                    keyPressEvent.preventDefault();
                    loginPresenter.authenticate();
                }
            }
        }, KeyPressEvent.getType());

        joinIconImg.setSrc(DefaultImageResource.INSTANCE.smallDetective().getSafeUri().asString());
        signUpButtonImg.setSrc(DefaultImageResource.INSTANCE.smallDetective().getSafeUri().asString());

        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                loginPresenter.authenticate();
            }
        });

        signUpButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                loginPresenter.join();
            }
        });

        forgotPasswordButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                loginPresenter.forgotPassword();
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
    public void setPresenter(LoginView.Presenter presenter) {
        this.loginPresenter = presenter;
    }

    interface MyUiBinder extends UiBinder<Widget, LoginViewImpl> {
    }

}
