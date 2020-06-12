/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface DefaultImageResource extends ClientBundle {

    public static final DefaultImageResource INSTANCE = GWT.create(DefaultImageResource.class);

    @Source("images/gatewayactsample.png")
    ImageResource gatewayActivationSample();

    @Source("images/smalldetective.png")
    ImageResource smallDetective();

    @Source("images/spinner.gif")
    ImageResource spinner();
}
