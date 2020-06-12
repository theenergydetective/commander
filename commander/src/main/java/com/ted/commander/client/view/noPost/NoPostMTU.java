/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.noPost;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;
import java.util.logging.Logger;


public class NoPostMTU extends Composite {

    static final Logger LOGGER = Logger.getLogger(NoPostMTU.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    @UiField
    DivElement mtuName;
    @UiField
    DivElement lastPostDate;
    @UiField
    DivElement mtuDescription;
    DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    public NoPostMTU(String mtuId, String mtuDescription, long lastPostDate) {
        initWidget(defaultBinder.createAndBindUi(this));
        this.mtuName.setInnerText(mtuId);
        this.mtuDescription.setInnerText(mtuDescription);
        if (lastPostDate == 0) {
            this.lastPostDate.setInnerText("Never");
        } else {
            this.lastPostDate.setInnerText(dateTimeFormat.format(new Date(lastPostDate * 1000)));
        }
    }

    interface DefaultBinder extends UiBinder<Widget, NoPostMTU> {
    }


}
