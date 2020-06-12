/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountMembership;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.enums.AccountRole;


public interface AccountMembershipView extends IsWidget {
    void setLogo(ImageResource imageResource);

    void setPresenter(AccountMembershipView.Presenter presenter);

    HasValue<String> getAccountNameField();

    HasValue<String> getNameField();

    HasValue<String> getEmailField();

    HasValue<AccountRole> getAccountRoleValue();


    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        public void updateAccount();

        public void deleteAccount();

        void goToAccountPlace();
    }


}
