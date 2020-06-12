/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;


public interface RequiredWebStringResource extends Constants {

    static RequiredWebStringResource INSTANCE = GWT.create(RequiredWebStringResource.class);

    String description();

    String accountName();

    String phone();

    String email();

    String confirmEmail();

    String password();

    String confirmPassword();

    String firstName();

    String lastName();

    String currentPassword();

    String newPassword();

    String powerMultiplier();

    String voltageMultiplier();

    String locationName();

    String timezone();
}
