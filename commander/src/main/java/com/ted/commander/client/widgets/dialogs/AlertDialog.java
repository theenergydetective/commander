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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;


public class AlertDialog extends PopupPanel {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    DivElement header;
    @UiField
    DivElement body;
    @UiField
    DivElement dialogDiv;
    @UiField
    PaperButtonElement closeButton;

    final HandlerManager handlerManager = new HandlerManager(this);

    public AlertDialog() {
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
        dialogDiv.getStyle().setZIndex(2147483647 - 100);

        closeButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
            }
        });

    }

    public HandlerRegistration addCloseHandler(CloseHandler handler){
        return handlerManager.addHandler(CloseEvent.TYPE, handler);
    }

    public void open(String headerText, String bodyText) {
        header.setInnerText(headerText);
        body.setInnerText(bodyText);
        center();
        show();
    }

    public void close() {
        hide();
        handlerManager.fireEvent(new CloseEvent());
    }

    interface MyUiBinder extends UiBinder<Widget, AlertDialog> {
    }

}
