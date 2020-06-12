/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.place.shared.Place;

import java.util.Arrays;


public class PlaceRequestEvent extends GwtEvent<PlaceRequestHandler> {

    public static Type<PlaceRequestHandler> TYPE = new Type<PlaceRequestHandler>();

    final Place destinationPage;
    final Place sourcePage;
    final Object[] params;

    public PlaceRequestEvent(Place destinationPage, Place sourcePage, Object[] params) {
        this.destinationPage = destinationPage;
        this.sourcePage = sourcePage;
        this.params = params;
    }

    public Place getDestinationPage() {
        return destinationPage;
    }

    public Place getSourcePage() {
        return sourcePage;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public Type<PlaceRequestHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PlaceRequestHandler handler) {
        handler.onRequest(this);
    }

    @Override
    public String toString() {
        return "PageRequestEvent{" +
                "destinationPage=" + destinationPage +
                ", sourcePage=" + sourcePage +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}

