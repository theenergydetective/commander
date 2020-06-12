/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.icons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;

public class MenuIconHolder {

    private static final IconHolderAppearance APPEARANCE = GWT.create(IconHolderAppearance.class);

    public static IconHolderAppearance.Images get() {
        return APPEARANCE.get();
    }

    public interface IconHolderAppearance {
        Images get();

        public interface Images {
            ImageResource dashboard();

            ImageResource userSettings();

            ImageResource accountSettings();

            ImageResource locationSettings();

            ImageResource energyPlanSettings();

            ImageResource comparisons();

            ImageResource advisor();

            ImageResource logout();

            ImageResource activate();

            ImageResource graph();

            ImageResource export();

            ImageResource billing();


        }
    }
}
