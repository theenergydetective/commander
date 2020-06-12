/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.AdviceRecipient;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created by pete on 6/30/2014.
 */
public interface AdviceServiceClient extends RestService {

    @GET
    @Path("advice")
    public void getForAccount(@QueryParam("accountId") long accountId,  MethodCallback<List<Advice>> callBack);

    @POST
    @Path("advice")
    public void create(Advice advice,  MethodCallback<RESTPostResponse> callBack);

    @DELETE
    @Path("advice")
    public void delete(Advice advice,  MethodCallback<Void> callBack);

    @PUT
    @Path("advice")
    public void put(Advice advice,  MethodCallback<Void> callBack);

    @GET
    @Path("advice/{adviceId}")
    public void get(@PathParam("adviceId") long adviceId,  MethodCallback<Advice> callBack);

    @DELETE
    @Path("advice/recipient")
    public void delete(AdviceRecipient adviceRecipient, MethodCallback<Void> callBack);

    @PUT
    @Path("advice/recipient")
    public void put(AdviceRecipient advice,  MethodCallback<Void> callBack);


    @DELETE
    @Path("advice/trigger")
    public void delete(AdviceTrigger adviceRecipient, MethodCallback<Void> callBack);

    @PUT
    @Path("advice/trigger")
    public void put(AdviceTrigger advice,  MethodCallback<Void> callBack);




}
