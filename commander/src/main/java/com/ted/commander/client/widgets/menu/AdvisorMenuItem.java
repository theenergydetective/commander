/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.AdviceListPlace;
import com.ted.commander.client.resources.WebStringResource;

public class AdvisorMenuItem extends MenuItem {

    public AdvisorMenuItem() {
        super(MenuIconHolder.get().advisor(), WebStringResource.INSTANCE.advisor(), new AdviceListPlace(""));
    }
}
