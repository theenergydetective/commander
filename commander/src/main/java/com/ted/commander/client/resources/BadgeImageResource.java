/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import java.io.Serializable;


public interface BadgeImageResource extends ClientBundle, Serializable {

    public static final BadgeImageResource INSTANCE = GWT.create(BadgeImageResource.class);

    @Source("images/badges/google-play-badge.png")
    ImageResource google();

    @Source("images/badges/apple-badge.png")
    ImageResource apple();

    @Source("images/badges/amazon-badge.png")
    ImageResource fire();

}
