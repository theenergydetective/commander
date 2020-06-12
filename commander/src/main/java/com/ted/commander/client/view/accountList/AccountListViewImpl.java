/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;

import java.util.logging.Logger;


public class AccountListViewImpl extends Composite implements AccountListView {

    static final Logger LOGGER = Logger.getLogger(AccountListViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;
    @UiField
    TitleBar titleBar;

    @UiField
    VerticalPanel accountTiles;

    ItemSelectedHandler<AccountMembership> accountMembershipItemSelectedHandler = new ItemSelectedHandler<AccountMembership>() {
        @Override
        public void onSelected(ItemSelectedEvent<AccountMembership> event) {
            presenter.edit(event.getItem());
        }
    };

    public AccountListViewImpl() {

        initWidget(defaultBinder.createAndBindUi(this));
        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new DashboardPlace(""));
            }
        });
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
    public void setAccountMemberships(AccountMemberships accountMemberships) {
        accountTiles.clear();
        for (AccountMembership accountMembership : accountMemberships.getAccountMemberships()) {
            AccountTile accountTile = new AccountTile(accountMembership);
            accountTile.addAccountMembershipSelectedHandler(accountMembershipItemSelectedHandler);
            accountTiles.add(accountTile);
        }

    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, AccountListViewImpl> {
    }


}
