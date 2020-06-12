/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.ExportPlace;
import com.ted.commander.client.resources.WebStringResource;

public class DataExportMenuItem extends MenuItem {

    public DataExportMenuItem() {
        super(MenuIconHolder.get().export(), WebStringResource.INSTANCE.dataExport(), new ExportPlace(""));
    }
}
