/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.model.LastPost;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by pete on 6/30/2014.
 */
public interface NoPostServiceClient extends RestService {

    @GET
    @Path("nopost")
    public void getNoPost(MethodCallback<List<LastPost>> callBack);


}
