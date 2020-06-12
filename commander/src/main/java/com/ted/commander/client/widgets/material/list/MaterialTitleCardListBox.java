/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.material.list;

import com.google.gwt.user.client.ui.ListBox;
import com.ted.commander.client.icons.ApplicationIconHolder;

public class MaterialTitleCardListBox extends ListBox {
    public static final MaterialListBoxAppearance MATERIAL_TEXT_BOX_APPEARANCE = com.google.gwt.core.shared.GWT.create(MaterialListBoxDefaultAppearance.class);

    public MaterialTitleCardListBox() {
        this(MATERIAL_TEXT_BOX_APPEARANCE);
    }

    protected MaterialTitleCardListBox(MaterialListBoxAppearance appearance) {
        setStylePrimaryName(appearance.css().listBox());
        getElement().getStyle().setBackgroundImage("url(" + ApplicationIconHolder.get().whiteCaretBottom().getSafeUri().asString() + ")");
    }

    public void setValue(String s) {
        if (s != null) {
            for (int i = 0; i < getItemCount(); i++) {
                String value = getValue(i);
                if (s.equals(value)) {
                    setSelectedIndex(i);
                    return;
                }
            }
        }
        setSelectedIndex(-1);
    }


}
