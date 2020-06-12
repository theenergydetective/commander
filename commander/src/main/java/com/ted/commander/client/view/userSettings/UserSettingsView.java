/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.userSettings;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.User;

public interface UserSettingsView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(UserSettingsView.Presenter presenter);

    User getUser();

    void setUser(User user);

    void setFirstNameInValid(boolean isValid, String msg);

    void setLastNameInValid(boolean isValid, String msg);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void updateUser();
    }


}
