/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard.optionsSettings;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.googlecode.mgwt.ui.client.widget.dialog.overlay.SlideUpDialogOverlay;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.model.DashboardOptions;
import com.ted.commander.common.model.VirtualECC;


public class OptionSettingsOverlay extends SlideUpDialogOverlay {

    final HandlerManager handlerManager = new HandlerManager(this);
    final OptionSettingsViewImpl optionSettingsView;

    public OptionSettingsOverlay(VirtualECC virtualECC, DashboardOptions dashboardOptions) {
        super();
        optionSettingsView = new OptionSettingsViewImpl(virtualECC, dashboardOptions);

        optionSettingsView.addItemChangedHandler(new ItemChangedHandler<DashboardOptions>() {
            @Override
            public void onChanged(ItemChangedEvent<DashboardOptions> event) {
                hide();
                handlerManager.fireEvent(event);
            }
        });

        optionSettingsView.addCloseClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });


        add(optionSettingsView);


    }

    public void addItemChangedHandler(ItemChangedHandler<DashboardOptions> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }


}
