/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.join;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

public interface JoinSuccessView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(JoinSuccessView.Presenter presenter);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {

    }


}
