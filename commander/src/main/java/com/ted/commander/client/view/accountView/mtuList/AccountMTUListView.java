/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.mtuList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.view.accountView.AccountView;
import com.ted.commander.common.model.AccountMembers;
import com.ted.commander.common.model.MTU;

import java.util.List;
import java.util.logging.Logger;


public class AccountMTUListView extends Composite {

    static final Logger LOGGER = Logger.getLogger(AccountMembers.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final boolean editable;
    AccountView.Presenter presenter;

    @UiField
    VerticalPanel mtuListPanel;
    ItemSelectedHandler<MTU> itemSelectedHandler = new ItemSelectedHandler<MTU>() {
        @Override
        public void onSelected(ItemSelectedEvent<MTU> event) {
            LOGGER.fine("MTU SELECTED FOR EDITING: " + event.getItem());
            presenter.editMTU(event.getItem());
        }
    };

    @UiConstructor
    public AccountMTUListView(final boolean isEditable) {
        this.editable = isEditable;
        initWidget(defaultBinder.createAndBindUi(this));
    }

    public void setMTUList(List<MTU> mtuList) {
        mtuListPanel.clear();
        for (MTU mtu : mtuList) {
            MTUTile mtuTile = new MTUTile(mtu, editable);
            if (editable) mtuTile.addItemSelectedHandler(itemSelectedHandler);
            mtuListPanel.add(mtuTile);
        }
    }

    public void setPresenter(AccountView.Presenter presenter) {
        this.presenter = presenter;
    }


    @UiTemplate("AccountMTUList.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, AccountMTUListView> {
    }


}
