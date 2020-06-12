/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.enums.AdviceState;
import com.ted.commander.common.model.Advice;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class AdviceTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(AdviceTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    PaperMaterialElement mainPanel;
    @UiField
    DivElement location;
    @UiField
    DivElement adviceName;
    @UiField
    Element alarmIcon;
    @UiField
    Element goodIcon;
    @UiField
    HTMLPanel container;

    public AdviceTile(final Advice advice, boolean showDiv) {
        initWidget(defaultBinder.createAndBindUi(this));

        adviceName.setInnerText(advice.getAdviceName());
        location.setInnerText(advice.getLocationName());

        if (advice.getState().equals(AdviceState.NORMAL))   {
            goodIcon.getStyle().clearDisplay();
            alarmIcon.getStyle().setDisplay(Style.Display.NONE);
        } else {
            alarmIcon.getStyle().clearDisplay();
            goodIcon.getStyle().setDisplay(Style.Display.NONE);
        }

        mainPanel.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                handlerManager.fireEvent(new ItemSelectedEvent<Advice>(advice));
            }
        });




    }


    public HandlerRegistration addItemSelectedHandler(ItemSelectedHandler<Advice> selectedHandler) {
        return handlerManager.addHandler(ItemSelectedEvent.TYPE, selectedHandler);
    }

    interface DefaultBinder extends UiBinder<Widget, AdviceTile> {
    }


}
