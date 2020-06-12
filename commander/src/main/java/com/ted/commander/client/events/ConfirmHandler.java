/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.events;

import com.google.gwt.event.shared.EventHandler;


public interface ConfirmHandler extends EventHandler {
    void onConfirm(ConfirmEvent event);
}

