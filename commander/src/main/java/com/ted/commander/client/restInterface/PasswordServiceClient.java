/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.PasswordResetRequest;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by pete on 6/30/2014.
 */
public interface PasswordServiceClient extends RestService {
    @POST
    @Path("password")
    public void resetPassword(PasswordResetRequest passwordResetRequest, MethodCallback<RESTPostResponse> callBack);

    @GET
    @Path("password")
    public void requestPasswordReset(@QueryParam("email") String email, MethodCallback<RESTPostResponse> callBack);

}
