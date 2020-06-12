/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.AdviceRecipient;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class RecipientTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(RecipientTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final HandlerManager handlerManager = new HandlerManager(this);
    @UiField
    DivElement displayNameField;
    @UiField
    Element phoneIcon;
    @UiField
    Element emailIcon;
    @UiField
    PaperMaterialElement mainPanel;

    public RecipientTile(final AdviceRecipient adviceRecipient) {
        initWidget(defaultBinder.createAndBindUi(this));

        this.displayNameField.setInnerText(adviceRecipient.getDisplayName());
        if (adviceRecipient.isSendEmail()){
            emailIcon.getStyle().clearDisplay();
        } else {
            emailIcon.getStyle().setDisplay(Style.Display.NONE);
        }

        if (adviceRecipient.isSendPush()){
            phoneIcon.getStyle().clearDisplay();
        } else {
            phoneIcon.getStyle().setDisplay(Style.Display.NONE);
        }

        mainPanel.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                handlerManager.fireEvent(new ItemSelectedEvent<AdviceRecipient>(adviceRecipient));
            }
        });

    }


    public HandlerRegistration addItemSelectedHandler(ItemSelectedHandler<AdviceRecipient> selectedHandler) {
        return handlerManager.addHandler(ItemSelectedEvent.TYPE, selectedHandler);
    }

    interface DefaultBinder extends UiBinder<Widget, RecipientTile> {
    }


}
