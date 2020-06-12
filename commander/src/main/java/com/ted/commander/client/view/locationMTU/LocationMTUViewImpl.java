/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationMTU;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.dialog.ConfirmDialog;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.radioManager.MTUTypeRadioManager;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.enums.VirtualECCType;
import com.vaadin.polymer.paper.element.PaperRadioButtonElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class LocationMTUViewImpl extends Composite implements LocationMTUView {

    static final Logger LOGGER = Logger.getLogger(LocationMTUViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;
    @UiField
    TitleBar titleBar;
    @UiField
    ScrollPanel frameScrollPanel;
    @UiField
    PaperButton deleteButton;

    @UiField
    PaperLabel mtuSerialNumber;
    @UiField
    PaperLabel typeField;
    @UiField
    PaperInputDecorator descriptionField;

    @UiField
    PaperInputDecorator powerMultiplier;
    @UiField
    PaperInputDecorator voltageMultiplier;
    @UiField
    PaperRadioGroupElement mtuTypeField;
    @UiField
    PaperRadioButtonElement genButton;
    @UiField
    PaperRadioButtonElement loadButton;
    @UiField
    PaperRadioButtonElement netButton;

    final MTUTypeRadioManager mtuTypeRadioManager;


    public LocationMTUViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        frameScrollPanel.setWidth(Window.getClientWidth() + "px");
        frameScrollPanel.setHeight(Window.getClientHeight() + "px");
        frameScrollPanel.setAlwaysShowScrollBars(false);

        mtuTypeRadioManager = new MTUTypeRadioManager(mtuTypeField);

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.returnToLocation();
            }
        });

        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ConfirmDialog.ConfirmCallback callback = new ConfirmDialog.ConfirmCallback() {
                    @Override
                    public void onOk() {
                        presenter.deleteMTU();
                    }

                    @Override
                    public void onCancel() {
                        LOGGER.fine("delete cancelled");
                    }
                };

                WebStringResource stringRes = WebStringResource.INSTANCE;
                ConfirmDialog confirmDialog = new ConfirmDialog(stringRes.confirmDelete(), stringRes.confirmDeleteMTU(), callback, stringRes.yes(), stringRes.no());
                confirmDialog.show();
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
    public HasValue<String> getTypeField() {
        return typeField;
    }

    @Override
    public HasValue<String> getSerialNumberField() {
        return mtuSerialNumber;
    }

    @Override
    public HasValidation<String> getDescriptionField() {
        return descriptionField;
    }

    @Override
    public HasValidation<String> getPowerMultiplier() {
        return powerMultiplier;
    }

    @Override
    public HasValidation<String> getVoltageMultiplier() {
        return voltageMultiplier;
    }

    @Override
    public HasValue<MTUType> getMTUTypeValue() {
        return mtuTypeRadioManager;
    }

    @Override
    public void setVirtualECCType(VirtualECCType virtualECCType) {
        netButton.setDisabled(false);
        loadButton.setDisabled(false);
        genButton.setDisabled(false);

        switch (virtualECCType){
            case NET_ONLY:
                loadButton.setDisabled(true);
                genButton.setDisabled(true);
                break;
            case LOAD_GEN:
                netButton.setDisabled(true);
                break;
            case NET_GEN:
                loadButton.setDisabled(true);
                break;
        }




    }


    interface DefaultBinder extends UiBinder<Widget, LocationMTUViewImpl> {
    }


}
