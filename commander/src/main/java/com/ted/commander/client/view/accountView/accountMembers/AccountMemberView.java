/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.accountMembers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.view.ClosableOverlay;
import com.ted.commander.client.view.accountView.AccountView;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.AccountMembers;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.logging.Logger;


public class AccountMemberView extends Composite {

    static final Logger LOGGER = Logger.getLogger(AccountMembers.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final boolean editable;
    AccountView.Presenter presenter;


    @UiField
    VerticalPanel memberList;
    @UiField
    PaperIconButton addButton;


    ItemSelectedHandler<AccountMember> itemSelectedHandler = new ItemSelectedHandler<AccountMember>() {
        @Override
        public void onSelected(ItemSelectedEvent<AccountMember> event) {
            presenter.editMember(event.getItem());
        }
    };


    @UiConstructor
    public AccountMemberView(final boolean isEditable) {
        this.editable = isEditable;
        initWidget(defaultBinder.createAndBindUi(this));


        addButton.setVisible(isEditable);

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (editable) {
                    ClosableOverlay overlay = new ClosableOverlay(presenter.getAddMemberView());
                    overlay.addCloseHandler(new CloseHandler() {
                        @Override
                        public void onClose(CloseEvent event) {
                            presenter.getAccountMembers(0, 0, "", "");
                        }
                    });
                    overlay.show();
                }
            }
        });
    }

    public void setAccountMembers(AccountMembers accountMembers) {
        memberList.clear();
        for (AccountMember accountMember : accountMembers.getAccountMembers()) {
            AccountMemberTile tile = new AccountMemberTile(accountMember, editable);
            if (editable) tile.addItemSelectedHandler(itemSelectedHandler);
            memberList.add(tile);
        }
    }


    public void setPresenter(AccountView.Presenter presenter) {
        this.presenter = presenter;
    }


    @UiTemplate("AccountMemberView.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, AccountMemberView> {
    }


}
