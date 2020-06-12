/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.EnergyPlanKey;
import com.ted.commander.common.model.MTU;
import com.ted.commander.common.model.VirtualECCMTU;

import java.util.List;

public interface LocationEditView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(LocationEditView.Presenter presenter);

    //Value Fields
    HasValidation<String> getNameValue();

    HasValue<String> getStreet1Value();

    HasValue<String> getStreet2Value();

    HasValue<String> getCityValue();

    HasValue<String> getStateValue();

    HasValue<String> getPostalValue();

    HasValue<String> getCountryValue();

    HasValidation<String> getTimeZoneValue();

    HasValue<String> getEnergyPlanValue();

    HasValue<Boolean> getIsSolarValue();

    HasValue<VirtualECCType> getVirtualECCValue();


    public void showMTUSelectionList(List<MTU> mtuList);

    public void showEnergyPlanList(List<EnergyPlanKey> energyPlanKeyList);

    public void showAdvanced(boolean showAdvanced);

    void setMTUList(List<VirtualECCMTU> virtualECCMTUs);

    void setReadOnly(Boolean readOnly);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void delete();

        void queryAccountMTUList();

        void addVirtualMTU(MTU mtu);

        void editVirtualMTU(VirtualECCMTU virtualECCMTU);


    }


}
