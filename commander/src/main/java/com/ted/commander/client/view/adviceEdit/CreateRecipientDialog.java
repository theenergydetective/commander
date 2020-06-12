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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemCreatedEvent;
import com.petecode.common.client.events.ItemCreatedHandler;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.AdviceRecipient;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;

import java.util.List;


public class CreateRecipientDialog extends PopupPanel {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    PaperButtonElement cancelButton;
    @UiField
    PaperButtonElement saveButton;
    @UiField
    CheckBox pushToggle;
    @UiField
    CheckBox emailToggle;
    @UiField
    ListBox memberListBox;
    @UiField
    DivElement dialogDiv;


    AdviceRecipient adviceRecipient = new AdviceRecipient();




    public CreateRecipientDialog(Advice advice, List<AccountMember> accountMembers) {
        setWidget(uiBinder.createAndBindUi(this));

        setAnimationEnabled(true);
        setAnimationType(AnimationType.CENTER);
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

        adviceRecipient.setAdviceId(advice.getId());


        for (AccountMember accountMember: accountMembers){
            memberListBox.addItem(accountMember.getUser().getFormattedName(), accountMember.getUser().getId().toString());
        }


        cancelButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
            }
        });

        saveButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                adviceRecipient.setUserId(Long.parseLong(memberListBox.getSelectedValue()));
                adviceRecipient.setSendEmail(emailToggle.getValue());
                adviceRecipient.setSendPush(pushToggle.getValue());
                hide();
                handlerManager.fireEvent(new ItemCreatedEvent<AdviceRecipient>(adviceRecipient));
            }
        });

        pushToggle.setValue(adviceRecipient.isSendPush());
        emailToggle.setValue(adviceRecipient.isSendEmail());
    }

    public void open() {
        center();
        show();
    }


    public HandlerRegistration addItemCreatedHandler(ItemCreatedHandler<AdviceRecipient> handler){
        return handlerManager.addHandler(ItemCreatedEvent.TYPE,handler);
    }

    interface MyUiBinder extends UiBinder<Widget, CreateRecipientDialog> {
    }

}
