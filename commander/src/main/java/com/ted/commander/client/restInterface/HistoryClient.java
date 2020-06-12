/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.DailySummary;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Created by pete on 10/13/2014.
 */
public interface HistoryClient extends RestService {


    @GET
    @Path("dailySummary/{virtualECCId}")
    public void getDailySummary(@PathParam("virtualECCId") Long virtualECCId, @QueryParam("startDate") String startDate, MethodCallback<DailySummary> callBack);


}
