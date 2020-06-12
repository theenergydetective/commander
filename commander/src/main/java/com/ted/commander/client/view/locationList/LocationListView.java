/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationList;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.VirtualECC;

import java.util.List;

public interface LocationListView extends IsWidget {
    void setLogo(ImageResource imageResource);

    void setPresenter(LocationListView.Presenter presenter);

    void setLocations(List<VirtualECC> virtualECCList);

    void setAccountMemberships(AccountMemberships accountMemberships);

    void setAccountMembership(AccountMembership accountMembership);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void newLocation();

        void edit(VirtualECC accountLocation);

        void listLocations(AccountMembership accountMembership);

        void selectAccount(AccountMembership accountMembership);
    }


}
