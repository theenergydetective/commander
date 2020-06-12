/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.LocationListPlace;
import com.ted.commander.client.resources.WebStringResource;

public class LocationsMenuItem extends MenuItem {
    public LocationsMenuItem() {
        super(MenuIconHolder.get().locationSettings(), WebStringResource.INSTANCE.locations(), new LocationListPlace(""));
    }
}
