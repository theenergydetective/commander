/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.widgets.menu.MenuOverlay;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.logging.Logger;

public class TitleBar extends Composite implements HasText {

    static final Logger LOGGER = Logger.getLogger(TitleBar.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    private final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    DivElement titleBarTitle;
    @UiField
    PaperIconButton menuButton;
    @UiField
    PaperIconButton backButton;

    Place selectedPlace = null;

    ItemSelectedHandler<Place> itemSelectedHandler = new ItemSelectedHandler<Place>() {
        @Override
        public void onSelected(ItemSelectedEvent<Place> event) {
            handlerManager.fireEvent(event);
        }
    };


    public TitleBar() {
        initWidget(defaultBinder.createAndBindUi(this));
        backButton.setVisible(false);
        menuButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                LOGGER.fine("MENU BUTTON TAPPED");
                MenuOverlay menuOverlay = new MenuOverlay(selectedPlace);
                menuOverlay.show();
                menuOverlay.addItemSelectedHandler(itemSelectedHandler);
            }
        });



        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new CloseEvent());
            }
        });


    }

    public void setSelectedPlace(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    @Override
    public String getText() {
        return titleBarTitle.getInnerText();
    }

    @Override
    public void setText(String s) {
        titleBarTitle.setInnerText(s);
    }

    public void setLogo(ImageResource imageResource) {
        //TODO: Pass this to the menu
    }

    public void setBackButton(boolean useBackButton) {
        menuButton.setVisible(!useBackButton);
        backButton.setVisible(useBackButton);
    }

    public void addItemSelectedHandler(ItemSelectedHandler<Place> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    public void addCloseHandler(CloseHandler handler) {
        handlerManager.addHandler(CloseEvent.TYPE, handler);
    }



    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, TitleBar> {
    }
}
