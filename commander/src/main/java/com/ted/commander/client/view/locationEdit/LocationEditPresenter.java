/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperSimplePicker;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.LocationEditPlace;
import com.ted.commander.client.places.LocationListPlace;
import com.ted.commander.client.places.LocationMTUPlace;
import com.ted.commander.client.resources.*;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.*;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LocationEditPresenter implements LocationEditView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(LocationEditPresenter.class.getName());

    final ClientFactory clientFactory;
    final LocationEditPlace place;
    final Long virtualECCId;


    LocationEditView view;
    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {

            LOGGER.fine("VALUE CHANGE HANDLER FIRED!");

            final VirtualECC virtualECC = clientFactory.getInstance().getLastEditLocation();


            if (isValid()) {
                final int oldHash = virtualECC.hashCode();
                boolean forceSave = false;
                VirtualECCType oldType = virtualECC.getSystemType();


                if (virtualECC.getCountry() == null || !virtualECC.getCountry().equals(view.getCountryValue().getValue())) {
                    virtualECC.setCountry(view.getCountryValue().getValue());
                    refreshRegionCode(virtualECC.getCountry());
                }

                VirtualECCType newType = VirtualECCType.NET_ONLY;

                if (view.getIsSolarValue().getValue()) {
                    if (oldType.equals(VirtualECCType.NET_ONLY))
                        newType = VirtualECCType.NET_GEN;
                    else {
                        newType = view.getVirtualECCValue().getValue();
                    }
                } else {
                    newType = VirtualECCType.NET_ONLY;
                }
                LOGGER.fine("NEW TYPE: " + newType);

                virtualECC.setName(view.getNameValue().getValue().trim());
                virtualECC.setSystemType(newType);
                if (view.getStreet1Value().getValue() != null) virtualECC.setStreet1(view.getStreet1Value().getValue().trim());
                if (view.getStreet2Value().getValue() != null) virtualECC.setStreet2(view.getStreet2Value().getValue().trim());
                if (view.getCityValue().getValue() != null) virtualECC.setCity(view.getCityValue().getValue().trim());
                if (view.getPostalValue().getValue() != null) virtualECC.setPostal(view.getPostalValue().getValue());
                virtualECC.setCountry(view.getCountryValue().getValue());
                virtualECC.setState(view.getStateValue().getValue());
                virtualECC.setTimezone(view.getTimeZoneValue().getValue());


                Long oldEnergyPlanId = virtualECC.getEnergyPlanId();
                virtualECC.setEnergyPlanId(Long.parseLong(view.getEnergyPlanValue().getValue()));
                if (!oldEnergyPlanId.equals(virtualECC.getEnergyPlanId())) {
                    forceSave = true;
                }

                virtualECC.setSystemType(newType);
                view.showAdvanced(!virtualECC.getSystemType().equals(VirtualECCType.NET_ONLY));

                if (!oldType.equals(newType)) {
                    LOGGER.fine("TYPE CHANGE:" + newType);

                    view.getIsSolarValue().setValue(!virtualECC.getSystemType().equals(VirtualECCType.NET_ONLY));
                    view.getVirtualECCValue().setValue(virtualECC.getSystemType());

                    RESTFactory.getVirtualECCService(clientFactory).deleteVirtualECCMTUS(virtualECC.getId(), new DefaultMethodCallback<Void>() {
                        @Override
                        public void onSuccess(Method method, Void aVoid) {
                            saveVirtualECC(virtualECC);
                            view.setMTUList(new ArrayList<VirtualECCMTU>());
                        }
                    });

                } else {
                    forceSave = true;
                    if (forceSave || oldHash != virtualECC.hashCode()) saveVirtualECC(virtualECC);
                }
            }
        }
    };


    public LocationEditPresenter(final ClientFactory clientFactory, final LocationEditPlace place) {
        LOGGER.fine("CREATING NEW LocationEditPresenter");
        this.clientFactory = clientFactory;
        this.place = place;

        this.view = clientFactory.getLocationEditView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());


        if (place.getVirtualECCId() == null) {
            virtualECCId = clientFactory.getInstance().getLastEditLocation().getId();
        } else {
            virtualECCId = place.getVirtualECCId();
        }


        RESTFactory.getEnergyPlanService(clientFactory).getKeysForAccount(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId(), new DefaultMethodCallback<List<EnergyPlanKey>>() {
            @Override
            public void onSuccess(Method method, List<EnergyPlanKey> keys) {

                final List<EnergyPlanKey> energyPlanKeys = keys;

                //Force reload of the location.
                RESTFactory.getVirtualECCService(clientFactory).get(virtualECCId, new DefaultMethodCallback<VirtualECC>() {
                    @Override
                    public void onSuccess(Method method, VirtualECC virtualECC) {
                        clientFactory.getInstance().setLastEditLocation(virtualECC);

                        boolean isEditor = clientFactory.getInstance().getLastEditedAccountMembership().isEditor();
                        view.setReadOnly(!isEditor);


                        LOGGER.fine("LOCATION EDITOR");
                        final PaperSimplePicker countryField = (PaperSimplePicker) view.getCountryValue();
                        final PaperSimplePicker timezoneField = (PaperSimplePicker) view.getTimeZoneValue();
                        final PaperSimplePicker energyPlanField = (PaperSimplePicker) view.getEnergyPlanValue();

                        for (CountryCode countryCode : CountryCodeFactory.getInstance(clientFactory.getUser().getLanguageCode()).getCountryCodeList()) {
                            countryField.addItem(countryCode.getCountry(), countryCode.getCode());
                        }

                        countryField.setValue(CountryCodeFactory.getInstance(clientFactory.getUser().getLanguageCode()).getCountryCodeList().get(0).getCode());

                        countryField.addValueChangeHandler(new ValueChangeHandler<String>() {
                            @Override
                            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                                String value = countryField.getValue();
                                refreshRegionCode(value);
                            }
                        });


                        for (TimeZoneCode timeZone : TimeZoneFactory.getInstance(clientFactory.getUser().getLanguageCode()).getTimeZoneList()) {
                            timezoneField.addItem(timeZone.getDescription(), timeZone.getCode());
                        }

                        for (EnergyPlanKey energyPlanKey: energyPlanKeys){
                            energyPlanField.addItem(energyPlanKey.getDescription(), energyPlanKey.getId().toString());
                        }
                        energyPlanField.addItem(WebStringResource.INSTANCE.defaultEnergyPlan(), "0");
                        energyPlanField.setValue(virtualECC.getEnergyPlanId().toString());

                        view.getNameValue().setValue(virtualECC.getName(), false);
                        view.getStreet1Value().setValue(virtualECC.getStreet1(), false);
                        view.getStreet2Value().setValue(virtualECC.getStreet2(), false);
                        view.getCityValue().setValue(virtualECC.getCity(), false);

                        view.getPostalValue().setValue(virtualECC.getPostal(), false);

                        view.getTimeZoneValue().setValue(virtualECC.getTimezone(), false);

                        if (isEditor) {
                            view.getCountryValue().setValue(virtualECC.getCountry(), false);
                            refreshRegionCode(virtualECC.getCountry());
                            view.getStateValue().setValue(virtualECC.getState(), false);
                        } else {
                            view.getStateValue().setValue(null, false);
                            for (CountryCode countryCode : CountryCodeFactory.getInstance(clientFactory.getUser().getLanguageCode()).getCountryCodeList()) {
                                if (countryCode.getCode().equals(virtualECC.getCountry())) {
                                    view.getCountryValue().setValue(countryCode.getCountry());
                                    List<RegionCode> regionCodeList = CountryCodeFactory.getInstance(clientFactory.getUser().getLanguageCode()).getRegionCodeList(countryCode.getCode());
                                    for (RegionCode regionCode : regionCodeList) {
                                        if (regionCode.getRegionCode().equals(virtualECC.getState())) {
                                            view.getStateValue().setValue(regionCode.getDescription());
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        LOGGER.fine("VIRTUAL ECC VALUE: " + virtualECC.getSystemType());

                        view.getIsSolarValue().setValue(!virtualECC.getSystemType().equals(VirtualECCType.NET_ONLY));
                        view.getVirtualECCValue().setValue(virtualECC.getSystemType());
                        view.showAdvanced(!virtualECC.getSystemType().equals(VirtualECCType.NET_ONLY));

                        view.getNameValue().addValueChangeHandler(valueChangeHandler);
                        view.getStreet1Value().addValueChangeHandler(valueChangeHandler);
                        view.getStreet2Value().addValueChangeHandler(valueChangeHandler);
                        view.getCityValue().addValueChangeHandler(valueChangeHandler);
                        view.getStateValue().addValueChangeHandler(valueChangeHandler);
                        view.getPostalValue().addValueChangeHandler(valueChangeHandler);
                        view.getCountryValue().addValueChangeHandler(valueChangeHandler);
                        view.getTimeZoneValue().addValueChangeHandler(valueChangeHandler);
                        view.getEnergyPlanValue().addValueChangeHandler(valueChangeHandler);
                        view.getIsSolarValue().addValueChangeHandler(valueChangeHandler);
                        view.getVirtualECCValue().addValueChangeHandler(valueChangeHandler);
                        reloadMTUList();
                    }
                });



            }
        });



    }

    private void saveVirtualECC(final VirtualECC virtualECC) {
        LOGGER.fine("SAVE VIRTUAL ECC CALLED: " + virtualECC);
        RESTFactory.getVirtualECCService(clientFactory).create(virtualECC, new DefaultMethodCallback<RESTPostResponse>() {
            @Override
            public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                if (method.getResponse().getStatusCode() == 200) {
                    LOGGER.fine("SAVE COMPLETE");
                    virtualECC.setId(restPostResponse.getId());
                }
            }
        });
    }

    void reloadMTUList() {
        VirtualECC virtualECC = clientFactory.getInstance().getLastEditLocation();
        RESTFactory.getVirtualECCService(clientFactory).getVirtualECCMTUs(virtualECC.getAccountId(), virtualECC.getId(), new DefaultMethodCallback<List<VirtualECCMTU>>() {
            @Override
            public void onSuccess(Method method, List<VirtualECCMTU> virtualECCMTUs) {
                view.setMTUList(virtualECCMTUs);
            }
        });
    }




    @Override
    public boolean isValid() {
        boolean isValid = true;
        if (clientFactory.getInstance().getLastEditedAccountMembership().isEditor()) {


            view.getNameValue().setInvalid("");
            view.getTimeZoneValue().setInvalid("");

            ((HasValidation) view.getStateValue()).setInvalid("");
            ((HasValidation) view.getCountryValue()).setInvalid("");

            if (view.getNameValue().getValue().trim().isEmpty()) {
                view.getNameValue().setInvalid(WebStringResource.INSTANCE.requiredField());
                isValid = false;
            }

            if (view.getTimeZoneValue().getValue() == null || view.getTimeZoneValue().getValue().trim().isEmpty()) {
                view.getTimeZoneValue().setInvalid(WebStringResource.INSTANCE.requiredField());
                isValid = false;
            }


            if (view.getCityValue().getValue() != null && !view.getCityValue().getValue().isEmpty() && view.getStateValue().getValue() == null) {
                ((HasValidation) view.getStateValue()).setInvalid("Required");
                isValid = false;
            }

            if (view.getCityValue().getValue() != null && !view.getCityValue().getValue().isEmpty() && view.getCountryValue().getValue() == null) {
                ((HasValidation) view.getCountryValue()).setInvalid("Required");
                isValid = false;
            }

        }

        return isValid;
    }


    @Override
    public Widget asWidget() {
        return view.asWidget();
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
    public void delete() {
        RESTFactory.getVirtualECCService(clientFactory).delete(clientFactory.getInstance().getLastEditLocation().getId(), new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                goTo(new LocationListPlace(""));
            }
        });
    }

    @Override
    public void queryAccountMTUList() {
        if (isValid()) {
            RESTFactory.getMTUService(clientFactory).getMTUs(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId(), 0, 0, "", "", new DefaultMethodCallback<List<MTU>>() {
                @Override
                public void onSuccess(Method method, List<MTU> mtus) {
                    view.showMTUSelectionList(mtus);
                }
            });
        }
    }

    @Override
    public void addVirtualMTU(MTU mtu) {
        final VirtualECC virtualECC = clientFactory.getInstance().getLastEditLocation();
        final VirtualECCMTU virtualECCMTU = new VirtualECCMTU(virtualECC.getId(), mtu);

        //check the type and alter it if needed.
        switch (virtualECC.getSystemType()) {
            case NET_ONLY:
                if (virtualECCMTU.getMtuType().equals(MTUType.LOAD)) virtualECCMTU.setMtuType(MTUType.NET);
                if (virtualECCMTU.getMtuType().equals(MTUType.GENERATION)) virtualECCMTU.setMtuType(MTUType.NET);
                break;
            case LOAD_GEN:
                if (virtualECCMTU.getMtuType().equals(MTUType.NET)) virtualECCMTU.setMtuType(MTUType.LOAD);
                break;
            case NET_GEN:
                if (virtualECCMTU.getMtuType().equals(MTUType.LOAD)) virtualECCMTU.setMtuType(MTUType.NET);
                break;
        }

        RESTFactory.getVirtualECCService(clientFactory).addVirtualECCMTU(virtualECCMTU, virtualECC.getId(), new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                //editVirtualMTU(virtualECCMTU);
                updateMTU(virtualECCMTU);

            }
        });

    }

    private void updateMTU(VirtualECCMTU mtu) {
        if (isValid()) {

            VirtualECC virtualECC = clientFactory.getInstance().getLastEditLocation();

            RESTFactory.getVirtualECCService(clientFactory).addVirtualECCMTU(mtu, virtualECC.getId(), new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    LOGGER.fine("MTU updated");
                    reloadMTUList();
                }
            });
        }
    }

    @Override
    public void editVirtualMTU(VirtualECCMTU virtualECCMTU) {
        clientFactory.getInstance().setLastEditedVirtualMTU(virtualECCMTU);
        goTo(new LocationMTUPlace(""));
    }




    private void refreshRegionCode(String countryCode) {
        final PaperSimplePicker stateRegionField = (PaperSimplePicker) view.getStateValue();
        stateRegionField.clear();

        List<RegionCode> regionCodeList = CountryCodeFactory.getInstance(clientFactory.getUser().getLanguageCode()).getRegionCodeList(countryCode);


        if (regionCodeList != null) {
            for (RegionCode regionCode : regionCodeList) {
                stateRegionField.addItem(regionCode.getDescription(), regionCode.getRegionCode());
            }

            if (regionCodeList.size() > 0) {
                stateRegionField.setValue(regionCodeList.get(0).getRegionCode());
            } else {
                stateRegionField.setValue(null);
            }
        } else {
            stateRegionField.setValue(null);
        }


        //stateRegionField.setEnabled(stateRegionField.getItemCount() > 0);
    }

}
