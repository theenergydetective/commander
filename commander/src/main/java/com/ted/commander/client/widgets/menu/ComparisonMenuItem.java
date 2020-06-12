/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.resources.WebStringResource;

public class ComparisonMenuItem extends MenuItem {

    public ComparisonMenuItem() {
        super(MenuIconHolder.get().comparisons(), WebStringResource.INSTANCE.comparison(), new ComparisonPlace(""));
    }
}
