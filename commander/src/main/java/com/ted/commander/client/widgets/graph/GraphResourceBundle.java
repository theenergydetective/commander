package com.ted.commander.client.widgets.graph;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Created by pete on 3/18/2015.
 */
public interface GraphResourceBundle extends ClientBundle {
    public static final GraphResourceBundle INSTANCE = GWT.create(GraphResourceBundle.class);

    @Source("graph.gss")
    public GraphResource graphCss();

}
