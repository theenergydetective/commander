/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.presenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.util.Validator;
import com.ted.commander.client.view.accountView.accountMembers.addMember.AddMemberView;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.AccountMember;
import com.ted.commander.common.model.User;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class AddMemberPresenter implements AddMemberView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(AddMemberPresenter.class.getName());

    final ClientFactory clientFactory;
    final AddMemberView view;


    WebStringResource stringRes = WebStringResource.INSTANCE;

    public AddMemberPresenter(ClientFactory clientFactory, Account account) {
        LOGGER.fine("CREATING NEW AddMemberPresenter");
        this.clientFactory = clientFactory;
        view = clientFactory.getAddMemberView();
        view.setPresenter(this);
        view.getEmail().setValue("");
        view.getEmailConfirmation().setValue("");
        view.getAccountRoleValue().setValue(AccountRole.READ_ONLY);
        view.getEmail().setErrorMessage("");
        view.getEmailConfirmation().setErrorMessage("");
        view.getEmail().setInvalid(false);
        view.getEmailConfirmation().setInvalid(false);
    }


    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void addMember() {
        boolean valid = true;
        view.getEmail().setInvalid(false);
        view.getEmailConfirmation().setInvalid(false);
        view.getEmail().setErrorMessage("");
        view.getEmailConfirmation().setErrorMessage("");

        if (view.getEmail().getValue().isEmpty()) {
            valid = false;
            view.getEmail().setErrorMessage(WebStringResource.INSTANCE.requiredField());
        }

        if (valid && !Validator.isValidEmail(view.getEmail().getValue())) {
            valid = false;
            view.getEmail().setErrorMessage(WebStringResource.INSTANCE.invalidEmailError());
            view.getEmail().setInvalid(true);
        }

        if (view.getEmailConfirmation().getValue().isEmpty()) {
            valid = false;
            view.getEmailConfirmation().setErrorMessage(WebStringResource.INSTANCE.requiredField());
            view.getEmailConfirmation().setInvalid(true);

        }

        if (valid) {
            if (!view.getEmail().getValue().equals(view.getEmailConfirmation().getValue())) {
                valid = false;
                view.getEmailConfirmation().setErrorMessage(WebStringResource.INSTANCE.emailMatchError());
                view.getEmailConfirmation().setInvalid(true);
            }
        }

        if (valid) {
            AccountMember accountMember = new AccountMember();
            accountMember.setAccountId(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId());
            accountMember.setUser(new User());
            accountMember.getUser().setUsername(view.getEmail().getValue().toLowerCase());
            accountMember.setAccountRole(view.getAccountRoleValue().getValue());
            RESTFactory.getAccountService(clientFactory).addAccountMember(accountMember, accountMember.getAccountId(), new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    view.close();
                }
            });
        }

    }
}
