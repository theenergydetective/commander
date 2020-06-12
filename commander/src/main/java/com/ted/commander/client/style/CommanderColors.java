/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.style;

import com.petecode.common.client.widget.paper.PaperColors;

public class CommanderColors implements PaperColors {

    public String getPrimary() {
        return "#009688";
    }

    @Override
    public String getPrimaryLight() {
        return "#B2DFDB";
    }

    @Override
    public String getPrimaryDark() {
        return "#00796B";
    }

    @Override
    public String getAccent() {
        return "#2962FF";
    }

    @Override
    public String getDefaultText() {
        return "#212121";
    }

    @Override
    public String getSecondaryText() {
        return "#727272";
    }

    @Override
    public String getDivider() {
        return "#B6B6B6";
    }

    @Override
    public String getError() {
        return "#FF0000";
    }

    @Override
    public String getBackground() {
        return "#B2DFDB";
    }
}
