/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.ComparisonQueryRequest;
import com.ted.commander.common.model.ComparisonQueryResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by pete on 10/13/2014.
 */
public interface ComparisonClient extends RestService {

    @POST
    @Path("comparison")
    public void generate(ComparisonQueryRequest request, MethodCallback<ComparisonQueryResponse> callBack);

}
