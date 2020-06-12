/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */
package com.ted.commander.client.widgets.material.list;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;

public class MaterialListBoxDefaultAppearance implements MaterialListBoxAppearance {

    static {
        Resources.INSTANCE.css().ensureInjected();
    }

    @Override
    public MaterialListBoxCss css() {
        return Resources.INSTANCE.css();
    }

    interface Resources extends ClientBundle {

        Resources INSTANCE = GWT.create(Resources.class);

        @Source({"material-listbox.css"})
        MaterialListBoxCss css();
    }

}
