/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.UserSettingsPlace;
import com.ted.commander.client.resources.WebStringResource;

public class UserSettingsMenuItem extends MenuItem {

    public UserSettingsMenuItem() {
        super(MenuIconHolder.get().userSettings(), WebStringResource.INSTANCE.userSettings(), new UserSettingsPlace(""));
    }
}
