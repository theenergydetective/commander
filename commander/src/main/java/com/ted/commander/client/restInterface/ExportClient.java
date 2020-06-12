/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.restInterface;

import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.model.export.BillingRequest;
import com.ted.commander.common.model.export.ExportRequest;
import com.ted.commander.common.model.export.ExportResponse;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by pete on 10/13/2014.
 */
public interface ExportClient extends RestService {

    @POST
    @Path("export/{virtualECCId}/{fileType}")
    public void getExportStatus(ExportRequest exportRequest, @PathParam("virtualECCId") Long virtualECCId, @PathParam("fileType") DataExportFileType fileType, MethodCallback<ExportResponse> callback);


    @POST
    @Path("billing/{fileType}")
    public void getBllingStatus(BillingRequest billingRequest, @PathParam("fileType") DataExportFileType fileType, MethodCallback<ExportResponse> callback);


}
