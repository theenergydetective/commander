/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.icons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public class ApplicationIconHolderDefaultAppearance implements ApplicationIconHolder.IconHolderAppearance {
    @Override
    public Images get() {
        return Resources.INSTANCE;
    }

    interface Resources extends ClientBundle, Images {

        Resources INSTANCE = GWT.create(Resources.class);
        @Source("resources/white-caret-bottom-2x.png")
        ImageResource whiteCaretBottom();

    }
}
