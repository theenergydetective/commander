/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.join;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.ted.commander.client.places.LoginPlace;
import com.ted.commander.client.widgets.toolbar.TitleBar;

import java.util.logging.Logger;


public class JoinSuccessViewImpl extends Composite implements JoinSuccessView {

    static final Logger LOGGER = Logger.getLogger(JoinSuccessViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    JoinSuccessView.Presenter presenter;
    @UiField
    TitleBar titleBar;

    public JoinSuccessViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new LoginPlace(""));
            }
        });
    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, JoinSuccessViewImpl> {
    }


}
