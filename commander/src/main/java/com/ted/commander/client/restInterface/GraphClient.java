/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.export.GraphRequest;
import com.ted.commander.common.model.export.GraphResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by pete on 10/13/2014.
 */
public interface GraphClient extends RestService {


    @POST
    @Path("graph/{virtualECCId}/{historyType}")
    public void request(GraphRequest graphRequest, @PathParam("virtualECCId") Long virtualECCId, @PathParam("historyType") HistoryType historyType, MethodCallback<GraphResponse> callback);

}
