/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.energyPlanSelection;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.googlecode.mgwt.ui.client.widget.dialog.overlay.SlideUpDialogOverlay;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.EnergyPlanKey;

import java.util.List;


public class EnergyPlanSelectionOverlay extends SlideUpDialogOverlay {

    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlanSelectionViewImpl selectionView;

    public EnergyPlanSelectionOverlay(List<EnergyPlanKey> energyPlanList) {
        super();

        selectionView = new EnergyPlanSelectionViewImpl(energyPlanList);
        selectionView.addItemSelectedHandler(new ItemSelectedHandler<EnergyPlanKey>() {
            @Override
            public void onSelected(ItemSelectedEvent<EnergyPlanKey> event) {
                hide();
                handlerManager.fireEvent(event);
            }
        });

        selectionView.addCloseClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        add(selectionView);


    }

    public void addSelectedHandler(ItemSelectedHandler<EnergyPlanKey> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


}
