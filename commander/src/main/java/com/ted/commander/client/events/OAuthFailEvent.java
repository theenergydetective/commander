/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class OAuthFailEvent extends GwtEvent<OAuthHandler> {

    public static Type<OAuthHandler> TYPE = new Type<OAuthHandler>();
    final String errorMessage;

    public OAuthFailEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public Type<OAuthHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(OAuthHandler handler) {
        handler.onFail(this);
    }


    @Override
    public String toString() {
        return "OAuthFailEvent{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}

