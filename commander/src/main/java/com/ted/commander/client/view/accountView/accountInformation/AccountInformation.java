/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountView.accountInformation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.widget.paper.HasValidation;

import java.util.logging.Logger;


public class AccountInformation extends Composite {

    static final Logger LOGGER = Logger.getLogger(AccountInformation.class.getName());
    private static ReadOnlyBinder readOnlyBinder = GWT.create(ReadOnlyBinder.class);
    private static EditableBinder editableBinder = GWT.create(EditableBinder.class);
    final boolean editable;

    @UiField
    HasValidation<String> accountNameField;
    @UiField
    HasValidation<String> phoneNumberField;

    @UiConstructor
    public AccountInformation(final boolean isEditable) {
        this.editable = isEditable;

        if (isEditable) {
            initWidget(editableBinder.createAndBindUi(this));
        } else {
            initWidget(readOnlyBinder.createAndBindUi(this));
        }


    }


    public HasValidation<String> getAccountNameField() {
        return accountNameField;
    }

    public HasValidation<String> getPhoneNumberField() {
        return phoneNumberField;
    }

    public String getAccountValue() {
        return accountNameField.getValue();
    }

    public String getPhoneValue() {
        return phoneNumberField.getValue();
    }


    @UiTemplate("AccountInformation.ui.xml")
    interface ReadOnlyBinder extends UiBinder<Widget, AccountInformation> {
    }

    @UiTemplate("AccountInformationEditable.ui.xml")
    interface EditableBinder extends UiBinder<Widget, AccountInformation> {
    }

}
