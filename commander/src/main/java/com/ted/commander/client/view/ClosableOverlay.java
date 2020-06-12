/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view;


import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.dialog.overlay.SlideUpDialogOverlay;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;

/**
 * Created by pete on 10/22/2014.
 */
public class ClosableOverlay extends SlideUpDialogOverlay {

        final HandlerManager handlerManager = new HandlerManager(this);
    final ClosableView view;

    public ClosableOverlay(ClosableView view) {
        super();
        this.view = view;
        add((Widget) view);
        view.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                hide();
                handlerManager.fireEvent(event);
            }
        });
    }

    public void addCloseHandler(CloseHandler closeHandler) {
        handlerManager.addHandler(CloseEvent.TYPE, closeHandler);
    }
}
