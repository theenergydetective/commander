/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

/**
 * Created by pete on 6/30/2014.
 */
public interface AuthServiceClient extends RestService {

    @DELETE
    @Path("auth")
    public void logout(MethodCallback callBack);
}
