/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.icons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class MenuIconHolderDefaultXHighAppearance implements MenuIconHolder.IconHolderAppearance {
    @Override
    public Images get() {
        return Resources.INSTANCE;
    }

    interface Resources extends ClientBundle, Images {

        Resources INSTANCE = GWT.create(Resources.class);

        @Source("resources/menu-dashboard-8x.png")
        ImageResource dashboard();

        @Source("resources/m-user-8x.png")
        ImageResource userSettings();

        @Source("resources/m-advisor-8x.png")
        ImageResource advisor();


        @Source("resources/m-accountsettings-8x.png")
        ImageResource accountSettings();

        @Source("resources/m-locations-8x.png")
        ImageResource locationSettings();

        @Source("resources/m-energyplans-8x.png")
        ImageResource energyPlanSettings();

        @Source("resources/m-comparison-8x.png")
        ImageResource comparisons();

        @Source("resources/m-logout-8x.png")
        ImageResource logout();

        @Source("resources/m-activate-8x.png")
        ImageResource activate();

        @Source("resources/menu-export-8x.png")
        ImageResource export();

        @Source("resources/menu-graph-8x.png")
        ImageResource graph();

        @Source("resources/m-billing-8x.png")
        ImageResource billing();
    }
}
