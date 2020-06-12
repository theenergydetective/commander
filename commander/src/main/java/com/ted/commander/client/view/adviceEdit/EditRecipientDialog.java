/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.petecode.common.client.events.ItemDeletedEvent;
import com.petecode.common.client.events.ItemDeletedHandler;
import com.ted.commander.common.model.AdviceRecipient;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;


public class EditRecipientDialog extends PopupPanel {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    PaperButtonElement cancelButton;
    @UiField
    PaperButtonElement deleteButton;
    @UiField
    PaperButtonElement saveButton;
    @UiField
    CheckBox pushToggle;
    @UiField
    CheckBox emailToggle;
    @UiField
    DivElement displayNameField;
    @UiField
    DivElement dialogDiv;


    public EditRecipientDialog(final AdviceRecipient adviceRecipient, final boolean readOnly) {
        setWidget(uiBinder.createAndBindUi(this));

        setAnimationEnabled(true);
        setAnimationType(PopupPanel.AnimationType.CENTER);
        setModal(true);
        setGlassEnabled(true);
        Style glassStyle = getGlassElement().getStyle();
        glassStyle.setProperty("width", "100%");
        glassStyle.setProperty("height", "100%");
        glassStyle.setProperty("backgroundColor", "#000");
        glassStyle.setProperty("opacity", "0.45");
        glassStyle.setProperty("mozOpacity", "0.45");
        glassStyle.setProperty("filter", " alpha(opacity=45)");
        dialogDiv.getStyle().setZIndex(2147483647);


        cancelButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
            }
        });

        saveButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                adviceRecipient.setSendEmail(emailToggle.getValue());
                adviceRecipient.setSendPush(pushToggle.getValue());
                hide();
                handlerManager.fireEvent(new ItemChangedEvent<AdviceRecipient>(adviceRecipient));
            }
        });

        deleteButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
                handlerManager.fireEvent(new ItemDeletedEvent<AdviceRecipient>(adviceRecipient));
            }
        });

        displayNameField.setInnerText(adviceRecipient.getDisplayName());
        pushToggle.setValue(adviceRecipient.isSendPush());
        emailToggle.setValue(adviceRecipient.isSendEmail());
        pushToggle.setEnabled(!readOnly);
        emailToggle.setEnabled(!readOnly);
        deleteButton.setDisabled(readOnly);
        saveButton.setDisabled(readOnly);
        if (readOnly){
            deleteButton.getStyle().setProperty("display", "none");
            saveButton.getStyle().setProperty("display", "none");
        } else {
            deleteButton.getStyle().setProperty("display", "");
            saveButton.getStyle().setProperty("display", "");
        }



    }

    public void open() {
        center();
        show();
    }


    public HandlerRegistration addItemChangedHandler(ItemChangedHandler<AdviceRecipient> handler){
        return handlerManager.addHandler(ItemChangedEvent.TYPE,handler);
    }
    public HandlerRegistration addItemDeletedHandler(ItemDeletedHandler<AdviceRecipient> handler){
        return handlerManager.addHandler(ItemDeletedEvent.TYPE,handler);
    }


    interface MyUiBinder extends UiBinder<Widget, EditRecipientDialog> {
    }

}
