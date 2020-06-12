/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.OAuthEvent;
import com.ted.commander.client.events.OAuthFailEvent;
import com.ted.commander.client.events.OAuthHandler;
import com.ted.commander.client.model.OAuthResponse;
import com.ted.commander.common.model.User;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pete on 6/30/2014.
 */
public class RESTFactory {

    static final Logger LOGGER = Logger.getLogger(RESTFactory.class.getName());
    //For debugging
    static final String resourceURL = getResourceURL();
    static final Resource resource = new Resource(resourceURL, new HashMap<String, String>());

    static String getResourceURL() {
        LOGGER.fine("REFERENCE URL:" + GWT.getModuleBaseURL());


        String resourceURL = "https://commander.theenergydetective.com/api";

        if (GWT.getModuleBaseURL().contains("white")) {
            resourceURL = "http://whitelabel.theenergydetective.com/api";
        }

        if (GWT.getModuleBaseURL().contains("petecode")) {
            resourceURL = "https://dev.petecode.com/api";
        }

        if (GWT.getModuleBaseURL().contains("69.1.4.237")) {
            resourceURL = "http://69.1.4.237:8081/api";
        }

        if (GWT.getModuleBaseURL().contains("192.168.1.121")) {
            resourceURL = "http://192.168.1.121:8081/api";
        }

        if (GWT.getModuleBaseURL().contains("127.0")) {
            resourceURL = "http://127.0.0.1:8081/api";
        }

        if (GWT.getModuleBaseURL().contains("127.0")) {
            resourceURL = "http://127.0.0.1:8081/api";
        }

        LOGGER.fine("RESOURCE URL:" + resourceURL);

        return resourceURL;
    }

    static String getOauthURL() {
        return getResourceURL().replace("/api", "/oauth/token");
    }


    public static String getThemeUrl() {
        return getResourceURL().replace("/api", "/style/commander-style.html");
    }


    public static NoPostServiceClient getNoPostService(ClientFactory clientFactory) {
        NoPostServiceClient serviceClient = GWT.create(NoPostServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static DashboardControllerClient getDashboardControllerClient(ClientFactory clientFactory) {
        DashboardControllerClient serviceClient = GWT.create(DashboardControllerClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static AdviceServiceClient getAdviceService(ClientFactory clientFactory) {
        AdviceServiceClient serviceClient = GWT.create(AdviceServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }


    public static PasswordServiceClient getPasswordService(ClientFactory clientFactory) {
        PasswordServiceClient serviceClient = GWT.create(PasswordServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }


    public static AuthServiceClient getLoginService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }

        AuthServiceClient serviceClient = GWT.create(AuthServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static UserServiceClient getUserService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        UserServiceClient serviceClient = GWT.create(UserServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static AccountServiceClient getAccountService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        AccountServiceClient serviceClient = GWT.create(AccountServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static MTUServiceClient getMTUService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        MTUServiceClient serviceClient = GWT.create(MTUServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static VirtualECCServiceClient getVirtualECCService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        VirtualECCServiceClient serviceClient = GWT.create(VirtualECCServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static EnergyPlanServiceClient getEnergyPlanService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        EnergyPlanServiceClient serviceClient = GWT.create(EnergyPlanServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static ResourceServiceClient getResourceService(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        ResourceServiceClient serviceClient = GWT.create(ResourceServiceClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static ExportClient getExportClient(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        ExportClient serviceClient = GWT.create(ExportClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static ComparisonClient getComparisonClient(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        ComparisonClient serviceClient = GWT.create(ComparisonClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static HistoryClient getHistoryClient(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        HistoryClient serviceClient = GWT.create(HistoryClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }

    public static GraphClient getGraphClient(ClientFactory clientFactory) {
        if (clientFactory.getOAuthResponse() != null && !clientFactory.getOAuthResponse().getAccess_token().isEmpty()) {
            resource.getHeaders().put("Authorization", "BEARER " + clientFactory.getOAuthResponse().getAccess_token());
        }
        GraphClient serviceClient = GWT.create(GraphClient.class);
        ((RestServiceProxy) serviceClient).setResource(resource);
        return serviceClient;
    }


    public static void authenticate(final ClientFactory clientFactory, final String username, final String password, final OAuthHandler handler) {
        StringBuilder formContent = new StringBuilder();
        formContent.append("grant_type=password");
        formContent.append("&client_id=web");
        formContent.append("&scope=api");
        formContent.append("&username=").append(username);
        formContent.append("&password=").append(password);

        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                LOGGER.fine("OAUTH RESPONSE RECEIVED: " + response.getText());
                //handler.onAuth(new OAuthEvent(OAuthResponse.fromJsonString(response.getText())));
                OAuthResponse oAuthResponse = OAuthResponse.fromJsonString(response.getText());
                LOGGER.fine("oAuthResponse:" + oAuthResponse);

                if (oAuthResponse.isError()) {
                    LOGGER.severe("Could not authenticate user:" + oAuthResponse);
                    handler.onFail(new OAuthFailEvent(oAuthResponse.getError()));
                } else {
                    LOGGER.fine("Returning successful authentication via password");
                    getUser(clientFactory, oAuthResponse, handler);
                    //handler.onAuth(new OAuthEvent(oAuthResponse));
                }

            }

            @Override
            public void onError(Request request, Throwable throwable) {
                LOGGER.log(Level.SEVERE, "FAILED AUTH:", throwable);
                handler.onFail(new OAuthFailEvent("SYSTEM"));
            }
        };
        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, getOauthURL());
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setHeader("Authorization", "Basic d2ViOmNvbW1hbmRlckFQSQ==");
        request.setRequestData(formContent.toString());
        request.setCallback(requestCallback);

        try {
            request.send();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "FAILED SEND:", ex);
        }
    }



    public static void authenticate(final ClientFactory clientFactory, final String refreshToken, final OAuthHandler handler) {
        LOGGER.fine("REFRESH TOKEN:" + refreshToken);
        StringBuilder formContent = new StringBuilder();
        formContent.append("grant_type=refresh_token");
        formContent.append("&client_id=web");
        formContent.append("&scope=api");
        formContent.append("&refresh_token=").append(refreshToken);

        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                LOGGER.fine("OAUTH RESPONSE RECEIVED: " + response.getText());
                OAuthResponse oAuthResponse = OAuthResponse.fromJsonString(response.getText());

                if (oAuthResponse.isError()) {
                    LOGGER.severe("Could not refresh token:" + oAuthResponse);
                    handler.onFail(new OAuthFailEvent(oAuthResponse.getError()));
                } else {
                    LOGGER.fine("Returning successful authentication via refresh_token");
                    getUser(clientFactory, oAuthResponse, handler);
                    //handler.onAuth(new OAuthEvent(oAuthResponse));
                }
            }

            @Override
            public void onError(Request request, Throwable throwable) {
                LOGGER.log(Level.SEVERE, "FAILED AUTH:", throwable);
                handler.onFail(new OAuthFailEvent("SYSTEM"));
            }
        };
        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, getOauthURL());
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setHeader("Authorization", "Basic d2ViOmNvbW1hbmRlckFQSQ==");
        request.setRequestData(formContent.toString());
        request.setCallback(requestCallback);

        try {
            request.send();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "FAILED SEND:", ex);
        }
    }


    private static void getUser(final ClientFactory clientFactory, final OAuthResponse oAuthResponse, final OAuthHandler handler) {
        if (oAuthResponse.getRefresh_token() != null && !oAuthResponse.getRefresh_token().isEmpty()) {
            LOGGER.fine("Updating refresh token");
            clientFactory.getInstance().setRefreshToken(oAuthResponse.getRefresh_token());
        }
        LOGGER.fine("Updating access_token");
        clientFactory.setOAuthResponse(oAuthResponse);


        LOGGER.fine("Looking up session user");
        RESTFactory.getUserService(clientFactory).getUser(new DefaultMethodCallback<User>() {
            @Override
            public void onSuccess(Method method, User user) {
                LOGGER.fine("USER:" + user);
                clientFactory.setUser(user);
                handler.onAuth(new OAuthEvent(oAuthResponse));
            }
        });

    }

}
