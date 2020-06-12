/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.userSettings;

import com.google.gwt.user.client.ui.IsWidget;


public interface ChangeEmailView extends IsWidget {
    void setNewInValid(boolean isInvalid, String msg);

    void setConfirmationInValid(boolean isInvalid, String msg);


    void setPresenter(ChangeEmailView.Presenter presenter);

    void reset();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void changeEmail(String newEmail, String confirmEmail);
    }

}
