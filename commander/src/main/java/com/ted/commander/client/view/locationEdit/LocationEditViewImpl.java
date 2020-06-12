/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.dialog.ConfirmDialog;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.petecode.common.client.widget.paper.PaperSimplePicker;
import com.ted.commander.client.places.LocationListPlace;
import com.ted.commander.client.radioManager.BooleanTypeRadioManager;
import com.ted.commander.client.radioManager.VirtualECCTypeRadioManager;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.locationEdit.mtuList.LocationMTUListView;
import com.ted.commander.client.view.locationEdit.mtuSelection.MTUSelectionOverlay;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.EnergyPlanKey;
import com.ted.commander.common.model.MTU;
import com.ted.commander.common.model.VirtualECCMTU;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.List;
import java.util.logging.Logger;


public class LocationEditViewImpl extends Composite implements LocationEditView {

    static final Logger LOGGER = Logger.getLogger(LocationEditViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final BooleanTypeRadioManager solarRadioManager;
    final VirtualECCTypeRadioManager virtualECCTypeRadioManager;
    Presenter presenter;
    @UiField
    HasValidation<String> locationNameField;
    @UiField
    HasValue<String> street1Field;
    @UiField
    HasValue<String> street2Field;
    @UiField
    HasValue<String> cityField;
    @UiField
    HasValue<String> postalField;
    @UiField
    DivElement advancedSelectionPanel;
    @UiField
    PaperIconButton addButton;
    @UiField
    PaperIconButton deleteButton;
    @UiField
    LocationMTUListView locationMTUListView;
    @UiField
    PaperRadioGroupElement solarGroup;
    @UiField
    PaperRadioGroupElement netGenGroup;
    @UiField
    HasValue<String> stateField;
    @UiField
    HasValue<String> countryField;
    @UiField
    HasValidation<String> timeZone;
    @UiField
    PaperIconButton backButton;
    @UiField
    DivElement netGenGroupPanel;
    @UiField
    Element netGenButton;
    @UiField
    Element loadGenButton;
    @UiField
    Element solarYesButton;
    @UiField
    Element solarNoButton;
    @UiField
    HasValidation<String>  energyPlanPicker;

    public LocationEditViewImpl(boolean readOnly) {
        initWidget(defaultBinder.createAndBindUi(this));

        solarRadioManager = new BooleanTypeRadioManager(solarGroup);
        virtualECCTypeRadioManager = new VirtualECCTypeRadioManager(netGenGroup);

        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new LocationListPlace(""));
            }
        });


        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.queryAccountMTUList();
            }
        });



        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ConfirmDialog.ConfirmCallback callback = new ConfirmDialog.ConfirmCallback() {
                    @Override
                    public void onOk() {
                        presenter.delete();
                    }

                    @Override
                    public void onCancel() {
                        LOGGER.fine("delete cancelled");
                    }
                };

                WebStringResource stringRes = WebStringResource.INSTANCE;
                ConfirmDialog confirmDialog = new ConfirmDialog(stringRes.confirmDelete(), stringRes.confirmDeleteVirtualECC(), callback, stringRes.yes(), stringRes.no());
                confirmDialog.show();
            }
        });

    }

    @Override
    public void setLogo(ImageResource imageResource) {

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        locationMTUListView.setPresenter(presenter);
    }

    @Override
    public HasValidation<String> getNameValue() {
        return locationNameField;
    }

    @Override
    public HasValue<String> getStreet1Value() {
        return street1Field;
    }

    @Override
    public HasValue<String> getStreet2Value() {
        return street2Field;
    }

    @Override
    public HasValue<String> getCityValue() {
        return cityField;
    }

    @Override
    public HasValue<String> getStateValue() {
        return stateField;
    }

    @Override
    public HasValue<String> getPostalValue() {
        return postalField;
    }

    @Override
    public HasValue<String> getCountryValue() {
        return countryField;
    }

    @Override
    public HasValidation<String> getTimeZoneValue() {
        return timeZone;
    }

    @Override
    public HasValidation<String> getEnergyPlanValue() {
        return energyPlanPicker;
    }

    @Override
    public HasValue<Boolean> getIsSolarValue() {
        return solarRadioManager;
    }

    @Override
    public HasValue<VirtualECCType> getVirtualECCValue() {
        return virtualECCTypeRadioManager;
    }


    @Override
    public void showMTUSelectionList(List<MTU> mtuList) {
        MTUSelectionOverlay mtuSelectionOverlay = new MTUSelectionOverlay(mtuList);
        mtuSelectionOverlay.show();
        mtuSelectionOverlay.addSelectedHandler(new ItemSelectedHandler<MTU>() {
            @Override
            public void onSelected(ItemSelectedEvent<MTU> event) {
                presenter.addVirtualMTU(event.getItem());
            }
        });
    }

    @Override
    public void showEnergyPlanList(List<EnergyPlanKey> energyPlanKeyList) {
        //TODO: Populate dropdown
//        EnergyPlanSelectionOverlay energyPlanSelectionOverlay = new EnergyPlanSelectionOverlay(energyPlanKeyList);
//        energyPlanSelectionOverlay.show();
//        energyPlanSelectionOverlay.addSelectedHandler(new ItemSelectedHandler<EnergyPlanKey>() {
//            @Override
//            public void onSelected(ItemSelectedEvent<EnergyPlanKey> event) {
//                presenter.addEnergyPlan(event.getItem());
//            }
//        });
    }


    @Override
    public void showAdvanced(boolean showAdvanced) {
        LOGGER.fine("SHOWADVANCED:" + showAdvanced);

        if (showAdvanced) {
            advancedSelectionPanel.getStyle().clearDisplay();
            netGenGroupPanel.scrollIntoView();
        } else {
            advancedSelectionPanel.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    @Override
    public void setMTUList(List<VirtualECCMTU> virtualECCMTUs) {
        locationMTUListView.setMTUList(virtualECCMTUs);
    }



    @Override
    public void setReadOnly(Boolean readOnly) {
        LOGGER.fine("Setting Read Only: " + readOnly);

        addButton.setVisible(!readOnly);
        deleteButton.setVisible(!readOnly);

        ((PaperInputDecorator) locationNameField).setReadonly(readOnly);
        ((PaperInputDecorator) street1Field).setReadonly(readOnly);
        ((PaperInputDecorator) street2Field).setReadonly(readOnly);
        ((PaperInputDecorator) cityField).setReadonly(readOnly);
        ((PaperInputDecorator) postalField).setReadonly(readOnly);

        locationMTUListView.setEditable(!readOnly);


        solarYesButton.setPropertyBoolean("disabled", readOnly);
        solarNoButton.setPropertyBoolean("disabled", readOnly);

        netGenButton.setPropertyBoolean("disabled", readOnly);
        loadGenButton.setPropertyBoolean("disabled", readOnly);

        ((PaperSimplePicker) timeZone).setEnabled(!readOnly);
        ((PaperSimplePicker) energyPlanPicker).setEnabled(!readOnly);
        ((PaperSimplePicker) stateField).setEnabled(!readOnly);
        ((PaperSimplePicker) countryField).setEnabled(!readOnly);

    }

    @UiTemplate("LocationEditViewImpl.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, LocationEditViewImpl> {
    }


}
