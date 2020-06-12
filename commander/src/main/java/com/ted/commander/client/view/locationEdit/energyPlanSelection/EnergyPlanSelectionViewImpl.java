/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.energyPlanSelection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.EnergyPlanKey;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.List;
import java.util.logging.Logger;


public class EnergyPlanSelectionViewImpl extends Composite {

    static final Logger LOGGER = Logger.getLogger(EnergyPlanSelectionViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    FlexPanel mainPanel;
    @UiField
    PaperButton cancelButton;
    @UiField
    Label titleLabel;
    @UiField
    VerticalPanel contentPanel;
    @UiField
    ScrollPanel frameScrollPanel;

    ItemSelectedHandler<EnergyPlanKey> itemSelectedHandler = new ItemSelectedHandler<EnergyPlanKey>() {
        @Override
        public void onSelected(ItemSelectedEvent<EnergyPlanKey> event) {
            handlerManager.fireEvent(event);
        }
    };


    public EnergyPlanSelectionViewImpl(List<EnergyPlanKey> energyPlanKeys) {
        initWidget(defaultBinder.createAndBindUi(this));

        int w = Window.getClientWidth();
        if (w > 500) w = 500;

        int h = Window.getClientHeight();
        if (h > 600) h = 600;

        frameScrollPanel.setWidth(w + "px");
        frameScrollPanel.setHeight(h + "px");


        for (EnergyPlanKey energyPlanKey : energyPlanKeys) {
            EnergyPlanSelectionItem selectionItem = new EnergyPlanSelectionItem(energyPlanKey);
            selectionItem.addItemSelectedHandler(itemSelectedHandler);
            contentPanel.add(selectionItem);
        }
    }


    public void addCloseClickHandler(ClickHandler clickHandler) {
        cancelButton.addClickHandler(clickHandler);
    }

    public void addItemSelectedHandler(ItemSelectedHandler<EnergyPlanKey> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }
    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, EnergyPlanSelectionViewImpl> {
    }


}
