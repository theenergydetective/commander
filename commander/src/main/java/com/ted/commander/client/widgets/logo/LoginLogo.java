/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.logo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.model.Instance;
import com.ted.commander.client.resources.LogoImageResource;
import com.ted.commander.client.resources.NBPowerLogoImageResource;


public class LoginLogo extends Composite {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    Image logoField;

    public LoginLogo(Instance instance) {
        initWidget(uiBinder.createAndBindUi(this));
        switch (instance.getLogo()) {
            case NBPOWER:
                logoField.setResource(NBPowerLogoImageResource.INSTANCE.bigLogo());
                break;
            default:
                logoField.setResource(LogoImageResource.INSTANCE.bigLogo());
        }
    }

    interface MyUiBinder extends UiBinder<Widget, LoginLogo> {
    }


}
