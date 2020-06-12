/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.AccountListPlace;
import com.ted.commander.client.resources.WebStringResource;

public class AccountSettingsMenuItem extends MenuItem {

    public AccountSettingsMenuItem() {
        super(MenuIconHolder.get().accountSettings(), WebStringResource.INSTANCE.accountSettings(), new AccountListPlace(""));
    }
}
