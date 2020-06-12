/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountList;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;

public interface AccountListView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(AccountListView.Presenter presenter);

    void setAccountMemberships(AccountMemberships accountMemberships);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void edit(AccountMembership accountMembership);

    }


}
