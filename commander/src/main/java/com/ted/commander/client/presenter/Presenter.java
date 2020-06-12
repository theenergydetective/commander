/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.place.shared.Place;

public interface Presenter {

    public boolean isValid();

    void goTo(Place place);

    void onResize();


}
