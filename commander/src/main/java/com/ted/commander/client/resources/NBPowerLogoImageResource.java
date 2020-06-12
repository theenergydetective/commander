/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;


public interface NBPowerLogoImageResource extends LogoImageResource {

    public static final NBPowerLogoImageResource INSTANCE = GWT.create(NBPowerLogoImageResource.class);

    @Source("images/logos/nblogoBig.png")
    ImageResource bigLogo();

    @Source("images/logos/nbpowerlogo.png")
    ImageResource medLogo();

    @Source("images/logos/nbpowerlogo.png")
    ImageResource headerLogo();

}
