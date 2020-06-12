/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.login;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;


public interface LoginView extends IsWidget {

    HasValidation<String> getUsername();

    HasValidation<String> getPassword();

    void setLogo(ImageResource imageResource);

    void setPresenter(LoginView.Presenter presenter);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {

        void authenticate();

        void join();

        void forgotPassword();
    }
}
