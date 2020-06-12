/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountMTU;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;


public interface AccountMTUView extends IsWidget {
    void setLogo(ImageResource imageResource);

    void setPresenter(AccountMTUView.Presenter presenter);



    HasValue<String> getTypeField();

    HasValue<String> getSerialNumberField();

    HasValidation<String> getDescriptionField();

    HasValue<String> getMTUTypeValue();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void deleteMTU();
        void goToAccountPlace();
    }


}
