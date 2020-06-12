/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.common.model.VirtualECC;

import java.util.logging.Logger;


public class LocationTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(LocationTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);



    @UiField
    PaperLabel nameField;
    @UiField
    PaperLabel addressField;
    @UiField
    HTMLPanel mainPanel;


    private HandlerManager handlerManager = new HandlerManager(this);

    public LocationTile(final VirtualECC virtualECC) {
        initWidget(defaultBinder.createAndBindUi(this));

        nameField.setValue(virtualECC.getName());
        addressField.setValue(virtualECC.getFormattedAddress());

        mainPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new ItemSelectedEvent<VirtualECC>(virtualECC));
            }
        }, ClickEvent.getType());

    }

    public void addSelectedHandler(ItemSelectedHandler<VirtualECC> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


    public void setWidth(String width) {
        nameField.setWidth(width);
        addressField.setWidth(width);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, LocationTile> {
    }

}
