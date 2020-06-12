/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.*;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;

/**
 * Created by pete on 6/30/2014.
 */
public interface UserServiceClient extends RestService {
    @POST
    @Path("user")
    public void createUser(JoinRequest joinRequest, MethodCallback<RESTPostResponse> callBack);

    @PUT
    @Path("user")
    public void updateUser(User user, MethodCallback<Void> callBack);


    @GET
    @Path("user/activate/{activationKey}")
    public void activateUser(@PathParam("activationKey") String activationKey, MethodCallback<Void> callBack);

    @GET
    @Path("user")
    public void getUser(MethodCallback<User> callBack);


    @GET
    @Path("user/{userId}/accountMemberships")
    public void getAccountMemberships(@PathParam("userId") long userId, MethodCallback<AccountMemberships> callBack);

    @PUT
    @Path("user/{userId}/email")
    public void changeEmail(UserEmail userEmail, @PathParam("userId") long userId, MethodCallback<Void> callBack);

    @PUT
    @Path("user/{userId}/password")
    public void changePassword(UserPassword userPassword, @PathParam("userId") long userId, MethodCallback<Void> callBack);

    @GET
    @Path("users")
    public void getUsers(@QueryParam("keyword") String keyword, @QueryParam("limit") int limit, MethodCallback<Users> callBack);


}
