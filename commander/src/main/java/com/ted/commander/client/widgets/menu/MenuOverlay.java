/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.ui.client.widget.dialog.overlay.SlideFromLeftOverlay;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPropertyHelper;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexSpacer;
import com.googlecode.mgwt.ui.client.widget.panel.flex.RootFlexPanel;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;

/**
 * Created by pete on 10/22/2014.
 */
public class MenuOverlay extends SlideFromLeftOverlay {
    private final HandlerManager handlerManager = new HandlerManager(this);


    public MenuOverlay(final Place selectedPlace) {
        super();
        RootFlexPanel flexPanel = new RootFlexPanel();
        flexPanel.setOrientation(FlexPropertyHelper.Orientation.VERTICAL);
        final MainMenu mainMenu = new MainMenu(selectedPlace);
        flexPanel.add(mainMenu);
        flexPanel.add(new FlexSpacer());
        add(flexPanel);

        mainMenu.addItemSelectedHandler(new ItemSelectedHandler<Place>() {
            @Override
            public void onSelected(ItemSelectedEvent<Place> event) {
                GWT.log("Firing Place: " + event.getItem().getClass());
                handlerManager.fireEvent(event);
                hide();
            }
        });


        mainMenu.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                hide();
            }
        });
    }

    public void addItemSelectedHandler(ItemSelectedHandler<Place> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }
}
