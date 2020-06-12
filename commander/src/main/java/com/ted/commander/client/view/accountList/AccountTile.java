/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.model.AccountMembership;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class AccountTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(AccountTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    PaperLabel accountNameField;
    @UiField
    PaperLabel accountRoleField;


    @UiField
    PaperMaterialElement mainPanel;

    private HandlerManager handlerManager = new HandlerManager(this);

    public AccountTile(final AccountMembership accountMembership) {
        initWidget(defaultBinder.createAndBindUi(this));
        accountNameField.setValue(accountMembership.getAccount().getName());

        switch (accountMembership.getAccountRole()) {
            case OWNER:
                accountRoleField.setValue(WebStringResource.INSTANCE.accountRoleOwner());
                break;
            case ADMIN:
                accountRoleField.setValue(WebStringResource.INSTANCE.accountRoleAdmin());
                break;
            case EDIT_ECCS:
                accountRoleField.setValue(WebStringResource.INSTANCE.accountRoleECCEditor());
                break;
            case READ_ONLY:
                accountRoleField.setValue(WebStringResource.INSTANCE.accountRoleReadOnly());
                break;
        }

        mainPanel.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                LOGGER.fine(accountMembership + " selected");
                handlerManager.fireEvent(new ItemSelectedEvent<AccountMembership>(accountMembership));
            }
        });

    }

    public void addAccountMembershipSelectedHandler(ItemSelectedHandler<AccountMembership> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, AccountTile> {
    }

}
