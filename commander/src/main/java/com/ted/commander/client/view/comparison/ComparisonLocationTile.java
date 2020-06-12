/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.common.model.VirtualECC;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.logging.Logger;


public class ComparisonLocationTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(ComparisonLocationTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final VirtualECC virtualECC;

    @UiField
    PaperIconButton deleteButton;
    @UiField
    PaperLabel addressField;
    @UiField
    PaperLabel nameField;
    private HandlerManager handlerManager = new HandlerManager(this);

    public ComparisonLocationTile(final VirtualECC virtualECC) {
        this.virtualECC = virtualECC;
        initWidget(defaultBinder.createAndBindUi(this));

        nameField.setValue(virtualECC.getName());
        addressField.setValue(virtualECC.getFormattedAddress());

        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new ItemSelectedEvent<VirtualECC>(virtualECC));
            }
        });

    }

    public void addSelectedHandler(ItemSelectedHandler<VirtualECC> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    public VirtualECC getLocation() {
        return virtualECC;
    }

    interface DefaultBinder extends UiBinder<Widget, ComparisonLocationTile> {
    }

}
