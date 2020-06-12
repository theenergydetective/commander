/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.mtuList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.model.MTU;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.iron.element.IronIconElement;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class MTUTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(MTUTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    IronIconElement rightChevronButton;

    @UiField
    PaperLabel mtuTypeField;
    @UiField
    PaperLabel mtuDescriptionField;
    @UiField
    PaperMaterialElement mainPanel;
    private HandlerManager handlerManager = new HandlerManager(this);

    public MTUTile(final MTU mtu, boolean isEditable) {
        initWidget(defaultBinder.createAndBindUi(this));

        if (!mtu.isSpyder()) {
            mtuDescriptionField.setText(WebStringResource.INSTANCE.mtu());
        } else {
            mtuDescriptionField.setText(WebStringResource.INSTANCE.spyder());
        }

        if (mtu.getDescription() == null || mtu.getDescription().isEmpty()) {
            mtuDescriptionField.setValue(mtu.getHexId());
        } else {
            if (!mtu.getDescription().equals(mtu.getHexId())) {
                mtuDescriptionField.setValue(mtu.getDescription() + " (" + mtu.getHexId() + ")");
            } else {
                mtuDescriptionField.setValue(mtu.getDescription());
            }
        }

        switch (mtu.getMtuType()) {
            case NET:
                mtuTypeField.setValue(WebStringResource.INSTANCE.mtuTypeNet());
                break;
            case LOAD:
                mtuTypeField.setValue(WebStringResource.INSTANCE.mtuTypeLoad());
                break;
            case GENERATION:
                mtuTypeField.setValue(WebStringResource.INSTANCE.mtuTypeGen());
                break;
            case STAND_ALONE:
                mtuTypeField.setValue(WebStringResource.INSTANCE.mtuTypeSA());
                break;
        }

        if (isEditable) {
            rightChevronButton.getStyle().setProperty("display", "");
        } else {
            rightChevronButton.getStyle().setProperty("display", "none");
        }

        if (isEditable) {
            mainPanel.addEventListener("click", new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    LOGGER.fine(mtu + " selected");
                    handlerManager.fireEvent(new ItemSelectedEvent<MTU>(mtu));
                }
            });
        }
    }

    public void addItemSelectedHandler(ItemSelectedHandler<MTU> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


    interface DefaultBinder extends UiBinder<Widget, MTUTile> {
    }

}
