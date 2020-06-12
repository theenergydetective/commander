/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.ActivationKeysPlace;
import com.ted.commander.client.resources.WebStringResource;

public class ActivateMenuItem extends MenuItem {

    public ActivateMenuItem() {
        super(MenuIconHolder.get().activate(), WebStringResource.INSTANCE.activateEccButton(), new ActivationKeysPlace(""));
    }
}
