/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.callback;


import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.googlecode.mgwt.ui.client.widget.dialog.Dialogs;
import com.ted.commander.client.resources.WebStringResource;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class DefaultMethodCallback<T> implements MethodCallback<T> {

    static final Logger LOGGER = Logger.getLogger(DefaultMethodCallback.class.getName());

    public void onFailure(Method method, Throwable throwable) {
        LOGGER.severe("ERROR STATUS CODE: " + method.getResponse().getStatusCode());
        if (method.getResponse().getStatusCode() == Response.SC_UNAUTHORIZED) {
            //Try Logging In Again
//            Instance.clear(null);
//            LOGGER.log(Level.SEVERE, "Method callback failure", throwable);
            Window.Location.replace(Window.Location.getPath());
        } else {
            LOGGER.log(Level.SEVERE, "Method callback failure", throwable);
            Dialogs.alert(WebStringResource.INSTANCE.error(), WebStringResource.INSTANCE.systemError(), new Dialogs.AlertCallback() {
                @Override
                public void onButtonPressed() {
                    LOGGER.fine("Alert Ack'd");
                    Window.Location.replace(Window.Location.getPath());
                }
            });
        }
    }
}
