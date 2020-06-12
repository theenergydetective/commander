/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.mtuSelection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.model.MTU;

import java.util.logging.Logger;

public class MTUSelectionItem extends Composite {

    static final Logger LOGGER = Logger.getLogger(MTUSelectionItem.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final MTU mtu;
    private final HandlerManager handlerManager = new HandlerManager(this);
    @UiField
    FocusPanel focusPanel;

    @UiField
    PaperLabel mtuDescriptionField;
    @UiField
    PaperLabel mtuTypeField;
    boolean selected = false;

    public MTUSelectionItem(final MTU mtu) {
        this.mtu = mtu;
        initWidget(defaultBinder.createAndBindUi(this));


        if (!mtu.isSpyder()) {
            mtuDescriptionField.setText(WebStringResource.INSTANCE.mtu());
        } else {
            mtuDescriptionField.setText(WebStringResource.INSTANCE.spyder());
        }

        if (mtu.getDescription() == null || mtu.getDescription().isEmpty()) {
            mtuDescriptionField.setValue(mtu.getHexId());
        } else {
            mtuDescriptionField.setValue(mtu.getDescription());
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

        focusPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new ItemSelectedEvent<MTU>(mtu));
            }
        });

        focusPanel.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                focusPanel.getElement().getStyle().setBackgroundColor("#f7f7f7");
            }
        });

        focusPanel.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {
                if (!selected) {
                    focusPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
                } else {
                    focusPanel.getElement().getStyle().setBackgroundColor("#f2f2f2");
                }
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

    public void addItemSelectedHandler(ItemSelectedHandler<MTU> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    @Override
    public void setWidth(String w) {
        focusPanel.setWidth(w);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, MTUSelectionItem> {
    }


}
