/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.ted.commander.client.model.OAuthResponse;


public class OAuthEvent extends GwtEvent<OAuthHandler> {

    public static Type<OAuthHandler> TYPE = new Type<OAuthHandler>();

    final OAuthResponse authResponse;


    public OAuthEvent(OAuthResponse authResponse) {
        this.authResponse = authResponse;

    }

    @Override
    public Type<OAuthHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(OAuthHandler handler) {
        handler.onAuth(this);
    }


    public OAuthResponse getAuthResponse() {
        return authResponse;
    }
}

