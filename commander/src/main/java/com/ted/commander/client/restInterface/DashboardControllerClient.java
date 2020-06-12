/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.DashboardResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * General methods for returning resources.
 */
public interface DashboardControllerClient extends RestService {

    @GET
    @Path("dashboard/{virtualECCId}")
    public void get(@PathParam("virtualECCId") long virtualECCId,
                    @QueryParam("startDate") String startDate,
                    @QueryParam("endDate") String endDate,
                    @QueryParam("weather") String weather,
                    MethodCallback<DashboardResponse> callBack);

}
