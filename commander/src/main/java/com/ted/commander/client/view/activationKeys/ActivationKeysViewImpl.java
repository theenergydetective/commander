/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.activationKeys;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.model.AccountMembership;

import java.util.List;
import java.util.logging.Logger;


public class ActivationKeysViewImpl extends Composite implements ActivationKeysView {

    static final Logger LOGGER = Logger.getLogger(ActivationKeysViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    PaperLabel activationURLField;

    @UiField
    VerticalPanel keyCardTiles;

    @UiField
    TitleBar titleBar;

    Presenter presenter;

    public ActivationKeysViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        //TODO: Make white label dynamic
        activationURLField.setValue("http://commander.theenergydetective.com/api/activate");

        if (GWT.getModuleBaseURL().contains("petecode")) {
            activationURLField.setValue("http://dev.petecode.com/api/activate");
        }

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new DashboardPlace(""));
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

    @Override
    public void setMemberships(List<AccountMembership> accountMembershipList) {
        keyCardTiles.clear();
        for (AccountMembership accountMembership : accountMembershipList) {
            keyCardTiles.add(new ActivationKeyTile(accountMembership));
        }

    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, ActivationKeysViewImpl> {
    }


}
