/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.LogoutPlace;
import com.ted.commander.client.resources.WebStringResource;

public class LogoutMenuItem extends MenuItem {

    public LogoutMenuItem() {
        super(MenuIconHolder.get().logout(), WebStringResource.INSTANCE.logout(), new LogoutPlace(""));
    }
}
