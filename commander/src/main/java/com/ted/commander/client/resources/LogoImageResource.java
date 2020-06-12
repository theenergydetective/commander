/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import java.io.Serializable;


public interface LogoImageResource extends ClientBundle, Serializable {

    public static final LogoImageResource INSTANCE = GWT.create(LogoImageResource.class);

    @Source("images/logos/bigLogo.png")
    ImageResource bigLogo();


    @Source("images/logos/medLogo.png")
    ImageResource medLogo();

    @Source("images/logos/tedlogo.png")
    ImageResource headerLogo();

}
