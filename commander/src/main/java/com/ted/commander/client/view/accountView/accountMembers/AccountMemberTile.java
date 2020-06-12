/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.accountMembers;

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
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.AccountMember;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class AccountMemberTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(AccountMemberTile.class.getName());
    private static DefaultBinder readOnlyBinder = GWT.create(DefaultBinder.class);
    @UiField
    PaperLabel accountNameField;
    @UiField
    PaperLabel accountRoleField;
    @UiField
    IronIcon rightChevronButton;

    @UiField
    PaperMaterialElement mainPanel;
    private HandlerManager handlerManager = new HandlerManager(this);

    public AccountMemberTile(final AccountMember accountMember, boolean isEditable) {
        if (accountMember.getAccountRole().equals(AccountRole.OWNER)) isEditable = false;
        initWidget(readOnlyBinder.createAndBindUi(this));

        rightChevronButton.setVisible(isEditable);

//        if (isEditable) focusPanel.addStyleName(style.clickable());

        accountNameField.setValue(accountMember.getUser().getFormattedName());

        switch (accountMember.getAccountRole()) {
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


        if (isEditable) {
            mainPanel.addEventListener("click", new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    LOGGER.fine("Main Panel Clicked");
                    handlerManager.fireEvent(new ItemSelectedEvent<AccountMember>(accountMember));
                }
            });
        }
    }

    public void addItemSelectedHandler(ItemSelectedHandler<AccountMember> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


    interface DefaultBinder extends UiBinder<Widget, AccountMemberTile> {
    }


}
