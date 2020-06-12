/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */
package com.ted.commander.client.widgets.material.list;

import com.google.gwt.resources.client.CssResource;

public interface MaterialListBoxAppearance {
    MaterialListBoxCss css();

    public interface MaterialListBoxCss extends CssResource {
        @ClassName("material-ListBox")
        String listBox();


    }
}
