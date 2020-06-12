/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard.optionsSettings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;


public class OptionSettingsRowHeader extends Composite {

    static final Logger LOGGER = Logger.getLogger(OptionSettingsRowHeader.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    public OptionSettingsRowHeader() {
        initWidget(defaultBinder.createAndBindUi(this));


    }

    interface DefaultBinder extends UiBinder<Widget, OptionSettingsRowHeader> {

    }


}
