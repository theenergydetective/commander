/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.billing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.common.model.AccountLocation;
import com.vaadin.polymer.paper.element.PaperCheckboxElement;

import java.util.logging.Logger;


public class BillingLocationRow extends Composite {
    static final Logger LOGGER = Logger.getLogger(BillingLocationRow.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final AccountLocation accountLocation;
    @UiField
    DivElement locationName;
    @UiField
    PaperCheckboxElement checkbox;
    @UiField
    FocusPanel focusPanel;

    public BillingLocationRow(final AccountLocation accountLocation) {
        initWidget(defaultBinder.createAndBindUi(this));
        this.accountLocation = accountLocation;
        LOGGER.severe("ADDING " + accountLocation.getVirtualECC().getName());
        locationName.setInnerText(accountLocation.getVirtualECC().getName());

        checkbox.setChecked(true);

        focusPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                checkbox.setChecked(!checkbox.getChecked());
            }
        });

    }

    public boolean getChecked() {
        return checkbox.getChecked();
    }

    public void setChecked(boolean checked) {
        checkbox.setChecked(checked);
    }

    public AccountLocation getAccountLocation() {
        return accountLocation;
    }

    interface DefaultBinder extends UiBinder<Widget, BillingLocationRow> {
    }


}

