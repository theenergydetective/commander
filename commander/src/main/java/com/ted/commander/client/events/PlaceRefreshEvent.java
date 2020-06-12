/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.events;

import com.google.gwt.event.shared.GwtEvent;


public class PlaceRefreshEvent extends GwtEvent<PlaceRefreshHandler> {

    public static Type<PlaceRefreshHandler> TYPE = new Type<PlaceRefreshHandler>();


    public PlaceRefreshEvent() {

    }

    @Override
    public Type<PlaceRefreshHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PlaceRefreshHandler handler) {
        handler.onRefresh(this);
    }

    @Override
    public String toString() {
        return "PlaceRefreshEvent{" +
                '}';
    }
}

