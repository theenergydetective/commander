/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.resetPassword;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;

public interface ResetPasswordView extends IsWidget {

    HasValidation<String> passwordField();
    HasValidation<String> cofirmPasswordField();
    void showError();
    void showConfirmation();
    void setLogo(ImageResource imageResource);
    void setPresenter(ResetPasswordView.Presenter presenter);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void submit();
        void back();
    }


}
