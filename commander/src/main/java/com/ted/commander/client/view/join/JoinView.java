/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.join;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

public interface JoinView extends IsWidget {

    String getUsername();

    String getConfirmUsername();

    String getConfirmPassword();

    String getPassword();

    String getLastName();

    String getFirstName();

    String getMiddleName();

    void setSubmitButtonEnabled(boolean enabled);

    void setLastNameInvalid(boolean invalid, String msg);

    void setFirstNameInvalid(boolean invalid, String msg);

    void setUsernameInvalid(boolean invalid, String msg);

    void setPasswordInvalid(boolean invalid, String msg);

    void setConfirmPasswordInvalid(boolean invalid, String msg);

    void setConfirmUsernameInvalid(boolean invalid, String msg);

    void setLogo(ImageResource imageResource);

    void setPresenter(JoinView.Presenter presenter);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void submit();
    }


}
