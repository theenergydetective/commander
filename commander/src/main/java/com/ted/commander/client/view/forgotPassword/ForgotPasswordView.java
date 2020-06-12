/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.forgotPassword;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;

public interface ForgotPasswordView extends IsWidget {

    HasValidation<String> getUsername();
    void setLogo(ImageResource imageResource);
    void setPresenter(ForgotPasswordView.Presenter presenter);
    void showConfirm();
    void showError();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void submit();
        void back();
    }


}
