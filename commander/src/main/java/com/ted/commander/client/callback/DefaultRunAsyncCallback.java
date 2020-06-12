/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.callback;

import com.google.gwt.core.client.RunAsyncCallback;
import com.googlecode.mgwt.ui.client.widget.dialog.Dialogs;
import com.ted.commander.client.resources.WebStringResource;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class DefaultRunAsyncCallback implements RunAsyncCallback {
    private static Logger LOGGER = Logger.getLogger(DefaultRunAsyncCallback.class.toString());

    @Override
    public void onFailure(Throwable throwable) {
        LOGGER.log(Level.SEVERE, "GWT Async call failure " + throwable.getMessage(), throwable);
        Dialogs.alert(WebStringResource.INSTANCE.error(), WebStringResource.INSTANCE.systemError(), new NoActionAlertCallback());
    }

}
