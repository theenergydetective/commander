/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.noPost;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;


public class NoPostLocation extends Composite {

    static final Logger LOGGER = Logger.getLogger(NoPostLocation.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    VerticalPanel mtuList;
    @UiField
    DivElement locationName;


    public NoPostLocation(String locationName) {
        initWidget(defaultBinder.createAndBindUi(this));
        this.locationName.setInnerText(locationName);
    }

    public void addMTU(String mtuId, String mtuDescription, long lastPostDate){
        mtuList.add(new NoPostMTU(mtuId, mtuDescription,lastPostDate));

    }
    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, NoPostLocation> {
    }


}
