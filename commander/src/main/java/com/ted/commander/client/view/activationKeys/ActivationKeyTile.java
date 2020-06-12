/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.activationKeys;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.common.model.AccountMembership;

import java.util.logging.Logger;


public class ActivationKeyTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(ActivationKeyTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    PaperLabel activationKeyField;

    public ActivationKeyTile(AccountMembership accountMembership) {
        initWidget(defaultBinder.createAndBindUi(this));
        activationKeyField.setText(accountMembership.getAccount().getName());
        activationKeyField.setValue(accountMembership.getAccount().getActivationKey());
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, ActivationKeyTile> {
    }


}
