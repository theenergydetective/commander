/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;


public interface TooltipWebStringResource extends Constants {

    static TooltipWebStringResource INSTANCE = GWT.create(TooltipWebStringResource.class);

    String prevWeek();

    String nextWeek();

    String prevMonth();

    String nextMonth();

    String changeLocation();

    String dashOptions();
}
