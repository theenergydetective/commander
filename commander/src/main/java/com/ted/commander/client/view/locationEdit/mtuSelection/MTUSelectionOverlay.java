/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.mtuSelection;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.googlecode.mgwt.ui.client.widget.dialog.overlay.SlideUpDialogOverlay;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.MTU;

import java.util.List;


public class MTUSelectionOverlay extends SlideUpDialogOverlay {

    final HandlerManager handlerManager = new HandlerManager(this);
    final MTUSelectionViewImpl selectionView;

    public MTUSelectionOverlay(List<MTU> mtuList) {
        super();

        selectionView = new MTUSelectionViewImpl(mtuList);
        selectionView.addItemSelectedHandler(new ItemSelectedHandler<MTU>() {
            @Override
            public void onSelected(ItemSelectedEvent<MTU> event) {
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

    public void addSelectedHandler(ItemSelectedHandler<MTU> handler) {
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


}
