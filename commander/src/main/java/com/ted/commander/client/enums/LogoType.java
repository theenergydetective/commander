/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.enums;


import com.google.gwt.resources.client.ImageResource;
import com.ted.commander.client.resources.LogoImageResource;
import com.ted.commander.client.resources.NBPowerLogoImageResource;

public enum LogoType {
    DEFAULT(LogoImageResource.INSTANCE),
    NBPOWER(NBPowerLogoImageResource.INSTANCE);

    final LogoImageResource logoImageResource;

    LogoType(LogoImageResource logoImageResource) {
        this.logoImageResource = logoImageResource;

    }

    public ImageResource getBigLogo() {
        return logoImageResource.bigLogo();
    }

    public ImageResource getHeaderLogo() {
        return logoImageResource.headerLogo();
    }

}
