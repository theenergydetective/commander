/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.LocationEditPlace;
import com.ted.commander.client.places.LocationMTUPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.locationMTU.LocationMTUView;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.VirtualECCMTU;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class LocationMTUPresenter implements LocationMTUView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(LocationMTUPresenter.class.getName());

    final ClientFactory clientFactory;
    final LocationMTUView view;
    final LocationMTUPlace place;
    WebStringResource stringRes = WebStringResource.INSTANCE;



    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            updateMTU();
        }
    };


    public LocationMTUPresenter(final ClientFactory clientFactory, LocationMTUPlace place) {
        LOGGER.fine("CREATING NEW LocationMTUPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getLocationMTUView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        VirtualECCMTU mtu = clientFactory.getInstance().getLastEditedVirtualMTU();

        if (mtu.getMtuDescription() == null) mtu.setMtuDescription(mtu.getHexId());
        view.getPowerMultiplier().setValue(NumberFormat.getDecimalFormat().format(mtu.getPowerMultiplier()));
        view.getVoltageMultiplier().setValue(NumberFormat.getDecimalFormat().format(mtu.getVoltageMultiplier()));

        view.getDescriptionField().setValue(mtu.getMtuDescription());
        view.getSerialNumberField().setValue(mtu.getHexId());
        view.getTypeField().setValue(mtu.isSpyder() ? WebStringResource.INSTANCE.spyder() : WebStringResource.INSTANCE.mtu());

        view.getMTUTypeValue().setValue(mtu.getMtuType());
        view.getMTUTypeValue().addValueChangeHandler(valueChangeHandler);

        view.getDescriptionField().addValueChangeHandler(valueChangeHandler);
        view.getPowerMultiplier().addValueChangeHandler(valueChangeHandler);
        view.getVoltageMultiplier().addValueChangeHandler(valueChangeHandler);
        view.setVirtualECCType(clientFactory.getInstance().getLastEditLocation().getSystemType());
    }

    @Override
    public void goTo(Place place) {
        if (isValid()) {
            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, this.place, null));
        }
    }

    @Override
    public void onResize() {

    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        view.getDescriptionField().setInvalid("");
        view.getVoltageMultiplier().setInvalid("");
        view.getPowerMultiplier().setInvalid("");

        if (view.getVoltageMultiplier().getValue().trim().isEmpty()) {
            view.getVoltageMultiplier().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }

        if (view.getPowerMultiplier().getValue().trim().isEmpty()) {
            view.getPowerMultiplier().setInvalid(WebStringResource.INSTANCE.requiredField());

            valid = false;
        }
        if (view.getDescriptionField().getValue().trim().isEmpty()) {
            view.getDescriptionField().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }

        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }



    private void updateMTU(final boolean redirect) {
        if (isValid()) {
            VirtualECCMTU mtu = clientFactory.getInstance().getLastEditedVirtualMTU();
            mtu.setMtuType(view.getMTUTypeValue().getValue());
            mtu.setMtuDescription(view.getDescriptionField().getValue().trim());
            mtu.setPowerMultiplier(Double.parseDouble(view.getPowerMultiplier().getValue()));
            mtu.setVoltageMultiplier(Double.parseDouble(view.getVoltageMultiplier().getValue()));

            VirtualECC virtualECC = clientFactory.getInstance().getLastEditLocation();

            RESTFactory.getVirtualECCService(clientFactory).addVirtualECCMTU(mtu, virtualECC.getId(), new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    LOGGER.fine("MTU updated");
                    if (redirect) goTo(new LocationEditPlace(""));
                }
            });
        }
    }

    @Override
    public void updateMTU() {
        updateMTU(false);
    }

    @Override
    public void returnToLocation() {
        updateMTU(true);
    }

    @Override
    public void deleteMTU() {
        VirtualECC virtualECC = clientFactory.getInstance().getLastEditLocation();
        VirtualECCMTU mtu = clientFactory.getInstance().getLastEditedVirtualMTU();
        RESTFactory.getVirtualECCService(clientFactory).deleteVirtualECCMTU(mtu, virtualECC.getId(), new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                goTo(new LocationEditPlace(""));
            }
        });
    }
}
