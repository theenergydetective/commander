package com.ted.commander.client.widgets.graph;

import com.google.gwt.resources.client.CssResource;

interface GraphResource extends CssResource {
    String graphBackground();
    String svg();

    String x();

    String axis();

    String y();

    String netLine();
    String netFill();
    String weatherLine();
    String weatherFill();
    String loadLine();
    String loadFill();
    String genLine();
    String genFill();

    String overlay();

    String cursorText();
    String tooltip();
}
