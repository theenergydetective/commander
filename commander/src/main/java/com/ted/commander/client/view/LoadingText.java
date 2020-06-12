package com.ted.commander.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

public class LoadingText extends Composite {

    static final Logger LOGGER = Logger.getLogger(LoadingText.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
//    @UiField
//    ImageElement spinner;

    public LoadingText() {
        initWidget(defaultBinder.createAndBindUi(this));
//        spinner.setActive(true);
        //spinner.setSrc(DefaultImageResource.INSTANCE.spinner().getSafeUri().asString());

    }

    interface DefaultBinder extends UiBinder<Widget, LoadingText> {
    }


}
