/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.alexa;

import java.io.Serializable;


public enum  AlexaResponseStatus implements Serializable {
    INVALID_AUTH,
    NO_LOCATIONS,
    SUCCESS,
    LOCATIONS
}
