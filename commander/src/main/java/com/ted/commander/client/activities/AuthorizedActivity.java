package com.ted.commander.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.place.shared.Place;
import com.petecode.common.client.logging.ConsoleLoggerFactory;
import com.ted.commander.client.callback.SimpleCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.OAuthEvent;
import com.ted.commander.client.events.OAuthFailEvent;
import com.ted.commander.client.events.OAuthHandler;
import com.ted.commander.client.restInterface.RESTFactory;

import java.util.logging.Logger;


public abstract class AuthorizedActivity extends AbstractActivity {
    static final Logger LOGGER = ConsoleLoggerFactory.getLogger(AuthorizedActivity.class);

    protected final Place place;
    protected final ClientFactory clientFactory;

    public AuthorizedActivity(Place place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    /**
     * This verifies that the user is in an authenticated state. It will refresh the access token if needed.
     *
     * @param callback
     */
    protected void authorize(final SimpleCallback<Boolean> callback) {
        //No login credentials present
        LOGGER.fine("Checking for refresh_token:" + clientFactory.getInstance().getRefreshToken());
        if (clientFactory.getInstance().getRefreshToken() == null || clientFactory.getInstance().getRefreshToken().isEmpty()) {
            LOGGER.fine("No refresh_token found. Redirecting to Login");
            callback.onCallback(false);
            return;
        }

        LOGGER.fine("Checking for access_token");
        if (clientFactory.getOAuthResponse() == null) {
            LOGGER.fine("No active access token found. Checking Refresh token");
            RESTFactory.authenticate(clientFactory, clientFactory.getInstance().getRefreshToken(), new OAuthHandler() {
                @Override
                public void onAuth(OAuthEvent event) {
                    LOGGER.fine("Refresh Token Successful");
                    callback.onCallback(true);
                    return;
                }

                @Override
                public void onFail(OAuthFailEvent event) {
                    LOGGER.fine("Refresh Token fail");
                    clientFactory.clearInstance();
                    callback.onCallback(false);
                    return;
                }
            });

        } else {

            LOGGER.fine("Check access token expiration");
            if (clientFactory.getOAuthResponse().isExpired()) {
                LOGGER.fine("Token Expired. Forcing refresh_token update");
                clientFactory.setOAuthResponse(null);
                authorize(callback);
                return;
            }
            callback.onCallback(true);
        }
    }


}
