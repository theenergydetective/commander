/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.noPost;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.places.NoPostPlace;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.model.LastPost;

import java.util.List;
import java.util.logging.Logger;


public class NoPostImpl extends Composite implements NoPostView {

    static final Logger LOGGER = Logger.getLogger(NoPostImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    Presenter presenter;


    @UiField
    TitleBar titleBar;
    @UiField
    VerticalPanel noPostPanel;



    public NoPostImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        titleBar.setSelectedPlace(new NoPostPlace(""));

        titleBar.addItemSelectedHandler(new ItemSelectedHandler<Place>() {
            @Override
            public void onSelected(ItemSelectedEvent<Place> event) {
                presenter.goTo(event.getItem());
            }
        });
    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNoPost(List<LastPost> lastPostList) {
        noPostPanel.clear();
        Long lastId = null;
        NoPostLocation noPostLocation = null;
        for (LastPost lastPost: lastPostList){
            if (noPostLocation == null || lastPost.getId() != lastId.longValue()){
                String locationName = lastPost.getEccName();
                if (lastPost.getActiveCount() > 0) locationName += "*";
                noPostLocation = new NoPostLocation(locationName);
                noPostPanel.add(noPostLocation);
                lastId = lastPost.getId();
            }
            noPostLocation.addMTU(lastPost.getMtuId(), lastPost.getMtuDescription(), lastPost.getLastPost());



        }

    }


    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, NoPostImpl> {
    }


}
