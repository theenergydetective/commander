/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanKey;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.ted.commander.common.model.RESTPostResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.*;
import java.util.List;

/**
 * Rest service interface for updated and editing energy plans
 */
public interface EnergyPlanServiceClient extends RestService {

    @GET
    @Path("account/{accountId}/energyPlanKeys")
    public void getKeysForAccount(@PathParam("accountId") long accountId, MethodCallback<List<EnergyPlanKey>> callBack);

    @POST
    @Path("energyPlan")
    public void create(EnergyPlan energyPlan, MethodCallback<RESTPostResponse> callBack);

    @PUT
    @Path("energyPlan")
    public void update(EnergyPlan energyPlan, MethodCallback<Void> callBack);

    @GET
    @Path("energyPlan")
    public void getForLocation(@QueryParam("virtualECCId") long virtualECCId, MethodCallback<EnergyPlan> callBack);


    @GET
    @Path("energyPlan/{energyPlanId}")
    public void get(@PathParam("energyPlanId") long energyPlanId, MethodCallback<EnergyPlan> callBack);

    @DELETE
    @Path("energyPlan/{energyPlanId}")
    public void delete(@PathParam("energyPlanId") long energyPlanId, MethodCallback<Void> callBack);

    @PUT
    @Path("energyPlan/{energyPlanId}/season")
    public void putSeason(@PathParam("energyPlanId") long energyPlanId, EnergyPlanSeason energyPlanSeason, MethodCallback<Void> callBack);

    @DELETE
    @Path("energyPlan/{energyPlanId}/season/{seasonId}")
    public void delSeason(@PathParam("energyPlanId") long energyPlanId, @PathParam("seasonId") long seasonId, MethodCallback<Void> callBack);



}
