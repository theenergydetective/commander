/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.RESTPostResponse;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.VirtualECCMTU;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

/**
 * Rest service interface for updated and editing virtual ECC's
 */
public interface VirtualECCServiceClient extends RestService {

    @POST
    @Path("virtualECC")
    public void create(VirtualECC virtualECC, MethodCallback<RESTPostResponse> callBack);

    @GET
    @Path("virtualECC")
    void getForAllAccounts(MethodCallback<List<AccountLocation>> callback);

    @PUT
    @Path("virtualECC")
    public void update(VirtualECC virtualECC, MethodCallback<Void> callBack);

    @GET
    @Path("virtualECC/{virtualECCId}")
    public void get(@PathParam("virtualECCId") long virtualECCId, MethodCallback<VirtualECC> callBack);

    @DELETE
    @Path("virtualECC/{virtualECCId}")
    public void delete(@PathParam("virtualECCId") long virtualECCId, MethodCallback<Void> callBack);


    @GET
    @Path("/account/{accountId}/virtualECCs")
    public void getForAccount(@PathParam("accountId") long accountId, MethodCallback<List<VirtualECC>> callBack);

    @GET
    @Path("/account/{accountId}/virtualECC/{virtualECCId}/mtu")
    public void getVirtualECCMTUs(@PathParam("accountId") long accountId, @PathParam("virtualECCId") long virtualECCId, MethodCallback<List<VirtualECCMTU>> callBack);

    @PUT
    @Path("virtualECC/{virtualECCId}/mtu")
    public void addVirtualECCMTU(VirtualECCMTU virtualECCMTU, @PathParam("virtualECCId") long virtualECCId, MethodCallback<Void> callBack);

    @DELETE
    @Path("virtualECC/{virtualECCId}/mtu")
    public void deleteVirtualECCMTU(VirtualECCMTU virtualECCMTU, @PathParam("virtualECCId") long virtualECCId, MethodCallback<Void> callBack);

    @DELETE
    @Path("virtualECC/{virtualECCId}/mtus")
    public void deleteVirtualECCMTUS(@PathParam("virtualECCId") long virtualECCId, MethodCallback<Void> callBack);

}
