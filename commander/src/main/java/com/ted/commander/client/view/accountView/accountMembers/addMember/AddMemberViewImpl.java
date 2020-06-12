/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.accountMembers.addMember;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.ted.commander.client.radioManager.AccountRoleRadioManager;
import com.ted.commander.common.enums.AccountRole;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;
import com.vaadin.polymer.paper.element.PaperInputElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;

import java.util.logging.Logger;


public class AddMemberViewImpl extends Composite implements AddMemberView {

    static final Logger LOGGER = Logger.getLogger(AddMemberViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final AccountRoleRadioManager accountRoleRadioManager;
    @UiField
    PaperButtonElement saveButton;


    @UiField
    PaperButtonElement cancelButton;

    @UiField
    PaperInputElement newEmailField;

    @UiField
    PaperInputElement confirmEmailField;
    @UiField

    PaperRadioGroupElement accountRoleGroup;
    Presenter presenter;


    public AddMemberViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        accountRoleRadioManager = new AccountRoleRadioManager(accountRoleGroup);

        cancelButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                close();
            }
        });

        saveButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                presenter.addMember();
            }
        });

    }


    @Override
    public PaperInputElement getEmail() {
        return newEmailField;
    }

    @Override
    public PaperInputElement getEmailConfirmation() {
        return confirmEmailField;
    }

    @Override
    public HasValue<AccountRole> getAccountRoleValue() {
        return accountRoleRadioManager;
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addCloseHandler(CloseHandler closeHandler) {
        handlerManager.addHandler(CloseEvent.TYPE, closeHandler);
    }


    @Override
    public void close() {
        handlerManager.fireEvent(new CloseEvent());
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, AddMemberViewImpl> {

    }

}
