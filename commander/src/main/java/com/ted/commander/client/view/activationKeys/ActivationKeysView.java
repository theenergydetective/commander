/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.activationKeys;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.AccountMembership;

import java.util.List;


public interface ActivationKeysView extends IsWidget {
    void setLogo(ImageResource imageResource);

    void setPresenter(ActivationKeysView.Presenter presenter);

    void setMemberships(List<AccountMembership> accountMembershipList);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {

    }

}
