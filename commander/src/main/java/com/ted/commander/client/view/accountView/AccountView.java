/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.view.accountView.accountMembers.addMember.AddMemberView;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.AccountMembers;
import com.ted.commander.common.model.MTU;

import java.util.List;

public interface AccountView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(AccountView.Presenter presenter);


    HasValidation<String> getNameField();

    HasValidation<String> getPhoneField();

    void setAccountMembers(AccountMembers accountMembers);

    void setMTUs(List<MTU> mtus);


    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        public void getAccountMembers(int start, int limit, String sort, String sortOrder);

        public void getMTUs(int start, int limit, String sort, String sortOrder);

        public AddMemberView getAddMemberView();

        public void editMember(AccountMember accountMember);

        public void editMTU(MTU mtu);

    }


}
