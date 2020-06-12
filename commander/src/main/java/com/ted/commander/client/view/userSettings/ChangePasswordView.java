/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.userSettings;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by pete on 10/24/2014.
 */
public interface ChangePasswordView extends IsWidget {
    void setExistingInValid(boolean isInvalid, String msg);

    void setNewInValid(boolean isInvalid, String msg);

    void setConfirmationInValid(boolean isInvalid, String msg);

    void setPresenter(ChangePasswordView.Presenter presenter);

    void reset();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void changePassword(String oldPassword, String newPassword, String confirmPassword);
    }

}
