/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.MTU;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created by pete on 6/30/2014.
 */
public interface MTUServiceClient extends RestService {

    /**
     * Returns a list of MTU's for a given account.
     *
     * @param accountId
     * @param start
     * @param limit
     * @param sort
     * @param sortOrder
     * @param callBack
     */
    @GET
    @Path("account/{accountId}/mtus")
    public void getMTUs(@PathParam("accountId") long accountId, @QueryParam("start") int start, @QueryParam("limit") int limit, @QueryParam("sort") String sort, @QueryParam("sortOrder") String sortOrder, MethodCallback<List<MTU>> callBack);


    @PUT
    @Path("mtu")
    public void updateMTU(MTU mtu, MethodCallback<Void> callBack);


    @DELETE
    @Path("mtu")
    public void deleteMTU(MTU mtu, MethodCallback<Void> callBack);
}
