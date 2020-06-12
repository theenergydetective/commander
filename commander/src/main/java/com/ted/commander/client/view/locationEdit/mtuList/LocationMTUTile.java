/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.mtuList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.model.VirtualECCMTU;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class LocationMTUTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(LocationMTUTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    Element rightChevronButton;

    @UiField
    PaperLabel mtuTypeField;
    @UiField
    PaperLabel mtuDescriptionField;

    @UiField
    PaperMaterialElement mainPanel;

    private HandlerManager handlerManager = new HandlerManager(this);

    public LocationMTUTile(final VirtualECCMTU mtu, boolean isEditable) {
        initWidget(defaultBinder.createAndBindUi(this));

        if (!mtu.isSpyder()) {
            mtuDescriptionField.setText(WebStringResource.INSTANCE.mtu());
        } else {
            mtuDescriptionField.setText(WebStringResource.INSTANCE.spyder());
        }

        if (mtu.getMtuDescription() == null || mtu.getMtuDescription().isEmpty()) {
            mtuDescriptionField.setValue(mtu.getHexId());
        } else {
            if (!mtu.getMtuDescription().equals(mtu.getHexId())) {
                mtuDescriptionField.setValue(mtu.getMtuDescription() + " (" + mtu.getHexId() + ")");
            } else {
                mtuDescriptionField.setValue(mtu.getMtuDescription());
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

        if (isEditable) rightChevronButton.getStyle().clearDisplay();
        else rightChevronButton.getStyle().setVisibility(Style.Visibility.HIDDEN);

        if (isEditable) {
            mainPanel.addEventListener("click", new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    LOGGER.fine(mtu + " selected");
                    handlerManager.fireEvent(new ItemSelectedEvent<VirtualECCMTU>(mtu));
                }
            });
        }
    }

    public void addItemSelectedHandler(ItemSelectedHandler<VirtualECCMTU> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


    interface DefaultBinder extends UiBinder<Widget, LocationMTUTile> {
    }

}
