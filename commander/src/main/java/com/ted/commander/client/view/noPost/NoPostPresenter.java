/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.noPost;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.NoPostPlace;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.model.LastPost;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.logging.Logger;


public class NoPostPresenter implements NoPostView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(NoPostPresenter.class.getName());

    Timer dataPollingTimer = new Timer() {
        @Override
        public void run() {
            refresh();
        }
    };


    final ClientFactory clientFactory;
    final NoPostView view;
    final NoPostPlace place;

    public NoPostPresenter(ClientFactory clientFactory, NoPostPlace place) {
        LOGGER.fine("CREATING NEW LOGIN PRESENTER");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getNoPostView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getBigLogo());
        refresh();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, new DashboardPlace(""), null));
    }

    @Override
    public void onResize() {

    }

    @Override
    public void refresh() {
        dataPollingTimer.cancel();
         RESTFactory.getNoPostService(clientFactory).getNoPost(new DefaultMethodCallback<List<LastPost>>() {
             @Override
             public void onSuccess(Method method, List<LastPost> lastPosts) {
                 LOGGER.fine("LAST POSTS RECEIVED: " + lastPosts.size());
                 view.setNoPost(lastPosts);
                 dataPollingTimer.schedule(60000);
             }
         });

    }
}
