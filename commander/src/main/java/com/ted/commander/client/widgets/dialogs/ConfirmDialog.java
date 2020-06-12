/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.events.ConfirmEvent;
import com.ted.commander.client.events.ConfirmHandler;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;


public class ConfirmDialog extends PopupPanel {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    DivElement header;
    @UiField
    DivElement body;
    @UiField
    PaperButtonElement cancelButton;
    @UiField
    PaperButtonElement confirmButton;
    @UiField
    HTMLPanel htmlPanel;
    @UiField
    DivElement dialogDiv;

    public ConfirmDialog() {
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

        cancelButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
                handlerManager.fireEvent(new ConfirmEvent(false));
            }
        });

        confirmButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
                handlerManager.fireEvent(new ConfirmEvent(true));
            }
        });
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        GWT.log(">>>>>>>>>>>>> ON ATTACH");
    }

    public void open(String headerText, String bodyText) {
        GWT.log(">>>OPEN CONFIRM DIALOG: " + headerText);
        header.setInnerText(headerText);
        body.setInnerText(bodyText);
        this.center();
        this.show();
    }


    public HandlerRegistration addConfirmHandler(ConfirmHandler confirmHandler){
        return handlerManager.addHandler(ConfirmEvent.TYPE,confirmHandler);
    }
    interface MyUiBinder extends UiBinder<Widget, ConfirmDialog> {
    }

}
