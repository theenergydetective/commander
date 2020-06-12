/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationMTU;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.common.enums.MTUType;
import com.ted.commander.common.enums.VirtualECCType;

public interface LocationMTUView extends IsWidget {
    void setLogo(ImageResource imageResource);

    void setPresenter(LocationMTUView.Presenter presenter);

    HasValue<String> getTypeField();

    HasValue<String> getSerialNumberField();

    HasValidation<String> getDescriptionField();

    HasValidation<String> getPowerMultiplier();

    HasValidation<String> getVoltageMultiplier();

    HasValue<MTUType> getMTUTypeValue();

    void setVirtualECCType(VirtualECCType virtualECCType);


    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        public void updateMTU();

        public void returnToLocation();

        public void deleteMTU();
    }


}
