/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.icons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class MenuIconHolderDefaultHighAppearance implements MenuIconHolder.IconHolderAppearance {
    @Override
    public Images get() {
        return Resources.INSTANCE;
    }

    interface Resources extends ClientBundle, Images {

        Resources INSTANCE = GWT.create(Resources.class);

        @Source("resources/menu-dashboard-6x.png")
        ImageResource dashboard();

        @Source("resources/m-user-6x.png")
        ImageResource userSettings();

        @Source("resources/m-advisor-6x.png")
        ImageResource advisor();


        @Source("resources/m-accountsettings-6x.png")
        ImageResource accountSettings();

        @Source("resources/m-locations-6x.png")
        ImageResource locationSettings();

        @Source("resources/m-energyplans-6x.png")
        ImageResource energyPlanSettings();

        @Source("resources/m-comparison-6x.png")
        ImageResource comparisons();

        @Source("resources/m-logout-6x.png")
        ImageResource logout();

        @Source("resources/m-activate-6x.png")
        ImageResource activate();

        @Source("resources/menu-export-6x.png")
        ImageResource export();

        @Source("resources/menu-graph-6x.png")
        ImageResource graph();

        @Source("resources/m-billing-6x.png")
        ImageResource billing();
    }
}
