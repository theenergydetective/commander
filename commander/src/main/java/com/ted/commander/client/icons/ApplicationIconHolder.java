/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.icons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;

public class ApplicationIconHolder {

    private static final IconHolderAppearance APPEARANCE = GWT.create(IconHolderAppearance.class);

    public static IconHolderAppearance.Images get() {
        return APPEARANCE.get();
    }

    public interface IconHolderAppearance {
        Images get();

        public interface Images {
            ImageResource whiteCaretBottom();
        }
    }
}
