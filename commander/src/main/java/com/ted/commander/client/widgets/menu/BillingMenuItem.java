/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.BillingPlace;
import com.ted.commander.client.resources.WebStringResource;

public class BillingMenuItem extends MenuItem {

    public BillingMenuItem() {
        super(MenuIconHolder.get().billing(), WebStringResource.INSTANCE.billing(), new BillingPlace(""));
    }
}
