/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.resources.WebStringResource;

public class DashboardMenuItem extends MenuItem {

    public DashboardMenuItem() {
        super(MenuIconHolder.get().dashboard(), WebStringResource.INSTANCE.dashboard(), new DashboardPlace(""));
    }
}
