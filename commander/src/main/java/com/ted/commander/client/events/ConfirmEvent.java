/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.events;

import com.google.gwt.event.shared.GwtEvent;


public class ConfirmEvent extends GwtEvent<ConfirmHandler> {

    public static Type<ConfirmHandler> TYPE = new Type<ConfirmHandler>();


    final Boolean confirmed;

    public ConfirmEvent(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    @Override
    public Type<ConfirmHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ConfirmHandler handler) {
        handler.onConfirm(this);
    }

    @Override
    public String toString() {
        return "ConfirmEvent{" +
                "confirmed=" + confirmed +
                '}';
    }
}

