/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * General methods for returning resources.
 */
public interface ResourceServiceClient extends RestService {

    @GET
    @Path("/resource/utilities")
    public void findUtilities(@QueryParam("query") String subString, @QueryParam("limit") int limit, MethodCallback<List<String>> callBack);

}
