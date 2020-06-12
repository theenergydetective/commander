/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.GraphingPlace;
import com.ted.commander.client.resources.WebStringResource;

public class GraphingMenuItem extends MenuItem {

    public GraphingMenuItem() {
        super(MenuIconHolder.get().graph(), WebStringResource.INSTANCE.graphing(), new GraphingPlace(""));
    }
}
