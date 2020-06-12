/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;

import java.util.logging.Logger;

public class MenuItem extends Composite {

    static final Logger LOGGER = Logger.getLogger(MenuItem.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final Place place;
    private final HandlerManager handlerManager = new HandlerManager(this);
    @UiField
    Image logo;
    @UiField
    DivElement textLabel;


    @UiField
    DivElement menuDiv;
    @UiField
    HTMLPanel menuItemPanel;

    boolean selected = false;

    public MenuItem(final ImageResource icon, final String text, final Place place) {
        this.place = place;
        initWidget(defaultBinder.createAndBindUi(this));
        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new CloseEvent());
            }
        });
        logo.setResource(icon);
        textLabel.setInnerText(text);

        Event.sinkEvents(menuDiv, Event.ONCLICK | Event.ONMOUSEOUT | Event.ONMOUSEOVER);
        Event.setEventListener(menuDiv, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch(event.getTypeInt()){
                    case Event.ONCLICK:{
                        handlerManager.fireEvent(new ItemSelectedEvent<Place>(place));
                        break;
                    }
                    case Event.ONMOUSEOVER:{
                        menuItemPanel.getElement().getStyle().setBackgroundColor("#f7f7f7");
                        break;
                    }
                    case Event.ONMOUSEOUT: {
                        if (!selected) {
                            menuItemPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
                        } else {
                            menuItemPanel.getElement().getStyle().setBackgroundColor("#f2f2f2");
                        }
                        break;
                    }
                }

            }
        });

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(Place selectedPlace) {
        selected = (selectedPlace.getClass().getName().equals(place.getClass().getName()));
        if (selected) {
            menuItemPanel.getElement().getStyle().setBackgroundColor("#f2f2f2");
        } else {
            menuItemPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) menuDiv.getStyle().clearDisplay();
        else menuDiv.getStyle().setDisplay(Style.Display.NONE);
    }

    public void addItemSelectedHandler(ItemSelectedHandler<Place> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, MenuItem> {
    }


}
