/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Created by pete on 9/17/2014.
 */
public interface TextFileResource extends ClientBundle {

    TextFileResource INSTANCE = GWT.create(TextFileResource.class);

    @Source("files/countryCodes.csv")
    TextResource EN_COUNTRYCODE();

    @Source("files/countryCodes_es.csv")
    TextResource ES_COUNTRYCODE();

    @Source("files/regionCodes.csv")
    TextResource EN_REGION();

    @Source("files/regionCodes_es.csv")
    TextResource ES_REGION();

    @Source("files/timeZones.csv")
    TextResource EN_TIMEZONE();

    @Source("files/timeZones_es.csv")
    TextResource ES_TIMEZONE();
}
