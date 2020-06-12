/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.events.CloseHandler;


public interface ClosableView extends IsWidget {
    void close();

    void addCloseHandler(CloseHandler closeHandler);

}
