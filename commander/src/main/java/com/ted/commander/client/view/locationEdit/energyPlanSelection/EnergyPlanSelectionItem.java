/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.energyPlanSelection;

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
import com.ted.commander.common.model.EnergyPlanKey;

import java.util.logging.Logger;

public class EnergyPlanSelectionItem extends Composite {

    static final Logger LOGGER = Logger.getLogger(EnergyPlanSelectionItem.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final EnergyPlanKey energyPlanKey;
    private final HandlerManager handlerManager = new HandlerManager(this);
    @UiField
    FocusPanel focusPanel;

    @UiField
    PaperLabel energyPlanNameLabel;
    @UiField
    PaperLabel energyPlanDescriptionLabel;

    boolean selected = false;

    public EnergyPlanSelectionItem(final EnergyPlanKey energyPlanKey) {
        this.energyPlanKey = energyPlanKey;
        initWidget(defaultBinder.createAndBindUi(this));

        energyPlanNameLabel.setValue(energyPlanKey.getDescription());
        energyPlanDescriptionLabel.setValue(energyPlanKey.getPlanType() + " " + energyPlanKey.getUtilityName());


        focusPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                handlerManager.fireEvent(new ItemSelectedEvent<EnergyPlanKey>(energyPlanKey));
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

    public void addItemSelectedHandler(ItemSelectedHandler<EnergyPlanKey> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    @Override
    public void setWidth(String w) {
        focusPanel.setWidth(w);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, EnergyPlanSelectionItem> {
    }


}
