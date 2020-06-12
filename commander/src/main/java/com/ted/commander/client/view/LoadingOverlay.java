/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view;


import com.google.gwt.user.client.ui.PopupPanel;
import com.petecode.common.client.widget.paper.PaperStyleBundle;

/**
 * Created by pete on 10/22/2014.
 */
public class LoadingOverlay extends PopupPanel {

    static LoadingOverlay loadingOverlay = null;


    public LoadingOverlay() {
        super();

        setStylePrimaryName(PaperStyleBundle.INSTANCE.css().loadingLabel());
        setWidget(new LoadingText());

        center();
        setAnimationEnabled(true);
        setAnimationType(AnimationType.CENTER);
        setGlassEnabled(true);
        setModal(true);
        getGlassElement().setClassName(PaperStyleBundle.INSTANCE.css().glass());


    }

    public static LoadingOverlay get() {
        if (loadingOverlay == null) {
            loadingOverlay = new LoadingOverlay();
            loadingOverlay.hide();
        }
        return loadingOverlay;
    }
}
