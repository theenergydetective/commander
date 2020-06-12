/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dailyDetail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;


public class DailyDetailViewNoPie extends Composite {

    static final Logger LOGGER = Logger.getLogger(DailyDetailViewNoPie.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    public DailyDetailViewNoPie() {
        initWidget(defaultBinder.createAndBindUi(this));

    }



    interface DefaultBinder extends UiBinder<Widget, DailyDetailViewNoPie> {
    }


}
