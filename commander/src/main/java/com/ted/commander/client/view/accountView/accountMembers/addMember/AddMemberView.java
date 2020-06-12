/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.accountMembers.addMember;

import com.google.gwt.user.client.ui.HasValue;
import com.ted.commander.client.view.ClosableView;
import com.ted.commander.common.enums.AccountRole;
import com.vaadin.polymer.paper.element.PaperInputElement;


public interface AddMemberView extends ClosableView {

    PaperInputElement getEmail();

    PaperInputElement getEmailConfirmation();

    HasValue<AccountRole> getAccountRoleValue();

    void setPresenter(AddMemberView.Presenter presenter);

    public interface Presenter {
        void addMember();
    }

}
