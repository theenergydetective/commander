/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.noPost;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.LastPost;

import java.util.List;

public interface NoPostView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(NoPostView.Presenter presenter);

    void setNoPost(List<LastPost> lastPostList);

    interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void refresh();
    }


}
