/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.AccountLocation;

import java.util.logging.Logger;


public class ClickableLocationRow extends Composite {
    static final Logger LOGGER = Logger.getLogger(ClickableLocationRow.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final AccountLocation accountLocation;
    @UiField
    DivElement locationName;
    @UiField
    FocusPanel focusPanel;

    private final HandlerManager handlerManager = new HandlerManager(this);

    public ClickableLocationRow(final AccountLocation accountLocation) {
        initWidget(defaultBinder.createAndBindUi(this));
        this.accountLocation = accountLocation;
        locationName.setInnerText(accountLocation.getVirtualECC().getName());

        focusPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new ItemSelectedEvent<AccountLocation>(accountLocation));
            }
        });

    }

    public void addItemSelectedHandler(ItemSelectedHandler<AccountLocation> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


    public AccountLocation getAccountLocation() {
        return accountLocation;
    }

    interface DefaultBinder extends UiBinder<Widget, ClickableLocationRow> {
    }


}

