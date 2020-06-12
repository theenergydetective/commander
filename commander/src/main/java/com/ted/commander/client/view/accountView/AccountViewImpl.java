/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.places.AccountListPlace;
import com.ted.commander.client.view.accountView.accountInformation.AccountInformation;
import com.ted.commander.client.view.accountView.accountMembers.AccountMemberView;
import com.ted.commander.client.view.accountView.mtuList.AccountMTUListView;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.AccountMembers;
import com.ted.commander.common.model.MTU;

import java.util.List;
import java.util.logging.Logger;


public class AccountViewImpl extends Composite implements AccountView {

    static final Logger LOGGER = Logger.getLogger(AccountViewImpl.class.getName());
    private static AdminBinder adminBinder = GWT.create(AdminBinder.class);
    private static EditECCBinder editECCBinder = GWT.create(EditECCBinder.class);
    private static ReadOnlyBinder readOnlyBinder = GWT.create(ReadOnlyBinder.class);
    Presenter presenter;
    @UiField
    TitleBar titleBar;
    @UiField
    AccountInformation accountInformation;
    @UiField
    AccountMemberView accountMemberView;
    @UiField
    AccountMTUListView accountMTUListView;


    public AccountViewImpl(AccountRole role) {
        switch (role) {
            case OWNER:
            case ADMIN:
                initWidget(adminBinder.createAndBindUi(this));
                break;
            case EDIT_ECCS:
                initWidget(editECCBinder.createAndBindUi(this));
                break;
            default:
                initWidget(readOnlyBinder.createAndBindUi(this));
        }

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new AccountListPlace(""));
            }
        });

    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        accountMemberView.setPresenter(presenter);
        accountMTUListView.setPresenter(presenter);
    }

    @Override
    public HasValidation<String> getNameField() {
        return accountInformation.getAccountNameField();
    }

    @Override
    public HasValidation<String> getPhoneField() {
        return accountInformation.getPhoneNumberField();
    }

    @Override
    public void setAccountMembers(AccountMembers accountMembers) {
        accountMemberView.setAccountMembers(accountMembers);

    }

    @Override
    public void setMTUs(List<MTU> mtus) {
        accountMTUListView.setMTUList(mtus);
    }

    @UiTemplate("AccountAdminViewImpl.ui.xml")
    interface AdminBinder extends UiBinder<Widget, AccountViewImpl> {
    }

    @UiTemplate("AccountEditECCViewImpl.ui.xml")
    interface EditECCBinder extends UiBinder<Widget, AccountViewImpl> {
    }

    @UiTemplate("AccountReadOnlyViewImpl.ui.xml")
    interface ReadOnlyBinder extends UiBinder<Widget, AccountViewImpl> {
    }


}
